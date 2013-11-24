//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Algorithm.Builtin;

import fastinfoset.Algorithm.Algorithm;
import fastinfoset.Algorithm.Algorithm;
import fastinfoset.Algorithm.EncodingAlgorithmException;
import java.nio.charset.Charset;

/**
 *
 * @author rpablos
 */
public class CDATA extends Algorithm {
    static Charset utf8CharSet = Charset.forName("utf-8");
    static public int id = 9;
    @Override
    public byte[] toByteArray(String str) {
        return str.getBytes(utf8CharSet);
    }

    @Override
    public String fromByteArray(byte[] data) {
        return new String(data,utf8CharSet);
    }
    
    @Override
    public String getURI() {
        return null;
    }

    @Override
    public int hashCode() {
        return id;
    }
    
    static public CDATA instance = new CDATA();

    @Override
    public Object objectFromByteArray(byte[] data)  {
        return fromByteArray(data);
    }

    @Override
    public String stringFromObject(Object object) {
        return (String) object;
    }
    
    
}
