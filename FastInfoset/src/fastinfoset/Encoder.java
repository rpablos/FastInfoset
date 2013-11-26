//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset;

import java.io.ByteArrayOutputStream;
import fastinfoset.Algorithm.Algorithm;
import fastinfoset.Algorithm.EncodingAlgorithmException;
import fastinfoset.Alphabet.Alphabet;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.xml.namespace.QName;
import javax.xml.stream.events.ProcessingInstruction;
import fastinfoset.Document.Additional_datum;
import fastinfoset.Document.Element.Attribute;
import fastinfoset.Document.Element.NamespaceAttribute;
import fastinfoset.Document.DocumentType;
import fastinfoset.Document.Element.AlgorithmAttribute;
import fastinfoset.util.IndexMap;
import fastinfoset.util.InitialVocabulary;
import fastinfoset.Document.Name_surrogate;
import fastinfoset.Document.Notation;
import fastinfoset.Document.UnparsedEntity;
import fastinfoset.util.HashMapObjectInt;
import fastinfoset.util.Vocabulary;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rpablos
 */
public class Encoder {

    boolean utf8encoding = true;

    Vocabulary vocabulary = new Vocabulary();
    Map<String,UnparsedEntity> UnParsedEntites = new HashMap<String, UnparsedEntity>();
    OutputStream _out;
    int current_octet = 0;
    List<Additional_datum> additional_data = null;

    
    public Encoder () {
        this(true);
    }
    public Encoder (boolean utf8encoding) {
        this.utf8encoding = utf8encoding;
    }
    public void setMaximumChunkLengthForIndexing(int length) {
        vocabulary.MAXIMUM_CHUNK_LENGTH = length;
    }
    public void setInitialVocabulary(InitialVocabulary initialVocabulary) {
        vocabulary = new Vocabulary(initialVocabulary);
    }
    public InitialVocabulary getDynamicGeneratedVocabularyAsInitial() {
        return vocabulary.toInitialVocabulary();
    }
    public InitialVocabulary getAllVocabularyAsInitial() {
        return vocabulary.toInitialVocabularyIncludingInitialVocabulary();
    }
    public void reset() {
        clear_currentoctet();
        vocabulary.reset();
    }
    
    //c.18
    protected void encodeQualifiedNameOrIndexOnThirdBit(QName qualified_name) throws IOException {
        int prefix_index = vocabulary.prefix.get(qualified_name.getPrefix());
        int namespace_index = vocabulary.namespace.get(qualified_name.getNamespaceURI());
        int localname_index = vocabulary.localname.get(qualified_name.getLocalPart());
        Name_surrogate name_surrogate = new Name_surrogate(prefix_index, namespace_index, localname_index);
        int name_surrogate_index = vocabulary.elementname.get(name_surrogate);
        if (name_surrogate_index != HashMapObjectInt.NOT_FOUND) {
            encodeIndexOnThirdbit(name_surrogate_index);
           
        } else
            encodeLiteralQualifiedNameOnThirdBit(qualified_name, name_surrogate);
    }

    //c.27
    protected void encodeIndexOnThirdbit(int name_surrogate_index) throws IOException {
        if (name_surrogate_index < 32) {
            
            current_octet |= name_surrogate_index;
            flush_currentoctet();
        } else if (name_surrogate_index < 2080) {
            name_surrogate_index -= 32;
            current_octet |= FastInfosetConstants.INTEGER_3RD_BIT_2_OCTETS_FLAG | (name_surrogate_index >> 8);
            flush_currentoctet();
            _out.write(name_surrogate_index & 255);
        } else if (name_surrogate_index < 526368) {
            
            name_surrogate_index -= 2080;
            current_octet |= FastInfosetConstants.INTEGER_3RD_BIT_3_OCTETS_FLAG | (name_surrogate_index >> 16);
            flush_currentoctet();
            _out.write(255 & (name_surrogate_index >> 8));
            _out.write(255 & name_surrogate_index);
        } else {
            name_surrogate_index -= 526368;
            current_octet |= FastInfosetConstants.INTEGER_3RD_BIT_4_OCTETS_FLAG;
            flush_currentoctet();
            _out.write(15 & (name_surrogate_index >> 16));
            _out.write(255 & (name_surrogate_index >> 8));
            _out.write(255 & name_surrogate_index);
        }
    }

    protected void clear_currentoctet() {
        current_octet = 0;
    }

    public void setOutputStream(OutputStream out) {
        _out = out;
    }

    protected void flush_currentoctet() throws IOException {
        _out.write(current_octet);
        current_octet = 0;
    }
    protected void alignToOctet() throws IOException {
        if (current_octet != 0) //c.2.11.1 y c.3.7.1
                flush_currentoctet();
    }

    //c.18
    protected void encodeLiteralQualifiedNameOnThirdBit(QName qualified_name, Name_surrogate name_surrogate) throws IOException {
        current_octet |= FastInfosetConstants.LITERAL_QUALIFIED_NAME_IDENTIFICATION;
        encodeLiteralQualifiedName(qualified_name, name_surrogate,vocabulary.elementname);
    }
    //c.13
    protected int encodeIdentifyingStringOrIndex(String str, int index, IndexMap<String> map) throws IOException {
        if (index != HashMapObjectInt.NO_INDEX) {
            current_octet |= FastInfosetConstants.IDENTIFYINGSTRINGORINDEX_INDEX;
            encodeIndexOnSecondbit(index);
        } else {
            //encodeNonEmptyOctetStringOnSecondBit(str.getBytes(utf8CharSet));
            int len = encodeUTFinInternalEncodingBuffer(str);
            encodeNonEmptyOctetStringOnSecondBit(_encodingBuffer, 0, len);
//            encodeUTFinInternalEncodingBuffer(str, utfEncoder);
//            encodeNonEmptyOctetStringOnSecondBit(internalEncodingBuffer.getInternalByteArray(), 0, internalEncodingBuffer.size());
            index = map.addNewIndexEntry(str);
        }
        return index;
    }
    protected int encodeIdentifyingStringOrIndex(String str,  IndexMap<String> map) throws IOException {
        return encodeIdentifyingStringOrIndex(str, map.get(str),map);
    }
    //c.25
    protected void encodeIndexOnSecondbit(int index) throws IOException {
        if (index < 64) {
            current_octet |= index;
            flush_currentoctet();
        } else if (index < 8256) {
            index -= 64;
            current_octet |= FastInfosetConstants.INTEGER_2ND_BIT_2_OCTETS_FLAG | (index >> 8);
            flush_currentoctet();
            _out.write(index & 255);
        } else {
            index -= 8256;
            current_octet |= FastInfosetConstants.INTEGER_2ND_BIT_3_OCTETS_FLAG;
            current_octet |= (15 & (index >> 16));
            flush_currentoctet();
            _out.write(255 & (index >> 8));
            _out.write(255 & index);
        }
    }

