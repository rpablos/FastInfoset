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

package fastinfoset.Algorithm.Builtin;

import fastinfoset.Algorithm.Algorithm;
import java.nio.charset.Charset;

/**
 *
 * @author rpablos
 */
public class CDATA extends Algorithm {
    static Charset utf8CharSet = Charset.forName("utf-8");
    static public int id = 9;
    @Override
    public byte[] toByteArray(String str) {
        return str.getBytes(utf8CharSet);
    }

    @Override
    public String fromByteArray(byte[] data) {
        return new String(data,utf8CharSet);
    }
    
    @Override
    public String getURI() {
        return null;
    }

    @Override
    public int hashCode() {
        return id;
    }
    
    static public CDATA instance = new CDATA();

    @Override
    public Object objectFromByteArray(byte[] data)  {
        return fromByteArray(data);
    }

    @Override
    public String stringFromObject(Object object) {
        return (String) object;
    }
    
    
}
