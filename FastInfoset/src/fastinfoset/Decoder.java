//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset;

import fastinfoset.Algorithm.Algorithm;
import fastinfoset.Alphabet.Alphabet;
import fastinfoset.Document.Additional_datum;
import fastinfoset.Document.DocumentType;
import fastinfoset.Document.Element.AlgorithmAttribute;
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
import fastinfoset.util.InitialVocabulary;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

/**
 *
 * @author rpablos
 */
public class Decoder {
    DecoderVocabulary vocabulary = new DecoderVocabulary();
    Map<String, InitialVocabulary> registredExternalVocabulary = new HashMap<String, InitialVocabulary>();
    Map<String, Algorithm> registredAlgorithms = new HashMap<String, Algorithm>();
    InputStream _in;
    int current_octet = 0;
    boolean _terminate = false;
    List<Additional_datum> additional_data = null;
    List<Notation> notations = null;
    List<UnparsedEntity> unparsedEntities = null;
    Boolean IsStandalone = null;
    String XMLEncoding = null;
    String XMLVersion = null;
    
    public void registerAlgorithms(List<Algorithm> algorithms) {
        for (Algorithm algo: algorithms)
            registredAlgorithms.put(algo.getURI(), algo);
    }
    public List<Algorithm> getRegistredAlgorithms() {
        return new ArrayList<Algorithm>(registredAlgorithms.values());
    }
    public void registerExternalVocabularies(Map<String, InitialVocabulary> vocabularies) {
        registredExternalVocabulary.putAll(vocabularies);
    }
    public Map<String, InitialVocabulary> getRegistredVocabularies() {
        return registredExternalVocabulary;
    }
    
    char[] _decodingBuffer = new char[1024];
    public int decodeUTF8inInternalEncodingBuffer(byte[] data) throws IOException {
        return decodeUTF8inInternalEncodingBuffer(data, 0, data.length);
    }
    public String decodeUTF8inInternalEncodingBufferAsString(byte[] data) throws IOException {
        int len= decodeUTF8inInternalEncodingBuffer(data, 0, data.length);
        return new String(_decodingBuffer,0,len);
    }
    protected int decodeUTF8inInternalEncodingBuffer(byte[] data, int offset, int length) throws IOException {
        ensureDecodingBufferSizeForUtf8String(length);
        int charlen = 0, b1;
        int end = offset+length;
        while (offset < end) {
            b1 = data[offset++] & 0xFF;
            if (b1 < 0x80)
                _decodingBuffer[charlen++] = (char) b1;
            else {
                checkEndUTF8String(offset,end);
                int b2 = data[offset++] & 0xFF;
                checkContinuationUTF8octet(b2, offset-1);
                if (b1 < 0xE0) {  // dos octetos
                    _decodingBuffer[charlen++] = (char) (((b1 & 0x1F) << 6) | (b2 & 0x3F));
                } else {
                    checkEndUTF8String(offset,end);
                    int b3 = data[offset++] & 0xFF;
                    checkContinuationUTF8octet(b3, offset-1);
                    if (b1 < 0xF0){  // tres octetos
                        _decodingBuffer[charlen++] = (char) ((b1 & 0x0F) << 12 | (b2 & 0x3F) << 6 | (b3 & 0x3F));
                    } else {  // 4 octetos
                        checkEndUTF8String(offset,end);
                        int b4 = data[offset++] & 0xFF;
                        checkContinuationUTF8octet(b4, offset-1);
                        int uc = ((b1 & 0x7) << 18) | ((b2 & 0x3f) << 12) | ((b3 & 0x3f) << 6) | (b4 & 0x3f);
                        if (uc < 0 || uc >= 0x200000) {
                                throw new IOException("hign surrogate and low surrogate expected");
                        }
                        _decodingBuffer[charlen++] = (char)( ( ( (uc - 0x10000) >> 10) & 0x3FF)  +0xd800); //high
                        _decodingBuffer[charlen++] = (char)( ( ( (uc - 0x10000)) & 0x3FF)  +0xdc00); //low
                    }
                }
            }
        }
        return charlen;
    }
    private void checkContinuationUTF8octet(int b, int pos) throws IOException {
        if ((b & 0xC0) != 0x80) 
                throw new IOException("Illegal state at position "+pos);
    }
    private void checkEndUTF8String(int offset, int end) throws IOException {
        if (offset == end)
             throw new IOException("Unexpected end of string");
    }
    private void ensureDecodingBufferSizeForUtf8String(int length) {
        if (_decodingBuffer.length < length) {
            _decodingBuffer = new char[length];
        }
    }
    public int decodeUTF16inInternalEncodingBuffer(byte[] data) throws IOException {
        return decodeUTF16inInternalEncodingBuffer(data, 0, data.length);
    }
    public String decodeUTF16inInternalEncodingBufferAsString(byte[] data) throws IOException {
        int len= decodeUTF16inInternalEncodingBuffer(data, 0, data.length);
        return new String(_decodingBuffer,0,len);
    }
    protected int decodeUTF16inInternalEncodingBuffer(byte[] data, int offset, int length) throws IOException {
        ensureDecodingBufferSizeForUtf16String(length);
        if ((length & 0x1) != 0) //impar
            throw new IOException("not a valid utf16");
        int len = length/2;
        for (int i = 0; i < len; i++)
            _decodingBuffer[i] = (char) ((data[2*i] << 8) | data[2*i+1]);
        return len;
    }

