//  Author: Ronald Pablos
//  Year: 2013

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
