//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Algorithm.Builtin;

import fastinfoset.Algorithm.Algorithm;
import fastinfoset.Algorithm.EncodingAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rpablos
 */
public class UUID extends  Algorithm {
    static public int id = 8;
    static public UUID instance = new UUID();

    @Override
    public byte[] toByteArray(String str) throws EncodingAlgorithmException {
        ArrayList<String> words = getList(str);
        byte[] result = new byte[16*words.size()];
        for (int i = 0; i < result.length; i += 16) {
            String word = words.get(i);
            if (word.length() != 36)
                throw new EncodingAlgorithmException("Incorrect uuid format");

            System.arraycopy(HEXADECIMAL.instance.toByteArray(word.substring(0, 8).toUpperCase()), 0,result,0+i,4);
            System.arraycopy(HEXADECIMAL.instance.toByteArray(word.substring(9, 13).toUpperCase()), 0,result,4+i,2);
            System.arraycopy(HEXADECIMAL.instance.toByteArray(word.substring(14, 18).toUpperCase()), 0,result,6+i,2);
            System.arraycopy(HEXADECIMAL.instance.toByteArray(word.substring(19, 23).toUpperCase()), 0,result,8+i,2);
            System.arraycopy(HEXADECIMAL.instance.toByteArray(word.substring(24).toUpperCase()), 0,result,10+i,6);
        }
        return result;
    }
    
    private ArrayList<String> getList(String str) {
        ArrayList<String> words = new ArrayList<String>();
        int start = 0;
        int index = start;
        while (start < str.length()) {
            index = str.indexOf(" ",start);
            if (index == -1)
                index = str.length();
            words.add(str.substring(start, index));
            start = index+1;
        }
        return words;
    }

    @Override
    public String fromByteArray(byte[] data) throws EncodingAlgorithmException {
        if (data.length % 16 != 0)
            throw new EncodingAlgorithmException("not list of uuids");
        StringBuilder sb = new StringBuilder(36*data.length/16);
        for (int i = 0; i < data.length; i +=16) {
            sb.append((i == 0)?"":" ");
            sb.append(hexString(data, i, 4));
            sb.append('-');
            sb.append(hexString(data, i+4, 2));
            sb.append('-');
            sb.append(hexString(data, i+6, 2));
            sb.append('-');
            sb.append(hexString(data, i+8, 2));
            sb.append('-');
            sb.append(hexString(data, i+10, 6));
        }
        return sb.toString();
    }
    
    private String hexString(byte[] data,int offset, int length) {
        final String HEX = "0123456789abcdef";
        StringBuilder sb = new StringBuilder(length*2);
        for (int i =0 ; i < length; i++) {
            int b = data[i+offset];
            sb.append(HEX.charAt((b >> 4) & 0xf));
            sb.append(HEX.charAt(b  & 0xf));
        }
        return sb.toString();
    }
    @Override
    public String getURI() {
        return null;
    }

    @Override
    public Object objectFromByteArray(byte[] data) throws EncodingAlgorithmException {
        if (data.length % 16 != 0)
            throw new EncodingAlgorithmException("not list of uuids");
        return data;
    }

    @Override
    public String stringFromObject(Object object) {
        try {
            return fromByteArray((byte[])object);
        } catch (EncodingAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
