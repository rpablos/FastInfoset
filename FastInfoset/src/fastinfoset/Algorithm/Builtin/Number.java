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

import fastinfoset.Algorithm.Algorithm;
import fastinfoset.Algorithm.EncodingAlgorithmException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author rpablos
 */
public abstract class Number<T> extends Algorithm {
    int id;
    
    @Override
    public String getURI() {
        return null;
    }

    @Override
    public int hashCode() {
        return id;
    }
    
    public void toOutputStream(String str, OutputStream out) throws IOException {
        ArrayList<T> ints = getList(str);
        for (T t: ints) {
            toOutputStream(t, out);
        }
    }

    @Override
    public byte[] toByteArray(String str) {
        ArrayList<T> ints = getList(str);
        return listToByteArray(ints);
    }
    private ArrayList<T> getList(String str) {
        ArrayList<T> ints = new ArrayList<T>();
        int start = 0;
        int index = start;
        while (start < str.length()) {
            index = str.indexOf(" ",start);
            if (index == -1)
                index = str.length();
            ints.add(valueOf(str.substring(start, index)));
            start = index+1;
        }
        return ints;
    }

    

    
    public int listToByteArray(List<T> list, byte[] b, int start){
        int pos = start;
        for (T i: list) {
            pos += ToByteArray(i, b, pos);
        }
        return pos-start;
    }
    protected byte[] listToByteArray(List<T> numbers) {
        byte[] b = new byte[numbers.size()*getSizeInOctets()];
        listToByteArray(numbers, b, 0);
        return b;
    }

    protected abstract int getSizeInOctets();
    protected abstract T valueOf(String str);
    public abstract int ToByteArray(T i, byte[] b, int start);
    protected abstract Object getArrayFromByteArray(byte[] data) throws EncodingAlgorithmException;
   
    protected abstract String toString(Object number);
    
    public int ToByteArray(T[] a, byte[] b, int start) {
        return listToByteArray(Arrays.asList(a), b, start);
    }
    public void toOutputStream(T t, OutputStream out) throws IOException {
        byte[] b = new byte[getSizeInOctets()];
        ToByteArray(t, b, 0);
        out.write(b);
    }
    
    @Override
    public String fromByteArray(byte[] data) throws EncodingAlgorithmException {
        StringBuilder sb = new StringBuilder(data.length/4*8);
        Object numbers = getArrayFromByteArray(data);
        for (int i = 0; i < Array.getLength(numbers); i++) {
            sb.append(toString(Array.get(numbers, i)));
            sb.append((i < (Array.getLength(numbers)-1))?" ":"");
        }
        return sb.toString();
    }

    @Override
    public Object objectFromByteArray(byte[] data) throws EncodingAlgorithmException {
        if (data.length % getSizeInOctets() != 0)
            throw new EncodingAlgorithmException("Incorrect data length: "+data.length);
        return getArrayFromByteArray(data);
    }

    @Override
    public byte[] toByteArray(Object object) throws EncodingAlgorithmException {
        return getByteArrayFromArray(object);
    }

    @Override
    public String stringFromObject(Object numbers) {
        StringBuilder sb = new StringBuilder(Array.getLength(numbers)*4);
        for (int i = 0; i < Array.getLength(numbers); i++) {
            sb.append(toString(Array.get(numbers, i)));
            sb.append((i < (Array.getLength(numbers)-1))?" ":"");
        }
        return sb.toString();
    }
     protected byte[] getByteArrayFromArray(Object array){
         int length = Array.getLength(array);
         int size = getSizeInOctets();
         byte[] result = new byte[size*length];
         for (int i = 0; i < length; i++) {
             ToByteArray((T)Array.get(array, i), result, i*size);
         }
         return result;
     }
}