    //c.22
    protected void encodeNonEmptyOctetStringOnSecondBit(byte[] octet_string) throws IOException {
        encodeNonEmptyOctetString(octet_string, 2);
    }
    protected void encodeNonEmptyOctetStringOnSecondBit(byte[] octet_string,int offset, int length) throws IOException {
        encodeNonEmptyOctetString(octet_string,offset, length, 2);
    }
    //c.12
    protected void encodeNamespaceAttributes(List<NamespaceAttribute> namespaceAttributes) throws IOException {
        if (namespaceAttributes ==  null) return;
        for (NamespaceAttribute att : namespaceAttributes) {
            current_octet |= FastInfosetConstants.NAMESPACE_ATTRIBUTE_IDENTIFICATION;
            
            String prefix = att.getPrefix();
            String namespace = att.getNamespace_name();
            if (!prefix.isEmpty()) {
                current_octet |= FastInfosetConstants.NAMESPACE_ATTRIBUTE_PREFIX_FLAG;
            }
            if (!namespace.isEmpty()) {
                current_octet |= FastInfosetConstants.NAMESPACE_ATTRIBUTE_NAMESPACE_FLAG;
            }
            flush_currentoctet();
            if (!prefix.isEmpty()) {
                encodeIdentifyingStringOrIndex(prefix,  vocabulary.prefix);
            }
            if (!namespace.isEmpty()) {
                encodeIdentifyingStringOrIndex(namespace, vocabulary.namespace);
            }
        }
        if (!namespaceAttributes.isEmpty()) {
            current_octet |= FastInfosetConstants.TERMINATION_PATTERN << 4;
            flush_currentoctet();
        }
    }
    //c.17
    protected void encodeQualifiedNameOrIndexOnSecondBit(QName qualified_name) throws IOException {
        int prefix_index = vocabulary.prefix.get(qualified_name.getPrefix());
        int namespace_index = vocabulary.namespace.get(qualified_name.getNamespaceURI());
        int localname_index = vocabulary.localname.get(qualified_name.getLocalPart());
        Name_surrogate name_surrogate = new Name_surrogate(prefix_index, namespace_index, localname_index);
        int name_surrogate_index = vocabulary.attributename.get(name_surrogate);
        if (name_surrogate_index != HashMapObjectInt.NOT_FOUND) {
            encodeIndexOnSecondbit(name_surrogate_index);
           
        } else
            encodeLiteralQualifiedNameOnSecondBit(qualified_name, name_surrogate);
    }
    //c.17
    protected void encodeLiteralQualifiedNameOnSecondBit(QName qualified_name, Name_surrogate name_surrogate) throws IOException {
        current_octet |= FastInfosetConstants.LITERAL_QUALIFIED_NAME_IDENTIFICATION << 1;
        encodeLiteralQualifiedName(qualified_name, name_surrogate,vocabulary.attributename);
    }
    //c.18 y c.17
    protected void encodeLiteralQualifiedName(QName qualified_name, Name_surrogate name_surrogate, IndexMap<Name_surrogate> map) throws IOException {
        boolean encodeNamespace = (name_surrogate.namespace != HashMapObjectInt.NO_INDEX) || (!qualified_name.getNamespaceURI().isEmpty());
        boolean encodePrefix = (name_surrogate.prefix != HashMapObjectInt.NO_INDEX) || (!qualified_name.getPrefix().isEmpty());
        if (encodeNamespace) {
            current_octet |= FastInfosetConstants.LITERAL_QNAME_NAMESPACE_NAME_FLAG;
            if (encodePrefix) {
                current_octet |= FastInfosetConstants.LITERAL_QNAME_PREFIX_FLAG;
            }
        }
        flush_currentoctet();
        
        boolean createSurrogate = true;
        if (encodeNamespace) {
            if (encodePrefix) {
                createSurrogate = 
                        ((name_surrogate.prefix = encodeIdentifyingStringOrIndex(qualified_name.getPrefix(), name_surrogate.prefix, vocabulary.prefix)) != HashMapObjectInt.NO_INDEX);
            }
            createSurrogate = 
                        ((name_surrogate.namespace = 
                            encodeIdentifyingStringOrIndex(qualified_name.getNamespaceURI(),
                            name_surrogate.namespace, vocabulary.namespace)) != HashMapObjectInt.NO_INDEX) && createSurrogate;
        }
        createSurrogate = 
                        ((name_surrogate.localname = 
                            encodeIdentifyingStringOrIndex(qualified_name.getLocalPart(),
                            name_surrogate.localname, vocabulary.localname)) != HashMapObjectInt.NO_INDEX) && createSurrogate;
        if (createSurrogate) {
            map.addNewIndexEntry(name_surrogate);
        }
    }

    //c.19
    protected void encodeEncodedCharacterStringOnThirdbit(String str) throws IOException {
        encodeEncodedCharacterStringOnThirdbit(str, null);
    }
    protected void encodeEncodedCharacterStringOnThirdbit(String str, Algorithm algo) throws IOException {
        if (algo == null) { 
            if (!utf8encoding)
                current_octet |= FastInfosetConstants.ENCODED_CHARACTER_STRING_3RD_BIT_UTF16_DISCRIMINANT;
            int len = encodeUTFinInternalEncodingBuffer(str);
            encodeNonEmptyOctetStringOnFifthBit(_encodingBuffer, 0, len);
        } else {
            int algo_index = HashMapObjectInt.NOT_FOUND;
            if (algo instanceof Alphabet) {
                current_octet |= FastInfosetConstants.ENCODED_CHARACTER_STRING_3RD_BIT_ALPHABET_DISCRIMINANT;
                algo_index = vocabulary.alphabets.get(algo);
            } else {
                current_octet |= FastInfosetConstants.ENCODED_CHARACTER_STRING_3RD_BIT_ALGORITHM_DISCRIMINANT;
                algo_index = vocabulary.algorithms.get(algo);
            }
            
            if (algo_index == HashMapObjectInt.NOT_FOUND)
                throw new IOException("algorithm not found");
            encodeOctetInteger(algo_index, 5);
            try {
                encodeNonEmptyOctetStringOnFifthBit(algo.toByteArray(str));
            } catch (EncodingAlgorithmException ex) {
                throw new IOException(ex);
            }
        }

    }

