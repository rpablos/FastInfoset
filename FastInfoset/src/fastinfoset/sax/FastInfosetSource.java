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

import fastinfoset.Decoder;
import fastinfoset.SAX_FI_Decoder;
import java.io.InputStream;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 *
 * @author rpablos
 */
public class FastInfosetSource extends SAXSource {
   
    public FastInfosetSource(InputStream inputStream) {
        super(new InputSource(inputStream));
    }

    @Override
    public XMLReader getXMLReader() {
        XMLReader reader = super.getXMLReader();
        if (reader == null) {
            reader = new SAX_FI_Decoder();
            setXMLReader(reader);
        }
        return reader;
    }
    
    public Decoder getFastInfosetDecoder() {
        return  (Decoder) getXMLReader();
    }
    
}