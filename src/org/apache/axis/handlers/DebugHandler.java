/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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

package org.apache.axis.handlers ;

import java.util.* ;

import org.apache.axis.* ;
import org.apache.axis.utils.* ;
import org.apache.axis.message.RPCArg;
import org.apache.axis.message.RPCBody;
import org.apache.axis.message.SOAPBody;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeader;

import org.w3c.dom.* ;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class DebugHandler extends BasicHandler {
    
    public void invoke(MessageContext msgContext) throws AxisFault {
        Debug.Print( 1, "Enter: DebugHandler::invoke" );
        try {
            Message       msg = msgContext.getRequestMessage();
            SOAPEnvelope  env = (SOAPEnvelope) msg.getAs( "SOAPEnvelope" );
            Vector        headers = null ;
            
            headers = env.getHeadersByNameAndURI("Debug", Constants.URI_DEBUG);

            for ( int i = 0 ; headers != null && i < headers.size() ; i ++ ) {
                SOAPHeader  header = (SOAPHeader) headers.get(i);
                Element     root   = header.getRoot();
                String      value = root.getFirstChild().getNodeValue();
                if ( value != null ) {
                    int     debugVal = Integer.parseInt( value );
                    Debug.Print( 1, "Setting debug level to: " + debugVal );
                    Debug.setDebugLevel( debugVal );
                    header.setProcessed( true );
                }
            }
        }
        catch( Exception e ) {
            Debug.Print( 1, e );
            throw new AxisFault( e );
        }
        Debug.Print( 1, "Exit: DebugHandler::invoke" );
    }

    public void undo(MessageContext msgContext) {
        Debug.Print( 1, "Enter: DebugHandler::undo" );
        Debug.Print( 1, "Exit: DebugHandler::undo" );
    }
    
};
