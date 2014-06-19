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
import java.util.Arrays;

/**
 *
 * @author rpablos
 */
public class BASE64 extends Algorithm {
    static public int id = 1;
    static public BASE64 instance = new BASE64();
    private static final char encodeTableBase64[] = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };
    private static final int decodeTableBase64[] = {
        /*'+'*/ 62,
        -1, -1, -1,
        /*'/'*/ 63,
        /*'0'*/ 52,
        /*'1'*/ 53,
        /*'2'*/ 54,
        /*'3'*/ 55,
        /*'4'*/ 56,
        /*'5'*/ 57,
        /*'6'*/ 58,
        /*'7'*/ 59,
        /*'8'*/ 60,
        /*'9'*/ 61,
         -1,-1, -1, -1, -1, -1, -1,
        /*'A'*/ 0,
        /*'B'*/ 1,
        /*'C'*/ 2,
        /*'D'*/ 3,
        /*'E'*/ 4,
        /*'F'*/ 5,
        /*'G'*/ 6,
        /*'H'*/ 7,
        /*'I'*/ 8,
        /*'J'*/ 9,
        /*'K'*/ 10,
        /*'L'*/ 11,
        /*'M'*/ 12,
        /*'N'*/ 13,
        /*'O'*/ 14,
        /*'P'*/ 15,
        /*'Q'*/ 16,
        /*'R'*/ 17,
        /*'S'*/ 18,
        /*'T'*/ 19,
        /*'U'*/ 20,
        /*'V'*/ 21,
        /*'W'*/ 22,
        /*'X'*/ 23,
        /*'Y'*/ 24,
        /*'Z'*/ 25,
        -1, -1, -1, -1, -1, -1,
        /*'a'*/ 26,
        /*'b'*/ 27,
        /*'c'*/ 28,
        /*'d'*/ 29,
        /*'e'*/ 30,
        /*'f'*/ 31,
        /*'g'*/ 32,
        /*'h'*/ 33,
        /*'i'*/ 34,
        /*'j'*/ 35,
        /*'k'*/ 36,
        /*'l'*/ 37,
        /*'m'*/ 38,
        /*'n'*/ 39,
        /*'o'*/ 40,
        /*'p'*/ 41,
        /*'q'*/ 42,
        /*'r'*/ 43,
        /*'s'*/ 44,
        /*'t'*/ 45,
        /*'u'*/ 46,
        /*'v'*/ 47,
        /*'w'*/ 48,
        /*'x'*/ 49,
        /*'y'*/ 50,
        /*'z'*/ 51
    };

    @Override
    public byte[] toByteArray(String str) throws EncodingAlgorithmException {
        byte[] result = new byte[((str.length() +3) /4) *3]; // caso peor
        int i = 0, idx = 0;
        try {
            while( (i = advanceToNonSpace(str, i)) < str.length()) {
                int x1 = decodeTableBase64[str.charAt(i) - '+'];
                int x2 = decodeTableBase64[str.charAt(i = advanceToNonSpace(str, ++i)) - '+'];
                int x3 = decodeTableBase64[str.charAt(i = advanceToNonSpace(str, ++i)) - '+'];
                int x4 = decodeTableBase64[str.charAt(i = advanceToNonSpace(str, ++i)) - '+'];

                result[idx++] = (byte) ((x1 << 2) | (x2 >> 4));
                if (x3 >= 0)
                    result[idx++] = (byte) (((x2 & 0x0f) << 4) | (x3 >> 2));
                if (x4 >= 0) 
                    result[idx++] = (byte) (((x3 & 0x03) << 6) | x4);
                i++;
            }
        } catch (StringIndexOutOfBoundsException ex) {
            throw new EncodingAlgorithmException(ex);
        }
        return (result = Arrays.copyOf(result, idx));
    }
    private int advanceToNonSpace(String str, int offset) {
        for (; offset < str.length() && Character.isWhitespace(str.charAt(offset)); offset++) ;
        return offset;
    }

    @Override
    public String getURI() {
        return null;
    }

    @Override
    public String fromByteArray(byte[] data)  {
        StringBuilder result = new StringBuilder((data.length+2)/3*4);
        for (int i = 0; i < data.length; i += 3) {
            int b1 = data[i] & 0xFF;
            int b2 = (i+1 < data.length) ? data[i+1] & 0xFF : 0;
            int b3 = (i+2 < data.length) ? data[i+2] & 0xFF : 0;

            result.append(encodeTableBase64[b1 >> 2]);
            result.append(encodeTableBase64[((b1 & 0x03) << 4) | (b2 >> 4)]);
            result.append(encodeTableBase64[((b2 & 0x0f) << 2) | (b3 >> 6)]);
            result.append(encodeTableBase64[b3 & 0x3f]);
        }
        int partial = (3 - data.length %3) % 3;
        for (int i = 0; i < partial; i++)
            result.setCharAt(result.length()-1-i, '=');
        return result.toString();
    }

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