    //c.4
    protected void encodeAttributes(List<Attribute> attributes) throws IOException {
        if(attributes == null)
            return;
        for (Attribute att : attributes) {
            encodeQualifiedNameOrIndexOnSecondBit(att.getQualified_name());
            Algorithm algo = null;
            if (att instanceof AlgorithmAttribute)
                algo = ((AlgorithmAttribute)att).getAlgorithm();
            encodeNonIdentifyingStringOrIndexOnFirstBit(att.getNormalized_value(), vocabulary.attribute_values,algo);
          
        }
        if (!attributes.isEmpty()) {
            current_octet |= FastInfosetConstants.TERMINATION_PATTERN << 4;
        }
    }

    //c.23
    protected void encodeNonEmptyOctetStringOnFifthBit(byte[] octet_string) throws IOException {
        encodeNonEmptyOctetString(octet_string, 5);
    }
    protected void encodeNonEmptyOctetStringOnFifthBit(byte[] octet_string,int offset, int length) throws IOException {
        encodeNonEmptyOctetString(octet_string,offset,length, 5);
    }

    //c.14
    protected void encodeNonIdentifyingStringOrIndexOnFirstBit(String str, IndexMap<String> map) throws IOException {
        encodeNonIdentifyingStringOrIndexOnFirstBit(str, map, null);
    }
    protected void encodeNonIdentifyingStringOrIndexOnFirstBit(String str, IndexMap<String> map, Algorithm algo) throws IOException {
        if (str.isEmpty()) {
            _out.write(0xFF);
            return;
        }
        int index = map.get(str);
        if (index != HashMapObjectInt.NOT_FOUND) {
            current_octet |= FastInfosetConstants.IDENTIFYINGSTRINGORINDEX_INDEX;
            encodeIndexOnSecondbit(index);
        } else {
            index = map.addNewIndexEntry(str);
            if (index != HashMapObjectInt.NO_INDEX) {
                //add-to-table equals true
                current_octet |= FastInfosetConstants.NONIDENTIFYINGSTRING_ADDTOTABLE;
            }
            encodeEncodedCharacterStringOnThirdbit(str,algo);
        }
    }
    //c.7
    protected void encodeCharacterChunk(String text) throws IOException {
        encodeCharacterChunk(text, null);
    }
    protected void encodeCharacterChunk(String text, Algorithm algo) throws IOException {
        alignToOctet();
        current_octet |= FastInfosetConstants.CHARACTER_CHUNK_IDENTIFICATION;
        encodeNonIdentifyingStringOrIndexOnThirdBit(text, vocabulary.character_chunks,algo);
    }
    protected void encodeObjectCharacterChunk(Object object, Algorithm algo) throws IOException {
        alignToOctet();
        current_octet |= FastInfosetConstants.CHARACTER_CHUNK_IDENTIFICATION;
        encodeObjectNonIdentifyingStringOrIndexOnThirdBit(object,algo);
    }
    protected void encodeObjectCharacterChunk(byte[] data, int algoId) throws IOException {
        alignToOctet();
        current_octet |= FastInfosetConstants.CHARACTER_CHUNK_IDENTIFICATION;
        encodeObjectNonIdentifyingStringOrIndexOnThirdBit(data,algoId);
    }
    //c.20
    protected void encodeEncodedCharacterStringOnFifthdbit(String str, Algorithm algo) throws IOException {
        if (algo == null){
            if (utf8encoding) {
            } else {
                current_octet |= FastInfosetConstants.ENCODED_CHARACTER_STRING_5TH_BIT_UTF16_DISCRIMINANT;
            }
            int len = encodeUTFinInternalEncodingBuffer(str);
            encodeNonEmptyOctetStringOnSeventhBit(_encodingBuffer, 0, len);
        } else {
            int algo_index = HashMapObjectInt.NOT_FOUND;
            if (algo instanceof Alphabet) {
                current_octet |= FastInfosetConstants.ENCODED_CHARACTER_STRING_5TH_BIT_ALPHABET_DISCRIMINANT;
                algo_index = vocabulary.alphabets.get(algo);
            } else {
                current_octet |= FastInfosetConstants.ENCODED_CHARACTER_STRING_5TH_BIT_ALGORITHM_DISCRIMINANT;
                algo_index = vocabulary.algorithms.get(algo);
            }
            
            if (algo_index == HashMapObjectInt.NOT_FOUND)
                throw new IOException("algorithm not found");
            encodeOctetInteger(algo_index, 7);
            try {
                encodeNonEmptyOctetStringOnSeventhBit(algo.toByteArray(str));
            } catch (EncodingAlgorithmException ex) {
                throw new IOException(ex);
            }
        }

    }
    protected void encodeObjectEncodedCharacterStringOnFifthdbit(Object object, Algorithm algo) throws IOException {
        if (algo == null){
            throw new IOException("No encoding algorithm");
        } else {
            int algo_index = HashMapObjectInt.NOT_FOUND;
            if (algo instanceof Alphabet) {
                current_octet |= FastInfosetConstants.ENCODED_CHARACTER_STRING_5TH_BIT_ALPHABET_DISCRIMINANT;
                algo_index = vocabulary.alphabets.get(algo);
            } else {
                current_octet |= FastInfosetConstants.ENCODED_CHARACTER_STRING_5TH_BIT_ALGORITHM_DISCRIMINANT;
                algo_index = vocabulary.algorithms.get(algo);
            }
            
            if (algo_index == HashMapObjectInt.NOT_FOUND)
                throw new IOException("algorithm not found");
            encodeOctetInteger(algo_index, 7);
            try {
                encodeNonEmptyOctetStringOnSeventhBit(algo.toByteArray(object));
            } catch (EncodingAlgorithmException ex) {
                throw new IOException(ex);
            }
        }

    }
    
    protected void encodeObjectEncodedCharacterStringOnFifthdbit(byte[] data, int algoId) throws IOException {
        if (algoId < 0){
            throw new IOException("Illegal encoding algorithm index");
        } else {
            encodeOctetInteger(algoId, 7);
            encodeNonEmptyOctetStringOnSeventhBit(data);
        }
    }
    
