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

package fastinfoset.util;

import fastinfoset.Algorithm.Algorithm;
import fastinfoset.Algorithm.EncodingAlgorithmException;

/**
 *
 * @author rpablos
 */
public class EncodedString {
    public enum Type {Algorithm,Alphabet, UTF8, UTF16};
    public Type type;
    public int AlgorithmIndex;
    public String theString;
    public byte [] theData;
    public Algorithm algorithm;

    
    public void setUTF8(String str) {
        type = Type.UTF8;
        theString = str;
        AlgorithmIndex = -1;
        theData = null;
    }
    public void setUTF16(String str) {
        type = Type.UTF16;
        theString = str;
        AlgorithmIndex = -1;
        theData = null;
    }
    public void setAlphabet(int index, Algorithm algorithm, String str) {
        type = Type.Alphabet;
        theString = str;
        AlgorithmIndex = index;
        theData = null;
        this.algorithm = algorithm;
    }
    public void setAlgorithm(int index, Algorithm algorithm, byte[] data) {
        type = Type.Algorithm;
        theString = null;
        AlgorithmIndex = index;
        theData = data;
        this.algorithm = algorithm;
    }
    public String getString() throws EncodingAlgorithmException {
        return (theString != null)?theString:(theString=algorithm.fromByteArray(theData));
    }
}
