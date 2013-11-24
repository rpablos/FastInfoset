//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Algorithm.Builtin;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;

/**
 *
 * @author rpablos
 */
public class LONG extends Number<Long> {
    static public int id = 4;
    static public LONG instance = new LONG();

    public LONG() {
        super.id = this.id;
    }

    
    @Override
    protected int getSizeInOctets() {
        return 8;
    }

    @Override
    protected Long valueOf(String str) {
        return Long.valueOf(str);
    }

    @Override
    public int ToByteArray(Long i, byte[] b, int start) {
        final long bits = i;
        b[start++] = (byte)((bits >>> 56) & 0xFF);
        b[start++] = (byte)((bits >>> 48) & 0xFF);
        b[start++] = (byte)((bits >>> 40) & 0xFF);
        b[start++] = (byte)((bits >>> 32) & 0xFF);
        b[start++] = (byte)((bits >>> 24) & 0xFF);
        b[start++] = (byte)((bits >>> 16) & 0xFF);
        b[start++] = (byte)((bits >>>  8) & 0xFF);
        b[start++] = (byte)(bits & 0xFF);
        return getSizeInOctets();
    }
    protected Object getArrayFromByteArray(byte[] data) {
        LongBuffer buffer = ByteBuffer.wrap(data).asLongBuffer();
        long[] numbers = new long[data.length/getSizeInOctets()];
        for (int i = 0; i < numbers.length; i++)
            numbers[i] = buffer.get();
        return numbers;
    }

    @Override
    protected String toString(Object number) {
        return Long.toString((Long)number);
    }
}
