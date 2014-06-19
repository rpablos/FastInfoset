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
import java.nio.LongBuffer;

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
