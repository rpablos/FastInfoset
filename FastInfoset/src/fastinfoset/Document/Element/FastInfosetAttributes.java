//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Document.Element;

import java.util.List;

/**
 *
 * @author rpablos
 */
public interface FastInfosetAttributes {
    public boolean isAlgorithmEncodedAttribute(int index); 
    public Object getAlgorithmObject(int index);
    public List<Attribute> getAttributes();
}
