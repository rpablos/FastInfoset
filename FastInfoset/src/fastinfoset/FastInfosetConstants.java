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

package fastinfoset;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author rpablos
 */
public class FastInfosetConstants {
    public static final byte[] FastInfosetIdentificacion = new byte[] { (byte)0xE0, 0};
    public static final byte[] FastInfosetVersion = new byte[] { 0, 1};
    
    public static final int DOCUMENT_ADDITIONAL_DATA_FLAG = 0x40; 
    public static final int DOCUMENT_INITIAL_VOCABULARY_FLAG = 0x20; 
    public static final int DOCUMENT_NOTATIONS_FLAG = 0x10; 
    public static final int DOCUMENT_UNPARSED_ENTITIES_FLAG = 0x08;
    public static final int DOCUMENT_CHARACTER_ENCODING_SCHEME_FLAG = 0x04;
    public static final int DOCUMENT_STANDALONE_FLAG = 0x02; 
    public static final int DOCUMENT_VERSION_FLAG = 0x01;
    
    
    public static final int ELEMENT_ATTRIBUTE_PRESENCE = 0x40; // 01000000
    public static final int ELEMENT_NAMESPACES_PRESENCE = 0x38; // 00111000
    
    public static final int NAMESPACE_ATTRIBUTE_IDENTIFICATION = 0xCC;
    public static final int NAMESPACE_ATTRIBUTE_PREFIX_FLAG = 0x02;
    public static final int NAMESPACE_ATTRIBUTE_NAMESPACE_FLAG = 0x01;
    
    public static final int INTEGER_3RD_BIT_2_OCTETS_FLAG = 0x20;
    public static final int INTEGER_3RD_BIT_3_OCTETS_FLAG = 0x28;
    public static final int INTEGER_3RD_BIT_4_OCTETS_FLAG = 0x30;
    public static final int INTEGER_2ND_BIT_2_OCTETS_FLAG = 0x40;
    public static final int INTEGER_2ND_BIT_3_OCTETS_FLAG = 0x60;
    public static final int INTEGER_4TH_BIT_2_OCTETS_FLAG = 0x10;
    public static final int INTEGER_4TH_BIT_3_OCTETS_FLAG = 0x14;
    public static final int INTEGER_4TH_BIT_4_OCTETS_FLAG = 0x18;
    
    public static final int OCTET_STRING_LENGTH_2ND_BIT_2_OCTETS_FLAG = 0x40;
    public static final int OCTET_STRING_LENGTH_2ND_BIT_3_OCTETS_FLAG = 0x60;
    public static final int OCTET_STRING_LENGTH_5TH_BIT_2_OCTETS_FLAG = 0x08;
    public static final int OCTET_STRING_LENGTH_5TH_BIT_3_OCTETS_FLAG = 0x0C;
    public static final int OCTET_STRING_LENGTH_7TH_BIT_2_OCTETS_FLAG = 0x02;
    public static final int OCTET_STRING_LENGTH_7TH_BIT_3_OCTETS_FLAG = 0x03;
    public static final int OCTET_STRING_LENGTH_2_OCTETS_FLAG = 0x80;
    public static final int OCTET_STRING_LENGTH_3_OCTETS_FLAG = 0xC0;
    
    public static final int MAXIMUM_TABLE_ENTRIES = 1 << 20;
    
    public static final int LITERAL_QUALIFIED_NAME_IDENTIFICATION = 0x3C;
    public static final int LITERAL_QNAME_NAMESPACE_NAME_FLAG = 0x01;
    public static final int LITERAL_QNAME_PREFIX_FLAG = 0x02;
    
    
    public static final int IDENTIFYINGSTRINGORINDEX_STRING = 0x00;
    public static final int IDENTIFYINGSTRINGORINDEX_INDEX = 0x80;
    public static final int IDENTIFYINGSTRINGORINDEX_3RD_BIT_INDEX = 0x20;
    
    public static final int NONIDENTIFYINGSTRING_ADDTOTABLE = 0x40;
    public static final int NONIDENTIFYINGSTRING_3RD_BIT_ADDTOTABLE = 0x10;
    
