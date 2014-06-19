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

import fastinfoset.Document.Name_surrogate;
import fastinfoset.Algorithm.Algorithm;
import fastinfoset.Alphabet.Alphabet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author rpablos
 */
public class Vocabulary extends BuiltinVocabulary {
    InitialVocabulary initialVocabulary = null;
    InternalInitialVocabulary internalInitialVocabulary =null;
    
    
    public static int INITIAL_CAPACITY_ALGORITHMS = 31;
    public static int INITIAL_CAPACITY_ALPHABETS = 31;
    
    public static int INITIAL_CAPACITY_PREFIX = 100;
    public static int INITIAL_CAPACITY_NAMESPACE = 100;
    public static int INITIAL_CAPACITY_LOCALNAME = 1000;
    
    public static int INITIAL_CAPACITY_OTHER_NCNAMES = 10;
    public static int INITIAL_CAPACITY_OTHER_URIS = 10;
    public static int INITIAL_CAPACITY_ATTRIBUTE_VALUES = 1000;
    public static int INITIAL_CAPACITY_CHARACTER_CHUNKS = 1000;
    public static int INITIAL_CAPACITY_OTHER_STRINGS = 20;
    
    public static int INITIAL_CAPACITY_ELEMENTNAME = 1000;
    public static int INITIAL_CAPACITY_ATTRIBUTENAME = 1000;

    public IndexMap<Algorithm> algorithms = new IndexMap<Algorithm>(INITIAL_CAPACITY_ALGORITHMS); 
    public IndexMap<Alphabet> alphabets = new IndexMap<Alphabet>(INITIAL_CAPACITY_ALPHABETS); 
    
    public IndexMap<String> prefix = new IndexMap<String>(INITIAL_CAPACITY_PREFIX); 
    public IndexMap<String> namespace = new IndexMap<String>(INITIAL_CAPACITY_NAMESPACE); 
    public IndexMap<String> localname = new IndexMap<String>(INITIAL_CAPACITY_LOCALNAME);
    
    public IndexMap<String> other_ncnames = new IndexMap<String>(INITIAL_CAPACITY_OTHER_NCNAMES);
    public IndexMap<String> other_uris = new IndexMap<String>(INITIAL_CAPACITY_OTHER_URIS);
    public IndexMap<String> attribute_values = new IndexMap<String>(INITIAL_CAPACITY_ATTRIBUTE_VALUES);
    public IndexMap<String> character_chunks = new IndexMap<String>(INITIAL_CAPACITY_CHARACTER_CHUNKS);
    public IndexMap<String> other_strings = new IndexMap<String>(INITIAL_CAPACITY_OTHER_STRINGS);
    
    public IndexMap<Name_surrogate> elementname = new IndexMap<Name_surrogate>(INITIAL_CAPACITY_ELEMENTNAME);
    public IndexMap<Name_surrogate> attributename = new IndexMap<Name_surrogate>(INITIAL_CAPACITY_ATTRIBUTENAME);

    public HashMapObjectInt<String> algorithmURIs = new HashMapObjectInt<String>(INITIAL_CAPACITY_ALGORITHMS); 
    
    public Vocabulary() {
        init(null);
    }
    
    public Vocabulary(InitialVocabulary initialVocabulary) {
        init(initialVocabulary);
    }
    private void init(InitialVocabulary initialVocabulary) {
        this.initialVocabulary = initialVocabulary;
        addBuiltinEntries();
        addInitialVocabulary(initialVocabulary);
        populateAlgorithmURIs();

            internalInitialVocabulary = new InternalInitialVocabulary();
            
            internalInitialVocabulary.algorithms = (IndexMap<Algorithm>) algorithms.clone();
            internalInitialVocabulary.alphabets = (IndexMap<Alphabet>) alphabets.clone();
            
            internalInitialVocabulary.prefix = (IndexMap<String>) prefix.clone();
            internalInitialVocabulary.namespace = (IndexMap<String>) namespace.clone();
            internalInitialVocabulary.localname = (IndexMap<String>) localname.clone();
            
            internalInitialVocabulary.other_ncnames = (IndexMap<String>) other_ncnames.clone();
            internalInitialVocabulary.other_uris = (IndexMap<String>) other_uris.clone();
            internalInitialVocabulary.attribute_values = (IndexMap<String>) attribute_values.clone();
            internalInitialVocabulary.character_chunks = (IndexMap<String>) character_chunks.clone();
            internalInitialVocabulary.other_strings = (IndexMap<String>) other_strings.clone();
            
            internalInitialVocabulary.elementname = (IndexMap<Name_surrogate>) elementname.clone();
            internalInitialVocabulary.attributename = (IndexMap<Name_surrogate>) attributename.clone();
    }
    
