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
import java.nio.IntBuffer;

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
