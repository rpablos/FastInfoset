/*
* Copyright 2014 Ronald Pablos.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package fastinfoset.Document.Element;

import java.util.LinkedList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author rpablos
 */
public class Attribute {
    QName qualified_name;
    String normalized_value;

    public Attribute(QName qualified_name, String normalized_value) {
        this.qualified_name = qualified_name;
        this.normalized_value = normalized_value;
    }
    
    public static List<Attribute> getAttributeList(Node node) {
        List<Attribute> list = new LinkedList<Attribute>();
        NamedNodeMap nnm = node.getAttributes();
        for (int i = 0; i < nnm.getLength(); i++) {
            Node a = nnm.item(i);
            String prefix = a.getPrefix();
            if (prefix == null) prefix =  XMLConstants.DEFAULT_NS_PREFIX;
            list.add(new Attribute(new QName(a.getNamespaceURI(),a.getLocalName(),prefix),a.getNodeValue()));
        }
        return list;
    }

    public String getNormalized_value()  {
        return normalized_value;
    }

    public QName getQualified_name() {
        return qualified_name;
    }
    
    public static void getAttributeList(Node node, List<Attribute> list, List<NamespaceAttribute> listns) {
        
        NamedNodeMap nnm = node.getAttributes();
        for (int i = 0; i < nnm.getLength(); i++) {
            Node a = nnm.item(i);
            String prefix = a.getPrefix();
            if (prefix == null) prefix =  "";
            String namespace = a.getNamespaceURI();
            String localname = a.getLocalName();
            String value = a.getNodeValue();
            
            if ( (namespace!= null) && namespace.equals("http://www.w3.org/2000/xmlns/")){
                String prefixName = localname;
                if (prefix.isEmpty())
                    prefixName = "";
                listns.add(new NamespaceAttribute(prefixName,value));
            }
            else {
                list.add(new Attribute(new QName(namespace,localname,prefix),value));
            }
        }
        
    }
}
