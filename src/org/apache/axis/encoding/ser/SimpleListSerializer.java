/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.axis.encoding.ser;
import java.io.IOException;
import java.lang.reflect.Array;

import javax.xml.namespace.QName;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.description.FieldDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SimpleType;
import org.apache.axis.encoding.SimpleValueSerializer;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Serializer for 
 * <xsd:simpleType ...>
 *   <xsd:list itemType="...">
 * </xsd:simpleType>
 * based on SimpleSerializer
 *
 * @author Ias <iasandcb@tmax.co.kr>
 */
public class SimpleListSerializer implements SimpleValueSerializer {
    public QName xmlType;
    public Class javaType;

    private BeanPropertyDescriptor[] propertyDescriptor = null;
    private TypeDesc typeDesc = null;

    public SimpleListSerializer(Class javaType, QName xmlType) {
        this.xmlType = xmlType;
        this.javaType = javaType;
    }
    public SimpleListSerializer(Class javaType, QName xmlType, TypeDesc typeDesc) {
        this.xmlType = xmlType;
        this.javaType = javaType;
        this.typeDesc = typeDesc;
    }

    
    /**
     * Serialize a list of primitives or simple values.
     */
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        if (value != null && value.getClass() == java.lang.Object.class) {
            throw new IOException(Messages.getMessage("cantSerialize02"));
        }

        // get any attributes
        if (value instanceof SimpleType)
            attributes = getObjectAttributes(value, attributes, context);

        context.startElement(name, attributes);
        if (value != null) {
            context.writeSafeString(getValueAsString(value, context));
        }
        context.endElement();
    }

    public String getValueAsString(Object value, SerializationContext context) {
        // We could have separate serializers/deserializers to take
        // care of Float/Double cases, but it makes more sence to
        // put them here with the rest of the java lang primitives.
      
      int length = Array.getLength(value);
      StringBuffer result = new StringBuffer();
      for (int i = 0; i < length; i++) {
        Object object = Array.get(value, i);
        if (object instanceof Float ||
            object instanceof Double) {
          double data = 0.0;
          if (object instanceof Float) {
              data = ((Float) object).doubleValue();
          } else {
              data = ((Double) object).doubleValue();
          }
          if (Double.isNaN(data)) {
              result.append("NaN");
          } else if (data == Double.POSITIVE_INFINITY) {
            result.append("INF");
          } else if (data == Double.NEGATIVE_INFINITY) {
              result.append("-INF");
          }
          else {
            result.append(object.toString());
          }
        }
        else {
          result.append(object.toString());
        }
        if (i < length - 1) {
            result.append(' ');
        }
      }
      return result.toString();
    }
    
    private Attributes getObjectAttributes(Object value,
                                           Attributes attributes,
                                           SerializationContext context) {
        if (typeDesc == null || !typeDesc.hasAttributes())
            return attributes;

        AttributesImpl attrs;
        if (attributes == null) {
            attrs = new AttributesImpl();
        } else if (attributes instanceof AttributesImpl) {
            attrs = (AttributesImpl)attributes;
        } else {
            attrs = new AttributesImpl(attributes);
        }

        try {
            // Find each property that is an attribute
            // and add it to our attribute list
            for (int i=0; i<propertyDescriptor.length; i++) {
                String propName = propertyDescriptor[i].getName();
                if (propName.equals("class"))
                    continue;

                FieldDesc field = typeDesc.getFieldByName(propName);
                // skip it if its not an attribute
                if (field == null || field.isElement())
                    continue;

                QName qname = field.getXmlName();
                if (qname == null) {
                    qname = new QName("", propName);
                }

                if (propertyDescriptor[i].isReadable() &&
                    !propertyDescriptor[i].isIndexed()) {
                    // add to our attributes
                    Object propValue = propertyDescriptor[i].get(value);
                    // If the property value does not exist, don't serialize
                    // the attribute.  In the future, the decision to serializer
                    // the attribute may be more sophisticated.  For example, don't
                    // serialize if the attribute matches the default value.
                    if (propValue != null) {
                        String propString = getValueAsString(propValue, context);

                        String namespace = qname.getNamespaceURI();
                        String localName = qname.getLocalPart();

                        attrs.addAttribute(namespace,
                                           localName,
                                           context.qName2String(qname),
                                           "CDATA",
                                           propString);
                    }
                }
            }
        } catch (Exception e) {
            // no attributes
            return attrs;
        }

        return attrs;
    }

    public String getMechanismType() { return Constants.AXIS_SAX; }

    /**
     * Return XML schema for the specified type, suitable for insertion into
     * the &lt;types&gt; element of a WSDL document, or underneath an
     * &lt;element&gt; or &lt;attribute&gt; declaration.
     *
     * @param javaType the Java Class we're writing out schema for
     * @param types the Java2WSDL Types object which holds the context
     *              for the WSDL being generated.
     * @return a type element containing a schema simpleType/complexType
     * @see org.apache.axis.wsdl.fromJava.Types
     */
    public Element writeSchema(Class javaType, Types types) throws Exception {
        // Let the caller generate WSDL if this is not a SimpleType
        if (!SimpleType.class.isAssignableFrom(javaType))
            return null;

        // ComplexType representation of SimpleType bean class
        Element complexType = types.createElement("complexType");
        types.writeSchemaElementDecl(xmlType, complexType);
        complexType.setAttribute("name", xmlType.getLocalPart());

        // Produce simpleContent extending base type.
        Element simpleContent = types.createElement("simpleContent");
        complexType.appendChild(simpleContent);
        Element extension = types.createElement("extension");
        simpleContent.appendChild(extension);

        // Get the base type from the "value" element of the bean
        String base = "string";
        for (int i=0; i<propertyDescriptor.length; i++) {
            String propName = propertyDescriptor[i].getName();
            if (!propName.equals("value")) {
                if (typeDesc != null) {
                    FieldDesc field = typeDesc.getFieldByName(propName);
                    if (field != null) {
                        if (field.isElement()) {
                            // throw?
                        }
                        QName qname = field.getXmlName();
                        if (qname == null) {
                            // Use the default...
                            qname = new QName("", propName);
                        }

                        //  write attribute element
                        Class fieldType = propertyDescriptor[i].getType();

                        // Attribute must be a simple type, enum or SimpleType
                        if (!types.isAcceptableAsAttribute(fieldType)) {
                            throw new AxisFault(Messages.getMessage("AttrNotSimpleType00",
                                    propName,
                                    fieldType.getName()));
                        }

                        // write attribute element
                        // TODO the attribute name needs to be preserved from the XML
                        Element elem = types.createAttributeElement(propName,
                                fieldType,
                                field.getXmlType(),
                                false,
                                extension.getOwnerDocument());
                        extension.appendChild(elem);
                    }
                }
                continue;
            }

            BeanPropertyDescriptor bpd = propertyDescriptor[i];
            Class type = bpd.getType();
            // Attribute must extend a simple type, enum or SimpleType
            if (!types.isAcceptableAsAttribute(type)) {
                throw new AxisFault(Messages.getMessage("AttrNotSimpleType01",
                        type.getName()));
            }
            base = types.writeType(type);
            extension.setAttribute("base", base);
        }

        // done
        return complexType;

    }
}