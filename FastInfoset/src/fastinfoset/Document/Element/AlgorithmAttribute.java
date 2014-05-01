//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Document.Element;

import fastinfoset.Algorithm.Algorithm;
import fastinfoset.Algorithm.EncodingAlgorithmException;
import fastinfoset.util.EncodedString;
import javax.xml.namespace.QName;

/**
 *
 * @author rpablos
 */
public class AlgorithmAttribute extends Attribute {
    Algorithm algo;
    Object object = null;
    
    public AlgorithmAttribute(QName qualified_name, String normalized_value, Algorithm algo) {
        super(qualified_name, normalized_value);
        this.algo = algo;
    }

    public AlgorithmAttribute(QName qname, Object object, Algorithm algo) { 
        super(qname, null);
        this.object = object;
        this.algo = algo;
    }
    public Object getDataObject() {
        return object;
    }
    public Algorithm getAlgorithm() {
        return algo;
    }

    @Override
    public String getNormalized_value() {
        if (normalized_value != null)
            return normalized_value;
        else
            return normalized_value=algo.stringFromObject(object);
    }
    
}
