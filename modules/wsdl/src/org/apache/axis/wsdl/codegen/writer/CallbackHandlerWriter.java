/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axis.wsdl.codegen.writer;

import java.io.File;

import org.apache.axis.wsdl.codegen.XSLTConstants;

/**
 * @author chathura@opensource.lk
 *
 */
public class CallbackHandlerWriter extends ClassWriter {
	
	 public CallbackHandlerWriter(String outputFileLocation) {
        this.outputFileLocation = new File(outputFileLocation);
    }

    public CallbackHandlerWriter(File outputFileLocation,int language) {
        this.outputFileLocation = outputFileLocation;
        this.language = language;
    }

    public void loadTemplate() {
        Class clazz = this.getClass();
        switch (language){
            case XSLTConstants.LanguageTypes.JAVA:
                this.xsltStream = clazz.getResourceAsStream(XSLTConstants.XSLTInterfaceImplementationTemplates.JAVA_TEMPLATE);
                break;
            case XSLTConstants.LanguageTypes.C_SHARP:
                this.xsltStream = clazz.getResourceAsStream(XSLTConstants.XSLTInterfaceImplementationTemplates.CSHARP_TEMPLATE);
                break;
            case XSLTConstants.LanguageTypes.C_PLUS_PLUS:
            case XSLTConstants.LanguageTypes.VB_DOT_NET:
            default:
                throw new UnsupportedOperationException();
        }

    }

}