    protected void encodeEncodedCharacterStringOnFifthdbit(String str) throws IOException {
        encodeEncodedCharacterStringOnFifthdbit(str, (Algorithm)null);
    }

    //c.24
    protected void encodeNonEmptyOctetStringOnSeventhBit(byte[] octet_string) throws IOException {
        encodeNonEmptyOctetString(octet_string, 7);
    }
    protected void encodeNonEmptyOctetStringOnSeventhBit(byte[] octet_string, int offset, int length) throws IOException {
        encodeNonEmptyOctetString(octet_string,offset,length, 7);
    }

    //c.15
    protected void encodeNonIdentifyingStringOrIndexOnThirdBit(String str, IndexMap<String> map) throws IOException {
        encodeNonIdentifyingStringOrIndexOnThirdBit(str, map, null);
    }
//    protected void encodeNonIdentifyingStringOrIndexOnThirdBit(String str, IndexMap<String> map, Algorithm algo) throws IOException {
//        encodeNonIdentifyingStringOrIndexOnThirdBit(str, map, algo, false);
//    }

    protected void encodeNonIdentifyingStringOrIndexOnThirdBit(String str, IndexMap<String> map, Algorithm algo) throws IOException {
        
        if (str.isEmpty()) {
            return;
        }
        int index = map.get(str,algo);
        if (index != HashMapObjectInt.NOT_FOUND) {
            current_octet |= FastInfosetConstants.IDENTIFYINGSTRINGORINDEX_3RD_BIT_INDEX;
            encodeIndexOnFourthbit(index);
        } else {
            index = map.addNewIndexEntry(str,algo);
            if (index != HashMapObjectInt.NO_INDEX) {
                //add-to-table equals true
                current_octet |= FastInfosetConstants.NONIDENTIFYINGSTRING_3RD_BIT_ADDTOTABLE;
            }
            encodeEncodedCharacterStringOnFifthdbit(str,algo);
        }
    }
    protected void encodeObjectNonIdentifyingStringOrIndexOnThirdBit(Object object, Algorithm algo) throws IOException {
        //literal encoding
        //add-to-table equals false for object algorithms
        encodeObjectEncodedCharacterStringOnFifthdbit(object,algo);
        
    }
    protected void encodeObjectNonIdentifyingStringOrIndexOnThirdBit(byte[] data, int algoId) throws IOException {
        //literal encoding
        //add-to-table equals false for object algorithms
        encodeObjectEncodedCharacterStringOnFifthdbit(data,algoId);
        
    }
    
    //c.28
    protected void encodeIndexOnFourthbit(int index) throws IOException {
        if (index < 16) {
            current_octet |= index;
            flush_currentoctet();
        } else if (index < 1040) {
            index -= 16;
            current_octet |= FastInfosetConstants.INTEGER_4TH_BIT_2_OCTETS_FLAG | (index >> 8);
            flush_currentoctet();
            _out.write(index & 255);
        } else if (index < 263184) {
            index -= 1040;
            current_octet |= FastInfosetConstants.INTEGER_4TH_BIT_3_OCTETS_FLAG | (index >> 16);
            flush_currentoctet();
            _out.write(255 & (index >> 8));
            _out.write(index & 255);
        } else {
            index -= 263184;
            current_octet |= FastInfosetConstants.INTEGER_4TH_BIT_4_OCTETS_FLAG;
            flush_currentoctet();
            _out.write(255 & (index >> 16));
            _out.write(255 & (index >> 8));
            _out.write(255 & index);
        }
    }
    
    protected void encodeNonEmptyOctetString(byte[] octet_string,int startingBit) throws IOException {
        encodeNonEmptyOctetString(octet_string, 0, octet_string.length, startingBit);
    }
    protected void encodeNonEmptyOctetString(byte[] octet_string,int offset, int length,int startingBit) throws IOException {
        // encode length
        int firstLimit =  1 + (1 << (8-startingBit));
        if (length < firstLimit) {
            current_octet |= (length - 1);
            flush_currentoctet();
        } else if (length < (firstLimit+256)) {
            current_octet |= FastInfosetConstants.OCTET_STRING_LENGTH_2_OCTETS_FLAG >>> (startingBit-1);
            flush_currentoctet();
            _out.write(length - firstLimit);
        } else {
            current_octet |= FastInfosetConstants.OCTET_STRING_LENGTH_3_OCTETS_FLAG >>> (startingBit-1);
            flush_currentoctet();
            int len = length - (firstLimit+256);
            _out.write(len >>> 24);
            _out.write((len >> 16) & 255);
            _out.write((len>> 8) & 255);
            _out.write(len & 255);
        }
        //encode octet_string
        _out.write(octet_string,offset,length);
    }
    
    protected void encodeOctetInteger(int octet, int startingBit) throws IOException {
        octet &= 0xFF;
        current_octet |= octet >> (startingBit-1);
        flush_currentoctet();
        current_octet |= octet << 8-(startingBit-1);
    }

    //c.8
    protected void encodeComment(String text) throws IOException {
        alignToOctet();
        current_octet |= FastInfosetConstants.COMMENT_IDENTIFICATION;
        flush_currentoctet();
        encodeNonIdentifyingStringOrIndexOnFirstBit(text, vocabulary.other_strings);
    }
    //c.5
    protected void encodeProcessingInstruction(String target, String data) throws IOException {
        alignToOctet();
        current_octet |= FastInfosetConstants.PROCESSING_INSTRUCTION_IDENTIFICATION;
        flush_currentoctet();
        encodeIdentifyingStringOrIndex(target, vocabulary.other_ncnames);
        encodeNonIdentifyingStringOrIndexOnFirstBit(data, vocabulary.other_strings);
    }
    