    private void addInitialVocabulary(InitialVocabulary initialVocabulary) {
        if (initialVocabulary == null)
            return;
        InitialVocabulary external = initialVocabulary.getExternalVocabulary();
        if (external != null) {
            addInitialVocabulary(external);
        }
        addToMap(algorithms, initialVocabulary.algorithms);
        addToMap(alphabets, initialVocabulary.alphabets);
        
        addToMap(prefix, initialVocabulary.prefixes);
        addToMap(namespace, initialVocabulary.namespaces);
        addToMap(localname, initialVocabulary.localnames);
        
        addToMap(other_ncnames, initialVocabulary.other_ncnames);
        addToMap(other_uris, initialVocabulary.other_uris);
        addToMap(attribute_values, initialVocabulary.attribute_values);
        addToMap(character_chunks, initialVocabulary.character_chunks);
        addToMap(other_strings, initialVocabulary.other_strings);
        
        addToMap(elementname, initialVocabulary.elementnames);
        addToMap(attributename, initialVocabulary.attributenames);
    }
    private void addBuiltinEntries() {
        addToMap(algorithms, algorithms_builtin);
        addToMap(alphabets, alphabets_builtin);
        addToMap(prefix, prefix_builtin);
        addToMap(namespace, namespace_builtin);
    }
    <T> void addToMap(IndexMap<T> map, List<T> set) {
        Iterator<T> it = set.iterator();
        while (it.hasNext()) {
            T t = it.next();
            if (t == null)
                map.index++; //null especial, para hacer correr el índice
            else 
                map.addNewIndexEntry(t);
        }
    }

    public InitialVocabulary getInitialVocabulary() {
        return initialVocabulary;
    }
    public void setInitialVocabulary(InitialVocabulary initialVocabulary) {
        algorithms.clear();
        alphabets.clear();
        prefix.clear();
        namespace.clear();
        localname.clear();
        other_ncnames.clear();
        other_uris.clear();
        attribute_values.clear();
        character_chunks.clear();
        other_strings.clear();
        elementname.clear();
        attributename.clear();
        init(initialVocabulary);
    }
    
