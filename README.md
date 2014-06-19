FastInfoset
===========

FastInfoset implementation as ITU recommendation [X.891](http://www.itu.int/rec/T-REC-X.891-200505-I).

It provides DOM and SAX encoders and decoders, using the standards APIs.
***
FastInfoset is very efficient way of transmitting a XML infoset, recommended for scarce bandwidth or high performance environments.
Though in a XML world, one never thinks in terms of abstract values, the data contained in a XML documents can be considered as a set of information: elements, attributes, characters, instructions, etc. That is what http://www.w3.org/TR/xml-infoset/ stands for. And this information can be transmitted in different ways, like a XML document or a FastInfoset document.


## Examples of usage


### Converting XML document to FastInfoset with SAX parsing:
    ...
    SAXParserFactory spf = SAXParserFactory.newInstance();
    spf.setNamespaceAware(true);
    SAXParser sp = spf.newSAXParser();
    XMLReader xmlreader = sp.getXMLReader();
    SAX_FI_Encoder myhandler = new SAX_FI_Encoder();
    
    myhandler.setOutputStream(out);
    xmlreader.setContentHandler(myhandler);
    xmlreader.setDTDHandler(myhandler);
    xmlreader.setProperty("http://xml.org/sax/properties/lexical-handler", myhandler);
    xmlreader.parse(new InputSource(new FileInputStream(in)));
    ...
    

    
### Converting XML document to FastInfoset with DOM parsing:
    ...
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document doc = db.parse(in);
    DOM_FI_Encoder domser = new DOM_FI_Encoder();
    domser.setOutputStream(out);
    domser.setEncodeXMLEncoding(true); //optional
    domser.setEncodeXMLVersion(true);  //optional
    domser.serialize(doc);
    ...

### Converting a FastInfoset to XML document with DOM parsing
    ...
    DOM_FI_Decoder domdecoder = new DOM_FI_Decoder();
    Document docpar = domdecoder.parse(new BufferedInputStream(new FileInputStream(in)));

    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource source = new DOMSource(docpar);
    StreamResult result = new StreamResult(out);
    transformer.transform(source, result);
    ...
### Converting a FastInfoset to XML document with SAX parsing

Apart of common SAX parsing based on event notification to handlers, also, you can use a `Transformer` with `FastInfosetResult` and `FastInfosetSource`.
For example, to process a FastInfoset document and transforms into a XML document:

    ...
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    FastInfosetSource source = new FastInfosetSource(new BufferedInputStream(new FileInputStream(in)));
    StreamResult result = new StreamResult(out);
    transformer.transform(source, result);
    ...

## Example of a XML document and comparison of size
Given the following XML document:
    
    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <example xmlns="http://mynamespace.com" xmlns:pf="prefix" xmlns:ns2="http://namespace2.com" xmlns:ns3="http://namespace3.com">
        <InformationElement>
            <!-- This is a comment -->
            <InformationElementId>0</InformationElementId>
            <label1>value 1</label1>
            <pf:NothingToDoLabel>value 1 in prefix namespace</pf:NothingToDoLabel>
            <label2>value 2</label2>
            <label3>value 3</label3>
            <label4 attribute="attribute value">This is the value 4</label4>
            <ns2:labelArray>
                <ns3:item>100</ns3:item>
                <ns3:item>80</ns3:item>
                <ns3:item>90</ns3:item>
                <ns3:item>100</ns3:item>
                <ns3:item>110</ns3:item>
            </ns2:labelArray>
        </InformationElement>
        <InformationElement>
            <!-- just another comment -->
            <InformationElementId>1</InformationElementId>
            <label1>value 5</label1>
            <pf:NothingToDoLabel>This is the value 2 in prefix namespace</pf:NothingToDoLabel>
            <label2>value 6</label2>
            <label3>value 7</label3>
            <label4 attribute="attribute value">This is the value 8</label4>
            <ns2:labelArray>
                <ns3:item>110</ns3:item>
                <ns3:item>80</ns3:item>
                <ns3:item>90</ns3:item>
                <ns3:item>100</ns3:item>
                <ns3:item>90</ns3:item>
            </ns2:labelArray>
        </InformationElement>
        <InformationElement>
            <InformationElementId>2</InformationElementId>
            <label1>value 9</label1>
            <pf:NothingToDoLabel>value 3 in prefix namespace</pf:NothingToDoLabel>
            <label2>value 10</label2>
            <label3>value 11</label3>
            <label4 attribute="attribute value">value 12</label4>
            <ns2:labelArray>
                <ns3:item>90</ns3:item>
                <ns3:item>100</ns3:item>
                <ns3:item>80</ns3:item>
                <ns3:item>110</ns3:item>
                <ns3:item>100</ns3:item>
            </ns2:labelArray>
        </InformationElement>
        <![CDATA[This is cdata]]>
    </example>
    
The size of the XML document is <b>2120</b> octets.

The size of the Fast Infoset is <b>743</b> octets without using any extra optimization like algorithms, alphabets or external vocabularies.

The first one does not fit in a common 1500 octets IP packet. The second does.

See [wiki](https://github.com/rpablos/FastInfoset/wiki/Fast-Infoset-Wiki) for some information about tools for playing with fast infoset.
