//  Author: Ronald Pablos
//  Year: 2013


package fastinfoset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import fastinfoset.Algorithm.Algorithm;
import fastinfoset.Algorithm.Builtin.CDATA;
import fastinfoset.Document.Element.Attribute;
import fastinfoset.Document.Element.NamespaceAttribute;
import fastinfoset.Document.UnparsedEntity;

/**
 *
 * @author rpablos
 */
public class DOM_FI_Encoder extends Encoder {

    public DOM_FI_Encoder() {
        super();
    }

    
    public DOM_FI_Encoder(boolean utf8encoding) {
        super(utf8encoding);
    }
    

    public final void serialize(Document d) throws IOException {
        encodeHeader(d);

        final NodeList nl = d.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            final Node n = nl.item(i);
            switch (n.getNodeType()) {
                case Node.ELEMENT_NODE:
                    serializeElement(n);
                    break;
                case Node.COMMENT_NODE:
                    serializeComment(n);
                    break;
                case Node.PROCESSING_INSTRUCTION_NODE:
                    serializeProcessingInstruction(n);
                    break;
                case Node.DOCUMENT_TYPE_NODE:
                    serializeDocumentType((DocumentType) n);
            }
        }
        encodeDocumentTermination();
        _out.flush();
    }
    private List<Attribute> _attributeBuffer = new ArrayList<Attribute>();
    private List<NamespaceAttribute> _nsattributeBuffer = new ArrayList<NamespaceAttribute>();

    private void serializeElement(Node n) throws IOException {
        alignToOctet();
        List<NamespaceAttribute> namespaceAttributes = null;
        List<Attribute> attributes = null;
        if (n.hasAttributes()) {

//            attributes = Attribute.getAttributeList(n);
//            namespaceAttributes = NamespaceAttribute.ExtractNamespaceAttributeList(attributes);
            _attributeBuffer.clear();
            _nsattributeBuffer.clear();
            Attribute.getAttributeList(n, attributes = _attributeBuffer, namespaceAttributes = _nsattributeBuffer);
        }

        if (attributes != null && !attributes.isEmpty()) {
            current_octet |= FastInfosetConstants.ELEMENT_ATTRIBUTE_PRESENCE;
        }
        if (namespaceAttributes != null && !namespaceAttributes.isEmpty()) {
            current_octet |= FastInfosetConstants.ELEMENT_NAMESPACES_PRESENCE;
            flush_currentoctet();
        }
        encodeNamespaceAttributes(namespaceAttributes);
        String prefix = n.getPrefix();
        if (prefix == null) {
            prefix = XMLConstants.DEFAULT_NS_PREFIX;
        }
        encodeQualifiedNameOrIndexOnThirdBit(new QName(n.getNamespaceURI(), n.getLocalName(), prefix));
        encodeAttributes(attributes);

        if (n.hasChildNodes()) {
            // Serialize the children
            final NodeList nl = n.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                final Node child = nl.item(i);
                switch (child.getNodeType()) {
                    case Node.ELEMENT_NODE:
                        serializeElement(child);
                        break;
                    case Node.TEXT_NODE:
                        serializeText(child);
                        break;
                    case Node.COMMENT_NODE:
                        serializeComment(child);
                        break;
                    case Node.CDATA_SECTION_NODE:
                        serializeCDATA(child);
                        break;
                    case Node.PROCESSING_INSTRUCTION_NODE:
                        serializeProcessingInstruction(child);
                        break;
                    case Node.ENTITY_REFERENCE_NODE:
                        serializeEntityReference(child);
                        break;
                }
            }
        }
        encodeElementTermination();
    }

    private void serializeText(Node n) throws IOException {
        serializeCharacterChunk(n, null);
    }

    private void serializeCDATA(Node n) throws IOException {
        serializeCharacterChunk(n, CDATA.instance);
    }

    private void serializeCharacterChunk(Node n, Algorithm algo) throws IOException {

        encodeCharacterChunk(n.getNodeValue(), algo);
    }

    private void serializeComment(Node n) throws IOException {
        
        final String text = n.getNodeValue();
        encodeComment(text);
    }

    private void serializeProcessingInstruction(Node pi) throws IOException {
        
        String target = pi.getNodeName();
        String data = pi.getNodeValue();
        encodeProcessingInstruction(target, data);
    }
    private void encodeHeader(Document d) throws IOException {
        List<UnparsedEntity> unParsedEntities = null;
        List<fastinfoset.Document.Notation> notations  = null;
        DocumentType docType = d.getDoctype();
        if (docType != null) {
            //Notations
            notations = getNotations(docType.getNotations());
            //Unparsed Entities
            unParsedEntities = getUnparsedEntities(docType.getEntities());
        }
        encodeHeader(d.getXmlEncoding(), d.getXmlStandalone(), d.getXmlVersion(), unParsedEntities, notations);
    }
    

    private void serializeDocumentType(DocumentType n) throws IOException {
        encodeDocumentType(new fastinfoset.Document.DocumentType(n.getSystemId(), n.getPublicId(),null));
    }
    

    public void serializeUnparsedEntities(List<UnparsedEntity> list) throws IOException {
            encodeUnparsedEntities(list);
    }

    private List<UnparsedEntity> getUnparsedEntities(NamedNodeMap entities) {
        List<UnparsedEntity> result = new ArrayList<UnparsedEntity>();
        for (int i = 0; i < entities.getLength(); i++) {
            Entity n = (Entity) entities.item(i);
            String systemid = n.getSystemId();
            String notationname = n.getNotationName();
            if (systemid != null && notationname != null) {
                result.add(new UnparsedEntity(n.getNodeName(), systemid, n.getPublicId(), notationname));
            }
        }
        return result;
    }
    
    private List<fastinfoset.Document.Notation> getNotations(NamedNodeMap notations) {
        List<fastinfoset.Document.Notation> result = new ArrayList<fastinfoset.Document.Notation>();
        for (int i = 0; i < notations.getLength(); i++) {
            Notation n = (Notation) notations.item(i);
            result.add(new fastinfoset.Document.Notation(n.getNodeName(), n.getSystemId(), n.getPublicId()));
        }
        return result;
    }

    private void serializeEntityReference(Node child) throws IOException {
        encodeEntityReference(child.getNodeName());
    }
}
