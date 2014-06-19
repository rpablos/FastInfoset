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

/**
 * Algorithm for transforming a <code>byte[]</code> into its hexadecimal string 
 * representation and viceversa.
 * @author rpablos
 */
public class HEXADECIMAL extends Algorithm {
    static public int id = 0;
    
    
    @Override
    public byte[] toByteArray(String str) throws EncodingAlgorithmException {
        int length = str.length();
        if ((str.length() & 1) != 0)
            throw new EncodingAlgorithmException("not even number of characters");
        byte[] result = new byte[length/2];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < 2; j++) {
                char ch = str.charAt(i*2+j);
                int index = ch - '0';
                if (index >= 10 || index < 0) {
                    index = ch- 'A' +10;        
                    if (index >= 16 || index < 10) 
                        throw new EncodingAlgorithmException("Not a hex string");
                }
                result[i] |= (byte) (index << ((j==0)?4:0));
            }
        }
        return result;
    }

    @Override
    public String fromByteArray(byte[] data) {
        final String HEX = "0123456789ABCDEF";
        StringBuilder sb = new StringBuilder(data.length*2);
        for (byte b: data) {
            sb.append(HEX.charAt((b >> 4) & 0xf));
            sb.append(HEX.charAt(b  & 0xf));
        }
        return sb.toString();
    }

    @Override
    public String getURI() {
        return null;
    }
    
    static public HEXADECIMAL instance = new HEXADECIMAL();

    @Override
    public Object objectFromByteArray(byte[] data) throws EncodingAlgorithmException {
        return data;
    }

    @Override
    public byte[] toByteArray(Object object) throws EncodingAlgorithmException {
        return (byte[]) object;
    }

    @Override
    public String stringFromObject(Object object) {
        return fromByteArray((byte[])object);
    }
    
}
