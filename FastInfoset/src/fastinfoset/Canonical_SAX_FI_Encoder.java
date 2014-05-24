//  Author: Ronald Pablos
//  Year: 2014

package fastinfoset;

import fastinfoset.util.AllowIndexMap;
import fastinfoset.util.InitialVocabulary;

/**
 *
 * @author rpablos
 */
public class Canonical_SAX_FI_Encoder extends SAX_FI_Encoder {
    Canonical_DOM_FI_Encoder.CanonicalAllow canonicalAllow = new Canonical_DOM_FI_Encoder.CanonicalAllow();
    public Canonical_SAX_FI_Encoder() {
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
}
