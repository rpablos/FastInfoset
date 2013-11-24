//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Alphabet;

import fastinfoset.Algorithm.Algorithm;
import fastinfoset.Algorithm.EncodingAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author rpablos
 */
public class Alphabet extends Algorithm {
    int[] unicode;
    int[] sortedunicode;
    int[] indexes;
    
    int hash = 0;
    public Alphabet(String alphabet) {
        unicode = StringToUnicode(alphabet);
        Set<Integer> charset = new HashSet<Integer>(unicode.length);
        for (int i: unicode) {
            charset.add(i);
        }
        if (unicode.length != charset.size())
            throw new IllegalArgumentException("Repeated character in alphabet");
        
        sortedunicode = Arrays.copyOf(unicode, unicode.length);
        Arrays.sort(sortedunicode);
        indexes = new int[unicode.length];
        for ( int i = 0; i < unicode.length; i++) {
            int pos = Arrays.binarySearch(sortedunicode, unicode[i]);
            indexes[pos] = i;
        }
        
    }

    public int[] getUnicode() {
        return unicode;
    }

    @Override
    public String toString() {
        return new String(unicode, 0, unicode.length);
    }
    
    public int charPosition(int ch) {
        int pos = Arrays.binarySearch(sortedunicode, ch);
        return (pos < 0)?-1:indexes[pos]; 
    }
    
    static public int[] StringToUnicode(String str) {
        int len = str.length();
        int[] unicode = new int[str.codePointCount(0, len)];
        int cp;
        for (int i= 0,j=0; i<len; i += Character.charCount(cp)) {
            cp = str.codePointAt(i);
            unicode[j++] = cp;
        }
        return unicode;
    }

    @Override
    public int hashCode() {
        if (hash == 0)
            return hash = new String(unicode, 0, unicode.length).hashCode();
        else
            return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Alphabet))
            return false;
        return Arrays.equals(unicode, ((Alphabet)obj).unicode);
    }
    
    int getNumberofBitsPerChar() {
        int result = 1;
        while ( (1 << result) <= unicode.length )
            result++;
        return result;
    }
    public byte [] getEncodedOctetString (String str) throws CharacterOutOfRangeException {
        int [] uStr = StringToUnicode(str);
        int bitsPerChar = getNumberofBitsPerChar();
        int bits = uStr.length * bitsPerChar;
        int bitsOfLastOctet = bits % 8;
        int totalOctets = (bits + 7)/ 8;
        byte[] result = new byte[totalOctets];
        int indexresult = 0;
        int currentbit = 0;
        
        
        
        
        for (int i = 0; i < uStr.length; i++) {
            int pos = charPosition(uStr[i]);
            if (pos < 0)
                throw new CharacterOutOfRangeException("character "+uStr[i]+" out of alphabet range");
            int bitsAvailableInCurrentOctet = 8 - currentbit;
            int headingBits = Math.min(bitsAvailableInCurrentOctet, bitsPerChar);
            int trailingBits = (bitsPerChar-headingBits) % 8;
            int noctets = (bitsPerChar-headingBits) / 8;
           //heading bits
            result[indexresult] |= (( pos >>> (8*noctets+trailingBits) ) << (bitsAvailableInCurrentOctet-headingBits));
            currentbit = ((currentbit+headingBits) % 8);
            if(currentbit==0) {
                indexresult++;
            }
            //aligned octets
            for (; noctets > 0; noctets--) 
                result[indexresult++] = (byte)( (0xFF & (pos>>>((noctets-1)*8+trailingBits))) );
            //trailing bits
            if (trailingBits > 0) {
                result[indexresult] = (byte) (pos << (8-trailingBits));
                currentbit += trailingBits;
            }
            
        }
        if (bitsOfLastOctet > 0) {
            result[indexresult] |= (1 << (8 - bitsOfLastOctet)) - 1;
        }
        return result;
    }

    @Override
    public byte[] toByteArray(String str) throws CharacterOutOfRangeException {
        return getEncodedOctetString(str);
    }

    @Override
    public String fromByteArray(byte[] data) {
        int bitsPerChar = getNumberofBitsPerChar();
        int currentbit = 0;
        int currentoctet = 0;
        StringBuilder sb = new StringBuilder(data.length*8/bitsPerChar);
        int mask = ~(~0 << bitsPerChar);
        while (currentoctet < data.length) {
            if ((8-currentbit) >= bitsPerChar) {
                int index = (data[currentoctet] & (mask << ((8 - bitsPerChar)-currentbit))) >>> (8-bitsPerChar-currentbit);
                if (index == mask)
                    break; //fin
                sb.appendCodePoint(unicode[ (data[currentoctet] & (mask << ((8 - bitsPerChar)-currentbit))) >>> (8-bitsPerChar-currentbit)]);
                currentbit += bitsPerChar;
                if (currentbit == 8) {
                    currentbit = 0;
                    currentoctet++;
                }
            } else {
                int availablebits = 8-currentbit;
                int firstpart = ~(~0 << availablebits) & data[currentoctet];
                currentbit = 0;
                currentoctet++;
                if (currentoctet == data.length)
                    break;
                for (int i = 0; i < (bitsPerChar-availablebits)/8; i++) {
                    firstpart = (firstpart << 8) | (data[currentoctet++] & 0xFF);
                }
                int bitsinsecondpart = (bitsPerChar % 8) - availablebits;
                int secondpart = (~(~0 << bitsinsecondpart)  << (8-bitsinsecondpart)) & data[currentoctet]  ;
                currentbit +=bitsinsecondpart;
                sb.appendCodePoint(unicode[firstpart << bitsinsecondpart | (secondpart>>> (8-bitsinsecondpart))]);
            }
        }
        return sb.toString();
    }
    
    

    @Override
    public String getURI() {
        return null;
    }

    @Override
    public Object objectFromByteArray(byte[] data) throws EncodingAlgorithmException {
        return fromByteArray(data);
    }

    @Override
    public String stringFromObject(Object object) {
        return (String) object;
    }
}
