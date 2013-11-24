//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Document.Element;

import fastinfoset.Algorithm.EncodingAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author rpablos
 */
public class NamespaceAttribute {
    String prefix;
    String namespace_name;

    public NamespaceAttribute(String prefix, String namespace_name) {
        this.prefix = prefix;
        this.namespace_name = namespace_name;
    }
    
    public static List<NamespaceAttribute> ExtractNamespaceAttributeList(List<Attribute> list) throws EncodingAlgorithmException {
        List<NamespaceAttribute> resultlist = new LinkedList<NamespaceAttribute>();
        ListIterator<Attribute> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            Attribute next = listIterator.next();
            if (next.getQualified_name().getNamespaceURI().equals("http://www.w3.org/2000/xmlns/")){
                String prefixName = next.getQualified_name().getLocalPart();
                if (next.getQualified_name().getPrefix().isEmpty())
                    prefixName = "";
                resultlist.add(new NamespaceAttribute(prefixName,next.getNormalized_value()));
                listIterator.remove();
            }
        }
        return resultlist;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getNamespace_name() {
        return namespace_name;
    }
    
    
}
