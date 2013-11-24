//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Document;

import fastinfoset.Document.Notation;

/**
 *
 * @author rpablos
 */
public class UnparsedEntity extends Notation {
    public String notationName;
    
    public UnparsedEntity(String _name, String _systemIdentifier, String _publicIdentifier, String _notationName) {
        super(_name, _systemIdentifier, _publicIdentifier);
        notationName = _notationName;
    }
    
}
