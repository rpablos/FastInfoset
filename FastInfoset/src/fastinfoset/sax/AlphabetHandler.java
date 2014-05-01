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
    public void alphabet(String str, Alphabet alphabet) throws SAXException;
}
