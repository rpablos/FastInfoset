//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset;


import fastinfoset.Algorithm.Algorithm;
import fastinfoset.Algorithm.Builtin.CDATA;
import fastinfoset.Alphabet.Alphabet;
import fastinfoset.Document.Additional_datum;
import fastinfoset.Document.DocumentType;
import fastinfoset.Document.Element.Attribute;
import fastinfoset.Document.Element.NamespaceAttribute;
import fastinfoset.Document.Name_surrogate;
import fastinfoset.Document.Notation;
import fastinfoset.Document.ProcessingInstruction;
import fastinfoset.Document.UnparsedEntity;
import fastinfoset.util.ArrayIndex;
import fastinfoset.util.DecoderVocabulary;
import fastinfoset.util.EncodedString;
import fastinfoset.util.HashMapObjectInt;
import fastinfoset.util.IndexMap;
import fastinfoset.util.InitialVocabulary;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author rpablos
 */
public class DOM_FI_Decoder extends Decoder {
    Document document;
    
    //store doctype, comments and PIs until document creation if we have to create the document
    private List<String> _comments = new ArrayList<String>();
    private List<ProcessingInstruction> _pis = new ArrayList<ProcessingInstruction>();
    private DocumentType docType;
    Document createDocument(DocumentType docType, QName rootElement) throws FastInfosetException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DOMImplementation domImplementation;
        try {
            domImplementation = dbf.newDocumentBuilder().getDOMImplementation();
        } catch (ParserConfigurationException ex) {
            throw new FastInfosetException(ex);
        }
        org.w3c.dom.DocumentType createDocumentType = null;
        if (docType != null)
            createDocumentType = domImplementation.createDocumentType(rootElement.getLocalPart(), docType.publicIdentifier,docType.systemIdentifier);
        Document doc = domImplementation.createDocument(rootElement.getNamespaceURI(), 
                rootElement.getPrefix().isEmpty()?rootElement.getLocalPart():
                                    rootElement.getPrefix()+":"+rootElement.getLocalPart(), 
                createDocumentType);
        for (ProcessingInstruction pi: _pis) {
            doc.insertBefore(doc.createProcessingInstruction(pi.target, pi.content), doc.getDocumentElement());
        }
        for (String str: _comments) {
            doc.insertBefore(doc.createComment(str), doc.getDocumentElement());
        }
        setVersionAndStandAlone(doc);
        return doc;
    }
    private void setVersionAndStandAlone(Document document) {
        if (XMLVersion != null)
            document.setXmlVersion(XMLVersion);
        if (IsStandalone != null)
            document.setXmlStandalone(IsStandalone);
    }
    public Document parse(InputStream in) throws IOException, FastInfosetException {
        parse(null,in);
        return document;
    }
    public void parse(Document document, InputStream in) throws IOException, FastInfosetException {
        _in = in;
        this.document = document;
        decodeHeader();
        if (document != null)
            setVersionAndStandAlone(this.document);
        //decode children
        while (!_terminate) {
            read();
            if (current_octet == (FastInfosetConstants.TERMINATION_PATTERN <<4))
                _terminate = true;
            else if ( (current_octet & 0x80) == 0) { //element
                boolean terminateOnFourBit = processElement(this.document);
                if (terminateOnFourBit && ((current_octet &0xf) == FastInfosetConstants.TERMINATION_PATTERN))
                        _terminate = true;
            } else if ((current_octet & FastInfosetConstants.DOCUMENT_TYPE_IDENTIFICATION_MASK) == FastInfosetConstants.DOCUMENT_TYPE_IDENTIFICATION) {
                docType = decodeDocumentType();
                if ( (current_octet &0xF) == FastInfosetConstants.TERMINATION_PATTERN)
                    _terminate = true;
            } else if (current_octet == FastInfosetConstants.PROCESSING_INSTRUCTION_IDENTIFICATION) {
                ProcessingInstruction pi = decodeProcessingInstruction();
                if (this.document != null)
                    this.document.appendChild(this.document.createProcessingInstruction(pi.target,pi.content));
                else
                    _pis.add(pi);
            } else if (current_octet == FastInfosetConstants.COMMENT_IDENTIFICATION) {
                String comment = decodeComment();
                if (this.document != null) 
                    this.document.appendChild(this.document.createComment(comment));
                else 
                    _comments.add(comment);
            } else {
                throw new FastInfosetException("Unknown identifier in document children: "+Integer.toHexString(current_octet));
            }
        }
        
    }

    //StringInfoHolder stringInfo = new StringInfoHolder();
    
    private boolean processElement(Node node) throws IOException, FastInfosetException {
        boolean hasAttributes = (current_octet & FastInfosetConstants.ELEMENT_ATTRIBUTE_PRESENCE) != 0;
        boolean hasNSAttributes = ((current_octet & FastInfosetConstants.ELEMENT_NAMESPACES_PRESENCE_MASK)) == FastInfosetConstants.ELEMENT_NAMESPACES_PRESENCE;
        // process ns attributes
        List<NamespaceAttribute> namespaceAttributes = null;
        if (hasNSAttributes) {
            namespaceAttributes = decodeNSAttributes();
        }

        QName qname = decodeQualifiedNameOrIndexOnThirdBit() ;
        Element elementNS;
        if (document == null) { //first element
            document = createDocument(docType, qname);
            elementNS = document.getDocumentElement();
        } else {
            elementNS = qname.getPrefix().isEmpty()?
                document.createElementNS(qname.getNamespaceURI(),qname.getLocalPart()):
                document.createElementNS(qname.getNamespaceURI(),qname.getPrefix()+":"+qname.getLocalPart());
            node.appendChild(elementNS);
        }
        
         List<Attribute> attributes = null;
         if (hasAttributes) {
            attributes = decodeAttributes();
        }
        if ((namespaceAttributes != null) && !namespaceAttributes.isEmpty()) {
            for (NamespaceAttribute nsatt: namespaceAttributes) {
                if (!nsatt.getPrefix().isEmpty())
                    elementNS.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:"+nsatt.getPrefix(), nsatt.getNamespace_name());
            }
        }
        if ((attributes != null) && !attributes.isEmpty()) {
            for (Attribute att: attributes) {
                elementNS.setAttributeNS(att.getQualified_name().getNamespaceURI(),
                        att.getQualified_name().getPrefix().isEmpty()?
                            att.getQualified_name().getLocalPart():
                            att.getQualified_name().getPrefix()+":"+att.getQualified_name().getLocalPart(),
                        att.getNormalized_value());
            }
        }
        if (hasAttributes && ((current_octet & 0xf) == FastInfosetConstants.TERMINATION_PATTERN))
            return false;
        
        
        //has children: process
        while ( (read() >>> 4) != FastInfosetConstants.TERMINATION_PATTERN) {
            // process children
            if ((current_octet & 0x80) == 0) {
                if (processElement(elementNS) && ((current_octet & 0xf) == FastInfosetConstants.TERMINATION_PATTERN))
                    return false;
            } else if ((current_octet & 0xC0) == FastInfosetConstants.CHARACTER_CHUNK_IDENTIFICATION) { 
                EncodedString text = decodeCharacterChunk();
                if (text.type.equals(text.type.Algorithm) && (text.AlgorithmIndex == CDATA.id))
                    elementNS.appendChild(this.document.createCDATASection(text.getString()));
                else
                    elementNS.appendChild(this.document.createTextNode(text.getString()));
                
            } else if (current_octet == FastInfosetConstants.PROCESSING_INSTRUCTION_IDENTIFICATION) {
                ProcessingInstruction pi = decodeProcessingInstruction();
                elementNS.appendChild(this.document.createProcessingInstruction(pi.target,pi.content));
            } else if (current_octet == FastInfosetConstants.COMMENT_IDENTIFICATION) {
                elementNS.appendChild(this.document.createComment(decodeComment()));
            } else if ((current_octet & 0xFC) == FastInfosetConstants.UNEXPANDED_ENTITY_IDENTIFICATION) {
                elementNS.appendChild(this.document.createEntityReference(decodeUnexpandedEntity()));
            }
        }
        
        return true;
    }
    
        
    
    public List<Notation> getNotations() {
        return notations;
    }
    public List<UnparsedEntity> getUnparsedEntities() {
        return unparsedEntities;
    }
    public void reset() {
        super.reset();
    }

    
}
