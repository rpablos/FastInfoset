//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Algorithm;

import fastinfoset.Algorithm.Builtin.BASE64;
import fastinfoset.FastInfosetConstants;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Guard;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author rpablos
 */
public class DataURL extends Algorithm {

    static Pattern pattern = Pattern.compile("data:(?:(\\w*)/(\\w*))?((?:;[^;=]*=[^;=]*)*)?(;base64)?,(.*)");
    static final int MEDIA_TYPE_FLAG = 0x80;
    static final int BASE64_FLAG = 0x40;
    Charset utf8charset = Charset.forName("utf-8");
   
    @Override
    public byte[] toByteArray(String str) throws EncodingAlgorithmException {
        Matcher matcher = pattern.matcher(str);
        if (!matcher.matches())
            throw new EncodingAlgorithmException("Not a data URL format");
        String type = matcher.group(1);
        String subtype = matcher.group(2);
        String parameters = matcher.group(3);
        StringTokenizer st = new StringTokenizer(parameters,";");
        boolean base64encoding = matcher.group(4) != null;
        byte[] data = (base64encoding)?BASE64.instance.toByteArray(matcher.group(5)):
                                       EscapedDataToByteArray(matcher.group(5));
        byte[] result = new byte[0];
        int offset = 0;
        result = ensureByteArraySize(result, offset, 5);
        result[offset] |= ((type != null)?MEDIA_TYPE_FLAG:0) | (base64encoding?BASE64_FLAG:0);
        offset = encodeNonEmptyOctetStringLength(st.countTokens()+1, 3, result, offset);
        if (type != null) {
            result = ensureByteArraySize(result, offset, type.length()+5);
            byte[] string = type.getBytes(utf8charset);
            offset = encodeNonEmptyOctetString(string, 0, string.length, 1, result, offset);
            result = ensureByteArraySize(result, offset, subtype.length()+5);
            string = subtype.getBytes(utf8charset);
            offset = encodeNonEmptyOctetString(string, 0, string.length, 1, result, offset);
        }
        while (st.hasMoreTokens()) {
            String parameter = st.nextToken();
            result = ensureByteArraySize(result, offset, parameter.length()+5);
            byte[] string = parameter.getBytes(utf8charset);
            offset = encodeNonEmptyOctetString(string, 0, string.length, 1, result, offset);
        }
        result = ensureByteArraySize(result, offset, data.length+5);
        offset = encodeNonEmptyOctetString(data,0,data.length,1,result,offset);
                
        return Arrays.copyOf(result, offset);
    }

    private byte[] ensureByteArraySize(byte[] array,int offset,int length) {
        if ( (offset + length) > array.length)
            return Arrays.copyOf(array, offset+length);
        return array;
    }
    @Override
    public String getURI() {
        return "http://www.ietf.org/rfc/rfc2397";
    }
    
    private byte[] EscapedDataToByteArray(String escaped) {
        byte[] result = new byte[escaped.length()];
        int index = 0;
        for ( int i = 0; i < result.length; i++) {
            char ch = escaped.charAt(i);
            if (ch != '%')
                result[index++] = (byte) ch;
            else {
                result[index++] = (byte) Integer.parseInt(escaped.substring(i+1, i+3), 16);
                i += 2;
            }
        }
        return result = Arrays.copyOf(result, index);
    }
    
