/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
