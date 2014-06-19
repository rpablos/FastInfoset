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

/**
 *
 * @author rpablos
 */
public class DecoderVocabulary extends BuiltinVocabulary {
    InitialVocabulary externalVocabulary;
    
    public ArrayIndex<Algorithm> algorithms = new ArrayIndex<Algorithm>();
    public ArrayIndex<Alphabet> alphabets = new ArrayIndex<Alphabet>();
    
    public ArrayIndex<String> prefixes = new ArrayIndex<String>();
    public ArrayIndex<String> namespaces = new ArrayIndex<String>();
    public ArrayIndex<String> localnames = new ArrayIndex<String>();
    
    public ArrayIndex<String> other_ncnames = new ArrayIndex<String>();
    public ArrayIndex<String> other_uris = new ArrayIndex<String>();
    public ArrayIndex<String> attribute_values = new ArrayIndex<String>();
    public ArrayIndex<String> character_chunks = new ArrayIndex<String>();
    public ArrayIndex<String> other_strings = new ArrayIndex<String>();
    
    public ArrayIndex<Name_surrogate> elementnames = new ArrayIndex<Name_surrogate>();
    public ArrayIndex<Name_surrogate> attributenames = new ArrayIndex<Name_surrogate>();
    
    ArrayIndex[] internalTables = new ArrayIndex[]{alphabets,algorithms, prefixes, namespaces, localnames,other_ncnames, other_uris,
        attribute_values,character_chunks,other_strings,elementnames, attributenames };
    int[] internalInitialindexes = new int[internalTables.length];

    String[] algorithmsURIs = new String[256];
    
    public DecoderVocabulary() {
        this(null);
    }
    public DecoderVocabulary(InitialVocabulary externalVocabulary) {
        this.externalVocabulary = externalVocabulary;
        addBuiltinEntries();
        
        for (int i = 0; i < internalTables.length; i++) {
            internalInitialindexes[i] = internalTables[i].getSize();
        }
        addInitialVocabulary(externalVocabulary);
    }

    private void addBuiltinEntries() {
        algorithms.addAll(algorithms_builtin);
        alphabets.addAll(alphabets_builtin);
        prefixes.addAll(prefix_builtin);
        namespaces.addAll(namespace_builtin);
    }

    private void addInitialVocabulary(InitialVocabulary initialVocabulary) {
        if (initialVocabulary == null)
            return;
        InitialVocabulary external = initialVocabulary.getExternalVocabulary();
        if (external != null) 
            addInitialVocabulary(external);
        algorithms.addAll(initialVocabulary.algorithms);
        alphabets.addAll(initialVocabulary.alphabets);
        prefixes.addAll(initialVocabulary.prefixes);
        namespaces.addAll(initialVocabulary.namespaces);
        localnames.addAll(initialVocabulary.localnames);
        other_ncnames.addAll(initialVocabulary.other_ncnames);
        other_uris.addAll(initialVocabulary.other_uris);
        attribute_values.addAll(initialVocabulary.attribute_values);
        character_chunks.addAll(initialVocabulary.character_chunks);
        other_strings.addAll(initialVocabulary.other_strings);
        elementnames.addAll(initialVocabulary.elementnames);
        attributenames.addAll(initialVocabulary.attributenames);
    }
    
    
    public void reset() {
        for (int i = 0; i < internalTables.length; i++)
            internalTables[i].truncate(internalInitialindexes[i]);
    }

    public InitialVocabulary getExternalVocabulary() {
        return externalVocabulary;
    }

    public void setExternalVocabulary(InitialVocabulary external) {
        reset();
        addInitialVocabulary(external);
    }
    public void setAlgorithmURI(String uri, int index) {
        if (index >= 0 && index < algorithmsURIs.length)
            algorithmsURIs[index] = uri;
    }
    public String getAlgorithmURI(int index) {
        return (index >= 0 && index < algorithmsURIs.length)?algorithmsURIs[index]:null;
    }
}
