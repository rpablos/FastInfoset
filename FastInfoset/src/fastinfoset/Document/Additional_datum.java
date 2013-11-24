//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Document;

/**
 *
 * @author rpablos
 */
public class Additional_datum {
    String id;
    byte[] data;

    public Additional_datum(String id, byte[] data) {
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }
    
    
    
}
