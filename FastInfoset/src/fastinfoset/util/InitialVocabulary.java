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

package fastinfoset.util;

import fastinfoset.Algorithm.Algorithm;
import fastinfoset.Alphabet.Alphabet;
import fastinfoset.Document.Name_surrogate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rpablos
 */
public class InitialVocabulary {
    String external_vocabulary_URI;
    InitialVocabulary external_vocabulary;
    
    public List<Algorithm> algorithms = new ArrayList<Algorithm>();
    public List<Alphabet> alphabets = new ArrayList<Alphabet>();
    
    public List<String> prefixes = new ArrayList<String>();
    public List<String> namespaces = new ArrayList<String>();
    public List<String> localnames = new ArrayList<String>();
    
    public List<String> other_ncnames = new ArrayList<String>();
    public List<String> other_uris = new ArrayList<String>();
    public List<String> attribute_values = new ArrayList<String>();
    public List<String> character_chunks = new ArrayList<String>();
    public List<String> other_strings = new ArrayList<String>();
    
    public List<Name_surrogate> elementnames = new ArrayList<Name_surrogate>();
    public List<Name_surrogate> attributenames = new ArrayList<Name_surrogate>();
    
    
    public void setExternalVocabulary(String uri, InitialVocabulary external) {
        external_vocabulary_URI = uri;
        external_vocabulary = external;
    }
    public InitialVocabulary getExternalVocabulary() {
        return external_vocabulary;
    }
    public String getExternalVocabularyURI() {
        return external_vocabulary_URI;
    }
    
    public boolean isEmpty() {
        if ((external_vocabulary != null) && (external_vocabulary_URI != null))
            return false;
        if (    !prefixes.isEmpty() || !namespaces.isEmpty() || !localnames.isEmpty() ||
                !other_ncnames.isEmpty() || !other_uris.isEmpty() || !attribute_values.isEmpty() ||
                !character_chunks.isEmpty() || !other_strings.isEmpty() ||
                !elementnames.isEmpty() || !attributenames.isEmpty() ||
                !algorithms.isEmpty() || !alphabets.isEmpty()
           )
            return false;
        return true;
    }
    public void addInitialVocabulary(InitialVocabulary iv) {
        algorithms.addAll(iv.algorithms);
        alphabets.addAll(iv.alphabets);
        
        prefixes.addAll(iv.prefixes);
        namespaces.addAll(iv.namespaces);
        localnames.addAll(iv.localnames);
        
        other_ncnames.addAll(iv.other_ncnames);
        other_uris.addAll(iv.other_uris);
        attribute_values.addAll(iv.attribute_values);
        character_chunks.addAll(iv.character_chunks);
        other_strings.addAll(iv.other_strings);
        
        elementnames.addAll(iv.elementnames);
        attributenames.addAll(iv.attributenames);
    }
}
