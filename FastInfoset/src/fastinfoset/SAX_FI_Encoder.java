//  Author: Ronald Pablos
//  Year: 2013


package fastinfoset;

import fastinfoset.Algorithm.Algorithm;
import fastinfoset.Algorithm.Builtin.CDATA;
import fastinfoset.Alphabet.Alphabet;
import fastinfoset.Document.DocumentType;
import fastinfoset.Document.Element.Attribute;
import fastinfoset.Document.Element.FastInfosetAttributes;
import fastinfoset.Document.Element.NamespaceAttribute;
import fastinfoset.Document.Notation;
import fastinfoset.Document.ProcessingInstruction;
import fastinfoset.Document.UnparsedEntity;
import fastinfoset.sax.AlphabetHandler;
import fastinfoset.sax.ObjectAlgorithmHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.Locator2;

/**
 *
 * @author rpablos
 */
public class SAX_FI_Encoder extends Encoder implements ContentHandler, DTDHandler, LexicalHandler, ObjectAlgorithmHandler,AlphabetHandler {

    protected boolean isStandAlone = false;
    
    boolean enDTD = false;
    boolean firstchild = true;
    protected boolean enCDATA = false;
    List<Notation> notations = new ArrayList<Notation>();
    List<UnparsedEntity> unparsedEntities = new ArrayList<UnparsedEntity>();
    private List<ProcessingInstruction> processingInstructionsInDTD = new ArrayList<ProcessingInstruction>();
    private DocumentType documentType = null;;
    Locator locator;

    public SAX_FI_Encoder() {
        super();
    }

    
    public SAX_FI_Encoder(boolean utf8encoding) {
        super(utf8encoding);
    }
    
    
    
    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    @Override
    public void startDocument() throws SAXException {  }

    @Override
    public void endDocument() throws SAXException {
        try {
            encodeDocumentTermination();
            _out.flush();;
        } catch (IOException ex) {
            throw new SAXException(ex);
        }
    }
    private List<NamespaceAttribute> _nsattributeBuffer = new ArrayList<NamespaceAttribute>();
    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        _nsattributeBuffer.add(new NamespaceAttribute(prefix, uri));
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {  }

    private List<Attribute> _attributeBuffer = new ArrayList<Attribute>();
    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (atts instanceof FastInfosetAttributes) {
            startElement(new QName(uri,localName,getPrefixFromQName(qName)),((FastInfosetAttributes)atts).getAttributes() );
            return;
        }
            
            if (atts.getLength() > 0) {
                for (int i = 0; i < atts.getLength(); i++)
                    _attributeBuffer.add(new Attribute(
                            new QName(atts.getURI(i),atts.getLocalName(i),getPrefixFromQName(atts.getQName(i))),
                            atts.getValue(i)));
            }

            startElement(new QName(uri,localName,getPrefixFromQName(qName)), _attributeBuffer);

            if (atts.getLength() > 0) {
                _attributeBuffer.clear();
            }
    }
    public void startElement(QName qname, List<Attribute> atts) throws SAXException {
        try {
            encodeInitialEncondingsIfFirstChild();
            encodeElement(qname, atts, _nsattributeBuffer);
            _nsattributeBuffer.clear();
        } catch (IOException ex) {
            throw new SAXException(ex);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        
        try {
            encodeElementTermination();
        } catch (IOException ex) {
            throw new SAXException(ex);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (length == 0) return;
        try {
            
            encodeCharacterChunk(new String(ch,start,length), enCDATA?CDATA.instance:null);
        } catch (IOException ex) {
                throw new SAXException(ex);
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        //System.out.println("ignorableWhitespace");
        characters(ch, start, length);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        if (enDTD) {
            processingInstructionsInDTD.add(new ProcessingInstruction(target, data));
            return;
        }
        try {
            encodeInitialEncondingsIfFirstChild();
            encodeProcessingInstruction(target, data);
            
        } catch (IOException ex) {
            throw new SAXException(ex);
        }
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        try {
            encodeEntityReference(name);
        } catch (IOException ex) {
            throw new SAXException(ex);
        }
    }

    @Override
    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
        notations.add(new Notation(name, systemId, publicId));
    }

    @Override
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
        unparsedEntities.add(new UnparsedEntity(name, systemId, publicId, notationName));
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        enDTD = true;
        documentType = new DocumentType(systemId, publicId, processingInstructionsInDTD);
    }

    @Override
    public void endDTD() throws SAXException {
        enDTD = false;
    }

    @Override
    public void startEntity(String name) throws SAXException { }

    @Override
    public void endEntity(String name) throws SAXException { }

    @Override
    public void startCDATA() throws SAXException {
        enCDATA = true;
    }

    @Override
    public void endCDATA() throws SAXException {
        enCDATA = false;
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        try {
            encodeInitialEncondingsIfFirstChild();
            encodeComment(new String(ch, start, length));
        } catch (IOException ex) {
            throw new SAXException(ex);
        }
    }

    public void setStandAlone(boolean StandAlone) {
        this.isStandAlone = StandAlone;
    }
    
    private void encodeInitialEncondingsIfFirstChild() throws IOException {
        if (firstchild) {
            firstchild = false;
            //codificar todo el header ahora que tenemos la info
            encodeHeader();
            if (documentType != null) {
                encodeDocumentType(documentType);
            }
        }
    }
    private void encodeHeader() throws IOException {
        encodeHeader((locator instanceof Locator2)?((Locator2)locator).getEncoding():null,
                isStandAlone,
                (locator instanceof Locator2)?((Locator2)locator).getXMLVersion():null,
                unparsedEntities, notations
                );
                
    }
    
    public static String getPrefixFromQName(String qName) {
        int i = qName.indexOf(':');
        return (i != -1)?qName.substring(0, i):"";
    }

    @Override
    public void object(Object object, Algorithm algorithm) throws SAXException {
        try {
            encodeObjectCharacterChunk(object, algorithm);
        } catch (IOException ex) {
                throw new SAXException(ex);
        }
    }

    @Override
    public void object(byte[] data, int algorithmId) throws SAXException {
        try {
            encodeObjectCharacterChunk(data, algorithmId);
        } catch (IOException ex) {
                throw new SAXException(ex);
        }
    }

    @Override
    public void object(byte[] data, String uri) throws SAXException {
        try {
            encodeObjectCharacterChunk(data, vocabulary.algorithmURIs.get(uri));
        } catch (IOException ex) {
                throw new SAXException(ex);
        }
    }
    
    @Override
    public void alphabet(String str, Alphabet alphabet) throws SAXException {
        if (str.isEmpty()) return;
        try {
            encodeCharacterChunk(str, alphabet);
        } catch (IOException ex) {
                throw new SAXException(ex);
        }
    }

}
