//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Algorithm;

import fastinfoset.FastInfosetException;

/**
 *
 * @author rpablos
 */
public abstract class Algorithm {
    abstract public byte[] toByteArray(String str) throws EncodingAlgorithmException;
    abstract public String getURI();

    abstract public String fromByteArray(byte[] data) throws EncodingAlgorithmException;
    abstract public Object objectFromByteArray(byte[] data) throws EncodingAlgorithmException;
    abstract public String stringFromObject(Object object);
    
    //could be optimized in overriden implementations
    public Object objectFromString(String str) throws EncodingAlgorithmException {
        return objectFromByteArray(toByteArray(str));
    }
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
