//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Alphabet;

import fastinfoset.Algorithm.EncodingAlgorithmException;

/**
 *
 * @author rpablos
 */
public class CharacterOutOfRangeException extends EncodingAlgorithmException {

    public CharacterOutOfRangeException(String message) {
        super(message);
    }
    
}
