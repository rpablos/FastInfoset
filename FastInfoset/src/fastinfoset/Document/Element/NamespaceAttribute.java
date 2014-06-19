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
