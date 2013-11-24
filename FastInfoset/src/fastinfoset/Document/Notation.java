//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Document;

/**
 *
 * @author rpablos
 */
public class Notation {
    public String name;
    public String systemIdentifier;
    public String publicIdentifier;
    
   
    public Notation(String _name, String _systemIdentifier, String _publicIdentifier) {
        name = _name;
        systemIdentifier = _systemIdentifier;
        publicIdentifier = _publicIdentifier;
    }
}
