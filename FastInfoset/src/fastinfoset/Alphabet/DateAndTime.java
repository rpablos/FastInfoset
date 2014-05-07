//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Alphabet;

/**
 * Alphabet for representing dates and times.
 * 
 * <p>This alphabet is "0123456789-:TZ "
 * @author rpablos
 */
public class DateAndTime extends Alphabet {
    public static int id = 1;

    private DateAndTime() {
        super("0123456789-:TZ ");
    }
    
    public static DateAndTime instance = new DateAndTime();
}
