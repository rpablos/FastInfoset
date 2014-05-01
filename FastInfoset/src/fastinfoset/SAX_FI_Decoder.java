//  Author: Ronald Pablos
//  Year: 2013


package fastinfoset;

import fastinfoset.Algorithm.Builtin.CDATA;
import fastinfoset.Alphabet.Alphabet;
import fastinfoset.Document.DocumentType;
import fastinfoset.Document.Element.Attribute;
import fastinfoset.Document.Element.NamespaceAttribute;
import fastinfoset.Document.Element.SAXAttributes;
import fastinfoset.Document.Notation;
import fastinfoset.Document.ProcessingInstruction;
import fastinfoset.Document.UnparsedEntity;
import fastinfoset.sax.AlphabetHandler;
import fastinfoset.sax.ObjectAlgorithmHandler;
import fastinfoset.util.EncodedString;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import javax.xml.namespace.QName;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author rpablos
 */
public class SAX_FI_Decoder extends Decoder implements XMLReader {
    private EntityResolver _entityResolver;
    private DTDHandler _dtdHandler;
    private ContentHandler _contentHandler;
    private ErrorHandler _errorHandler;
    private LexicalHandler _lexicalHandler;
    private ObjectAlgorithmHandler _objectAlgorithmHandler = null;
    private AlphabetHandler _alphabetHandler = null;
    private static final String LEXICAL_HANDLER_PROPERTY =  "http://xml.org/sax/properties/lexical-handler";
    private static final String NAMESPACE_PREFIXES_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
    private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";

    public SAX_FI_Decoder() {
        DefaultHandler handler = new DefaultHandler();
        
        _entityResolver = handler;
        _dtdHandler = handler;
        _contentHandler = handler;
        _errorHandler = handler;
        _lexicalHandler = new LexicalHandlerImpl();
    }
    
    private static final class LexicalHandlerImpl implements LexicalHandler {
        public void comment(char[] ch, int start, int end) { }
        
        public void startDTD(String name, String publicId, String systemId) { }
        public void endDTD() { }
        
        public void startEntity(String name) { }
        public void endEntity(String name) { }
        
        public void startCDATA() { }
        public void endCDATA() { }
    };

