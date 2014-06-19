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

import fastinfoset.Algorithm.Algorithm;
import fastinfoset.Algorithm.Builtin.HEXADECIMAL;
import fastinfoset.Alphabet.DateAndTime;
import fastinfoset.Alphabet.Numeric;
import fastinfoset.Document.Element.AlgorithmAttribute;
import fastinfoset.Document.Element.Attribute;
import fastinfoset.SAX_FI_Encoder;
import fastinfoset.sax.FastInfosetSource;
import fastinfoset.util.InitialVocabulary;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 *
 * @author rpablos
 */
public class TypedSAXEncoder extends SAX_FI_Encoder {
    
    static public void main(String args[]) throws  Exception {
        if ((args.length < 3) || ((args.length == 4) && !args[0].equals("-d"))) {
            System.out.println("Usage for encoding: TypedSaxEncoder <config> <xml> <fi>");
            System.out.println("Usage for decoding: TypedSaxEncoder -d <config> <fi> <xml>");
            System.exit(0);
        }
        boolean decode = args[0].equals("-d");
        File config = new File(args[decode?1:0]);
        File source = new File(args[decode?2:1]);
        File destination = new File(args[decode?3:2]);
        Properties properties = new Properties();
        properties.load(new FileInputStream(config));
        String maxChunkLenStr = (String) properties.get("MAXIMUMCHUNKLENGTH");
        int maxChunkLen = -1;
        try {
            maxChunkLen = Integer.parseInt(maxChunkLenStr);
        } catch (Exception e) {}
        if (maxChunkLen < 0)
            maxChunkLen = 60;
        InitialVocabulary externalVocabulary = new InitialVocabulary();
        String externalURI = (String) properties.get("EXTERNALURI");
        PopulateVocabulary((String)properties.get("EXTERNALLOCALNAMES"),externalVocabulary.localnames);
        PopulateVocabulary((String)properties.get("EXTERNALPREFIXES"),externalVocabulary.prefixes);
        PopulateVocabulary((String)properties.get("EXTERNALNAMESPACES"),externalVocabulary.namespaces);
        
        
        Map<QName,Algorithm> elementToalgorithm = new HashMap<QName,Algorithm>();
        PopulateMapWithAlgorithm((String) properties.get("NUMERICELEMENTS"), elementToalgorithm, Numeric.instance);
        PopulateMapWithAlgorithm((String) properties.get("HEXADECIMALELEMENTS"), elementToalgorithm, HEXADECIMAL.instance);
        PopulateMapWithAlgorithm((String) properties.get("DATETIMEELEMENTS"), elementToalgorithm, DateAndTime.instance);
        Map<QName,Algorithm> attributeToalgorithm = new HashMap<QName,Algorithm>();
        PopulateMapWithAlgorithm((String) properties.get("NUMERICATTRIBUTES"), attributeToalgorithm, Numeric.instance);
        PopulateMapWithAlgorithm((String) properties.get("HEXADECIMALATTRIBUTES"), attributeToalgorithm, HEXADECIMAL.instance);
        PopulateMapWithAlgorithm((String) properties.get("DATETIMEATTRIBUTES"), attributeToalgorithm, DateAndTime.instance);
        Map<QName,Character> csvElements = new HashMap<QName,Character>();
        PopulateMapWithDelimiter((String) properties.get("CSVELEMENTS"),csvElements,',');
        PopulateMapWithDelimiter((String) properties.get("SEMICOLONELEMENTS"),csvElements,';');
        PopulateMapWithDelimiter((String) properties.get("COLONELEMENTS"),csvElements,':');
        if (!decode) {
            InitialVocabulary initialVocabulary = new InitialVocabulary();
            initialVocabulary.setExternalVocabulary(externalURI, externalVocabulary);
            OutputStream out = new BufferedOutputStream(new FileOutputStream(destination),1<<16);
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            SAXParser sp = spf.newSAXParser();
            XMLReader xmlreader = sp.getXMLReader();
            TypedSAXEncoder handler = new TypedSAXEncoder(elementToalgorithm,attributeToalgorithm,csvElements);
            handler.setOutputStream(out);
            handler.setDefaultAllowPolicyMaximumChunkLengthForIndexing(maxChunkLen);
            if (!initialVocabulary.isEmpty())
                handler.setInitialVocabulary(initialVocabulary);
            
            xmlreader.setContentHandler(handler);
            xmlreader.setDTDHandler(handler);
            xmlreader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
            long time = System.currentTimeMillis();
            xmlreader.parse(new InputSource(new FileInputStream(source)));
            System.out.println("Time: "+(System.currentTimeMillis()-time)+" ms"); 
        } else {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            FastInfosetSource fisource = new FastInfosetSource(new BufferedInputStream(new FileInputStream(source)));
            Map<String, InitialVocabulary> externalVocabularies = new HashMap<String, InitialVocabulary>();
            externalVocabularies.put(externalURI, externalVocabulary);
            fisource.getFastInfosetDecoder().registerExternalVocabularies(externalVocabularies);
            StreamResult result = new StreamResult(destination);
            long time = System.currentTimeMillis();
            transformer.transform(fisource, result);
            System.out.println("Time: "+(System.currentTimeMillis()-time)+" ms with SAX transform");
        }
    }
    private static final Pattern pattern = Pattern.compile("\\s*([^\\s\\{\\}]*)(?:\\{(.*)\\})?\\s*");
    static private QName getQName(String str) {
         
         Matcher matcher = pattern.matcher(str);
         if (!matcher.matches())
             throw new IllegalArgumentException(str);
         return new QName(matcher.group(2),matcher.group(1));
    }
    static void PopulateMapWithAlgorithm(String str, Map<QName,Algorithm> map, Algorithm algo ) {
        if (str == null) return;
        StringTokenizer st = new StringTokenizer(str,",");
        while (st.hasMoreTokens()) {
            map.put(getQName(st.nextToken()), algo);
            
        }
    }
    static void PopulateMapWithDelimiter(String str, Map<QName,Character> map,Character delim) {
        if (str == null) return;
        StringTokenizer st = new StringTokenizer(str,",");
        while (st.hasMoreTokens()) {
            map.put(getQName(st.nextToken()),delim);
            
        }
    }