    //c.9
    protected void encodeDocumentType(DocumentType documentType) throws IOException {
        alignToOctet();
        current_octet |= FastInfosetConstants.DOCUMENT_TYPE_IDENTIFICATION;
        boolean encodesystemid = (documentType.systemIdentifier != null) && !documentType.systemIdentifier.isEmpty();
        boolean encodepublicid = (documentType.publicIdentifier != null) && !documentType.publicIdentifier.isEmpty();
        if (encodesystemid) {
            current_octet |= FastInfosetConstants.DOCUMENT_TYPE_SYSTEMID_PRESENCE;
        }
        if (encodepublicid) {
            current_octet |= FastInfosetConstants.DOCUMENT_TYPE_PUBLICID_PRESENCE;
        }
        flush_currentoctet();
        if (encodesystemid) {
            encodeIdentifyingStringOrIndex(documentType.systemIdentifier, vocabulary.other_uris);
        }
        if (encodepublicid) {
            encodeIdentifyingStringOrIndex(documentType.publicIdentifier, vocabulary.other_uris);
        }
        if (documentType.instructions != null) {
            for (fastinfoset.Document.ProcessingInstruction instruction: documentType.instructions) {
                encodeProcessingInstruction(instruction.target, instruction.content);
            }
        }
        current_octet |= FastInfosetConstants.TERMINATION_PATTERN << 4;
    }
    

    //c.11
    protected void encodeNotation(String nodeName, String systemid, String publicid) throws IOException {
        current_octet |= FastInfosetConstants.NOTATION_IDENTIFICATION;
        boolean encodesystemid = (systemid != null) && !systemid.isEmpty();
        boolean encodepublicid = (publicid != null) && !publicid.isEmpty();
        if (encodesystemid) {
            current_octet |= FastInfosetConstants.DOCUMENT_TYPE_SYSTEMID_PRESENCE;
        }
        if (encodepublicid) {
            current_octet |= FastInfosetConstants.DOCUMENT_TYPE_PUBLICID_PRESENCE;
        }
        flush_currentoctet();
        encodeIdentifyingStringOrIndex(nodeName, vocabulary.other_ncnames);
        if (encodesystemid) {
            encodeIdentifyingStringOrIndex(systemid, vocabulary.other_uris);
        }
        if (encodepublicid) {
            encodeIdentifyingStringOrIndex(publicid, vocabulary.other_uris);
        }
    }
    //c.10
    protected void encodeUnparsedEntity(UnparsedEntity entity) throws IOException {
        current_octet |= FastInfosetConstants.UNPARSED_ENTITY_IDENTIFICATION;
        boolean encodepublicid = (entity.publicIdentifier != null) && !entity.publicIdentifier.isEmpty();
        if (encodepublicid) {
            current_octet |= FastInfosetConstants.DOCUMENT_TYPE_PUBLICID_PRESENCE;
        }
        flush_currentoctet();
        encodeIdentifyingStringOrIndex(entity.name, vocabulary.other_ncnames);
        encodeIdentifyingStringOrIndex(entity.systemIdentifier, vocabulary.other_uris);
        if (encodepublicid) {
            encodeIdentifyingStringOrIndex(entity.publicIdentifier, vocabulary.other_uris);
        }
        encodeIdentifyingStringOrIndex(entity.notationName, vocabulary.other_ncnames);
    }

    protected void encodeUnparsedEntities(List<UnparsedEntity> list) throws IOException {
        UnParsedEntites.clear();
        if (list == null) return;
        for (UnparsedEntity entity : list) {
            encodeUnparsedEntity(entity);
            UnParsedEntites.put(entity.name, entity);
        }
        current_octet = FastInfosetConstants.TERMINATION_PATTERN << 4;
        flush_currentoctet();
    }
    //c.6
    protected void encodeEntityReference(String entityreference) throws IOException {
        alignToOctet();
        current_octet |= FastInfosetConstants.UNEXPANDED_ENTITY_IDENTIFICATION;
        String systemid = null;
        String publicid = null;
        UnparsedEntity entity = UnParsedEntites.get(entityreference);
        if (entity != null) {
            systemid = entity.systemIdentifier;
            publicid = entity.publicIdentifier;
        }
        boolean encodesystemid = (systemid != null) && !systemid.isEmpty();
        boolean encodepublicid = (publicid != null) && !publicid.isEmpty();
        if (encodesystemid) {
            current_octet |= FastInfosetConstants.DOCUMENT_TYPE_SYSTEMID_PRESENCE;
        }
        if (encodepublicid) {
            current_octet |= FastInfosetConstants.DOCUMENT_TYPE_PUBLICID_PRESENCE;
        }
        flush_currentoctet();
        encodeIdentifyingStringOrIndex(entityreference, vocabulary.other_ncnames);
        if (encodesystemid) {
            encodeIdentifyingStringOrIndex(systemid, vocabulary.other_uris);
        }
        if (encodepublicid) {
            encodeIdentifyingStringOrIndex(publicid, vocabulary.other_uris);
        }
    }
    
    //c.21
    protected void encodeSequenceOfLength(int length) throws IOException {
        length--;
        if (length < 128) {
            current_octet = length;
            flush_currentoctet();
        }
        else { 
            length -= 128;
            current_octet = 0x80 | ((length >> 16) & 0xF);
            flush_currentoctet();
            _out.write((length >> 8) & 0xFF);
            _out.write(length & 0xFF);
        }
    }
    //C.16
    protected void encodeNameSurrogateOnSeventhBit(Name_surrogate surrogate) throws IOException {
        boolean encodeNamespace = surrogate.namespace != HashMapObjectInt.NO_INDEX;
        boolean encodePrefix = surrogate.prefix != HashMapObjectInt.NO_INDEX;
        if (encodeNamespace) {
            current_octet |= FastInfosetConstants.LITERAL_QNAME_NAMESPACE_NAME_FLAG;
            if (encodePrefix) {
                current_octet |= FastInfosetConstants.LITERAL_QNAME_PREFIX_FLAG;
            }
        }
        flush_currentoctet();
        if (encodeNamespace) {
            if (encodePrefix) {
                encodeIndexOnSecondbit(surrogate.prefix);
            }
            encodeIndexOnSecondbit(surrogate.namespace);
        }
        encodeIndexOnSecondbit(surrogate.localname);
    }
    
    //c.2.4
    protected void encodeAdditionalData(List<Additional_datum> additional_data) throws IOException {
        encodeSequenceOfLength(additional_data.size());
        for (Additional_datum additional_datum : additional_data) {
            int len = encodeUTFinInternalEncodingBuffer(additional_datum.getId());
            encodeNonEmptyOctetStringOnSecondBit(_encodingBuffer, 0, len);
            encodeNonEmptyOctetStringOnSecondBit(additional_datum.getData());
        }
    }
    
