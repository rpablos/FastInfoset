//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.util;

import fastinfoset.Document.Name_surrogate;
import fastinfoset.Algorithm.Algorithm;
import fastinfoset.Algorithm.Builtin.BASE64;
import fastinfoset.Algorithm.Builtin.BOOLEAN;
import fastinfoset.Algorithm.Builtin.CDATA;
import fastinfoset.Algorithm.Builtin.DOUBLE;
import fastinfoset.Algorithm.Builtin.FLOAT;
import fastinfoset.Algorithm.Builtin.HEXADECIMAL;
import fastinfoset.Algorithm.Builtin.INT;
import fastinfoset.Algorithm.Builtin.LONG;
import fastinfoset.Algorithm.Builtin.SHORT;
import fastinfoset.Algorithm.Builtin.UUID;
import fastinfoset.Alphabet.Alphabet;
import fastinfoset.Alphabet.DateAndTime;
import fastinfoset.Alphabet.Numeric;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import fastinfoset.FastInfosetConstants;

/**
 *
 * @author rpablos
 */
public class Vocabulary extends BuiltinVocabulary {
    InitialVocabulary initialVocabulary = null;
//    Vocabulary internalInitialVocabulary =null;
    InternalInitialVocabulary internalInitialVocabulary =null;
    public int MAXIMUM_CHUNK_LENGTH = 32;
    
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
    
//    final static List<Algorithm> algorithms_builtin = new ArrayList<Algorithm>();
//    final static List<Alphabet> alphabets_builtin = new ArrayList<Alphabet>();
//     
//    final static List<String> prefix_builtin = new ArrayList<String>();
//    final static List<String> namespace_builtin = new ArrayList<String>();
//    static {
//        prefix_builtin.add("xml");
//        namespace_builtin.add("http://www.w3.org/XML/1998/namespace");
//        //builtin algorithms
//        Algorithm[] builtinAlgorithmsArray = new Algorithm[31];
//        builtinAlgorithmsArray[HEXADECIMAL.id] = HEXADECIMAL.instance;
//        builtinAlgorithmsArray[BASE64.id] = BASE64.instance;
//        builtinAlgorithmsArray[SHORT.id] = SHORT.instance;
//        builtinAlgorithmsArray[INT.id] = INT.instance;
//        builtinAlgorithmsArray[LONG.id] = LONG.instance;
//        builtinAlgorithmsArray[BOOLEAN.id] = BOOLEAN.instance;
//        builtinAlgorithmsArray[FLOAT.id] = FLOAT.instance;
//        builtinAlgorithmsArray[DOUBLE.id] = DOUBLE.instance;
//        builtinAlgorithmsArray[UUID.id] = UUID.instance;
//        builtinAlgorithmsArray[CDATA.id] = CDATA.instance;
//        algorithms_builtin.addAll(Arrays.asList(builtinAlgorithmsArray));
//        Alphabet[] builtinAlphabetArray = new Alphabet[15];
//        builtinAlphabetArray[Numeric.id] = Numeric.instance;
//        builtinAlphabetArray[DateAndTime.id] = DateAndTime.instance;
//        alphabets_builtin.addAll(Arrays.asList(builtinAlphabetArray));
//    }
    
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
        this(null);
    }
    
//    public Vocabulary(InitialVocabulary initialVocabulary) {
//        this(initialVocabulary,true);
//    }
    public Vocabulary(InitialVocabulary initialVocabulary, int maxChunkLength) {
        this(initialVocabulary);
        MAXIMUM_CHUNK_LENGTH = maxChunkLength;
    }
    public Vocabulary(InitialVocabulary initialVocabulary/*, boolean createInternal*/) {
        this.initialVocabulary = initialVocabulary;
        addBuiltinEntries();
        addInitialVocabulary(initialVocabulary);
        character_chunks.setAllowInsertion(new AllowLimitedStringLenghts());
        populateAlgorithmURIs();
//        if (createInternal) {
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
//        }
//        else {
           
//        }
        //limitar la longitud de las cadenas que se insertan en la tabla CHARACTER_CHUNKS
      
    }
    
    private void addInitialVocabulary(InitialVocabulary initialVocabulary) {
        if (initialVocabulary == null)
            return;
        InitialVocabulary external = initialVocabulary.getExternalVocabulary();
        if (external != null) {
            addInitialVocabulary(external);
//            addToMap(prefix, external.prefixes);
//            addToMap(namespace, external.namespaces);
//            addToMap(localname, external.localnames);
//            
//            addToMap(other_ncnames, external.other_ncnames);
//            addToMap(other_uris, external.other_uris);
//            addToMap(attribute_values, external.attribute_values);
//            addToMap(character_chunks, external.character_chunks);
//            addToMap(other_strings, external.other_strings);
//        
//            addToMap(elementname, external.elementnames);
//            addToMap(attributename, external.attributenames);
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
        //algorithms.remove(null);
        addToMap(alphabets, alphabets_builtin);
        //alphabets.remove(null);
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
//            if (t == null || !map.containsKey(t)) // null especial, para hacer correr el índice
//                map.addNewIndexEntry(t);
        }
    }

    public InitialVocabulary getInitialVocabulary() {
        return initialVocabulary;
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
        for (Algorithm algo: initialVocabulary.algorithms) {
            if (algo.getURI() != null) {
                algorithmURIs.put(algo.getURI(), algorithms.get(algo));
            }
        }
    }
    class AllowLimitedStringLenghts implements AllowIndexMap<String> {

        @Override
        public boolean isInsertionAllowed(String str, Algorithm algo) {
            return str.length() <= MAXIMUM_CHUNK_LENGTH;
        }

        @Override
        public boolean isObtentionAllowed(Algorithm algo) {
            return (algo == null || !(algo instanceof CDATA));
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
