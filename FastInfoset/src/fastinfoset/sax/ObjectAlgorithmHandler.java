//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.sax;

import fastinfoset.Algorithm.Algorithm;
import org.xml.sax.SAXException;

/**
 *
 * @author rpablos
 */
public interface ObjectAlgorithmHandler {
    public void object(Object object, Algorithm algorithm) throws SAXException;
    /**
     * Receive notification of algorithm encoded object
     * 
     * Only called when the algorithm is not available or there is no URI
     * Otherwise, the object method is called
     * @param data encoded data
     * @param algorithmId index for the algorithm
     * @throws org.xml.sax.SAXException
     */
    public void object(byte[] data, int algorithmId) throws SAXException;
    
    /**
     * Receive notification of algorithm encoded object
     * 
     * Only called when the algorithm is not available but there exists a 
     * algorithm URI from the transmitted initial vocabulary
     * @param data encoded data
     * @param uri algorithm URI
     * @throws SAXException
     */
    public void object(byte[] data, String uri) throws SAXException;
}
