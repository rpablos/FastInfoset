//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Algorithm;

/**
 * An algorithm is procedure for transforming a character string to an octet string and vice versa. 
 * The transformation must be bijection. It intends to be a very efficient coding, 
 * either for processing or for reducing size of data.
 * 
 * <p>Addionally, the class provides tranformations from object to character string 
 * and octet string and viceversa. The object is intended for processing within the application
 * more easily and efficiently than a character string. The object type depends on
 * the algorithm and the algorithm developer can choose the object type that best suits
 * to the application needs.
 * @author rpablos
 */
public abstract class Algorithm {
    
    /**
     * Transforms an character string to a {@code byte[]} representation.
     * @param str the character string to be encoded as {@code byte[]}.
     * @return the byte array representation 
     * @throws EncodingAlgorithmException
     */
    abstract public byte[] toByteArray(String str) throws EncodingAlgorithmException;
    
    /**
     * Returns the URI associated with this algorithm.
     * <p>The URI is null if the algorithm is built-in algorithm
     *
     * @return the algorithm URI
     */
    abstract public String getURI();

    abstract public String fromByteArray(byte[] data) throws EncodingAlgorithmException;
    abstract public Object objectFromByteArray(byte[] data) throws EncodingAlgorithmException;
    abstract public String stringFromObject(Object object);
    
    /**
     * Transforms a string representation to an object
     * <p>The type of object will depend on the specific algorithm
     * <p>It is encouraged to override this method for optimized implementation,
     * shince by default this method returns: 
     * {@code objectFromByteArray(toByteArray(str))}
     * @param str the string representation of object
     * @return An object
     * @throws EncodingAlgorithmException
     */
    public Object objectFromString(String str) throws EncodingAlgorithmException {
        return objectFromByteArray(toByteArray(str));
    }
    /**
     * Transforms an object to a {@code byte[]} representation
     * <p>The type of object will depend on the specific algorithm
     * <p>It is encouraged to override this method for optimized implementation,
     * shince by default this method returns: 
     * {@code toByteArray(stringFromObject(object))}
     * 
     * @param object object to be encoded as <code>byte[]</code>.
     * @return the byte array representation
     * @throws EncodingAlgorithmException
     */
    public byte[] toByteArray(Object object) throws EncodingAlgorithmException {
        return toByteArray(stringFromObject(object));
    }
    
    @Override
    public boolean equals(Object obj) {
        if  (this == obj)
            return true;
        Algorithm algo = (Algorithm) obj;
        if (getURI() != null && getURI().equals(algo.getURI()))
            return true;
        if (getURI() == null && (this.getClass().isInstance(obj)))
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        String uri = getURI();
        return (uri == null)?0:uri.hashCode();
    }
    
}
