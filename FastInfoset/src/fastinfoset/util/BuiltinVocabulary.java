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
import java.util.List;

/**
 *
 * @author rpablos
 */
public class BuiltinVocabulary {
    final static List<Algorithm> algorithms_builtin = new ArrayList<Algorithm>();
    final static List<Alphabet> alphabets_builtin = new ArrayList<Alphabet>();
     
    final static List<String> prefix_builtin = new ArrayList<String>();
    final static List<String> namespace_builtin = new ArrayList<String>();
    static {
        prefix_builtin.add("xml");
        namespace_builtin.add("http://www.w3.org/XML/1998/namespace");
        //builtin algorithms
        Algorithm[] builtinAlgorithmsArray = new Algorithm[31];
        builtinAlgorithmsArray[HEXADECIMAL.id] = HEXADECIMAL.instance;
        builtinAlgorithmsArray[BASE64.id] = BASE64.instance;
        builtinAlgorithmsArray[SHORT.id] = SHORT.instance;
        builtinAlgorithmsArray[INT.id] = INT.instance;
        builtinAlgorithmsArray[LONG.id] = LONG.instance;
        builtinAlgorithmsArray[BOOLEAN.id] = BOOLEAN.instance;
        builtinAlgorithmsArray[FLOAT.id] = FLOAT.instance;
        builtinAlgorithmsArray[DOUBLE.id] = DOUBLE.instance;
        builtinAlgorithmsArray[UUID.id] = UUID.instance;
        builtinAlgorithmsArray[CDATA.id] = CDATA.instance;
        algorithms_builtin.addAll(Arrays.asList(builtinAlgorithmsArray));
        Alphabet[] builtinAlphabetArray = new Alphabet[15];
        builtinAlphabetArray[Numeric.id] = Numeric.instance;
        builtinAlphabetArray[DateAndTime.id] = DateAndTime.instance;
        alphabets_builtin.addAll(Arrays.asList(builtinAlphabetArray));
    }
}
