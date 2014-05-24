//  Author: Ronald Pablos
//  Year: 2013

package fastinfoset.tools;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author rpablos
 */
public class FrequencyAnalyzer {
    public static void main(String args[]) throws Exception {
        myDefaultHandler handler = new myDefaultHandler();
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        saxFactory.setNamespaceAware(true);
        SAXParser parser = saxFactory.newSAXParser();
        for (String arg: args) {
            parser.reset();
            parser.parse(new File(arg), handler);
        }
        handler.printFrequency();
    }
    
    static class myDefaultHandler extends DefaultHandler {
        static class IntegerHolder {
            int theInteger = 0;
        }
        Map<String,IntegerHolder> namespaces = new HashMap<String, IntegerHolder>();
        Map<String,IntegerHolder> prefixes = new HashMap<String, IntegerHolder>();
        Map<String,IntegerHolder> localnames = new HashMap<String, IntegerHolder>();
        Map<QName,IntegerHolder> elementnames = new HashMap<QName, IntegerHolder>();
        Map<QName,IntegerHolder> attributenames = new HashMap<QName, IntegerHolder>();
        Map<String,IntegerHolder> attributevalues = new HashMap<String, IntegerHolder>();
        
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            String prefix = getPrefix(qName);
            CountIntoTable(localName, localnames);
            QName qname = new QName(uri, localName, prefix);
            CountIntoTable(qname, elementnames);
            for (int i = 0; i < attributes.getLength(); i++) {
                prefix = getPrefix(attributes.getQName(i));
                qname = new QName(attributes.getURI(i), attributes.getLocalName(i), prefix);
                CountIntoTable(qname, attributenames);
                CountIntoTable(attributes.getValue(i), attributevalues);
            }
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            if (!uri.isEmpty()) CountIntoTable(uri, namespaces);
            if (!prefix.isEmpty()) CountIntoTable(prefix, prefixes);
        }
        
        <T> void CountIntoTable(T t, Map<T, IntegerHolder> map) {
            IntegerHolder ih = map.get(t);
            if (ih == null)
                map.put(t, ih = new IntegerHolder());
            ih.theInteger++;
        }
        static private String getPrefix(String qname) {
            int pos = qname.indexOf(':');
            return (pos > 0)?qname.substring(0, pos):"";
        }
        
        public <T> void printFrequency(String title, Map<T, IntegerHolder> map) {
            Map.Entry<T,IntegerHolder>[] entryArray = map.entrySet().toArray(new Entry[0]);
            Arrays.sort(entryArray,new Comparador<T>());
            System.out.println("\n"+title+" --> "+entryArray.length+" entries");
            for (Entry<T,IntegerHolder> entry: entryArray) {
                System.out.println(entry.getKey()+": "+entry.getValue().theInteger);
            }
        }
        public void printFrequency() {
            printFrequency("NAMESPACES", namespaces);
            printFrequency("PREFIXES", prefixes);
            printFrequency("LOCAL NAMES", localnames);
            printFrequency("ELEMENT NAMES", elementnames);
            printFrequency("ATTRIBUTE NAMES", attributenames);
            printFrequency("ATTRIBUTE VALUES", attributevalues);
        }
        static class Comparador<T> implements Comparator<Map.Entry<T,IntegerHolder>> {

            @Override
            public int compare(Entry<T, IntegerHolder> o1, Entry<T, IntegerHolder> o2) {
                return o2.getValue().theInteger - o1.getValue().theInteger;
            }
        }
    }
}