    public static final int ENCODED_CHARACTER_STRING_3RD_BIT_UTF8_DISCRIMINANT = 0x00;
    public static final int ENCODED_CHARACTER_STRING_3RD_BIT_UTF16_DISCRIMINANT = 0x10;
    public static final int ENCODED_CHARACTER_STRING_3RD_BIT_ALPHABET_DISCRIMINANT = 0x20;
    public static final int ENCODED_CHARACTER_STRING_3RD_BIT_ALGORITHM_DISCRIMINANT = 0x30;
    public static final int ENCODED_CHARACTER_STRING_3RD_BIT_MASK_DISCRIMINANT = 0x30;
    public static final int ENCODED_CHARACTER_STRING_5TH_BIT_UTF8_DISCRIMINANT = 0x00;
    public static final int ENCODED_CHARACTER_STRING_5TH_BIT_UTF16_DISCRIMINANT = 0x04;
    public static final int ENCODED_CHARACTER_STRING_5TH_BIT_ALPHABET_DISCRIMINANT = 0x08;
    public static final int ENCODED_CHARACTER_STRING_5TH_BIT_ALGORITHM_DISCRIMINANT = 0x0C;
    public static final int ENCODED_CHARACTER_STRING_5TH_BIT_MASK_DISCRIMINANT = 0x0C;
    
    public static final int CHARACTER_CHUNK_IDENTIFICATION = 0x80;
    public static final int COMMENT_IDENTIFICATION = 0xE2;
    public static final int PROCESSING_INSTRUCTION_IDENTIFICATION = 0xE1;
    public static final int DOCUMENT_TYPE_IDENTIFICATION = 0xC4;
    public static final int NOTATION_IDENTIFICATION = 0xC0;
    public static final int UNPARSED_ENTITY_IDENTIFICATION = 0xD0;
    public static final int UNEXPANDED_ENTITY_IDENTIFICATION = 0xC8;
    
    public static final int DOCUMENT_TYPE_SYSTEMID_PRESENCE = 0x02;
    public static final int DOCUMENT_TYPE_PUBLICID_PRESENCE = 0x01;
    
    public static final int TERMINATION_PATTERN = 0xf;
    
    public static final int INITIAL_VOCABULARY_EXTERNAL_VOCABULARY = 0x10;
    public static final int INITIAL_VOCABULARY_ALPHABETS = 0x08;
    public static final int INITIAL_VOCABULARY_ALGORITHMS = 0x04;
    public static final int INITIAL_VOCABULARY_PREFIXES = 0x02;
    public static final int INITIAL_VOCABULARY_NAMESPACES = 0x01;
    public static final int INITIAL_VOCABULARY_LOCALNAMES = 0x80;
    public static final int INITIAL_VOCABULARY_OTHER_NCNAMES = 0x40;
    public static final int INITIAL_VOCABULARY_OTHER_URIS = 0x20;
    public static final int INITIAL_VOCABULARY_ATTRIBUTE_VALUES = 0x10;
    public static final int INITIAL_VOCABULARY_CHARACTER_CHUNKS = 0x08;
    public static final int INITIAL_VOCABULARY_OTHER_STRINGS = 0x04;
    public static final int INITIAL_VOCABULARY_ELEMENT_NAMES = 0x02;
    public static final int INITIAL_VOCABULARY_ATTRIBUTE_NAMES = 0x01;
    
    public static  byte[][] XML_HEADER_DECLARATION = new byte[0][0];
    static  {
        try {        
            XML_HEADER_DECLARATION = new byte[][] {
             "<?xml encoding='finf'?>".getBytes("UTF-8"),
             "<?xml version='1.0' encoding='finf'?>".getBytes("UTF-8"),
             "<?xml version='1.1' encoding='finf'?>".getBytes("UTF-8"),
             "<?xml encoding='finf' standalone='no'?>".getBytes("UTF-8"),
             "<?xml encoding='finf' standalone='yes'?>".getBytes("UTF-8"),
             "<?xml version='1.0' encoding='finf' standalone='no'?>".getBytes("UTF-8"),
             "<?xml version='1.1' encoding='finf' standalone='no'?>".getBytes("UTF-8"),
             "<?xml version='1.0' encoding='finf' standalone='yes'?>".getBytes("UTF-8"),
             "<?xml version='1.1' encoding='finf' standalone='yes'?>".getBytes("UTF-8")
            };
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
       
    }
    public static final int NOTATION_IDENTIFICATION_MASK = 0xFC;
    public static final int UNPARSED_ENTITY_IDENTIFICATION_MASK = 0xFE;
    public static final int DOCUMENT_TYPE_IDENTIFICATION_MASK = 0xFC;
    public static final int ELEMENT_NAMESPACES_PRESENCE_MASK = 0x3C;
    public static final int NAMESPACE_ATTRIBUTE_IDENTIFICATION_MASK = 0xFC;
    public static final int LITERAL_QUALIFIED_NAME_IDENTIFICATION_MASK = 0x3C;
}
