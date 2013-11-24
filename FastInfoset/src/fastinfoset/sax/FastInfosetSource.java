//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.sax;

import fastinfoset.Decoder;
import fastinfoset.SAX_FI_Decoder;
import java.io.InputStream;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 *
 * @author rpablos
 */
public class FastInfosetSource extends SAXSource {
   
    public FastInfosetSource(InputStream inputStream) {
        super(new InputSource(inputStream));
    }

    @Override
    public XMLReader getXMLReader() {
        XMLReader reader = super.getXMLReader();
        if (reader == null) {
            reader = new SAX_FI_Decoder();
            setXMLReader(reader);
        }
        return reader;
    }
    
    public Decoder getFastInfosetDecoder() {
        return  (Decoder) getXMLReader();
    }
    
}