//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.sax;

import fastinfoset.Encoder;
import fastinfoset.SAX_FI_Encoder;
import java.io.OutputStream;
import javax.xml.transform.sax.SAXResult;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

/**
 *
 * @author rpablos
 */
public class FastInfosetResult extends SAXResult {
    OutputStream _outputStream;
    
    public FastInfosetResult(OutputStream outputStream) {
        _outputStream = outputStream;
    }
    
    @Override
    public ContentHandler getHandler() {
        ContentHandler handler = super.getHandler();
        if (handler == null) {
            handler = new SAX_FI_Encoder();
            setHandler(handler);
        }
        ((SAX_FI_Encoder) handler).setOutputStream(_outputStream);
        return handler;        
    }
    
    @Override
    public LexicalHandler getLexicalHandler() {
        return (LexicalHandler) getHandler();
    }
    public Encoder getFastInfosetDecoder() {
        return  (Encoder) getHandler();
    }
}