    private String ByteArrayToEscapedData(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length*2);
        for (byte b: data) {
            char c = (char) (b & 0xFF);
            if ( ((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z')) || ((c >= '0') && (c <= '9')) )
                sb.append(c);
            else {
                sb.append('%');
                String hexstr = Integer.toHexString((int)c);
                if (hexstr.length() < 2)
                    sb.append('0');
                sb.append(hexstr);
            }
        }
        return sb.toString();
    }

    @Override
    public String fromByteArray(byte[] data) throws EncodingAlgorithmException {
        StringBuilder result = new StringBuilder("data:");
        int offset = 0;
        boolean base64encoding = (data[offset] & BASE64_FLAG) != 0;
        boolean mediatype = (data[offset] & MEDIA_TYPE_FLAG) != 0;
        int[] parameter_length_offset = decodeNonEmptyOctetStringLength(3, data, offset);
        parameter_length_offset[1]--;
        offset = parameter_length_offset[0];
        if (mediatype) {
            OffsetAndByteArray oaba = decodeNonEmptyOctetString(1, data, offset);
            offset = oaba.offset;
            result.append(new String(oaba.data,utf8charset));
            oaba = decodeNonEmptyOctetString(1, data, offset);
            offset = oaba.offset;
            result.append('/');
            result.append(new String(oaba.data,utf8charset));
        }
        
        for (int i = 0; i < parameter_length_offset[1]; i++) {
            OffsetAndByteArray oaba = decodeNonEmptyOctetString(1, data, offset);
            offset = oaba.offset;
            result.append(';');
            result.append(new String(oaba.data,utf8charset));
        }
        if (base64encoding)
            result.append(";base64");
        OffsetAndByteArray oaba = decodeNonEmptyOctetString(1, data, offset);
        byte[] encodedData = oaba.data;
        result.append(',');
        result.append(base64encoding?BASE64.instance.fromByteArray(encodedData):ByteArrayToEscapedData(encodedData));
        return result.toString();
    }

    @Override
    public Object objectFromByteArray(byte[] data) throws EncodingAlgorithmException {
        String type = null, subtype = null;
        Map<String,String> parameters = new LinkedHashMap<String, String>();
        int offset = 0;
        boolean base64encoding = (data[offset] & BASE64_FLAG) != 0;
        boolean mediatype = (data[offset] & MEDIA_TYPE_FLAG) != 0;
        int[] parameter_length_offset = decodeNonEmptyOctetStringLength(3, data, offset);
        parameter_length_offset[1]--;
        offset = parameter_length_offset[0];
        if (mediatype) {
            OffsetAndByteArray oaba = decodeNonEmptyOctetString(1, data, offset);
            offset = oaba.offset;
            type = new String(oaba.data,utf8charset);
            oaba = decodeNonEmptyOctetString(1, data, offset);
            offset = oaba.offset;
            subtype = new String(oaba.data,utf8charset);
        }
        
        for (int i = 0; i < parameter_length_offset[1]; i++) {
            OffsetAndByteArray oaba = decodeNonEmptyOctetString(1, data, offset);
            offset = oaba.offset;
            String parameterstr = new String(oaba.data,utf8charset);
            int index = parameterstr.indexOf('=');
            parameters.put(parameterstr.substring(0, index),parameterstr.substring(index+1));
        }
        
        OffsetAndByteArray oaba = decodeNonEmptyOctetString(1, data, offset);
        byte[] octetdata = oaba.data;

        return new DataURLObject(type, subtype, parameters, base64encoding, octetdata);
    }

    @Override
    public String stringFromObject(Object object) {
        DataURLObject duo = (DataURLObject) object;
        StringBuilder result = new StringBuilder(50);
        result.append("data:");
        result.append((duo.getType() != null)?duo.getType()+"/"+duo.getSubtype():"");
        Map<String, String> parameters = duo.getParameters();
        if (parameters != null)
            for (Map.Entry<String,String> entry: parameters.entrySet()) {
                result.append(';');
                result.append(entry.getKey()+"="+entry.getValue());
            }
        if (duo.isBase64encoding())
            result.append(";base64");
        result.append(',');
        result.append(duo.isBase64encoding()?BASE64.instance.fromByteArray(duo.getData()):ByteArrayToEscapedData(duo.getData()));
       
        return result.toString();
    }
    
    protected int encodeNonEmptyOctetString(byte[] octet_string,int offset, int length,int startingBit,byte[] destination, int dstoffset)  {
        
        dstoffset=encodeNonEmptyOctetStringLength( length, startingBit, destination, dstoffset);
        //encode octet_string
        System.arraycopy(octet_string, offset, destination, dstoffset, length);
        return dstoffset+=length;
    }
    protected int encodeNonEmptyOctetStringLength( int length,int startingBit,byte[] destination, int dstoffset)  {
        
        // encode length
        int firstLimit =  1 + (1 << (8-startingBit));
        if (length < firstLimit) {
            destination[dstoffset++] |= (length -1);
        } else if (length < (firstLimit+256)) {
            destination[dstoffset++] |= FastInfosetConstants.OCTET_STRING_LENGTH_2_OCTETS_FLAG >>> (startingBit-1);
            destination[dstoffset++] = (byte) (length - firstLimit);
        } else {
            destination[dstoffset++] |= FastInfosetConstants.OCTET_STRING_LENGTH_3_OCTETS_FLAG >>> (startingBit-1);
            
            int len = length - (firstLimit+256);
            destination[dstoffset++] = (byte)(len >>> 24);
            destination[dstoffset++] = (byte)((len >> 16) & 0xFF);
            destination[dstoffset++] = (byte)((len>> 8) & 0xFF);
            destination[dstoffset++] = (byte)(len & 0xFF);
        }
        
        return dstoffset;
    }
    
    protected int[] decodeNonEmptyOctetStringLength(int startingBit,byte[] source, int offset)  {
        int length = 0;
        // decode length
        if (((1 << (8 - startingBit)) & source[offset]) == 0) {
            length = 1 + (source[offset++] & ((1 << (8 - startingBit)) - 1));
        } else if (((source[offset] << (startingBit - 1)) & 255) == FastInfosetConstants.OCTET_STRING_LENGTH_2_OCTETS_FLAG) {
            length = source[++offset] + 1 + (1 << (8 - startingBit));
        } else {
            length = ((source[++offset] & 0xFF) << 24);
            length |= ((source[++offset] & 0xFF) << 16);
            length |= ((source[++offset] & 0xFF) << 8);
            length |= (source[++offset] & 0xFF);
            length += 1 + (1 << (8 - startingBit)) + 256;
        }
        return new int[] {offset,length};
    }
    
    protected OffsetAndByteArray decodeNonEmptyOctetString(int startingBit, byte[] source, int offset)  {
        int[] offset_length = decodeNonEmptyOctetStringLength(startingBit, source, offset);
        offset = offset_length[0];
        //decode octet_string
        byte[] result = new byte[offset_length[1]];
        System.arraycopy(source, offset, result, 0, result.length);
        offset += result.length;
        return new OffsetAndByteArray(result,offset);
    }
    
    private class OffsetAndByteArray {
        byte[] data;
        int offset;

        public OffsetAndByteArray(byte[] data, int offset) {
            this.data = data;
            this.offset = offset;
        }
        
    }
    
    public static class DataURLObject {
        String type;
        String subtype;
        Map<String,String> parameters;
        boolean base64encoding;
        byte[] data;

        public DataURLObject(String type, String subtype, Map<String, String> parameters, boolean base64encoding, byte[] data) {
            this.type = type;
            this.subtype = subtype;
            this.parameters = parameters;
            this.base64encoding = base64encoding;
            this.data = data;
        }

        public boolean isBase64encoding() {
            return base64encoding;
        }

        public String getType() {
            return type;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public String getSubtype() {
            return subtype;
        }

        public byte[] getData() {
            return data;
        }
        
    }
}
