//  Author: Ronald Pablos
//  Year: 2014

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