    protected void encodeNotations(List<Notation> notations) throws IOException {
        if (notations == null) return;
        for (Notation notation : notations) {
            encodeNotation(notation.name, notation.systemIdentifier, notation.publicIdentifier);
        }
        current_octet = FastInfosetConstants.TERMINATION_PATTERN << 4;
        flush_currentoctet();
    }
    //codificacion con Charset y bufer
//    private byte[] encodingBuffer = new byte[1024];
//    private ByteBuffer _bb = ByteBuffer.wrap(encodingBuffer);
//    private InternalEncodingBuffer internalEncodingBuffer = new InternalEncodingBuffer(encodingBuffer.length);
//    private void encodeUTFinInternalEncodingBuffer(String str, CharsetEncoder utfEncoder) throws IOException {
//        CharBuffer cb = CharBuffer.wrap(str);
//        utfEncoder.reset();
//        internalEncodingBuffer.reset();
//        CoderResult cr;
//        do {
//            cr = utfEncoder.encode(cb, _bb, true);
//            internalEncodingBuffer.write(encodingBuffer,0,_bb.position());
//            _bb.clear();
//        } while (cr.isOverflow());
//        utfEncoder.flush(_bb);
//        internalEncodingBuffer.write(encodingBuffer,0,_bb.position());
//    }
//    private class InternalEncodingBuffer extends ByteArrayOutputStream {
//
//        public InternalEncodingBuffer(int size) {
//            super(size);
//        }
//        public byte[] getInternalByteArray(){
//            return buf;
//        }
//    }
    byte[] _encodingBuffer = new byte[1024];
    char[] _charBuffer = new char[1024];
    protected int encodeUTFinInternalEncodingBuffer(String str) throws IOException {
        final int length = str.length();
            if (length < _charBuffer.length) {
                str.getChars(0, length, _charBuffer, 0);
                return utf8encoding?encodeUTF8String(_charBuffer, 0, length):
                                    encodeUTF16String(_charBuffer, 0, length);
            } else {
                char[] ch = str.toCharArray();
                return utf8encoding?encodeUTF8String(ch, 0, length):
                                    encodeUTF16String(ch, 0, length);
            }
    }
    protected int encodeUTFinInternalEncodingBuffer(char[] ca, int offset, int length) throws IOException {
        return utf8encoding?encodeUTF8String(ca, offset, length):
                                    encodeUTF16String(ca, offset, length);
    }
    private final int encodeUTF8String(char[] ch, int offset, int length) throws IOException {
        int bpos = 0;

        ensureEncodingBufferSizeForUtf8String(length);

        final int end = offset + length;
        char c;
        while (end != offset) {
            c = ch[offset++];
            if (c < 0x80) {
                // 1 byte, 7 bits
                _encodingBuffer[bpos++] = (byte) c;
            } else if (c < 0x800) {
                // 2 bytes, 11 bits
                _encodingBuffer[bpos++] =
                    (byte) (0xC0 | (c >> 6));    // first 5
                _encodingBuffer[bpos++] =
                    (byte) (0x80 | (c & 0x3F));  // second 6
            } else if (c <= '\uFFFF') { 
                if (!Character.isHighSurrogate(c) && !Character.isLowSurrogate(c)) {
                    // 3 bytes, 16 bits
                    _encodingBuffer[bpos++] =
                        (byte) (0xE0 | (c >> 12));   // first 4
                    _encodingBuffer[bpos++] =
                        (byte) (0x80 | ((c >> 6) & 0x3F));  // second 6
                    _encodingBuffer[bpos++] =
                        (byte) (0x80 | (c & 0x3F));  // third 6
                } else {
                    // 4 bytes, high and low surrogate
                    encodeCharacterAsUtf8FourByte(c, ch, offset, end, bpos);
                    
                    bpos += 4;
                    offset++;
                }
            }
        }

        return bpos;
    }
    private void encodeCharacterAsUtf8FourByte(int c, char[] ch, int chpos, int chend, int bpos) throws IOException {
        if (chpos == chend) {
            throw new IOException("Unexpected end of string");
        }
        
        final char d = ch[chpos];
        if (!Character.isLowSurrogate(d)) {
            throw new IOException("Illegal character. Low surrogate expected");
        }
        
        final int uc = (((c & 0x3ff) << 10) | (d & 0x3ff)) + 0x10000;
        if (uc < 0 || uc >= 0x200000) {
            throw new IOException("");
        }

        _encodingBuffer[bpos++] = (byte)(0xF0 | ((uc >> 18)));
        _encodingBuffer[bpos++] = (byte)(0x80 | ((uc >> 12) & 0x3F));
        _encodingBuffer[bpos++] = (byte)(0x80 | ((uc >> 6) & 0x3F));
        _encodingBuffer[bpos++] = (byte)(0x80 | (uc & 0x3F));
    }
    private void ensureEncodingBufferSizeForUtf8String(int length) {
        final int newLength = 4 * length;
        if (_encodingBuffer.length < newLength) {
            _encodingBuffer = new byte[newLength];
        }
    }
    protected final int encodeUTF16String(char[] ch, int offset, int length) throws IOException {
        int byteLength = 0;

        ensureEncodingBufferSizeForUtf16String(length);

        final int n = offset + length;
        for (int i = offset; i < n; i++) {
            final int c = (int) ch[i];
            _encodingBuffer[byteLength++] = (byte)(c >> 8);
            _encodingBuffer[byteLength++] = (byte)(c & 0xFF);
        }

        return byteLength;
    }
    private void ensureEncodingBufferSizeForUtf16String(int length) {
        final int newLength = 2 * length;
        if (_encodingBuffer.length < newLength) {
            _encodingBuffer = new byte[newLength];
        }
    }

    
    //vocabularies
    protected void encodeInitialVocabulary(InitialVocabulary initialVocabulary) throws IOException {
        int mascara1 = 0;
        int mascara2 = 0;
        String externalURI = initialVocabulary.getExternalVocabularyURI();
        if (externalURI != null && !externalURI.isEmpty()) {
            mascara1 |= FastInfosetConstants.INITIAL_VOCABULARY_EXTERNAL_VOCABULARY;
        }
        if (!initialVocabulary.alphabets.isEmpty()) {
            mascara1 |= FastInfosetConstants.INITIAL_VOCABULARY_ALPHABETS;
        }
        if (!initialVocabulary.algorithms.isEmpty()) {
            mascara1 |= FastInfosetConstants.INITIAL_VOCABULARY_ALGORITHMS;
        }
        if (!initialVocabulary.prefixes.isEmpty()) {
            mascara1 |= FastInfosetConstants.INITIAL_VOCABULARY_PREFIXES;
        }
        if (!initialVocabulary.namespaces.isEmpty()) {
            mascara1 |= FastInfosetConstants.INITIAL_VOCABULARY_NAMESPACES;
        }
        current_octet = mascara1;
        flush_currentoctet(); // primera parte de la mascara de opciones
        if (!initialVocabulary.localnames.isEmpty()) {
            mascara2 |= FastInfosetConstants.INITIAL_VOCABULARY_LOCALNAMES;
        }
        if (!initialVocabulary.other_ncnames.isEmpty()) {
            mascara2 |= FastInfosetConstants.INITIAL_VOCABULARY_OTHER_NCNAMES;
        }
        if (!initialVocabulary.other_uris.isEmpty()) {
            mascara2 |= FastInfosetConstants.INITIAL_VOCABULARY_OTHER_URIS;
        }
        if (!initialVocabulary.attribute_values.isEmpty()) {
            mascara2 |= FastInfosetConstants.INITIAL_VOCABULARY_ATTRIBUTE_VALUES;
        }
        if (!initialVocabulary.character_chunks.isEmpty()) {
            mascara2 |= FastInfosetConstants.INITIAL_VOCABULARY_CHARACTER_CHUNKS;
        }
        if (!initialVocabulary.other_strings.isEmpty()) {
            mascara2 |= FastInfosetConstants.INITIAL_VOCABULARY_OTHER_STRINGS;
        }
        if (!initialVocabulary.elementnames.isEmpty()) {
            mascara2 |= FastInfosetConstants.INITIAL_VOCABULARY_ELEMENT_NAMES;
        }
        if (!initialVocabulary.attributenames.isEmpty()) {
            mascara2 |= FastInfosetConstants.INITIAL_VOCABULARY_ATTRIBUTE_NAMES;
        }
        current_octet = mascara2;
        flush_currentoctet(); // segunda parte de la mascara de opciones
        if ((mascara1 & FastInfosetConstants.INITIAL_VOCABULARY_EXTERNAL_VOCABULARY) != 0) {
            int len = encodeUTFinInternalEncodingBuffer(externalURI);
            encodeNonEmptyOctetStringOnSecondBit(_encodingBuffer, 0, len);
        }
        if ((mascara1 & FastInfosetConstants.INITIAL_VOCABULARY_ALPHABETS) != 0) {
            encodeAlphabetVocabularyTable(initialVocabulary.alphabets);
        }
        if ((mascara1 & FastInfosetConstants.INITIAL_VOCABULARY_ALGORITHMS) != 0) {
            encodeAlgorithmVocabularyTable(initialVocabulary.algorithms);
        }
        if ((mascara1 & FastInfosetConstants.INITIAL_VOCABULARY_PREFIXES) != 0) {
            encodeNonEmptyOctetStringVocabularyTable(initialVocabulary.prefixes);
        }
        if ((mascara1 & FastInfosetConstants.INITIAL_VOCABULARY_NAMESPACES) != 0) {
            encodeNonEmptyOctetStringVocabularyTable(initialVocabulary.namespaces);
        }
        if ((mascara2 & FastInfosetConstants.INITIAL_VOCABULARY_LOCALNAMES) != 0) {
            encodeNonEmptyOctetStringVocabularyTable(initialVocabulary.localnames);
        }
        if ((mascara2 & FastInfosetConstants.INITIAL_VOCABULARY_OTHER_NCNAMES) != 0) {
            encodeNonEmptyOctetStringVocabularyTable(initialVocabulary.other_ncnames);
        }
        if ((mascara2 & FastInfosetConstants.INITIAL_VOCABULARY_OTHER_URIS) != 0) {
            encodeNonEmptyOctetStringVocabularyTable(initialVocabulary.other_uris);
        }
        if ((mascara2 & FastInfosetConstants.INITIAL_VOCABULARY_ATTRIBUTE_VALUES) != 0) {
            encodeEncodedCharacterStringVocabularyTable(initialVocabulary.attribute_values);
        }
        if ((mascara2 & FastInfosetConstants.INITIAL_VOCABULARY_CHARACTER_CHUNKS) != 0) {
            encodeEncodedCharacterStringVocabularyTable(initialVocabulary.character_chunks);
        }
        if ((mascara2 & FastInfosetConstants.INITIAL_VOCABULARY_OTHER_STRINGS) != 0) {
            encodeEncodedCharacterStringVocabularyTable(initialVocabulary.other_strings);
        }
        if ((mascara2 & FastInfosetConstants.INITIAL_VOCABULARY_ELEMENT_NAMES) != 0) {
            encodeNameSurrogateVocabularyTable(initialVocabulary.elementnames);
        }
        if ((mascara2 & FastInfosetConstants.INITIAL_VOCABULARY_ATTRIBUTE_NAMES) != 0) {
            encodeNameSurrogateVocabularyTable(initialVocabulary.attributenames);
        }
    }
    
