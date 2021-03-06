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
import fastinfoset.FastInfosetConstants;
import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * @author rpablos
 */
public class IndexMap<K> extends HashMapObjectInt<K> implements Cloneable {
    int MAXIMUM_TABLE_ENTRIES = FastInfosetConstants.MAXIMUM_TABLE_ENTRIES;
    AllowIndexMap<K> allow = null;
    int index = 0;
  
    
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
    
    public void setAllowIndexMap(AllowIndexMap<K> allow) {
        this.allow = allow;
    }
    public AllowIndexMap<K> getAllowIndexMap(){
        return allow;
    }

    @Override
    public int get(Object key) {
        return get(key,null);
    }
    
    public int get(Object key, Algorithm algo) {
        if ((allow != null) && !allow.isObtentionAllowed(algo))
                return NO_INDEX;
        return super.get(key);
    }

    @Override
    public void clear() {
        super.clear();
        index  = 0;
    }

    @Override
    protected Object clone()  {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
           throw new RuntimeException(ex);
        }
    }
    
    
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
