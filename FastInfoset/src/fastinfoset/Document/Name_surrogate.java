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

package fastinfoset.Document;

/**
 *
 * @author rpablos
 */
public class Name_surrogate {
    public int prefix;
    public int namespace;
    public int localname;

    public Name_surrogate(int prefix, int namespace, int localname) {
        this.prefix = prefix;
        this.namespace = namespace;
        this.localname = localname;
    }

    @Override
    public int hashCode() {
//        if (localname == HashMapObjectInt.NO_INDEX)
//            return 0;
//        int hash = localname;
        //return (namespace == HashMapObjectInt.NO_INDEX)?~localname: ~(localname << (namespace & 0x3)) ^ (namespace>>>16 | namespace<<16);
        return localname ^ ( (namespace << 16) | (namespace >>> 16));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Name_surrogate))
            return false;
        if (obj == this) 
            return true;
        Name_surrogate name = (Name_surrogate) obj;
        return (prefix == name.prefix) && (namespace == name.namespace) &&
               (localname == name.localname);
//        if ((prefix != name.prefix)  &&
//                ( ((prefix != null) && !prefix.equals(name.prefix)) || 
//                  ((name.prefix != null) && name.prefix.equals(prefix)) ) )
//                return false;
//        if (    (namespace != name.namespace)  && 
//                ( ((namespace != null) && !namespace.equals(name.namespace)) || 
//                  (( name.namespace != null) && !name.namespace.equals(namespace)) ) )
//                return false;
//        if (    (localname != name.localname)  && 
//                ( ((localname != null) && !localname.equals(name.localname)) || 
//                  ((name.localname != null) && !name.localname.equals(localname)) ) )
//                return false;
//        return true;
    }
    
    
}
