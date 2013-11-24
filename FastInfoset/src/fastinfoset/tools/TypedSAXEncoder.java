//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.tools;

import fastinfoset.Algorithm.Algorithm;
import fastinfoset.Algorithm.Builtin.CDATA;
import fastinfoset.Algorithm.Builtin.HEXADECIMAL;
import fastinfoset.Alphabet.Numeric;
import fastinfoset.Document.Element.AlgorithmAttribute;
import fastinfoset.Document.Element.Attribute;
import fastinfoset.SAX_FI_Encoder;
import fastinfoset.util.InitialVocabulary;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
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
        if (args.length != 3) {
            System.out.println("Uso: TypedSaxEncoder <config> <xml> <fi>");
            System.exit(0);
        }

        Properties properties = new Properties();
        properties.load(new FileInputStream(args[0]));
        InitialVocabulary externalVocabulary = new InitialVocabulary();
        String externalURI = (String) properties.get("EXTERNALURI");
        PopulateVocabulary((String)properties.get("EXTERNALLOCALNAMES"),externalVocabulary.localnames);
        PopulateVocabulary((String)properties.get("EXTERNALPREFIXES"),externalVocabulary.prefixes);
        PopulateVocabulary((String)properties.get("EXTERNALNAMESPACES"),externalVocabulary.namespaces);
        InitialVocabulary initialVocabulary = new InitialVocabulary();
        initialVocabulary.setExternalVocabulary(externalURI, externalVocabulary);
        
        Map<QName,Algorithm> elementToalgorithm = new HashMap<QName,Algorithm>();
        PopulateMapWithAlgorithm((String) properties.get("NUMERICELEMENTS"), elementToalgorithm, Numeric.instance);
        Map<QName,Algorithm> attributeToalgorithm = new HashMap<QName,Algorithm>();
        PopulateMapWithAlgorithm((String) properties.get("NUMERICATTRIBUTES"), attributeToalgorithm, Numeric.instance);
        PopulateMapWithAlgorithm((String) properties.get("HEXADECIMALATTRIBUTES"), attributeToalgorithm, HEXADECIMAL.instance);
        Map<QName,Character> csvElements = new HashMap<QName,Character>();
        PopulateMapWithDelimiter((String) properties.get("CSVELEMENTS"),csvElements,',');
        
        OutputStream out = new BufferedOutputStream(new FileOutputStream(args[2]),1<<16);
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        //spf.setFeature("http://xml.org/sax/features/namespace-prefixes", Boolean.TRUE);
        SAXParser sp = spf.newSAXParser();
        XMLReader xmlreader = sp.getXMLReader();
        TypedSAXEncoder handler = new TypedSAXEncoder(elementToalgorithm,attributeToalgorithm,csvElements);
        handler.setOutputStream(out);
        handler.setMaximumChunkLengthForIndexing(60);
        if (!initialVocabulary.isEmpty())
            handler.setInitialVocabulary(initialVocabulary);
        xmlreader.setContentHandler(handler);
        xmlreader.setDTDHandler(handler);
        xmlreader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
        long time = System.currentTimeMillis();
        xmlreader.parse(new InputSource(new FileInputStream(args[1])));
        System.out.println("Tiempo: "+(System.currentTimeMillis()-time)+" ms"); 
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
