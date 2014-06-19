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

import fastinfoset.Algorithm.EncodingAlgorithmException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rpablos
 */
public class BOOLEAN extends Number<Boolean> {
    static public int id = 5;
    static public BOOLEAN instance = new BOOLEAN();

    public BOOLEAN() {
        super.id = this.id;
    }

    
    @Override
    protected int getSizeInOctets() {
        return 1;
    }

    @Override
    protected Boolean valueOf(String str) {
        return Boolean.valueOf(str);
    }

    @Override
    public int ToByteArray(Boolean i, byte[] b, int start) {
        b[start++] = (byte)((3 <<  4) | (i.booleanValue()?1<<3:0));
        return 1;
    }
    
    @Override
    public int listToByteArray(List<Boolean> list, byte[] b, int start){
        int unusedbits = (8 - ((list.size()+4) % 8)) % 8;
        b[start] = (byte) (unusedbits << 4);
        int currentbit = 4;
        for (Boolean v: list) {
            b[start] |= v.booleanValue()?1<<(7-currentbit):0;
            if (++currentbit > 7) {
                currentbit = 0;
                start++;
            }
        }
        return ((list.size()+4)+7)/8;
    }
    @Override
    protected byte[] listToByteArray(List<Boolean> numbers) {
        byte[] b = new byte[((numbers.size()+4)+7)/8];
        listToByteArray(numbers, b, 0);
        return b;
    }
    
    protected Object getArrayFromByteArray(byte[] data) throws EncodingAlgorithmException {
        if (data.length == 0)
            throw new EncodingAlgorithmException("No data in boolean algorithm coding");
        int unusedbits = (data[0] >> 4) & 0xF;
        if ( (unusedbits > 7) || ((unusedbits > 3) && (data.length == 1)))
            throw new EncodingAlgorithmException("Illegal length of unused bits in boolean coding");
        int currentbit = 4;
        int start, j;
        boolean[] result = new boolean[data.length*8-4 - unusedbits];
        for (start = 0, j  = 0; j < result.length; ) {
            result[j++] = (data[start] & (1<<(7-currentbit))) != 0;
            if (++currentbit > 7) {
                currentbit = 0;
                start++;
            }
        }
        return result;
    }

    @Override
    public Object objectFromByteArray(byte[] data) throws EncodingAlgorithmException {
        return getArrayFromByteArray(data);
    }

    @Override
    protected String toString(Object number) {
        return Boolean.toString((Boolean)number);
    }

    @Override
    protected byte[] getByteArrayFromArray(Object array) {
        int len = Array.getLength(array);
        List<Boolean> barray = new ArrayList<Boolean>(len);
        for (int i = 0; i < len; i++)
            barray.add((Boolean)Array.get(array,i));
        return listToByteArray(barray);
    }
    
}