    @Override
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals(NAMESPACES_FEATURE) || name.equals(NAMESPACE_PREFIXES_FEATURE)) {
            return true;
        } else {
            throw new SAXNotRecognizedException("Feature Not Supported: "+ name);
        }
    }

    @Override
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals(NAMESPACES_FEATURE)) {
            if (value == false) {
                throw new SAXNotSupportedException(name + ":" + value);
            }
        } else if (name.equals(NAMESPACE_PREFIXES_FEATURE)) {
            if (value == false) {
                throw new SAXNotSupportedException(name + ":" + value);
            }
        } else {
            throw new SAXNotRecognizedException("Feature Not Supported: " + name);
        }
    }

    @Override
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals(LEXICAL_HANDLER_PROPERTY)) {
            return getLexicalHandler();
        } else {
            throw new SAXNotRecognizedException("Property Not Recognized: "+name);
        }
    }

    @Override
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals(LEXICAL_HANDLER_PROPERTY)) {
            if (value instanceof LexicalHandler) {
                setLexicalHandler((LexicalHandler)value);
            } else {
                throw new SAXNotSupportedException(LEXICAL_HANDLER_PROPERTY);
            }
        } else {
            throw new SAXNotRecognizedException("Property Not Recognized:" + value);
        }
    }

    @Override
    public void setEntityResolver(EntityResolver resolver) {
        _entityResolver = resolver;
    }

    @Override
    public EntityResolver getEntityResolver() {
        return _entityResolver;
    }

    @Override
    public void setDTDHandler(DTDHandler handler) {
        _dtdHandler = handler;
    }

    @Override
    public DTDHandler getDTDHandler() {
        return _dtdHandler;
    }
    public void setLexicalHandler(LexicalHandler handler) {
        _lexicalHandler = handler;
    }
    
    public LexicalHandler getLexicalHandler() {
        return _lexicalHandler;
    }
    
    @Override
    public void setContentHandler(ContentHandler handler) {
        _contentHandler = handler;
    }

    @Override
    public ContentHandler getContentHandler() {
        return _contentHandler;
    }

    @Override
    public void setErrorHandler(ErrorHandler handler) {
        _errorHandler = handler;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return _errorHandler;
    }
    
    public void setObjectAlgorithmHandler(ObjectAlgorithmHandler handler) {
        _objectAlgorithmHandler = handler;
    }
    
    public ObjectAlgorithmHandler getObjectAlgorithmHandler() {
        return _objectAlgorithmHandler;
    }
    public void setAlphabetHandler(AlphabetHandler handler) {
        _alphabetHandler = handler;
    }
    
    public AlphabetHandler getAlphabetHandler() {
        return _alphabetHandler;
    }
    @Override
    public void parse(InputSource input) throws IOException, SAXException {
        try {
            InputStream s = input.getByteStream();
            if (s == null) {
                String systemId = input.getSystemId();
                if (systemId == null) {
                    throw new SAXException("no input source");
                }
                parse(systemId);
            } else {
                parse(s);
            }
        } catch (FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void parse(String systemId) throws IOException, SAXException {
        try {
            parse(new URL(systemId).openStream());
        } catch (FastInfosetException ex) {
            throw new SAXException(ex);
        }
    }
    public void parse(InputStream in) throws IOException, SAXException, FastInfosetException {
        try{
            _in = in;
            decodeHeader();
            processDocumentChildren();
        } catch (RuntimeException re) {
            _errorHandler.fatalError(new SAXParseException(re.getClass().getName(),null, re));
            throw new FastInfosetException(re);
        } catch (FastInfosetException fe) {
            _errorHandler.fatalError(new SAXParseException(fe.getClass().getName(), null,fe));
            throw fe;
        }
        finally{
            reset();
        }
    }

    private void processDocumentChildren() throws IOException, FastInfosetException {
        try {
             _contentHandler.startDocument();

             while (!_terminate) {
                read();
                if (current_octet == (FastInfosetConstants.TERMINATION_PATTERN <<4))
                    _terminate = true;
                else if ( (current_octet & 0x80) == 0) { //element
                    boolean terminateOnFourBit = processElement();
                    if (terminateOnFourBit && ((current_octet &0xf) == FastInfosetConstants.TERMINATION_PATTERN))
                            _terminate = true;
                } else if ((current_octet & FastInfosetConstants.DOCUMENT_TYPE_IDENTIFICATION_MASK) == FastInfosetConstants.DOCUMENT_TYPE_IDENTIFICATION) {
                    DocumentType docType = decodeDocumentType();
                    _lexicalHandler.startDTD("dtd", docType.publicIdentifier, docType.systemIdentifier);
                    List<ProcessingInstruction> pis =  docType.instructions;
                    if ( pis != null) {
                        for (ProcessingInstruction pi: pis)
                            _contentHandler.processingInstruction(pi.target, pi.content);
                    }
                    if ( notations != null) {
                        for (Notation notation: notations)
                            _dtdHandler.notationDecl(notation.name,notation.publicIdentifier, notation.systemIdentifier);
                    }
                    if ( unparsedEntities != null) {
                        for (UnparsedEntity ue: unparsedEntities)
                            _dtdHandler.unparsedEntityDecl(ue.name,ue.publicIdentifier,ue.systemIdentifier,ue.notationName);
                    }
                    _lexicalHandler.endDTD();
                    if ( (current_octet &0xF) == FastInfosetConstants.TERMINATION_PATTERN)
                        _terminate = true;
                } else if (current_octet == FastInfosetConstants.PROCESSING_INSTRUCTION_IDENTIFICATION) {
                    ProcessingInstruction pi = decodeProcessingInstruction();
                    _contentHandler.processingInstruction(pi.target, pi.content);
                } else if (current_octet == FastInfosetConstants.COMMENT_IDENTIFICATION) {
                    char[] comment = decodeComment().toCharArray();
                    _lexicalHandler.comment(comment, 0 ,comment.length);
                } else {
                    throw new FastInfosetException("Unknown identifier in document children: "+Integer.toHexString(current_octet));
                }
            }
            _contentHandler.endDocument();
        } catch (SAXException ex) {
            throw new FastInfosetException(ex);
        }
    }

    SAXAttributes _saxAttributesBuffer = new SAXAttributes(null);
    private boolean processElement() throws IOException, FastInfosetException {
        try{
            boolean hasAttributes = (current_octet & FastInfosetConstants.ELEMENT_ATTRIBUTE_PRESENCE) != 0;
            boolean hasNSAttributes = ((current_octet & FastInfosetConstants.ELEMENT_NAMESPACES_PRESENCE_MASK)) == FastInfosetConstants.ELEMENT_NAMESPACES_PRESENCE;
            // process ns attributes
            List<NamespaceAttribute> namespaceAttributes = null;
            if (hasNSAttributes) {
                namespaceAttributes = decodeNSAttributesClone();
            }

            QName qname = decodeQualifiedNameOrIndexOnThirdBit() ;

             List<Attribute> attributes = null;
             if (hasAttributes) {
                attributes = decodeAttributes();
            }
            if ((namespaceAttributes != null) && !namespaceAttributes.isEmpty()) {
                for (NamespaceAttribute nsatt: namespaceAttributes) {
                        _contentHandler.startPrefixMapping(nsatt.getPrefix(), nsatt.getNamespace_name());
                }
            }

            _saxAttributesBuffer.setAttributeList(attributes);
            _contentHandler.startElement(   qname.getNamespaceURI(), qname.getLocalPart(), 
                                            qname.getPrefix().isEmpty()?qname.getLocalPart():qname.getPrefix()+":"+qname.getLocalPart(), _saxAttributesBuffer);

            if (hasAttributes && ((current_octet & 0xf) == FastInfosetConstants.TERMINATION_PATTERN)) {
                processEndElement(qname, namespaceAttributes);
                return false;
            }


            //has children: process
            while ( (read() >>> 4) != FastInfosetConstants.TERMINATION_PATTERN) {
                // process children
                if ((current_octet & 0x80) == 0) {
                    if (processElement() && ((current_octet & 0xf) == FastInfosetConstants.TERMINATION_PATTERN)) {
                        processEndElement(qname, namespaceAttributes);
                        return false;
                    }
                } else if ((current_octet & 0xC0) == FastInfosetConstants.CHARACTER_CHUNK_IDENTIFICATION) { 
                    EncodedString text = decodeCharacterChunk();
                    if (text.type.equals(text.type.Algorithm) && (text.AlgorithmIndex == CDATA.id)) {
                        _lexicalHandler.startCDATA();
                        char[] ca = text.getString().toCharArray();
                        _contentHandler.characters(ca, 0, ca.length);
                        _lexicalHandler.endCDATA();
                    }
                    else {
                        if ((_objectAlgorithmHandler != null) && (text.type.equals(text.type.Algorithm))) {
                            if (text.algorithm == null) {
                                String uri = vocabulary.getAlgorithmURI(text.AlgorithmIndex);
                                if (uri == null)
                                    _objectAlgorithmHandler.object(text.theData, text.AlgorithmIndex);
                                else
                                    _objectAlgorithmHandler.object(text.theData, uri);
                            }
                            else
                                _objectAlgorithmHandler.object(text.algorithm.objectFromByteArray(text.theData), text.algorithm);
                        } else if ((_alphabetHandler != null) && (text.type.equals(text.type.Alphabet))) {
                            _alphabetHandler.alphabet(text.getString(), (Alphabet)text.algorithm);
                        }
                        else {
                            char[] ca = text.getString().toCharArray();
                            _contentHandler.characters(ca, 0, ca.length);
                        }
                    }

                } else if (current_octet == FastInfosetConstants.PROCESSING_INSTRUCTION_IDENTIFICATION) {
                    ProcessingInstruction pi = decodeProcessingInstruction();
                    _contentHandler.processingInstruction(pi.target, pi.content);
                } else if (current_octet == FastInfosetConstants.COMMENT_IDENTIFICATION) {
                     char[] ca = decodeComment().toCharArray();
                      _lexicalHandler.comment(ca, 0, ca.length);
                } else if ((current_octet & 0xFC) == FastInfosetConstants.UNEXPANDED_ENTITY_IDENTIFICATION) {
                    _contentHandler.skippedEntity(decodeUnexpandedEntity());
                }
            }
            processEndElement(qname, namespaceAttributes);
            return true;
        } catch (SAXException ex) {
            throw new FastInfosetException(ex);
        }
    }
    
    void processEndElement(QName qname, List<NamespaceAttribute> namespaceAttributes) throws SAXException {
        _contentHandler.endElement( qname.getNamespaceURI(), qname.getLocalPart(), 
                                            qname.getPrefix().isEmpty()?qname.getLocalPart():qname.getPrefix()+":"+qname.getLocalPart());
        if ((namespaceAttributes != null) && !namespaceAttributes.isEmpty()) {
                for (NamespaceAttribute nsatt: namespaceAttributes) {
                        _contentHandler.endPrefixMapping(nsatt.getPrefix());
                }
        }
    }
}
