/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fastinfoset;

import fastinfoset.Algorithm.Algorithm;
import fastinfoset.Algorithm.Builtin.CDATA;
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
    }

    @Override
    public void setInitialVocabulary(InitialVocabulary initialVocabulary) {
        //do nothing;
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