    public InitialVocabulary toInitialVocabulary() {
        InitialVocabulary result = new InitialVocabulary();
        //result.algorithms.addAll(Arrays.asList(algorithms.getKeysInOrder(new Algorithm[0],internalInitialVocabulary.algorithms.index)));
        //result.alphabets.addAll(Arrays.asList(alphabets.getKeysInOrder(new Alphabet[0],internalInitialVocabulary.alphabets.index)));
        result.prefixes.addAll(Arrays.asList(prefix.getKeysInOrder(new String[0],internalInitialVocabulary.prefix.index)));
        result.namespaces.addAll(Arrays.asList(namespace.getKeysInOrder(new String[0],internalInitialVocabulary.namespace.index)));
        result.localnames.addAll(Arrays.asList(localname.getKeysInOrder(new String[0],internalInitialVocabulary.localname.index)));
        result.other_ncnames.addAll(Arrays.asList(other_ncnames.getKeysInOrder(new String[0],internalInitialVocabulary.other_ncnames.index)));
        result.other_uris.addAll(Arrays.asList(other_uris.getKeysInOrder(new String[0],internalInitialVocabulary.other_uris.index)));
        result.attribute_values.addAll(Arrays.asList(attribute_values.getKeysInOrder(new String[0],internalInitialVocabulary.attribute_values.index)));
        result.character_chunks.addAll(Arrays.asList(character_chunks.getKeysInOrder(new String[0],internalInitialVocabulary.character_chunks.index)));
        result.other_strings.addAll(Arrays.asList(other_strings.getKeysInOrder(new String[0],internalInitialVocabulary.other_strings.index)));
        result.elementnames.addAll(Arrays.asList(elementname.getKeysInOrder(new Name_surrogate[0],internalInitialVocabulary.elementname.index)));
        result.attributenames.addAll(Arrays.asList(attributename.getKeysInOrder(new Name_surrogate[0],internalInitialVocabulary.attributename.index)));
        return result;
    }
    public InitialVocabulary toInitialVocabularyIncludingInitialVocabulary() {
        InitialVocabulary result = new InitialVocabulary();
        result.algorithms.addAll(Arrays.asList(algorithms.getKeysInOrder(new Algorithm[0],algorithms_builtin.size())));
        result.alphabets.addAll(Arrays.asList(alphabets.getKeysInOrder(new Alphabet[0],alphabets_builtin.size())));
        result.prefixes.addAll(Arrays.asList(prefix.getKeysInOrder(new String[0],prefix_builtin.size())));
        result.namespaces.addAll(Arrays.asList(namespace.getKeysInOrder(new String[0],namespace_builtin.size())));
        result.localnames.addAll(Arrays.asList(localname.getKeysInOrder(new String[0])));
        result.other_ncnames.addAll(Arrays.asList(other_ncnames.getKeysInOrder(new String[0])));
        result.other_uris.addAll(Arrays.asList(other_uris.getKeysInOrder(new String[0])));
        result.attribute_values.addAll(Arrays.asList(attribute_values.getKeysInOrder(new String[0])));
        result.character_chunks.addAll(Arrays.asList(character_chunks.getKeysInOrder(new String[0])));
        result.other_strings.addAll(Arrays.asList(other_strings.getKeysInOrder(new String[0])));
        result.elementnames.addAll(Arrays.asList(elementname.getKeysInOrder(new Name_surrogate[0])));
        result.attributenames.addAll(Arrays.asList(attributename.getKeysInOrder(new Name_surrogate[0])));
        return result;
    }

    public void reset() {
        prefix = (IndexMap<String>) internalInitialVocabulary.prefix.clone();
        namespace = (IndexMap<String>) internalInitialVocabulary.namespace.clone();
        localname = (IndexMap<String>) internalInitialVocabulary.localname.clone();
        other_ncnames = (IndexMap<String>) internalInitialVocabulary.other_ncnames.clone();
        other_uris = (IndexMap<String>) internalInitialVocabulary.other_uris.clone();
        attribute_values = (IndexMap<String>) internalInitialVocabulary.attribute_values.clone();
        character_chunks = (IndexMap<String>) internalInitialVocabulary.character_chunks.clone();
        other_strings = (IndexMap<String>) internalInitialVocabulary.other_strings.clone();
        elementname = (IndexMap<Name_surrogate>) internalInitialVocabulary.elementname.clone();
        attributename =  (IndexMap<Name_surrogate>) internalInitialVocabulary.attributename.clone();
        
    }

    private void populateAlgorithmURIs() {
        if (initialVocabulary == null)
            return;
        for (Algorithm algo: initialVocabulary.algorithms) {
            if (algo.getURI() != null) {
                algorithmURIs.put(algo.getURI(), algorithms.get(algo));
            }
        }
    }

    static private class InternalInitialVocabulary {
        public IndexMap<Algorithm> algorithms; 
        public IndexMap<Alphabet> alphabets; 

        public IndexMap<String> prefix; 
        public IndexMap<String> namespace; 
        public IndexMap<String> localname;

        public IndexMap<String> other_ncnames;
        public IndexMap<String> other_uris;
        public IndexMap<String> attribute_values;
        public IndexMap<String> character_chunks;
        public IndexMap<String> other_strings;

        public IndexMap<Name_surrogate> elementname;
        public IndexMap<Name_surrogate> attributename;
    }
}
