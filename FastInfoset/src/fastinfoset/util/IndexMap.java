//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.util;

import fastinfoset.Algorithm.Algorithm;
import java.util.HashMap;
import java.util.LinkedHashMap;
import fastinfoset.FastInfosetConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rpablos
 */
public class IndexMap<K> extends /*HashMap<K, Integer>*/ HashMapObjectInt<K> implements Cloneable {
    int MAXIMUM_TABLE_ENTRIES = FastInfosetConstants.MAXIMUM_TABLE_ENTRIES;
    AllowIndexMap<K> allow = null;
    int index = 0;
    //IndexMap<K> initialIndexMap = null;
    
    public IndexMap(int initialCapacity) {
        super(initialCapacity);
    }
    public IndexMap(int initialCapacity, int maximum_size) {
        super(initialCapacity);
        MAXIMUM_TABLE_ENTRIES = maximum_size;
    }
    public int addNewIndexEntry(K key) {
        return addNewIndexEntry(key,null);
    }
    public int addNewIndexEntry(K key, Algorithm algo) {
        if ((size() >= MAXIMUM_TABLE_ENTRIES) || ((allow != null) && !allow.isInsertionAllowed(key,algo)))
            return NO_INDEX;
        int result;
        if ( (result=put(key, index,false)) == NOT_FOUND)
            result = index; 
        index++;
        return result;
    }
    
    public void setAllowInsertion(AllowIndexMap<K> allow) {
        this.allow = allow;
    }

//    void setInitialIndexMap(IndexMap<K> initialIndexMap) {
//        if (initialIndexMap.size() > 0) {
//            this.initialIndexMap = initialIndexMap;
//            this.index = initialIndexMap.index;
//        }
//    }

//    @Override
//    public Integer get(Object key) {
//        Integer result = null;
//        if (initialIndexMap != null)
//            result = initialIndexMap.get(key);
//        return (result != null)?result:super.get(key);
//    }
    @Override
    public int get(Object key) {
        return get(key,null);
    }
    
    public int get(Object key, Algorithm algo) {
//        int result = HashMapObjectInt.NOT_FOUND;
//        if (initialIndexMap != null)
//            result = initialIndexMap.get(key);
//        return (result != HashMapObjectInt.NOT_FOUND)?result:super.get(key);
        if ((allow != null) && !allow.isObtentionAllowed(algo))
                return NO_INDEX;
        return super.get(key);
    }

    @Override
    public void clear() {
        super.clear();
        index  = 0;
//        index = (initialIndexMap ==null)?0:initialIndexMap.index;
    }

    @Override
    protected Object clone()  {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
           throw new RuntimeException(ex);
        }
    }
    
    
//    public K[] getKeysInOrder(K[] k) {
//        Set<Map.Entry<K,Integer>> entries = entrySet();
//        Map.Entry<K,Integer>[] list = entries.toArray(new Map.Entry[0]);
//        Arrays.sort(list, new Comparator<Map.Entry<K,Integer>>() {
//
//            @Override
//            public int compare(Map.Entry<K, Integer> o1, Map.Entry<K, Integer> o2) {
//                return o1.getValue().intValue() - o2.getValue().intValue();
//            }
//        });
//        K[] result = (k.length >= list.length)?
//                k:(K[]) java.lang.reflect.Array.newInstance(k.getClass().getComponentType(), list.length);
//        int i;
//        for (i = 0; i< list.length; i++)
//            result[i] = list[i].getKey();
//        if (i < result.length)
//            result[i] = null;
//        return result;
//    }
    public K[] getKeysInOrder(K[] k) {
        return getKeysInOrder(k,0);
    }
    public K[] getKeysInOrder(K[] k, int startindex) {
        HashMapObjectInt.Entry<K>[] entries = getEntries();
        Arrays.sort(entries, new Comparator<HashMapObjectInt.Entry<K>>() {

            @Override
            public int compare(HashMapObjectInt.Entry<K> o1, HashMapObjectInt.Entry<K> o2) {
                return o1.getInt() - o2.getInt();
            }
        });
        if ( startindex >0) {
            int j = 0;
            for (j = 0; j < entries.length && entries[j].getInt() < startindex; j++)
                ;
            entries = Arrays.copyOfRange(entries, j, entries.length);
        }
        K[] result = (k.length >= entries.length)?
                k:(K[]) java.lang.reflect.Array.newInstance(k.getClass().getComponentType(), entries.length);
        int i;
        for (i = 0; i< entries.length; i++)
            result[i] = entries[i].getKey();
        if (i < result.length)
            result[i] = null;
        return result;
    }
}
