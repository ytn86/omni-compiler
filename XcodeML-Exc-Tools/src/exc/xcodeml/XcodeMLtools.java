/*
 * $TSUKUBA_Release: Omni OpenMP Compiler 3 $
 * $TSUKUBA_Copyright:
 *  PLEASE DESCRIBE LICENSE AGREEMENT HERE
 *  $
 */
// package exc.xcodeml;

package exc.xcodeml;

import java.io.*;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.StreamSource;

import exc.object.*;

import xcodeml.IXobject;

/**
 * basse tools class for XcodeML
 */
public abstract class XcodeMLtools {
	protected XobjectFile xobjFile;

	// constructor
	public XcodeMLtools() {
	}

	// abstract public void write(Xobject File xobj, String xmlFile);
	// abstract public void decompile(Xobject File xobj, String xmlFile);

	/*
	 * DOM reader
	 */
	public Document readDocument(Reader reader) {
		Document doc = null;
		String s = null;
		try {
			StringBuilder sb = new StringBuilder(1024 * 4);
			BufferedReader br = new BufferedReader(reader);
            		String line = null;

            		while((line = br.readLine()) != null) {
            			sb.append(line);
            			sb.append("\n");
            }

            		s = sb.toString();
            		sb = null;
            		StringReader sreader = new StringReader(s);
            		InputSource source = new InputSource(sreader);

            		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(source);
		} catch (Exception e) {
			fatal("error in DOM read e=" + e);
			System.exit(1);
		}
		return doc;
	}

	Node getElement(Node n, String name) {
		if (n == null)
			return null;

		NodeList list = n.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node thisNode = list.item(i);
			if (thisNode.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (thisNode.getNodeName() == name)
				return thisNode;
		}
		return null;
	}

	Node getContent(Node n) {
		if (n == null)
			return null;

		NodeList list = n.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node thisNode = list.item(i);
			if (thisNode.getNodeType() != Node.ELEMENT_NODE)
				continue;
			return thisNode;
		}
		return null;
	}

	String getContentText(Node n) {
		if (n == null)
			return null;
		if (n.getFirstChild() == null) {
		    return "";
		}

		return n.getFirstChild().getNodeValue();
	}

	String getAttr(Node n, String name) {
		if (n == null)
			return null;

		Node nn = n.getAttributes().getNamedItem(name);
		if (nn != null)
			return nn.getNodeValue();
		return null;
	}

	boolean getAttrBool(Node n, String name) {
		if (n == null)
			return false;

		Node nn = n.getAttributes().getNamedItem(name);
		if (nn != null)
			return true;
		return false;
	}

	Xobject getAttrIntFlag(Node n, String name) {
		if (n == null)
			return Xcons.Int(Xcode.INT_CONSTANT, 0);

		Node nn = n.getAttributes().getNamedItem(name);
		if (nn != null)
			return Xcons.Int(Xcode.INT_CONSTANT, 1);
		return Xcons.Int(Xcode.INT_CONSTANT, 0);
	}

	// abstract public void write(Xobject File xobj, String xmlFile);
	// abstract public void decompile(Xobject File xobj, String xmlFile);

	abstract void enterType(Node nn);

	abstract void enterGlobalIdent(Node nn);

	abstract void enterGlobalDecl(Node nn);

	abstract Xobject toIdent(Node n);

	abstract Xobject toXobject(Node n);

	public XobjectFile read(Reader reader) {
		Document doc = readDocument(reader);
		Node rootNode = doc.getDocumentElement();
		if (rootNode.getNodeName() != "XcodeProgram")
			fatal("Toplevel must be XcodeProgram");

		xobjFile = new XobjectFile();

		xobjFile.setProgramAttributes(getAttr(rootNode, "source"),
				getAttr(rootNode, "language"),
				getAttr(rootNode, "compile-info"),
				getAttr(rootNode, "version"), getAttr(rootNode, "time"));

		Node n, nn;
		NodeList list;

		// process type table part
		n = getElement(rootNode, "typeTable");
		list = n.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			nn = list.item(i);
			if (nn.getNodeType() != Node.ELEMENT_NODE)
				continue;
			enterType(nn);
		}

		// fix type
		xobjFile.fixupTypeRef();

		// process global symbol tables
		n = getElement(rootNode, "globalSymbols");
		xobjFile.setIdentList(toIdentList(n));

		// process global declarations
		n = getElement(rootNode, "globalDeclarations");
		list = n.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			nn = list.item(i);
			if (nn.getNodeType() != Node.ELEMENT_NODE)
				continue;
			enterGlobalDecl(nn);
		}

		setIdentDecl(xobjFile);

		return xobjFile;
	}

	Xobject toIdentList(Node n) {
		Xobject identList = new XobjList(Xcode.ID_LIST);
		NodeList list = n.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node nn = list.item(i);
			if (nn.getNodeType() != Node.ELEMENT_NODE)
				continue;
			identList.add(toIdent(nn));
		}
		return identList;
	}

	private void setIdentDecl(XobjectFile objFile) {
		topdownXobjectDefIterator ite = new topdownXobjectDefIterator(objFile);
		for (ite.init(); !ite.end(); ite.next()) {
			XobjectDef def = ite.getDef();
			if (!def.isFuncDef() && !def.isFmoduleDef())
				continue;
			Xobject decls = def.getFuncDecls();
			if (decls == null)
				continue;
			for (Xobject decl : (XobjList) decls) {
				if (decl.Opcode() == Xcode.VAR_DECL) {
					String name = decl.getArg(0).getName();
					Ident id = def.findIdent(name, IXobject.FINDKIND_VAR);
					if (id != null && id.Type() != null
							&& id.Type().isFparameter()) {
						id.setFparamValue(decl.getArgOrNull(1));
					}
				}
			}
		}
	}

	Xtype getType(String tid) {
		return xobjFile.findType(tid);
	}

	// for debug
	static void printXML(Document doc, OutputStream out) {
		try {
			javax.xml.transform.TransformerFactory tfactory = javax.xml.transform.TransformerFactory
					.newInstance();
			javax.xml.transform.Transformer xform = tfactory.newTransformer();
			javax.xml.transform.Source src = new DOMSource(doc);
			javax.xml.transform.Result result = new javax.xml.transform.stream.StreamResult(
					out);
			xform.transform(src, result);
		} catch (javax.xml.transform.TransformerException e) {
			System.err.println("error:" + e);
		}
	}

	static void fatal(String msg) {
		System.err.println("Fatal XcodeMLtools: " + msg);
		Thread.dumpStack();
		System.exit(1);
	}
}
