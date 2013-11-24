//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Algorithm.Builtin;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

/**
 *
 * @author rpablos
 */
public class INT extends Number<Integer> {
    static public int id = 3;
    static public INT instance = new INT();

    public INT() {
        super.id = this.id;
    }

    
    @Override
    protected int getSizeInOctets() {
        return 4;
    }

    @Override
    protected Integer valueOf(String str) {
        return Integer.valueOf(str);
    }

    @Override
    public int ToByteArray(Integer i, byte[] b, int start) {
        final int bits = i;
        b[start++] = (byte)((bits >>> 24) & 0xFF);
        b[start++] = (byte)((bits >>> 16) & 0xFF);
        b[start++] = (byte)((bits >>>  8) & 0xFF);
        b[start++] = (byte)(bits & 0xFF);
        return getSizeInOctets();
    }
    
    protected Object getArrayFromByteArray(byte[] data) {
        IntBuffer buffer = ByteBuffer.wrap(data).asIntBuffer();
        int[] numbers = new int[data.length/getSizeInOctets()];
        for (int i = 0; i < numbers.length; i++)
            numbers[i] = buffer.get();
        return numbers;
    }

    @Override
    protected String toString(Object number) {
        return Integer.toString((Integer)number);
    }
}
