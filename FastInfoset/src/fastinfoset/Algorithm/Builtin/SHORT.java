//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Algorithm.Builtin;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

/**
 *
 * @author rpablos
 */
public class SHORT extends Number<Short> {
    static public int id = 2;
    static public SHORT instance = new SHORT();

    public SHORT() {
        super.id = this.id;
    }

    
    @Override
    protected int getSizeInOctets() {
        return 2;
    }

    @Override
    protected Short valueOf(String str) {
        return Short.valueOf(str);
    }

    @Override
    public int ToByteArray(Short i, byte[] b, int start) {
        final short bits = i;
        b[start++] = (byte)((bits >>>  8) & 0xFF);
        b[start++] = (byte)(bits & 0xFF);
        return getSizeInOctets();
    }
    protected Object getArrayFromByteArray(byte[] data) {
        ShortBuffer buffer = ByteBuffer.wrap(data).asShortBuffer();
        short[] numbers = new short[data.length/getSizeInOctets()];
        for (int i = 0; i < numbers.length; i++)
            numbers[i] = buffer.get();
        return numbers;
    }

    @Override
    protected String toString(Object number) {
        return Short.toString((Short)number);
    }
}