    protected void encodeAlphabetVocabularyTable(List<Alphabet> table) throws IOException {
        encodeSequenceOfLength(table.size());
        for (Alphabet alphabet : table) {
            int len = encodeUTFinInternalEncodingBuffer(alphabet.toString());
            encodeNonEmptyOctetStringOnSecondBit(_encodingBuffer, 0, len);
        }
    }
    protected void encodeAlgorithmVocabularyTable(List<Algorithm> table) throws IOException {
        encodeSequenceOfLength(table.size());
        for (Algorithm algo : table) {
            int len = encodeUTFinInternalEncodingBuffer(algo.getURI());
            encodeNonEmptyOctetStringOnSecondBit(_encodingBuffer, 0, len);
        }
    }
    
    protected void encodeEncodedCharacterStringVocabularyTable(List<String> table) throws IOException {
        encodeSequenceOfLength(table.size());
        for (String str : table) {
            encodeEncodedCharacterStringOnThirdbit(str);
        }
    }

    protected void encodeNameSurrogateVocabularyTable(List<Name_surrogate> table) throws IOException {
        encodeSequenceOfLength(table.size());
        for (Name_surrogate surrogate : table) {
            encodeNameSurrogateOnSeventhBit(surrogate);
        }
    }

    protected void encodeNonEmptyOctetStringVocabularyTable(List<String> table) throws IOException {
        encodeSequenceOfLength(table.size());
        for (String str : table) {
            int len = encodeUTFinInternalEncodingBuffer(str);
            encodeNonEmptyOctetStringOnSecondBit(_encodingBuffer, 0, len);
        }
    }

