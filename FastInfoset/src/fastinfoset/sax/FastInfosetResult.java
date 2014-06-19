/*
* Copyright 2014 Ronald Pablos.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package fastinfoset.sax;

import fastinfoset.Encoder;
import fastinfoset.SAX_FI_Encoder;
import java.io.OutputStream;
import javax.xml.transform.sax.SAXResult;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

/**
 *
 * @author rpablos
 */
public class FastInfosetResult extends SAXResult {
    OutputStream _outputStream;
    
    public FastInfosetResult(OutputStream outputStream) {
        _outputStream = outputStream;
    }
    
    @Override
    public ContentHandler getHandler() {
        ContentHandler handler = super.getHandler();
        if (handler == null) {
            handler = new SAX_FI_Encoder();
            setHandler(handler);
        }
        ((SAX_FI_Encoder) handler).setOutputStream(_outputStream);
        return handler;        
    }
    
    @Override
    public LexicalHandler getLexicalHandler() {
        return (LexicalHandler) getHandler();
    }
    public Encoder getFastInfosetDecoder() {
        return  (Encoder) getHandler();
    }
}
