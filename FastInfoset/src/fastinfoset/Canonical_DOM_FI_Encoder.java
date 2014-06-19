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

package fastinfoset;

import fastinfoset.Algorithm.Algorithm;
import fastinfoset.util.AllowIndexMap;
import fastinfoset.util.InitialVocabulary;

/**
 *
 * @author rpablos
 */
public class Canonical_DOM_FI_Encoder extends DOM_FI_Encoder {
    CanonicalAllow canonicalAllow = new CanonicalAllow();
    public Canonical_DOM_FI_Encoder() {
        vocabulary.character_chunks.setAllowIndexMap(canonicalAllow);
        vocabulary.attribute_values.setAllowIndexMap(canonicalAllow);
        vocabulary.other_strings.setAllowIndexMap(canonicalAllow);
    }

    @Override
    public void setInitialVocabulary(InitialVocabulary initialVocabulary) {
        //do nothing;
    }
    @Override
    public void setAllowPolicyForChunks(AllowIndexMap<String> allowPolicy) {
        //do nothing
    }

    @Override
    public void setAllowPolicyForAttributeValues(AllowIndexMap<String> allowPolicy) {
        //do nothing
    }
    @Override
    public void setAllowPolicyForOtherStrings(AllowIndexMap<String> allowPolicy) {
        //do nothing
    }
    
    static class CanonicalAllow implements AllowIndexMap<String> {
        
        @Override
        public boolean isInsertionAllowed(String str, Algorithm algo) {
            return false;
        }

        @Override
        public boolean isObtentionAllowed(Algorithm algo) {
            return true;
        }
        
    }
}
