//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Algorithm.Builtin;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;

/**
 *
 * @author rpablos
 */
public class DOUBLE extends Number<Double> {
    static public int id = 7;
    static public DOUBLE instance = new DOUBLE();

    public DOUBLE() {
        super.id = this.id;
    }

    
    @Override
    protected int getSizeInOctets() {
        return 8;
    }

    @Override
    protected Double valueOf(String str) {
        return Double.valueOf(str);
    }

    @Override
    public int ToByteArray(Double i, byte[] b, int start) {
        final long bits = Double.doubleToLongBits(i);
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
        DoubleBuffer buffer = ByteBuffer.wrap(data).asDoubleBuffer();
        double[] numbers = new double[data.length/getSizeInOctets()];
        for (int i = 0; i < numbers.length; i++)
            numbers[i] = buffer.get();
        return numbers;
    }

    @Override
    protected String toString(Object number) {
        return Double.toString((Double)number);
    }
}
