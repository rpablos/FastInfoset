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

import fastinfoset.FastInfosetConstants;
import fastinfoset.FastInfosetException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author rpablos
 */
public class ArrayIndex<T> implements Cloneable {
    public Object[] array;
    int size = 0;

    public ArrayIndex(int initialCapacity) {
        array = new Object[Math.max(1,initialCapacity)];
    }
    public ArrayIndex() {
        this(100);
    }
    
    public int add(T t) {
        if (size >= FastInfosetConstants.MAXIMUM_TABLE_ENTRIES)
            return HashMapObjectInt.NO_INDEX;
        if (size == array.length)
            resize(array.length * 2);
        array[size] = t;
        return size++;
    }
    
    public void addAll(List<T> list) {
        for (T t: list) {
            add(t);
        }
    }
    public T get(int index) throws FastInfosetException {
        try {
            return (T) array[index];
        } catch(java.lang.ArrayIndexOutOfBoundsException ex) {
            throw new fastinfoset.FastInfosetException(ex);
        }
    }
    
    public T getNullIfNotFound(int index) throws FastInfosetException {
        return (index >= 0 || index < size)?(T) array[index]:null;
    }

    public int getSize() {
        return size;
    }

    public void truncate(int size) {
        //array = Arrays.copyOf(array, size);
        this.size = size;
    }
    private void resize(int size) {
        array = Arrays.copyOf(array, size);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {  
        ArrayIndex<T> result =   (ArrayIndex<T>) super.clone();
        result.array = Arrays.copyOf(array, array.length);
        return result;
    }
    
    public void ensureCapacity(int capacity) {
        if (capacity <= array.length)
            return;
        resize(capacity);
    }
    
}
