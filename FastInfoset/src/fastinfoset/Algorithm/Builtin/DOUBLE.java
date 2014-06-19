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

package fastinfoset.Algorithm.Builtin;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

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
