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



package fastinfoset.tools;


import fastinfoset.SAX_FI_Encoder;
import fastinfoset.sax.FastInfosetSource;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 *
 * @author rpablos
 */
public class FICoder {
 
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {     
       if (args.length < 2 || args.length > 3) {
           System.out.println("Uso: java -jar FastInfoset.jar [-d] <origen> <destino>");
           System.out.println("Por defecto, codifica un XML origen en un FastInfoset destino");
           System.out.println("Con la opción -d, decodifica un FastInfoset origen en un XML destino");
           System.exit(0);
       }
       boolean decode = false;
       File origen;
       File destino;
       if (args.length == 3) {
           decode = (args[0].equals("-d"));
           origen = new File (args[1]);
           destino = new File (args[2]);
       } else {
           origen = new File (args[0]);
           destino = new File (args[1]);
       }
       DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
       dbf.setNamespaceAware(true);
       DocumentBuilder db = dbf.newDocumentBuilder();
       
       if (!decode) {
           OutputStream out = new BufferedOutputStream(new FileOutputStream(destino),1<<16);

           SAXParserFactory spf = SAXParserFactory.newInstance();
           spf.setNamespaceAware(true);
           SAXParser sp = spf.newSAXParser();
           XMLReader xmlreader = sp.getXMLReader();
           SAX_FI_Encoder myhandler = new SAX_FI_Encoder();
           
           myhandler.setOutputStream(out);
           xmlreader.setContentHandler(myhandler);
           xmlreader.setDTDHandler(myhandler);
           xmlreader.setProperty("http://xml.org/sax/properties/lexical-handler", myhandler);
           long time = System.currentTimeMillis();
           xmlreader.parse(new InputSource(new BufferedInputStream(new FileInputStream(origen))));
           System.out.println("Tiempo: "+(System.currentTimeMillis()-time)+" ms con parseo y serialización SAX"); 
           
       } else {
           TransformerFactory transformerFactory = TransformerFactory.newInstance();
           Transformer transformer = transformerFactory.newTransformer();
           FastInfosetSource source = new FastInfosetSource(new BufferedInputStream(new FileInputStream(origen)));
           StreamResult result = new StreamResult(destino);
           long time = System.currentTimeMillis();
           transformer.transform(source, result);
           System.out.println("Tiempo: "+(System.currentTimeMillis()-time)+" ms con procesado SAX transform"); 
       }
    }
    
    
}
