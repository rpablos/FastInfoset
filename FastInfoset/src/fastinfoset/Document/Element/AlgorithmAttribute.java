//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Document.Element;

import fastinfoset.Algorithm.Algorithm;
import javax.xml.namespace.QName;

/**
 *
 * @author rpablos
 */
public class AlgorithmAttribute extends Attribute {
    Algorithm algo;
    Object object = null;
    
    /**
     * Create an attribute, whose string value must be encoded with a specified algorithm
     * 
     * When this constructor is used, the method {@link #getDataObject() } returns null
     * 
     * @param qualified_name the attribute qualified name 
     * @param normalized_value the attribute string value
     * @param algo the algorithm to be use for this attribute string value 
     */
    public AlgorithmAttribute(QName qualified_name, String normalized_value, Algorithm algo) {
        super(qualified_name, normalized_value);
        this.algo = algo;
    }

    /**
     * Create an attribute, whose value is an object. 
     * This object is encoded or decoded using  specified algorithm
     * 
     * @param qname the attribute qualified name 
     * @param object the attribute value in form of object
     * @param algo the algorithm to encode/decode this attribute value
     */
    public AlgorithmAttribute(QName qname, Object object, Algorithm algo) { 
        super(qname, null);
        this.object = object;
        this.algo = algo;
    }

    /**
     * This method returns the attribute value in an Object. 
     * The exact type of this object is algoritm specific. 
     * When using algoritms, It is an option and it is more efficient for 
     * the application to retrieve an object directly instead of a string {@link #getNormalized_value() }, 
     * whose parsing end up generating a data object inside the application.
     * 
     * @return This attribute value if form of Object
     * 
     */
    public Object getDataObject() {
        return object;
    }

    /**
     * Returns the algorithm to be used for encoding or used for decoding 
     * this attribute
     *
     * @return the algorithm 
     */
    public Algorithm getAlgorithm() {
        return algo;
    }

    /**
     * Returns this attribute value if form of String.
     * if it is necessary a conversion is performed: {@link Algorithm#stringFromObject(java.lang.Object) }.
     *
     * @return the string value of this attribute
     */
    @Override
    public String getNormalized_value() {
        if (normalized_value != null)
            return normalized_value;
        else
            return normalized_value=algo.stringFromObject(object);
    }
    
}
