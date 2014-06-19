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

package fastinfoset.sax;

import fastinfoset.Algorithm.Algorithm;
import org.xml.sax.SAXException;

/**
 *
 * @author rpablos
 */
public interface ObjectAlgorithmHandler {

    /**
     * Receive notification of algorithm encoded object
     * 
     * @param object the object
     * @param algorithm the algorithm
     * @throws SAXException
     */
    public void object(Object object, Algorithm algorithm) throws SAXException;
    /**
     * Receive notification of algorithm encoded object
     * 
     * Only called when the algorithm is not available or there is no URI
     * Otherwise, other <code>object</code> method is called
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
