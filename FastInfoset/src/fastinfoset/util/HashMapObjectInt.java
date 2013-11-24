//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.util;

import java.util.Arrays;

/**
 *
 * @author rpablos
 */
public class HashMapObjectInt<K> implements Cloneable {

    static int primos[] = {Integer.MAX_VALUE,
        5,11,23,47,97,197,263,397,797,929,1117,1597,2309,3203,4783,6421,9901,12853,
        20507,25717,35437,51437,73459,102877,205759,411527,823117,1646237,3292489,
        6584983,13169977,26339969,52679969,105359939,210719881,421439783,842879579,1685759167
    }; 
    static {
        Arrays.sort(primos);
    }
    public interface Entry<K> {
        public K getKey();
        public int getInt();
    }
    static private class EntryObjectInt<K> implements Entry<K> {
        K key;
        int value;
        EntryObjectInt<K> next;
        int hash;
        public EntryObjectInt(K key, int value, EntryObjectInt<K> next, int hash) {
            this.key = key;
            this.value = value;
            this.next = next;
            this.hash = hash;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public int getInt() {
            return value;
        }
        
    } 
    EntryObjectInt<K>[] table;
    int size = 0;
    int threshold;
    float loadfactor = 0.75f;
    
    public HashMapObjectInt() {
        this(512);
    }

    
    public HashMapObjectInt(int initialCapacity) {
        table = new EntryObjectInt[findProperCapacity(initialCapacity)];
        threshold = (int)(table.length * loadfactor);
    }
    private int findProperCapacity(int capacity) {
        int i = Arrays.binarySearch(primos, capacity);
        if (i<0) {
            i = -i -1; 
        }
        return primos[i];
    }
    public static int NOT_FOUND = -1;
    public static int NO_INDEX = NOT_FOUND;
    
    public int get(Object key) {
        int hash = key.hashCode();
        final int tableIndex = indexFor(hash,table.length);
     
        for (EntryObjectInt<K> e = table[tableIndex]; e != null; e = e.next) {
            if (e.hash == hash && (key == e.key || key.equals(e.key))) {
                return e.value;
            }
        }
        
        return NOT_FOUND;
    }
    
    public int put(K key, int value) {
        return put(key, value, true);
    }
    public int put(K key, int value, boolean replace) {
        int hash = key.hashCode();
        int i = indexFor(hash,table.length);
        for (EntryObjectInt<K> e = table[i]; e != null; e = e.next) {
            if (e.hash == hash && (e.key == key || key.equals(e.key))) {
                int oldValue = e.value;
                if (replace) 
                    e.value = value;
                return oldValue;
            }
        }

        
        addEntry(hash, key, value, i);
        return NOT_FOUND;
    }

    public int size() {
        return size;
    }
    public void clear() {
        EntryObjectInt<K>[] tab = table;
        for (int i = 0; i < tab.length; i++)
            tab[i] = null;
        size = 0;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        HashMapObjectInt<K> result =   (HashMapObjectInt<K>) super.clone();
        result.table = Arrays.copyOf(table, table.length);
        return result;
    }
    
    
    public Entry<K>[] getEntries(){
        EntryObjectInt<K> next = null;
        int index = 0,index2 = 0;
        EntryObjectInt<K>[] t = table;
        Entry<K>[] result = new Entry [size];
        while (index < t.length) {
            if (next == null)
                while (index < t.length && (next = t[index++]) == null)
                    ;
            while (next != null) {
                result [index2++] = next;
                next = next.next;
            }
        }
        return result;
    }
    
    private int indexFor(int hash, int modulo) {
//        int index = hash % modulo;
//        return (index<0)?-index:index;
        return (hash & 0x7FFFFFFF) % modulo;
    }
    protected void addEntry(int hash, K key, int value, int bucketIndex) {
	EntryObjectInt<K> e = table[bucketIndex];
        table[bucketIndex] = new EntryObjectInt<K>(key, value, e, hash);
        
        if (size++ >= threshold)
            resize(findProperCapacity((int)Math.min((long)Integer.MAX_VALUE, (long)table.length << 1)));
    }
    
      
    protected final void resize(int newCapacity) {
        
        EntryObjectInt<K>[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == Integer.MAX_VALUE) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        EntryObjectInt<K>[] newTable = new EntryObjectInt[newCapacity];
        transfer(newTable);
        table = newTable;
        threshold = (int)(table.length * loadfactor);        
    }
    
    private final void transfer(EntryObjectInt<K>[] newTable) {
        EntryObjectInt<K>[] src = table;
        int newCapacity = newTable.length;
        for (int j = 0; j < src.length; j++) {
            EntryObjectInt<K> e = src[j];
            if (e != null) {
                src[j] = null;
                do {
                    EntryObjectInt<K> next = e.next;
                    int i = indexFor(e.hash, newCapacity);  
                    e.next = newTable[i];
                    newTable[i] = e;
                    e = next;
                } while (e != null);
            }
        }
    }
}
