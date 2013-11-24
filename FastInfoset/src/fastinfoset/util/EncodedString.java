//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.util;

import fastinfoset.Algorithm.Algorithm;
import fastinfoset.Algorithm.EncodingAlgorithmException;
import fastinfoset.Alphabet.Alphabet;

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
        algorithm = algorithm;
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
