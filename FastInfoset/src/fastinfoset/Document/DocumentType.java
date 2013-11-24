//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Document;

import java.util.List;

/**
 *
 * @author rpablos
 */
public class DocumentType {
    public String systemIdentifier;
    public String publicIdentifier;
    public List<ProcessingInstruction> instructions;

    public DocumentType(String systemIdentifier, String publicIdentifier, List<ProcessingInstruction> instructions) {
        this.systemIdentifier = systemIdentifier;
        this.publicIdentifier = publicIdentifier;
        this.instructions = instructions;
    }
    
}
