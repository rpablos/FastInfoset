/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fastinfoset;

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
    }
    @Override
    public void setInitialVocabulary(InitialVocabulary initialVocabulary) {
        //do nothing;
    }
}
