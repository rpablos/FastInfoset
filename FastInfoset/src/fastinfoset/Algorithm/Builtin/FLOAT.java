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
import java.nio.FloatBuffer;

/**
 *
 * @author rpablos
 */
public class FLOAT extends Number<Float> {
    static public int id = 6;
    static public FLOAT instance = new FLOAT();

    public FLOAT() {
        super.id = this.id;
    }

    
    @Override
    protected int getSizeInOctets() {
        return 4;
    }

    @Override
    protected Float valueOf(String str) {
        return Float.valueOf(str);
    }

    @Override
    public int ToByteArray(Float i, byte[] b, int start) {
        final int bits = Float.floatToIntBits(i);
        b[start++] = (byte)((bits >>> 24) & 0xFF);
        b[start++] = (byte)((bits >>> 16) & 0xFF);
        b[start++] = (byte)((bits >>>  8) & 0xFF);
        b[start++] = (byte)(bits & 0xFF);
        return getSizeInOctets();
    }
    protected Object getArrayFromByteArray(byte[] data) {
        FloatBuffer buffer = ByteBuffer.wrap(data).asFloatBuffer();
        float[] numbers = new float[data.length/getSizeInOctets()];
        for (int i = 0; i < numbers.length; i++)
            numbers[i] = buffer.get();
        return numbers;
    }

    @Override
    protected String toString(Object number) {
        return Float.toString((Float)number);
    }
}
