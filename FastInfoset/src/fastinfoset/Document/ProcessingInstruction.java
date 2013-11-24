//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Document;

/**
 *
 * @author rpablos
 */
public class ProcessingInstruction {
    public String target;
    public String content;

    public ProcessingInstruction(String target, String content) {
        this.target = target;
        this.content = content;
    }
    
    
}