    private static void PopulateVocabulary(String str, List<String> table) {
        if (str == null) return;
        StringTokenizer st = new StringTokenizer(str,",");
        while (st.hasMoreTokens()) {
            table.add(st.nextToken());
        }
    }
    
    Map<QName,Algorithm> elementToalgorithm;
    Map<QName,Algorithm> attributeToalgorithm;
    Map<QName,Character> delimitedElement;
    private TypedSAXEncoder(Map<QName,Algorithm> elementToalgorithm, 
                            Map<QName,Algorithm> attributeToalgorithm,
                            Map<QName,Character> delimitedElement) {
        this.delimitedElement = (delimitedElement == null)?new HashMap<QName, Character>():delimitedElement;
        this.elementToalgorithm = (elementToalgorithm == null)?new HashMap<QName, Algorithm>():elementToalgorithm;
        this.attributeToalgorithm = (attributeToalgorithm == null)?new HashMap<QName, Algorithm>():attributeToalgorithm;
    }
    
    StringBuilder characterbuffer = new StringBuilder();
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (!enCDATA && (currentAlgorithm != null || currentDelimiter != null))
            characterbuffer.append(ch, start, length);
        else
            super.characters(ch, start, length);
    }

    private List<Attribute> _attributeBuffer = new ArrayList<Attribute>();
    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        encodeCharacterBuffer();
        QName elementName = new QName(uri,localName,getPrefixFromQName(qName));
        currentAlgorithm = elementToalgorithm.get(elementName);
        currentDelimiter = delimitedElement.get(elementName);
        if (atts.getLength()> 0) {
            for (int i = 0; i < atts.getLength(); i++) {
                QName attName = new QName(atts.getURI(i),atts.getLocalName(i),getPrefixFromQName(atts.getQName(i)));
                Algorithm algo = attributeToalgorithm.get(attName);
                _attributeBuffer.add((algo == null)?new Attribute(attName,atts.getValue(i)):
                                                    new AlgorithmAttribute(attName, atts.getValue(i), algo));
            }
        }
        super.startElement(elementName, _attributeBuffer);
        if (atts.getLength()> 0)
            _attributeBuffer.clear();
        //super.startElement(uri, localName, qName, atts);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        encodeCharacterBuffer();
        currentAlgorithm = null;
        currentDelimiter = null;
        super.endElement(uri, localName, qName);
    }
    
    Algorithm currentAlgorithm;
    Character currentDelimiter;
    private void encodeCharacterBuffer() throws SAXException {
        if (characterbuffer.length() >0) {
            try {
                if (currentDelimiter != null) {
                    StringTokenizer st = new StringTokenizer(characterbuffer.toString(),currentDelimiter.toString(),true);
                    while (st.hasMoreTokens()) {
                        String token = st.nextToken();
                        encodeCharacterChunk(token,
                                    (token.charAt(0) == currentDelimiter.charValue())?null:currentAlgorithm);
                    }
                } else {
                    char[] ca = new char[characterbuffer.length()];
                    characterbuffer.getChars(0, characterbuffer.length(), ca, 0);
                    encodeCharacterChunk(new String(ca,0,ca.length), currentAlgorithm);
                }
            } catch (IOException ex) {
                    throw new SAXException(ex);
            }
            characterbuffer.delete(0, characterbuffer.length());
        }
    }
    
    
}
