//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.sax;

import fastinfoset.Alphabet.Alphabet;
import org.xml.sax.SAXException;

/**
 *
 * @author rpablos
 */
public interface AlphabetHandler {

    /**
     *
     * Receive notification of alphabet encoded string
     * 
     * @param str the string encoded with alphabet
     * @param alphabet the alphabet used for the encoding of the string
     * @throws SAXException
     */
    public void alphabet(String str, Alphabet alphabet) throws SAXException;
}
