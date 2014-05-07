//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Alphabet;

/**
 * Alphabet for representing numeric character strings.
 * 
 * <p>This alphabet is "0123456789-+.e "
 * @author rpablos
 */
public class Numeric extends Alphabet {
    public static int id = 0;

    private Numeric() {
        super("0123456789-+.e ");
    }
    
    public static Numeric instance = new Numeric();
}
