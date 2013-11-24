//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.util;

import fastinfoset.Algorithm.Algorithm;

/**
 *
 * @author rpablos
 */
public interface AllowIndexMap<K> {
     public boolean isInsertionAllowed(K str, Algorithm algo);
     public boolean isObtentionAllowed(Algorithm algo);
}