    protected void encodeDocumentCharEncoding(String xmlEncoding) throws IOException {
        int len = encodeUTFinInternalEncodingBuffer(xmlEncoding);
        encodeNonEmptyOctetStringOnSecondBit(_encodingBuffer, 0, len);
    }

    protected void encodeDocumentStandalone(boolean xmlStandalone) throws IOException {
        current_octet |= xmlStandalone ? 1 : 0;
        flush_currentoctet();
    }

    protected void encodeDocumentVersion(String xmlVersion) throws IOException {
        encodeNonIdentifyingStringOrIndexOnFirstBit(xmlVersion, vocabulary.other_strings);
    }
    protected boolean encodeStandalone = false;
    protected boolean encodeXMLVersion = false;
    protected boolean encodeXMLEncoding = false;
    protected boolean xlmDeclaration = false;


    public void setEncodeXMLDeclaration(boolean xmlDeclaration) {
        this.xlmDeclaration = xmlDeclaration;
    }
    public void setEncodeXMLVersion(boolean encodeXMLVersion) {
        this.encodeXMLVersion = encodeXMLVersion;
    }

    public void setEncodeXMLEncoding(boolean encodeXMLEncoding) {
        this.encodeXMLEncoding = encodeXMLEncoding;
    }

    public void setEncodeStandalone(boolean encodeStandalone) {
        this.encodeStandalone = encodeStandalone;
    }
    protected void encodeHeader(String XMLEncoding, boolean standalone, String XMLVersion, List<UnparsedEntity> unparsedEntities, List<Notation> notations) throws IOException {
        if (xlmDeclaration) {
            int len = encodeUTFinInternalEncodingBuffer("<?xml " + ((!encodeXMLVersion || XMLVersion == null) ? "" : "version='" + XMLVersion + "' ") + "encoding='finf'" + ((encodeStandalone) ? " standalone=" + (standalone ? "'yes'" : "'no'") : "") + "?>");
            _out.write(_encodingBuffer, 0, len);
        }
        _out.write(FastInfosetConstants.FastInfosetIdentificacion); //E0 00
        _out.write(FastInfosetConstants.FastInfosetVersion); //00 01
        int mascara = 0;
        if (additional_data != null) {
            mascara |= FastInfosetConstants.DOCUMENT_ADDITIONAL_DATA_FLAG;
        }
        if ((vocabulary.getInitialVocabulary() != null) && (!vocabulary.getInitialVocabulary().isEmpty())) {
            mascara |= FastInfosetConstants.DOCUMENT_INITIAL_VOCABULARY_FLAG;
        }
        //Notations
        if ((notations != null) && (notations.size() > 0)) {
            mascara |= FastInfosetConstants.DOCUMENT_NOTATIONS_FLAG;
        }
        //Unparsed Entities
        if ((unparsedEntities != null) && (unparsedEntities.size() > 0)) {
            mascara |= FastInfosetConstants.DOCUMENT_UNPARSED_ENTITIES_FLAG;
        }
        //Character encoding
        if (encodeXMLEncoding && (XMLEncoding != null)) {
            mascara |= FastInfosetConstants.DOCUMENT_CHARACTER_ENCODING_SCHEME_FLAG;
        }
        //Standlone
        if (encodeStandalone) {
            mascara |= FastInfosetConstants.DOCUMENT_STANDALONE_FLAG;
        }
        //version
        if (encodeXMLVersion && (XMLVersion != null)) {
            mascara |= FastInfosetConstants.DOCUMENT_VERSION_FLAG;
        }
        current_octet = mascara;
        flush_currentoctet(); //escribe máscara
        //codificar las opciones
        if ((mascara & FastInfosetConstants.DOCUMENT_ADDITIONAL_DATA_FLAG) != 0) {
            encodeAdditionalData(additional_data);
        }
        if ((mascara & FastInfosetConstants.DOCUMENT_INITIAL_VOCABULARY_FLAG) != 0) {
            encodeInitialVocabulary(vocabulary.getInitialVocabulary());
        }
        if ((mascara & FastInfosetConstants.DOCUMENT_NOTATIONS_FLAG) != 0) {
            encodeNotations(notations);
        }
        if ((mascara & FastInfosetConstants.DOCUMENT_UNPARSED_ENTITIES_FLAG) != 0) {
            encodeUnparsedEntities(unparsedEntities);
        }
        if ((mascara & FastInfosetConstants.DOCUMENT_CHARACTER_ENCODING_SCHEME_FLAG) != 0) {
            encodeDocumentCharEncoding(XMLEncoding);
        }
        if ((mascara & FastInfosetConstants.DOCUMENT_STANDALONE_FLAG) != 0) {
            encodeDocumentStandalone(standalone);
        }
        if ((mascara & FastInfosetConstants.DOCUMENT_VERSION_FLAG) != 0) {
            encodeDocumentVersion(XMLVersion);
        }
    }
    protected void encodeElementTermination() throws IOException {
         if (current_octet == 0) {
                current_octet |= FastInfosetConstants.TERMINATION_PATTERN << 4;
            } else {
                current_octet |= FastInfosetConstants.TERMINATION_PATTERN;
                flush_currentoctet();
            }

    }
    protected void encodeDocumentTermination() throws IOException {
        current_octet |= (current_octet == 0) ? FastInfosetConstants.TERMINATION_PATTERN << 4 : FastInfosetConstants.TERMINATION_PATTERN;
        flush_currentoctet();
    }

    protected void encodeElement(QName qname, List<Attribute> atts, List<NamespaceAttribute> nsatts) throws IOException {
        alignToOctet();
        int attsSize = (atts == null)?0:atts.size();
        if (attsSize > 0) {
            current_octet |= FastInfosetConstants.ELEMENT_ATTRIBUTE_PRESENCE;
        }
        if (!nsatts.isEmpty()) {
            current_octet |= FastInfosetConstants.ELEMENT_NAMESPACES_PRESENCE;
            flush_currentoctet();
            encodeNamespaceAttributes(nsatts);
        }
        encodeQualifiedNameOrIndexOnThirdBit(qname);
        if (attsSize > 0) {
            encodeAttributes(atts);
        }
    }

    

    


}
