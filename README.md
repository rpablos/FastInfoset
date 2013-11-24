FastInfoset
===========

FastInfoset implementation as ITU recommendation X.891

It provides DOM and SAX encoders and decoders, using the standards APIs.

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
    xmlreader.parse(new InputSource(new FileInputStream(origen)));
    ...
    
Also, you can use a `Transformer` with `FastInfosetResult` and `FastInfosetSource`.
For example, to process a FastInfoset document and transforms into a XML document:

    ...
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    FastInfosetSource source = new FastInfosetSource(new BufferedInputStream(new FileInputStream(in)));
    StreamResult result = new StreamResult(out);
    transformer.transform(source, result);
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
