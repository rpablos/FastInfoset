//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.Document.Element;

import java.util.List;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;

/**
 *
 * @author rpablos
 */
public class SAXAttributes implements Attributes,FastInfosetAttributes {
    List<Attribute> attList;
    public SAXAttributes(List<Attribute> list) {
            attList = list;
    }

    
    @Override
    public int getLength() {
        return (attList == null)?0:attList.size();
    }

    @Override
    public String getURI(int index) {
        return attList.get(index).getQualified_name().getNamespaceURI();
    }

    @Override
    public String getLocalName(int index) {
        return attList.get(index).getQualified_name().getLocalPart();
    }

    @Override
    public String getQName(int index) {
        QName qname = attList.get(index).getQualified_name();
        return qname.getPrefix().isEmpty()?qname.getLocalPart(): qname.getPrefix()+":"+qname.getLocalPart();
    }

    @Override
    public String getType(int index) {
        return "CDATA";
    }

    @Override
    public String getValue(int index) {
            return attList.get(index).getNormalized_value();
    }

    @Override
    public int getIndex(String uri, String localName) {
        QName qname = new QName(uri,localName);
        for (int i = 0; i < attList.size(); i++) {
            if (attList.get(i).getQualified_name().equals(qname))
                return i;
        }
        return -1;
    }

    @Override
    public int getIndex(String qName) {
        int index = qName.indexOf(':');
        String prefix = (index != -1)?qName.substring(0, index):"";
        String localpart = (index != -1)?qName.substring(index+1):qName;
        for (int i = 0; i < attList.size(); i++) {
            QName qname = attList.get(i).getQualified_name();
            if (qname.getPrefix().equals(prefix) && qname.getLocalPart().equals(localpart))
                return i;
        }
        return -1;
    }

    @Override
    public String getType(String uri, String localName) {
        return "CDATA";
    }

    @Override
    public String getType(String qName) {
         return "CDATA";
    }

    @Override
    public String getValue(String uri, String localName) {
        QName qname = new QName(uri,localName);
        for (int i = 0; i < attList.size(); i++) {
            if (attList.get(i).qualified_name.equals(qname))
                    return attList.get(i).getNormalized_value();
        }
        return null;
    }

    @Override
    public String getValue(String qName) {
        int index = qName.indexOf(':');
        String prefix = (index != -1)?qName.substring(0, index):"";
        String localpart = (index != -1)?qName.substring(index+1):qName;
        for (int i = 0; i < attList.size(); i++) {
            QName qname = attList.get(i).qualified_name;
            if (qname.getPrefix().equals(prefix) && qname.getLocalPart().equals(localpart))
                    return attList.get(i).getNormalized_value();
        }
        return null;
    }

    @Override
    public boolean isAlgorithmEncodedAttribute(int index) {
        return attList.get(index) instanceof AlgorithmAttribute;
    }
    @Override
    public Object getAlgorithmObject(int index) {
        Attribute att = attList.get(index);
        if (att instanceof AlgorithmAttribute) {
            return ((AlgorithmAttribute)att).object;
        } else
            return null;
    }

    @Override
    public List<Attribute> getAttributes() {
        return attList;
    }
    

    public void setAttributeList(List<Attribute> attList) {
        this.attList = attList;
    }
    
}
