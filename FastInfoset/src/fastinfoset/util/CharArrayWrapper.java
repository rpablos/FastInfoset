//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.util;



public class CharArrayWrapper implements CharSequence {
    protected char[] array;
    protected int offset;
    protected int length;

    public CharArrayWrapper(char[] array, int offset, int length) {
        this.array = array;
        this.offset = offset;
        this.length = length;
    }
    
    @Override
    public int length() {
        return length;
    }

    @Override
    public char charAt(int index) {
        return array[offset+index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new CharArrayWrapper(array, offset+start, end-start);
    }

    @Override
    public String toString() {
        return new String(array,offset,length);
    }
    
}
