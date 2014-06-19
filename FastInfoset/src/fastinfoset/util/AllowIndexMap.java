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

/**
 * Interface for controlling the indexing in vocabulary tables
 * 
 * @author rpablos
 * @param <K>
 */
public interface AllowIndexMap<K> {

    /**
     * Returns true if the the parameter <code>str</code> is permitted to be
     * indexed, when it is encoded using the algorithm specified.
     * @param str the value to be indexed
     * @param algo the algorithm used to encode the value. Could be null if no 
     * algorithm is used.
     * @return <code>true</code> if it is permitted the indexing.
     * <code>false</code> otherwise.
     */
    public boolean isInsertionAllowed(K str, Algorithm algo);

    /**
     * Returns true if it is permitted to obtain an already existing index, when
     * using the specified algorithm.
     * It is used to force literal encoding (no index encoding) when using a specified
     * algorithm, for example, CDATA.
     * 
     * @param algo the algorithm to be used to encode the value.
     * @return
     */
    public boolean isObtentionAllowed(Algorithm algo);
}