    private void ensureDecodingBufferSizeForUtf16String(int length) {
        if (_decodingBuffer.length < length/2) {
            _decodingBuffer = new char[length/2];
        }
    }

    protected int read() throws IOException {
        if ((current_octet = _in.read()) == -1) {
            throw new EOFException();
        }
        return current_octet;
    }
    
    void readFully(byte[] b) throws IOException {
        readFully(b, 0, b.length);
    }

    void readFully(byte[] b, int off, int len) throws IOException {
        if (len < 0) {
            throw new IndexOutOfBoundsException();
        }
        int n = 0;
        while (n < len) {
            int count = _in.read(b, off + n, len - n);
            if (count < 0) {
                throw new EOFException();
            }
            n += count;
        }
    }
    
    boolean byteArrayPartiallyEquals(byte[] src, int src_offset, byte[] dst, int dst_offset, int length) {
        for (int i = 0; i < length; i++) {
            if (src[src_offset + i] != dst[dst_offset + i]) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean decodeFastinfosetDeclaration() throws IOException, FastInfosetException {
        byte[] buffer = new byte[FastInfosetConstants.FastInfosetIdentificacion.length + FastInfosetConstants.FastInfosetVersion.length];
        readFully(buffer);
        if (!byteArrayPartiallyEquals(buffer, 0, FastInfosetConstants.FastInfosetIdentificacion, 0, FastInfosetConstants.FastInfosetIdentificacion.length) || !byteArrayPartiallyEquals(buffer, FastInfosetConstants.FastInfosetIdentificacion.length, FastInfosetConstants.FastInfosetVersion, 0, FastInfosetConstants.FastInfosetVersion.length)) {
            //test the textual XML declaration
            if (!byteArrayPartiallyEquals(buffer, 0, FastInfosetConstants.XML_HEADER_DECLARATION[0], 0, buffer.length)) {
                return false;
            }
            byte[] buffer2 = Arrays.copyOf(buffer, buffer.length);
            for (int i = 0; i < FastInfosetConstants.XML_HEADER_DECLARATION.length; i++) {
                int offset = buffer2.length;
                buffer2 = Arrays.copyOf(buffer2, FastInfosetConstants.XML_HEADER_DECLARATION[i].length);
                readFully(buffer2, offset, FastInfosetConstants.XML_HEADER_DECLARATION[i].length - offset);
                if (byteArrayPartiallyEquals(FastInfosetConstants.XML_HEADER_DECLARATION[i], 0, buffer2, 0, FastInfosetConstants.XML_HEADER_DECLARATION[i].length)) {
                    readFully(buffer);
                    if (!byteArrayPartiallyEquals(buffer, 0, FastInfosetConstants.FastInfosetIdentificacion, 0, FastInfosetConstants.FastInfosetIdentificacion.length) || !byteArrayPartiallyEquals(buffer, FastInfosetConstants.FastInfosetIdentificacion.length, FastInfosetConstants.FastInfosetVersion, 0, FastInfosetConstants.FastInfosetVersion.length)) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }

    protected void decodeHeader() throws IOException, FastInfosetException {
        if (!decodeFastinfosetDeclaration()) {
            throw new FastInfosetException("Not a fast infoset document");
        }
        int mascara = read(); //mascara
        if ((mascara & FastInfosetConstants.DOCUMENT_ADDITIONAL_DATA_FLAG) != 0) {
            additional_data = decodeAdditionalData();
        }
        if ((mascara & FastInfosetConstants.DOCUMENT_INITIAL_VOCABULARY_FLAG) != 0) {
            decodeInitialVocabulary();
        }
        if ((mascara & FastInfosetConstants.DOCUMENT_NOTATIONS_FLAG) != 0) {
            notations = decodeNotations();
        }
        if ((mascara & FastInfosetConstants.DOCUMENT_UNPARSED_ENTITIES_FLAG) != 0) {
            unparsedEntities = decodeUnparsedEntities();
        }
        if ((mascara & FastInfosetConstants.DOCUMENT_CHARACTER_ENCODING_SCHEME_FLAG) != 0) {
            XMLEncoding = decodeDocumentCharEncoding();
        }
        if ((mascara & FastInfosetConstants.DOCUMENT_STANDALONE_FLAG) != 0) {
            IsStandalone = decodeDocumentStandalone();
        }
        if ((mascara & FastInfosetConstants.DOCUMENT_VERSION_FLAG) != 0) {
            XMLVersion = decodeDocumentVersion();
        }
    }

    //c.21
    protected int decodeSequenceOfLength() throws IOException {
        read();
        if (current_octet < 128) {
            return current_octet + 1;
        } else {
            int b1 = current_octet & 15;
            int b2 = read();
            return ((b1 << 16) | (b2 << 8) | read()) + 129;
        }
    }

    //c.2.4
    protected List<Additional_datum> decodeAdditionalData() throws IOException {
        int len = decodeSequenceOfLength();
        List<Additional_datum> result = new ArrayList<Additional_datum>(len);
        for (; len > 0; len--) {
            int l = decodeUTF8inInternalEncodingBuffer(decodeNonEmptyOctetStringOnSecondBit());
            String id = new String(_decodingBuffer, 0, l);
            byte[] data = decodeNonEmptyOctetStringOnSecondBit();
            result.add(new Additional_datum(id, data));
        }
        return result;
    }

    protected byte[] decodeNonEmptyOctetString(int startingBit) throws IOException {
        int length = 0;
        // decode length
        if (((1 << (8 - startingBit)) & current_octet) == 0) {
            length = 1 + (current_octet & ((1 << (8 - startingBit)) - 1));
        } else if (((current_octet << (startingBit - 1)) & 255) == FastInfosetConstants.OCTET_STRING_LENGTH_2_OCTETS_FLAG) {
            length = read() + 1 + (1 << (8 - startingBit));
        } else {
            length = (read() << 24);
            length |= (read() << 16);
            length |= (read() << 8);
            length |= read();
            length += 1 + (1 << (8 - startingBit)) + 256;
        }
        //decode octet_string
        byte[] result = new byte[length];
        readFully(result);
        return result;
    }

    protected byte[] decodeNonEmptyOctetStringOnSecondBit() throws IOException {
        return decodeNonEmptyOctetString(2);
    }

    protected List<Notation> decodeNotations() throws IOException, FastInfosetException {
        List<Notation> result = new ArrayList<Notation>();
        while (((read() & FastInfosetConstants.NOTATION_IDENTIFICATION_MASK) ^ FastInfosetConstants.NOTATION_IDENTIFICATION) == 0) {
            Notation notation = decodeNotation();
            result.add(notation);
        }
        if (current_octet != (FastInfosetConstants.TERMINATION_PATTERN << 4)) {
            throw new FastInfosetException("Incorrect end of notations");
        }
        return result;
    }

    //c.11
    protected Notation decodeNotation() throws IOException, FastInfosetException {
        boolean decodesystemid = (FastInfosetConstants.DOCUMENT_TYPE_SYSTEMID_PRESENCE & current_octet) != 0;
        boolean decodepublicid = (FastInfosetConstants.DOCUMENT_TYPE_PUBLICID_PRESENCE & current_octet) != 0;
        String name = decodeIdentifyingStringOrIndex(vocabulary.other_ncnames);
        String systemid = null;
        String publicid = null;
        if (decodesystemid) {
            systemid = decodeIdentifyingStringOrIndex(vocabulary.other_uris);
        }
        if (decodepublicid) {
            publicid = decodeIdentifyingStringOrIndex(vocabulary.other_uris);
        }
        return new Notation(name, systemid, publicid);
    }

    protected String decodeDocumentCharEncoding() throws IOException {
        read();
        return decodeUTF8inInternalEncodingBufferAsString(decodeNonEmptyOctetStringOnSecondBit());
    }

    protected boolean decodeDocumentStandalone() throws IOException {
        return read() != 0;
    }

    protected String decodeDocumentVersion() throws IOException, FastInfosetException {
        return decodeNonIdentifyingStringOrIndexOnFirstBit(vocabulary.other_strings).getString();
    }
    
    EncodedString encodedStringBuffer = new EncodedString();
    protected EncodedString decodeEncodedCharacterStringOnFifthbit() throws IOException, FastInfosetException {
        switch (current_octet & FastInfosetConstants.ENCODED_CHARACTER_STRING_5TH_BIT_MASK_DISCRIMINANT) {
            case FastInfosetConstants.ENCODED_CHARACTER_STRING_5TH_BIT_UTF8_DISCRIMINANT:
                encodedStringBuffer.setUTF8(decodeUTF8inInternalEncodingBufferAsString(decodeNonEmptyOctetStringOnSeventhBit()));
                return encodedStringBuffer;
            case FastInfosetConstants.ENCODED_CHARACTER_STRING_5TH_BIT_UTF16_DISCRIMINANT:
                encodedStringBuffer.setUTF16(decodeUTF16inInternalEncodingBufferAsString(decodeNonEmptyOctetStringOnSeventhBit()));
                return encodedStringBuffer;
            case FastInfosetConstants.ENCODED_CHARACTER_STRING_5TH_BIT_ALGORITHM_DISCRIMINANT:
                int index = decodeOctetInteger(7);
                Algorithm algo = vocabulary.algorithms.get(index);
                encodedStringBuffer.setAlgorithm(index, algo, decodeNonEmptyOctetStringOnSeventhBit());
                return encodedStringBuffer;
            case FastInfosetConstants.ENCODED_CHARACTER_STRING_5TH_BIT_ALPHABET_DISCRIMINANT:
                int index2 = decodeOctetInteger(7);
                Algorithm algo2 = vocabulary.alphabets.get(index2);
                encodedStringBuffer.setAlphabet(index2, algo2.fromByteArray(decodeNonEmptyOctetStringOnSeventhBit()));
                return encodedStringBuffer;
        }
        throw new FastInfosetException();
    }

    protected EncodedString decodeEncodedCharacterStringOnThirdbit() throws IOException, FastInfosetException {
        switch (current_octet & FastInfosetConstants.ENCODED_CHARACTER_STRING_3RD_BIT_MASK_DISCRIMINANT) {
            case FastInfosetConstants.ENCODED_CHARACTER_STRING_3RD_BIT_UTF8_DISCRIMINANT:
                encodedStringBuffer.setUTF8(decodeUTF8inInternalEncodingBufferAsString(decodeNonEmptyOctetStringOnFifthBit()));
                return encodedStringBuffer;
            case FastInfosetConstants.ENCODED_CHARACTER_STRING_3RD_BIT_UTF16_DISCRIMINANT:
                encodedStringBuffer.setUTF16(decodeUTF16inInternalEncodingBufferAsString(decodeNonEmptyOctetStringOnFifthBit()));
                return encodedStringBuffer;
            case FastInfosetConstants.ENCODED_CHARACTER_STRING_3RD_BIT_ALGORITHM_DISCRIMINANT:
                int index = decodeOctetInteger(5);
                Algorithm algo = vocabulary.algorithms.get(index);
                encodedStringBuffer.setAlgorithm(index, algo, decodeNonEmptyOctetStringOnFifthBit());
                return encodedStringBuffer;
            case FastInfosetConstants.ENCODED_CHARACTER_STRING_3RD_BIT_ALPHABET_DISCRIMINANT:
                int index2 = decodeOctetInteger(5);
                Algorithm algo2 = vocabulary.alphabets.get(index2);
                encodedStringBuffer.setAlphabet(index2, algo2.fromByteArray(decodeNonEmptyOctetStringOnFifthBit()));
                return encodedStringBuffer;
        }
        throw new FastInfosetException();
    }

    protected void decodeEncodedCharacterStringVocabularyTable(ArrayIndex<String> table) throws IOException, FastInfosetException {
        int len = decodeSequenceOfLength();
        for (int i = 0; i < len; i++) {
            read();
            table.add(decodeEncodedCharacterStringOnThirdbit().getString());
        }
    }

    //c.13
    protected String decodeIdentifyingStringOrIndex(ArrayIndex<String> table) throws IOException, FastInfosetException {
        if ((read() & FastInfosetConstants.IDENTIFYINGSTRINGORINDEX_INDEX) != 0) {
            int index = decodeIndexOnSecondBit();
            return table.get(index);
        } else {
            String str = decodeUTF8inInternalEncodingBufferAsString(decodeNonEmptyOctetStringOnSecondBit());
            table.add(str);
            return str;
        }
    }

    protected int decodeIdentifyingStringOrIndex(StringHolder sh, ArrayIndex<String> table) throws IOException, FastInfosetException {
        if ((read() & FastInfosetConstants.IDENTIFYINGSTRINGORINDEX_INDEX) != 0) {
            int index = decodeIndexOnSecondBit();
            sh.theString = table.get(index);
            return index;
        } else {
            String str = decodeUTF8inInternalEncodingBufferAsString(decodeNonEmptyOctetStringOnSecondBit());
            sh.theString = str;
            return table.add(str);
        }
    }

    protected int decodeIndex(int startingbit) throws IOException {
        if ((current_octet & (1 << (8 - startingbit))) == 0) {
            return current_octet & ((1 << (8 - startingbit)) - 1);
        } else if ((current_octet & (56 >> (startingbit - 3))) == (32 >> (startingbit - 3))) {
            int b1 = current_octet & ((1 << (6 - startingbit)) - 1);
            return ((b1 << 8) | read()) + (1 << (8 - startingbit));
        } else if ((current_octet & (56 >> (startingbit - 3))) == (40 >> (startingbit - 3))) {
            int b1 = current_octet & ((1 << (6 - startingbit)) - 1);
            int b2 = read();
            return ((b1 << 16) | (b2 << 8) | read()) + (1 << (8 - startingbit)) + (1 << (8 + 6 - startingbit));
        } else {
            int b1 = read();
            int b2 = read();
            return ((b1 << 16) | (b2 << 8) | read()) + (1 << (8 - startingbit)) + (1 << (8 + 6 - startingbit)) + (1 << (16 + 6 - startingbit));
        }
    }

    protected int decodeIndexOnFourthBit() throws IOException {
        return decodeIndex(4);
    }

    //c.25
    protected int decodeIndexOnSecondBit() throws IOException {
        if ((current_octet & 64) == 0) {
            return current_octet & 63;
        } else if ((current_octet & 32) == 0) {
            int b1 = current_octet & 31;
            return ((b1 << 8) | read()) + 64;
        } else {
            int b1 = current_octet & 15;
            int b2 = read();
            return ((b1 << 16) | (b2 << 8) | read()) + 8256;
        }
    }

    protected int decodeIndexOnThirdBit() throws IOException {
        return decodeIndex(3);
    }

    protected void decodeInitialVocabulary() throws IOException, FastInfosetException {
        int mascara1 = read();
        int mascara2 = read();
        if ((mascara1 & FastInfosetConstants.INITIAL_VOCABULARY_EXTERNAL_VOCABULARY) != 0) {
            read();
            String externalURI = decodeUTF8inInternalEncodingBufferAsString(decodeNonEmptyOctetStringOnSecondBit());
            vocabulary.setExternalVocabulary(registredExternalVocabulary.get(externalURI));
        }
        if ((mascara1 & FastInfosetConstants.INITIAL_VOCABULARY_ALPHABETS) != 0) {
            decodeAlphabetVocabularyTable(vocabulary.alphabets);
        }
        if ((mascara1 & FastInfosetConstants.INITIAL_VOCABULARY_ALGORITHMS) != 0) {
            decodeAlgorithmVocabularyTable(vocabulary.algorithms);
        }
        if ((mascara1 & FastInfosetConstants.INITIAL_VOCABULARY_PREFIXES) != 0) {
            decodeNonEmptyOctetStringVocabularyTable(vocabulary.prefixes);
        }
        if ((mascara1 & FastInfosetConstants.INITIAL_VOCABULARY_NAMESPACES) != 0) {
            decodeNonEmptyOctetStringVocabularyTable(vocabulary.namespaces);
        }
        if ((mascara2 & FastInfosetConstants.INITIAL_VOCABULARY_LOCALNAMES) != 0) {
            decodeNonEmptyOctetStringVocabularyTable(vocabulary.localnames);
        }
        if ((mascara2 & FastInfosetConstants.INITIAL_VOCABULARY_OTHER_NCNAMES) != 0) {
            decodeNonEmptyOctetStringVocabularyTable(vocabulary.other_ncnames);
        }
        if ((mascara2 & FastInfosetConstants.INITIAL_VOCABULARY_OTHER_URIS) != 0) {
            decodeNonEmptyOctetStringVocabularyTable(vocabulary.other_uris);
        }
        if ((mascara2 & FastInfosetConstants.INITIAL_VOCABULARY_ATTRIBUTE_VALUES) != 0) {
            decodeEncodedCharacterStringVocabularyTable(vocabulary.attribute_values);
        }
        if ((mascara2 & FastInfosetConstants.INITIAL_VOCABULARY_CHARACTER_CHUNKS) != 0) {
            decodeEncodedCharacterStringVocabularyTable(vocabulary.character_chunks);
        }
        if ((mascara2 & FastInfosetConstants.INITIAL_VOCABULARY_OTHER_STRINGS) != 0) {
            decodeEncodedCharacterStringVocabularyTable(vocabulary.other_strings);
        }
        if ((mascara2 & FastInfosetConstants.INITIAL_VOCABULARY_ELEMENT_NAMES) != 0) {
            decodeNameSurrogateVocabularyTable(vocabulary.elementnames);
        }
        if ((mascara2 & FastInfosetConstants.INITIAL_VOCABULARY_ATTRIBUTE_NAMES) != 0) {
            decodeNameSurrogateVocabularyTable(vocabulary.attributenames);
        }
    }

    protected QName decodeLiteralQualifiedName(ArrayIndex<Name_surrogate> table) throws IOException, FastInfosetException {
        boolean decodeprefix = (current_octet & FastInfosetConstants.NAMESPACE_ATTRIBUTE_PREFIX_FLAG) != 0;
        boolean decodenamespace = (current_octet & FastInfosetConstants.NAMESPACE_ATTRIBUTE_NAMESPACE_FLAG) != 0;
        String prefix = "";
        String namespace = "";
        String localname;
        boolean createSurrogate = true;
        Name_surrogate surrogate = new Name_surrogate(HashMapObjectInt.NO_INDEX, HashMapObjectInt.NO_INDEX, HashMapObjectInt.NO_INDEX);
        if (decodeprefix) {
            surrogate.prefix = decodeIdentifyingStringOrIndex(_sh, vocabulary.prefixes);
            prefix = _sh.theString;
            createSurrogate = surrogate.prefix != HashMapObjectInt.NO_INDEX;
        }
        if (decodenamespace) {
            surrogate.namespace = decodeIdentifyingStringOrIndex(_sh, vocabulary.namespaces);
            namespace = _sh.theString;
            createSurrogate = createSurrogate && (surrogate.namespace != HashMapObjectInt.NO_INDEX);
        }
        surrogate.localname = decodeIdentifyingStringOrIndex(_sh, vocabulary.localnames);
        localname = _sh.theString;
        createSurrogate = createSurrogate && (surrogate.localname != HashMapObjectInt.NO_INDEX);
        if (createSurrogate) {
            table.add(surrogate);
        }
        return new QName(namespace, localname, prefix);
    }

    ArrayList<NamespaceAttribute> _attnsbuffer = new ArrayList<NamespaceAttribute>();
    protected List<NamespaceAttribute> decodeNSAttributes() throws IOException, FastInfosetException {
        _attnsbuffer.clear();
        ;
        while (((read() & FastInfosetConstants.NAMESPACE_ATTRIBUTE_IDENTIFICATION_MASK) ^ FastInfosetConstants.NAMESPACE_ATTRIBUTE_IDENTIFICATION) == 0) {
            boolean decodeprefix = (current_octet & FastInfosetConstants.NAMESPACE_ATTRIBUTE_PREFIX_FLAG) != 0;
            boolean decodenamespace = (current_octet & FastInfosetConstants.NAMESPACE_ATTRIBUTE_NAMESPACE_FLAG) != 0;
            String prefix = "";
            String namespace = "";
            if (decodeprefix) {
                prefix = decodeIdentifyingStringOrIndex(vocabulary.prefixes);
            }
            if (decodenamespace) {
                namespace = decodeIdentifyingStringOrIndex(vocabulary.namespaces);
            }
            _attnsbuffer.add(new NamespaceAttribute(prefix, namespace));
        }
        if (current_octet != (FastInfosetConstants.TERMINATION_PATTERN << 4)) {
            throw new FastInfosetException("Incorrect end of namespace attributes");
        }
        read();
        return _attnsbuffer;
    }
    
    protected List<NamespaceAttribute> decodeNSAttributesClone() throws IOException, FastInfosetException {
        return (List<NamespaceAttribute>)((ArrayList<NamespaceAttribute>)decodeNSAttributes()).clone();
    }
    
    protected Name_surrogate decodeNameSurrogateOnSeventhBit() throws IOException {
        Name_surrogate surrogate = new Name_surrogate(HashMapObjectInt.NO_INDEX, HashMapObjectInt.NO_INDEX, HashMapObjectInt.NO_INDEX);
        boolean decodeprefix = (current_octet & FastInfosetConstants.LITERAL_QNAME_PREFIX_FLAG) != 0;
        boolean decodenamespace = (current_octet & FastInfosetConstants.LITERAL_QNAME_NAMESPACE_NAME_FLAG) != 0;
        if (decodeprefix) {
            read();
            surrogate.prefix = decodeIndexOnSecondBit();
        }
        if (decodenamespace) {
            read();
            surrogate.namespace = decodeIndexOnSecondBit();
        }
        read();
        surrogate.localname = decodeIndexOnSecondBit();
        return surrogate;
    }

    protected void decodeNameSurrogateVocabularyTable(ArrayIndex<Name_surrogate> table) throws IOException {
        int len = decodeSequenceOfLength();
        for (int i = 0; i < len; i++) {
            read();
            table.add(decodeNameSurrogateOnSeventhBit());
        }
    }

    protected byte[] decodeNonEmptyOctetStringOnFifthBit() throws IOException {
        return decodeNonEmptyOctetString(5);
    }

    protected byte[] decodeNonEmptyOctetStringOnSeventhBit() throws IOException {
        return decodeNonEmptyOctetString(7);
    }

    protected void decodeNonEmptyOctetStringVocabularyTable(ArrayIndex<String> table) throws IOException {
        int len = decodeSequenceOfLength();
        for (int i = 0; i < len; i++) {
            read();
            table.add(decodeUTF8inInternalEncodingBufferAsString(decodeNonEmptyOctetStringOnSecondBit()));
        }
    }

    protected EncodedString decodeNonIdentifyingStringOrIndexOnFirstBit(ArrayIndex<String> table) throws IOException, FastInfosetException {
        read();
        
        if ((current_octet & FastInfosetConstants.IDENTIFYINGSTRINGORINDEX_INDEX) != 0) {
            String str = "";
            if (current_octet != 0xFF) { //no es la cadena vacia
                int index = decodeIndexOnSecondBit();
                str = table.get(index);
            }
            encodedStringBuffer.setUTF8(str);
            return encodedStringBuffer;
        } else {
            boolean addtotable = (current_octet & FastInfosetConstants.NONIDENTIFYINGSTRING_ADDTOTABLE) != 0;
            EncodedString str = decodeEncodedCharacterStringOnThirdbit();
            if (addtotable) {
                table.add(str.getString());
            }
            return str;
        }
    }

    protected EncodedString decodeNonIdentifyingStringOrIndexOnThirdBit(ArrayIndex<String> table) throws FastInfosetException, IOException {
        if ((current_octet & FastInfosetConstants.IDENTIFYINGSTRINGORINDEX_3RD_BIT_INDEX) != 0) {
            int index = decodeIndexOnFourthBit();
            String str = table.get(index);
            encodedStringBuffer.setUTF8(str);
            return encodedStringBuffer;
        } else {
            boolean addtotable = (current_octet & FastInfosetConstants.NONIDENTIFYINGSTRING_3RD_BIT_ADDTOTABLE) != 0;
            EncodedString str = decodeEncodedCharacterStringOnFifthbit();
            if (addtotable) {
                table.add(str.getString());
            }
            return str;
        }
    }

    protected int decodeOctetInteger(int startingBit) throws IOException {
        int result = (current_octet << (startingBit - 1)) & 255;
        read();
        return result |= current_octet >>> 8 - (startingBit - 1);
    }

    protected ProcessingInstruction decodeProcessingInstruction() throws IOException, FastInfosetException {
        String target = decodeIdentifyingStringOrIndex(vocabulary.other_ncnames);
        String data = decodeNonIdentifyingStringOrIndexOnFirstBit(vocabulary.other_strings).getString();
        return new ProcessingInstruction(target, data);
    }

    protected QName decodeQualifiedNameOrIndexOnSecondBit() throws IOException, FastInfosetException {
        if ((current_octet & (FastInfosetConstants.LITERAL_QUALIFIED_NAME_IDENTIFICATION_MASK << 1)) == (FastInfosetConstants.LITERAL_QUALIFIED_NAME_IDENTIFICATION << 1)) {
            return decodeLiteralQualifiedName(vocabulary.attributenames);
        } else {
            Name_surrogate surrogate = vocabulary.attributenames.get(decodeIndexOnSecondBit());
            return surrogateToQName(surrogate);
        }
    }

    protected QName decodeQualifiedNameOrIndexOnThirdBit() throws FastInfosetException, IOException {
        if ((current_octet & FastInfosetConstants.LITERAL_QUALIFIED_NAME_IDENTIFICATION_MASK) == FastInfosetConstants.LITERAL_QUALIFIED_NAME_IDENTIFICATION) {
            return decodeLiteralQualifiedName(vocabulary.elementnames);
        } else {
            Name_surrogate surrogate = vocabulary.elementnames.get(decodeIndexOnThirdBit());
            return surrogateToQName(surrogate);
        }
    }

    protected String decodeUnexpandedEntity() throws IOException, FastInfosetException {
        boolean decodesystemid = (FastInfosetConstants.DOCUMENT_TYPE_SYSTEMID_PRESENCE & current_octet) != 0;
        boolean decodepublicid = (FastInfosetConstants.DOCUMENT_TYPE_PUBLICID_PRESENCE & current_octet) != 0;
        String name = decodeIdentifyingStringOrIndex(vocabulary.other_ncnames);
        String systemid = null;
        String publicid = null;
        if (decodesystemid) {
            systemid = decodeIdentifyingStringOrIndex(vocabulary.other_uris);
        }
        if (decodepublicid) {
            publicid = decodeIdentifyingStringOrIndex(vocabulary.other_uris);
        }
        return name;
    }

    protected List<UnparsedEntity> decodeUnparsedEntities() throws IOException, FastInfosetException {
        List<UnparsedEntity> result = new ArrayList<UnparsedEntity>();
        while (((read() & FastInfosetConstants.UNPARSED_ENTITY_IDENTIFICATION_MASK) ^ FastInfosetConstants.UNPARSED_ENTITY_IDENTIFICATION) == 0) {
            UnparsedEntity unparsedEntity = decodeUnparsedEntity();
            result.add(unparsedEntity);
        }
        if (current_octet != (FastInfosetConstants.TERMINATION_PATTERN << 4)) {
            throw new FastInfosetException("Incorrect end of unparsed entities");
        }
        return result;
    }

    protected UnparsedEntity decodeUnparsedEntity() throws IOException, FastInfosetException {
        boolean decodepublicid = (FastInfosetConstants.DOCUMENT_TYPE_PUBLICID_PRESENCE & current_octet) != 0;
        String name = decodeIdentifyingStringOrIndex(vocabulary.other_ncnames);
        String systemid = null;
        String publicid = null;
        systemid = decodeIdentifyingStringOrIndex(vocabulary.other_uris);
        if (decodepublicid) {
            publicid = decodeIdentifyingStringOrIndex(vocabulary.other_uris);
        }
        String notationName = decodeIdentifyingStringOrIndex(vocabulary.other_ncnames);
        return new UnparsedEntity(name, systemid, publicid, notationName);
    }

    protected void decodeAlgorithmVocabularyTable(ArrayIndex<Algorithm> table) throws IOException {
        int len = decodeSequenceOfLength();
        for (int i = 0; i < len; i++) {
            read();
            String algorithmURI = decodeUTF8inInternalEncodingBufferAsString(decodeNonEmptyOctetStringOnSecondBit());
            table.add(registredAlgorithms.get(algorithmURI));
        }
    }

    protected void decodeAlphabetVocabularyTable(ArrayIndex<Alphabet> table) throws IOException {
        int len = decodeSequenceOfLength();
        for (int i = 0; i < len; i++) {
            read();
            table.add(new Alphabet(decodeUTF8inInternalEncodingBufferAsString(decodeNonEmptyOctetStringOnSecondBit())));
        }
    }

    protected QName surrogateToQName(Name_surrogate surrogate) throws FastInfosetException {
        String prefix = "";
        String namespace = "";
        String localname;
        if (surrogate.namespace != HashMapObjectInt.NO_INDEX) {
            if (surrogate.prefix != HashMapObjectInt.NO_INDEX) {
                prefix = vocabulary.prefixes.get(surrogate.prefix);
            }
            namespace = vocabulary.namespaces.get(surrogate.namespace);
        }
        localname = vocabulary.localnames.get(surrogate.localname);
        return new QName(namespace, localname, prefix);
    }

    private static class StringHolder {
        String theString;
    }
    private StringHolder _sh = new StringHolder();
    List<Attribute> _attbuffer = new ArrayList<Attribute>();
    
    protected List<Attribute> decodeAttributes() throws IOException, FastInfosetException {
        _attbuffer.clear();
        while ((read() >>> 4) != FastInfosetConstants.TERMINATION_PATTERN) {
            QName qname = decodeQualifiedNameOrIndexOnSecondBit();
            EncodedString value = decodeNonIdentifyingStringOrIndexOnFirstBit(vocabulary.attribute_values);
            if (value.type.equals(value.type.Algorithm))
                _attbuffer.add(new AlgorithmAttribute(qname, value.algorithm.objectFromByteArray(value.theData),value.algorithm));
            else
                _attbuffer.add(new Attribute(qname, value.getString()));
        }
        return _attbuffer;
    }

    protected EncodedString decodeCharacterChunk() throws FastInfosetException, IOException {
        return decodeNonIdentifyingStringOrIndexOnThirdBit(vocabulary.character_chunks);
    }

    protected String decodeComment() throws IOException, FastInfosetException {
        return decodeNonIdentifyingStringOrIndexOnFirstBit(vocabulary.other_strings).getString();
    }

    protected DocumentType decodeDocumentType() throws IOException, FastInfosetException {
        boolean decodesystemid = (FastInfosetConstants.DOCUMENT_TYPE_SYSTEMID_PRESENCE & current_octet) != 0;
        boolean decodepublicid = (FastInfosetConstants.DOCUMENT_TYPE_PUBLICID_PRESENCE & current_octet) != 0;
        String systemid = null;
        String publicid = null;
        if (decodesystemid) {
            systemid = decodeIdentifyingStringOrIndex(vocabulary.other_uris);
        }
        if (decodepublicid) {
            publicid = decodeIdentifyingStringOrIndex(vocabulary.other_uris);
        }
        List<ProcessingInstruction> pis = new ArrayList<ProcessingInstruction>();
        while (read() == FastInfosetConstants.PROCESSING_INSTRUCTION_IDENTIFICATION) {
            ProcessingInstruction pi = decodeProcessingInstruction();
            pis.add(pi);
        }
        if ((current_octet >>> 4) != FastInfosetConstants.TERMINATION_PATTERN) {
            throw new FastInfosetException("Incorrect end of processing instruction in document type declaration");
        }
        return new DocumentType(systemid, publicid, pis);
    }

    protected void reset() {
        vocabulary.reset();
        notations = null;
        unparsedEntities = null;
        additional_data = null;
        XMLEncoding = null;
        XMLVersion = null;
        IsStandalone = null;
        current_octet = 0;
        _terminate = false;
    }
}
