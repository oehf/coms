/*
* Copyright 2012 the original author or authors.
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

package org.openehealth.coms.cc.web_frontend.consentcreator.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URL;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.log4j.Logger;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.Base64;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.OIDObject;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * DocumentFactory provides the operations needed for construction of the
 * consent documents.
 * 
 * @author Lennart Koester, Markus Birkle, Nilay Yueksekogul
 * @version 1.0 14.02.2011
 * 
 */

public class DocumentFactory {

	private URL classpath = DocumentFactory.class.getResource("");
	String filepathFiles = "";
	String filepathFilesConfig = "";
	String filepathFilesSkeleton = "";
	String filepathFilesUnclearedConsents = "";

	private String urlPolicySetSkeleton = "";
	private String urlCdaSkeleton = "";
	private String urlStylesheet = "";
	private String urlRule = "";
	private String urlRuleBasicParticipation = "";
	private String urlRuleDenyParticipation = "";

	private String urlCustodian = "";
	private String urlDataEnterer = "";
	private String urlLegalAuthenticator = "";
	private String urlAssignedAuthor = "";
	private String urlScanningDevice = "";

	private String urlResources = "";
	private String urlEmailProperties = "";
	private String urlEmailFile = "";

	private String urlShipperProperties = "";

	private FopFactory fopFactory;
	private FOUserAgent foUserAgent;
	private Transformer transformer;
	private Transformer serializer;

	private Document domResources = null;
	private Properties emailProperties = new Properties();
	private String stringEmail = new String();

	private Properties shipperProperties = new Properties();

	private String urlEmailHeaderImage = "";
	private String urlEmailDashImage = "";

	private String allOID = "";

	private Properties fileProperties = new Properties();

	public DocumentFactory() {

		filepathFiles = classpath.toString();
		
		try {
			File fileP = new File((new URL(filepathFiles.substring(0,
					filepathFiles.length() - 8)
					+ "files/config/filenames.properties").getFile()));
			FileInputStream inP = new FileInputStream(fileP);
			fileProperties.load(inP);
			inP.close();
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		filepathFiles = filepathFiles.substring(0, filepathFiles.length() - 8)
				+ fileProperties.getProperty("path.files");

		filepathFilesConfig = filepathFiles
				+ fileProperties.getProperty("path.config");
		filepathFilesSkeleton = filepathFiles
				+ fileProperties.getProperty("path.skeleton");
		filepathFilesUnclearedConsents = fileProperties
				.getProperty("path.unclearedconsents");

		urlPolicySetSkeleton = filepathFilesSkeleton
				+ fileProperties.getProperty("filename.policySkeleton");
		urlCdaSkeleton = filepathFilesSkeleton
				+ fileProperties.getProperty("filename.cdaSkeleton");
		urlStylesheet = filepathFilesSkeleton
				+ fileProperties.getProperty("filename.stylesheet");
		urlRule = filepathFilesSkeleton
				+ fileProperties.getProperty("filename.rule");
		urlRuleBasicParticipation = filepathFilesSkeleton
				+ fileProperties.getProperty("filename.ruleBasicParticipation");
		urlRuleDenyParticipation = filepathFilesSkeleton
				+ fileProperties.getProperty("filename.ruleDenyParticipation");

		urlCustodian = filepathFilesConfig
				+ fileProperties.getProperty("filename.custodian");
		urlDataEnterer = filepathFilesConfig
				+ fileProperties.getProperty("filename.dataEnterer");
		urlLegalAuthenticator = filepathFilesConfig
				+ fileProperties.getProperty("filename.legalAuthenticator");
		urlAssignedAuthor = filepathFilesConfig
				+ fileProperties.getProperty("filename.assignedAuthor");
		urlScanningDevice = filepathFilesConfig
				+ fileProperties.getProperty("filename.scanningDevice");

		urlResources = filepathFilesConfig
				+ fileProperties.getProperty("filename.resources");

		urlEmailProperties = filepathFilesConfig
				+ fileProperties.getProperty("filename.email.properties");
		urlEmailFile = filepathFilesSkeleton
				+ fileProperties.getProperty("filename.email");
		urlEmailHeaderImage = filepathFilesSkeleton
				+ fileProperties.getProperty("filename.email.logo");
		urlEmailDashImage = filepathFilesSkeleton
				+ fileProperties.getProperty("filename.email.dash");

		urlShipperProperties = filepathFilesConfig
				+ fileProperties.getProperty("filename.shipper.properties");

		try {
			fopFactory = FopFactory.newInstance();
			foUserAgent = fopFactory.newFOUserAgent();

			TransformerFactory factory = TransformerFactory.newInstance();

			URL u = new URL(urlStylesheet);

			transformer = factory.newTransformer(new StreamSource(u.getFile()));
			transformer.setParameter("versionParam", "2.0");

			serializer = factory.newTransformer();
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");

			domResources = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(urlResources.toString());

			allOID = domResources.getElementsByTagName("resource").item(0)
					.getAttributes().item(1).getTextContent();

			File file = new File((new URL(urlEmailProperties)).getFile());
			FileInputStream in = new FileInputStream(file);
			emailProperties.load(in);
			in.close();

			File emailFile = new File((new URL(urlEmailFile)).getFile());
			stringEmail = FileUtils.readFileToString(emailFile);

			emailProperties.setProperty("mail.image.headerbackground",
					urlEmailHeaderImage);
			emailProperties.setProperty("mail.image.dash", urlEmailDashImage);

			file = new File((new URL(urlShipperProperties)).getFile());
			FileInputStream in2 = new FileInputStream(file);
			shipperProperties.load(in2);
			in2.close();

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}

	/**
	 * Creates a PDF representation of the given CDA String and writes the
	 * resulting PDF into the OutputStream.
	 * 
	 * @param cdaFile
	 * @param out
	 * @return - An XML Element containing the Base64 encodet PDF.
	 */
	private Document constructPDF(String cdaAsString, OutputStream out) {

		Document nonXMLBody = null;

		if (out == null) {

			out = new ByteArrayOutputStream();
		}

		try {
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent,
					out);

			Source srcN = new StreamSource(new StringReader(cdaAsString));
			Source srcN2 = new StreamSource(new StringReader(cdaAsString));

			Result res = new SAXResult(fop.getDefaultHandler());
			transformer.transform(srcN, res);

			nonXMLBody = DocumentBuilderFactory
					.newInstance()
					.newDocumentBuilder()
					.parse(new InputSource(
							new java.io.StringReader(
									"<nonXMLBody><text mediaType=\"application/pdf\" representation=\"B64\"></text></nonXMLBody>")));

			ByteArrayOutputStream bout = new ByteArrayOutputStream();

			Fop fop2 = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent,
					bout);

			res = new SAXResult(fop2.getDefaultHandler());

			transformer.transform(srcN2, res);

			byte[] array = bout.toByteArray();

			String pdfB64 = Base64.encodeBytes(array);

			nonXMLBody.getElementsByTagName("text").item(0)
					.setTextContent(pdfB64);

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
		return nonXMLBody;
	}

	/**
	 * Constructs a CDA Consent for the given user.
	 * 
	 * @param user
	 * @param policySet
	 *            - null if either the participation should end or if a new
	 *            PolicySet should be created.
	 * @param author
	 * @param participation
	 *            - true if the user wants to participate, else false
	 * @param out
	 *            - The OutputStream on which the generated PDF presentation
	 *            will be written
	 * @return - The generated CDA file.
	 * 
	 */
	public Document constructCDA(User user, Document policySet, User author,
			boolean participation, OutputStream out) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document domCDA = null;
		Document domAssignedAuthor = null;
		Document domScanningDevice = null;
		Document domDataEnterer = null;
		Document domCustodian = null;
		Document domLegalAuthenticator = null;
		String cdaAsString = "";

		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
			
			domCDA = db.parse(urlCdaSkeleton);
			domAssignedAuthor = db.parse(urlAssignedAuthor);
			domScanningDevice = db.parse(urlScanningDevice);
			domDataEnterer = db.parse(urlDataEnterer);
			domCustodian = db.parse(urlCustodian);
			domLegalAuthenticator = db.parse(urlLegalAuthenticator);

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		Date d = new Date();

		String uniqueID = RandomStringUtils.randomNumeric(64);

		domCDA.getElementsByTagName("id").item(0).getAttributes().item(0)
				.setNodeValue(uniqueID);
		domCDA.getElementsByTagName("id").item(0).getAttributes().item(1)
				.setNodeValue("1.2.276.0.76.3.1.78.1.0.10.40");

		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmssZ");

		domCDA.getElementsByTagName("effectiveTime").item(0).getAttributes()
				.item(0).setTextContent(sd.format(d));

		Node title = domCDA.getElementsByTagName("title").item(0);

		if (participation) {
			title.setTextContent("Dies ist die Einwilligungserklärung für die Teilnahme an ISIS von "
					+ user.getForename() + " " + user.getName());
		} else {
			title.setTextContent("Dieses Dokument erklärt den Verzicht von "
					+ user.getForename() + " " + user.getName()
					+ " auf die Teilnahme an ISIS.");
		}

		Node pR = domCDA.getElementsByTagName("patientRole").item(0);

		NodeList prChildren = pR.getChildNodes();

		Node id = prChildren.item(1);

		// extension
		id.getAttributes().item(0).setNodeValue(user.getID()); // PID
		// root
		id.getAttributes().item(1)
				.setNodeValue("1.2.276.0.76.3.1.78.1.0.10.20"); // Assigning
																// Authority

		NodeList addr = prChildren.item(3).getChildNodes();

		addr.item(1).setTextContent(user.getStreet());
		addr.item(3).setTextContent(user.getCity());
		addr.item(5).setTextContent("");//Hier könnte das Bundesland
		// stehen!
		
		addr.item(7).setTextContent(String.valueOf(user.getZipcode()));
		addr.item(9).setTextContent("Deutschland");// Es sollte nicht
													// standardmäßig Deutschland
													// gesetzt werden!

		NodeList pat = prChildren.item(5).getChildNodes();

		NodeList name = pat.item(1).getChildNodes();

		if (user.getGender().equalsIgnoreCase("male")) {
			name.item(1).setTextContent("Herr");
			pat.item(3).getAttributes().item(0).setTextContent("M");
		} else {
			name.item(1).setTextContent("Frau");
			pat.item(3).getAttributes().item(0).setTextContent("F");
		}
		name.item(3).setTextContent(user.getForename());
		name.item(5).setTextContent(user.getName());

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

		pat.item(5).getAttributes().item(0)
				.setTextContent(formatter.format(user.getBirthdate()));

		if (policySet == null) {
			policySet = getSkeletonPolicySet(participation, user);

		}
		Document doc = db.newDocument();

		Element root = doc.createElement("component");
		doc.appendChild(root);

		Node copy = doc.importNode(policySet.getDocumentElement(), true);
		root.appendChild(copy);

		NodeList list = domCDA.getElementsByTagName("structuredBody");

		Node f = domCDA.importNode(doc.getDocumentElement(), true);

		list.item(0).appendChild(f);

		Node docuOfNode = domCDA.getElementsByTagName("documentationOf")
				.item(0);

		if (author == user) {

			domAssignedAuthor.getElementsByTagName("time").item(0)
					.getAttributes().item(0).setNodeValue(sd.format(d));

			domAssignedAuthor.getElementsByTagName("id").item(0)
					.getAttributes().item(0).setNodeValue(author.getID()); // ID
			domAssignedAuthor.getElementsByTagName("id").item(0)
					.getAttributes().item(1)
					.setNodeValue("1.2.276.0.76.3.1.78.1.0.10.20");// MPI-assigning
																	// authority

			if (user.getGender().equalsIgnoreCase("male")) {
				domAssignedAuthor.getElementsByTagName("prefix").item(0)
						.setTextContent("Herr");
			} else {
				name.item(1).setTextContent("Frau");
				domAssignedAuthor.getElementsByTagName("prefix").item(0)
						.setTextContent("Frau");
			}

			// suffix Wert setzen
			// domAssignedAuthor.getElementsByTagName("suffix").item(0).getAttributes().item(0).setNodeValue("");
			
			domAssignedAuthor.getElementsByTagName("given").item(0)
					.setTextContent(author.getForename());
			domAssignedAuthor.getElementsByTagName("family").item(0)
					.setTextContent(author.getName());

			// TODO Werte setzen assignedPerson oder Werte loeschen
		} else if (author != user && author != null) {
			// Zukunft: Erzeugung durch Dritte
		} else {
			// Std values from AssignedAuthor.xml
		}

		Node copyAssignedAuthorNode = domCDA.importNode(
				domAssignedAuthor.getDocumentElement(), true);
		Node copyScanningDeviceNode = domCDA.importNode(
				domScanningDevice.getDocumentElement(), true);
		Node copyDataEntererNode = domCDA.importNode(
				domDataEnterer.getDocumentElement(), true);
		Node copyCustodianNode = domCDA.importNode(
				domCustodian.getDocumentElement(), true);
		Node copyLegalAuthenticator = domCDA.importNode(
				domLegalAuthenticator.getDocumentElement(), true);

		sd.applyPattern("yyyyMMdd");

		domCDA.getElementsByTagName("low").item(0).getAttributes().item(0)
				.setNodeValue(sd.format(d));

		Date d2 = new Date((long) (d.getTime() + 30 * 3.1556926 * Math.pow(10,
				10)));

		domCDA.getElementsByTagName("high").item(0).getAttributes().item(0)
				.setNodeValue(sd.format(d2));

		copyAssignedAuthorNode.getChildNodes().item(3).getAttributes().item(0)
				.setNodeValue(sd.format(d));

		Node clinicalDocumentNode = domCDA.getElementsByTagName(
				"ClinicalDocument").item(0);

		clinicalDocumentNode.insertBefore(copyAssignedAuthorNode, docuOfNode);
		clinicalDocumentNode.insertBefore(copyScanningDeviceNode, docuOfNode);
		clinicalDocumentNode.insertBefore(copyDataEntererNode, docuOfNode);
		clinicalDocumentNode.insertBefore(copyCustodianNode, docuOfNode);
		clinicalDocumentNode.insertBefore(copyLegalAuthenticator, docuOfNode);

		Document noNSCDA = (Document) domCDA.cloneNode(true);

		NodeList l = noNSCDA.getElementsByTagName("ClinicalDocument");

		NamedNodeMap attributes = l.item(0).getAttributes();
		attributes.removeNamedItem("xmlns");
		attributes.removeNamedItem("xmlns:voc");
		attributes.removeNamedItem("xmlns:xsi");
		attributes.removeNamedItem("xsi:schemaLocation");

		l = noNSCDA.getElementsByTagName("PolicySet");
		attributes = l.item(0).getAttributes();
		attributes.removeNamedItem("xmlns");
		attributes.removeNamedItem("PolicyCombiningAlgId");
		attributes.removeNamedItem("xmlns:xsi");
		attributes.removeNamedItem("xsi:schemaLocation");
		attributes.removeNamedItem("PolicySetId");

		formatter = new SimpleDateFormat("dd.MM.yyyy");

		noNSCDA.getElementsByTagName("birthTime").item(0).getAttributes()
				.item(0).setTextContent(formatter.format(user.getBirthdate()));

		cdaAsString = getStringFromDocument(noNSCDA);
		Document nonXMLBody = constructPDF(cdaAsString, out);

		Node copyNode = domCDA
				.importNode(nonXMLBody.getDocumentElement(), true);

		Node component = domCDA.getElementsByTagName("component").item(0);

		Node structBody = component.getChildNodes().item(1);

		component.insertBefore(copyNode, structBody);

		NamedNodeMap nlm = domCDA.getDocumentElement()
				.getElementsByTagName("PolicySet").item(0).getAttributes();
		// nlm.removeNamedItem("xmlns:xsi");
		// nlm.removeNamedItem("xsi:schemaLocation");

		return domCDA;
	}
	

	
	
	/**
	 * Creates a PolicySet for the given user under the aspect whether he wants
	 * to participate or not.
	 * 
	 * @param participation
	 * @param user
	 * @return
	 */
	public Document getSkeletonPolicySet(boolean participation, User user) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		Document domPolicySet = null;

		String document = urlPolicySetSkeleton.toString();
		String rule = "";
		String text = "";

		if (participation) {
			text = "Dies ist die Einwilligungserklärung von "
					+ user.getForename() + " " + user.getName();
			rule = urlRuleBasicParticipation.toString();
		} else {
			text = user.getForename() + " " + user.getName()
					+ " verweigert mit diesem Dokument die Teilnahme an ISIS";
			rule = urlRuleDenyParticipation.toString();
		}

		try {
			DocumentBuilder db = dbf.newDocumentBuilder();

			domPolicySet = db.parse(document);

			NodeList listDesc = domPolicySet.getDocumentElement()
					.getElementsByTagName("Description");

			listDesc.item(0).setTextContent(text);

			Document rdw = db.parse(rule);

			Node nm = rdw.getFirstChild();

			Element el = (Element) nm;

			Date k = new Date();

			el.setAttribute(
					"RuleId",
					"urn:oasis:names:tc:xacml:2.0:example:ruleid:"
							+ k.getTime());

			NodeList t = rdw.getElementsByTagName("AttributeValue");

			String value = getResources().get(0).getIdentifier();

			t.item(0).setNodeValue(value);

			Node f = domPolicySet.importNode(rdw.getDocumentElement(), true);

			NodeList l = domPolicySet.getElementsByTagName("Policy");

			l.item(3).appendChild(f);

			domPolicySet.getElementsByTagName("AttributeValue").item(1)
					.setTextContent(value);

			if (!participation) {

				l.item(0).getParentNode().removeChild(l.item(0));
				l.item(0).getParentNode().removeChild(l.item(0));
				l.item(0).getParentNode().removeChild(l.item(0));
			}

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		return domPolicySet;
	}

	/**
	 * Produces a MD5 Hash based on the contents of the Document
	 * 
	 * @param dom
	 * @return
	 */
	public String getMD5Hash(Document dom) {

		String hash = null;
		String s = getStringFromDocument(dom);

		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(s.getBytes(), 0, s.length());
			hash = new BigInteger(1, md5.digest()).toString(16);

		} catch (final NoSuchAlgorithmException e) {
			Logger.getLogger(this.getClass()).error(e);
		}
		return hash;
	}

	/**
	 * Produces a MD5 Hash based on the given String
	 * 
	 * @param s
	 * @return
	 */
	public String getMD5Hash(String s) {

		String hash = null;

		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(s.getBytes(), 0, s.length());
			hash = new BigInteger(1, md5.digest()).toString(16);

		} catch (final NoSuchAlgorithmException e) {
			Logger.getLogger(this.getClass()).error(e);
		}
		return hash;
	}

	/**
	 * Deletes the uncleared consent with the given filename from the file
	 * system.
	 * 
	 * @param filename
	 * @return
	 */
	public boolean deleteUnclearedConsent(String filename) {

		File deleteFile = new File(filepathFilesUnclearedConsents, filename
				+ ".xml");

		return deleteFile.delete();

	}

	/**
	 * Reads the document types from the resources.xml file.
	 * 
	 * @return
	 */
	public Vector<OIDObject> getResources() {

		Vector<OIDObject> voo = new Vector<OIDObject>();

		NodeList nl = domResources.getElementsByTagName("resource");

		for (int i = 0; i < nl.getLength(); i++) {

			String name = nl.item(i).getAttributes().item(0).getNodeValue();
			String identifier = nl.item(i).getAttributes().item(1)
					.getNodeValue();

			voo.add(new OIDObject(identifier, name, true));
		}
		return voo;
	}

	/**
	 * Returns the email Properties.
	 * 
	 * @return
	 */
	public Properties getEmailProperties() {

		return emailProperties;
	}

	/**
	 * Returns the emails needed for user-interaction
	 * 
	 * @return
	 */
	public String getEmailBody() {

		return stringEmail;
	}

	/**
	 * Returns the shipper Properties.
	 * 
	 * @return
	 */
	public Properties getShipperProperties() {
		return shipperProperties;
	}

	/**
	 * Returns the String representation of the given Document.
	 * 
	 * @param doc
	 * @return
	 */

	private String getStringFromDocument(Node root) {
		
		StringBuilder result = new StringBuilder();

		if (root.getNodeType() == 3)
			result.append(root.getNodeValue());
		else {
			if (root.getNodeType() != 9) {
				StringBuffer attrs = new StringBuffer();
				for (int k = 0; k < root.getAttributes().getLength(); ++k) {
					attrs.append(" ")
							.append(root.getAttributes().item(k).getNodeName())
							.append("=\"")
							.append(root.getAttributes().item(k).getNodeValue())
							.append("\" ");
				}
				result.append("<").append(root.getNodeName()).append(" ")
						.append(attrs).append(">");
			} else {
				result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			}

			NodeList nodes = root.getChildNodes();
			for (int i = 0, j = nodes.getLength(); i < j; i++) {
				Node node = nodes.item(i);
				result.append(getStringFromDocument(node));
			}

			if (root.getNodeType() != 9) {
				result.append("</").append(root.getNodeName()).append(">");
			}
		}

		return result.toString();
	}

	/**
	 * Returns the CDA Document Object retrieved from the file with the given
	 * filename
	 * 
	 * @param filename
	 * @return
	 */
	public Document getUnclearedConsent(String filename) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document domCDA = null;

		DocumentBuilder db = null;

		File retrieveFile = new File(filepathFilesUnclearedConsents + filename
				+ ".xml");

		if (retrieveFile.exists()) {
			try {
				db = dbf.newDocumentBuilder();
				domCDA = db.parse(retrieveFile);
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}

		return domCDA;

	}

	/**
	 * Stores the given Document under the given filename onto the file system.
	 * 
	 * @param cdaFile
	 * @param filename
	 * @return
	 */
	public boolean storeUnclearedConsent(Document cdaFile, String filename) {

		File storeFile = new File(filepathFilesUnclearedConsents, filename
				+ ".xml");
		if (!storeFile.exists()) {
			try {
				storeFile.createNewFile();
			} catch (IOException e) {
				storeFile.delete();
				Logger.getLogger(this.getClass()).error(e);
				return false;
			}
		}
		try {

			// Prepare the DOM document for writing
			Source source = new DOMSource(cdaFile);

			// Prepare the output file
			Result result = new StreamResult(storeFile);

			// Write the DOM document to the file
			Transformer xformer = TransformerFactory.newInstance()
					.newTransformer();
			xformer.transform(source, result);
		} catch (Exception e) {
			storeFile.delete();
			Logger.getLogger(this.getClass()).error(e);
			return false;
		}
		return true;
	}

	/**
	 * Returns a Document Object containing the basic structure of a XACML Rule.
	 * 
	 * @return
	 */
	private Document getRuleSkeleton() {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document domRule = null;
		File ruleSkeletonFile = null;

		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		
			ruleSkeletonFile = new File(new URL(urlRule).getFile());
			
			domRule = db.parse(ruleSkeletonFile);
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
		return domRule;
	}

	/**
	 * Adds a Document Rule to the given PolicySet.
	 * 
	 * @param policySet
	 * @param rule
	 * @param ruleType
	 *            - defines to which Policy the rule will be added.
	 * @return
	 */
	public Document addRuleToPolicySet(Document policySet, Document rule,
			int ruleType) {

		NodeList policies = policySet.getElementsByTagName("Policy");

		Node policy = policies.item(ruleType - 1);
		
		Vector<Node> ruleList = new Vector<Node>();

		for (int i = 0; i < policy.getChildNodes().getLength(); i++) {
	
			if (policy.getChildNodes().item(i).getNodeName()
					.equalsIgnoreCase("Rule")) {
				
				ruleList.add(policy.getChildNodes().item(i));

			}
		}
		
		
		if (ruleList.size() > 0) {

			int ruleListLength = ruleList.size();

			String subjectOrgaNewRule = rule
					.getElementsByTagName("AttributeValue").item(0)
					.getTextContent();

			String subjectPersonelNewRule = rule
					.getElementsByTagName("AttributeValue").item(1)
					.getTextContent();

			String resourceNewRule = rule
					.getElementsByTagName("AttributeValue").item(2)
					.getTextContent();

			int i = ruleListLength - 1;

			while (i >= 0) {

				String subjectOrgaRule = ruleList.elementAt(i).getChildNodes()
						.item(3).getChildNodes().item(1).getChildNodes()
						.item(1).getChildNodes().item(1).getChildNodes()
						.item(1).getTextContent();
				String subjectPersonelRule = ruleList.elementAt(i)
						.getChildNodes().item(3).getChildNodes().item(1)
						.getChildNodes().item(1).getChildNodes().item(3)
						.getChildNodes().item(1).getTextContent();
				String resourceRule = ruleList.elementAt(i).getChildNodes()
						.item(3).getChildNodes().item(3).getChildNodes()
						.item(1).getChildNodes().item(1).getChildNodes()
						.item(1).getTextContent();

				if (getOIDLength(resourceRule) > getOIDLength(resourceNewRule)) {

					if (i == ruleListLength - 1) {

						Node copy = policySet.importNode(
								rule.getDocumentElement(), true);

						policy.appendChild(copy);
					} else {

						Node copy = policySet.importNode(
								rule.getDocumentElement(), true);

						policy.insertBefore(copy, ruleList.elementAt(i + 1));
					}
					i = -1;
				} else if (getOIDLength(resourceRule) == getOIDLength(resourceNewRule)) {

					if (getOIDLength(subjectOrgaRule) > getOIDLength(subjectOrgaNewRule)) {

						Node copy = policySet.importNode(
								rule.getDocumentElement(), true);

						if (i == ruleListLength - 1) {
							policy.appendChild(copy);
						} else {
							policy.insertBefore(copy, ruleList.elementAt(i + 1));

						}
					} else if (getOIDLength(subjectOrgaRule) == getOIDLength(subjectOrgaNewRule)) {

						if (subjectPersonelRule.length() > subjectPersonelNewRule
								.length()) {

							if (i == ruleListLength - 1) {

								Node copy = policySet.importNode(
										rule.getDocumentElement(), true);

								policy.appendChild(copy);
							} else {

								Node copy = policySet.importNode(
										rule.getDocumentElement(), true);

								policy.insertBefore(copy,
										ruleList.elementAt(i + 1));
							}
							i = -1;

						} else {

							Node copy = policySet.importNode(
									rule.getDocumentElement(), true);
							policy.insertBefore(copy, ruleList.elementAt(i));
							i = -1;
						}

					} else {

						if (i == 0) {
							Node copy = policySet.importNode(
									rule.getDocumentElement(), true);
							policy.insertBefore(copy, ruleList.elementAt(i));
							i = -1;
						} else {
							boolean added = false;

							for (int t = i; t >= 0; t--) {

								System.out.println(ruleList.elementAt(t)
										.getChildNodes().item(3)
										.getChildNodes().item(1)
										.getChildNodes().item(1)
										.getChildNodes().item(1)
										.getChildNodes().item(1)
										.getTextContent());

								if (getOIDLength(resourceNewRule) == getOIDLength(ruleList
										.elementAt(t).getChildNodes().item(3)
										.getChildNodes().item(3)
										.getChildNodes().item(1)
										.getChildNodes().item(1)
										.getChildNodes().item(1)
										.getTextContent())) {
									// Größer gleich?
									if (!(getOIDLength(subjectOrgaNewRule) > getOIDLength(ruleList
											.elementAt(t).getChildNodes()
											.item(3).getChildNodes().item(1)
											.getChildNodes().item(1)
											.getChildNodes().item(1)
											.getChildNodes().item(1)
											.getTextContent()))) {

										if (!(subjectPersonelNewRule.length() > getOIDLength(ruleList
												.elementAt(t).getChildNodes()
												.item(3).getChildNodes()
												.item(1).getChildNodes()
												.item(1).getChildNodes()
												.item(3).getChildNodes()
												.item(1).getTextContent()))) {

											if (t == ruleListLength - 1) {

												Node copy = policySet
														.importNode(
																rule.getDocumentElement(),
																true);

												policy.appendChild(copy);
											} else {

												Node copy = policySet
														.importNode(
																rule.getDocumentElement(),
																true);

												policy.insertBefore(
														copy,
														ruleList.elementAt(t + 1));
											}
											added = true;
											t = -1;

										} else {

											Node copy = policySet.importNode(
													rule.getDocumentElement(),
													true);
											policy.insertBefore(copy,
													ruleList.elementAt(t + 1));
											t = -1;
											added = true;
										}
									}
								} else {
									Node copy = policySet.importNode(
											rule.getDocumentElement(), true);
									policy.insertBefore(copy,
											ruleList.elementAt(t + 1));
									t = -1;
									added = true;
								}
							}
							if (!added) {
								Node copy = policySet.importNode(
										rule.getDocumentElement(), true);
								policy.insertBefore(copy, ruleList.elementAt(0));
								i = -1;
							}
						}
					}
					i = -1;
				} else if (getOIDLength(resourceRule) < getOIDLength(resourceNewRule)
						&& i == 0) {

					Node copy = policySet.importNode(rule.getDocumentElement(),
							true);

					policy.insertBefore(copy, policy.getChildNodes().item(2));
					i = -1;
				}
				i--;
			}

		} else {
			Node copy = policySet.importNode(rule.getDocumentElement(), true);
			policy.appendChild(copy);
		}
		return policySet;
	}

	/**
	 * Returns the length of the given oid
	 * 
	 * @param oid
	 * @return
	 */
	private int getOIDLength(String oid) {

		int length = 0;

		for (int i = 0; i < oid.length(); i++) {

			if (oid.charAt(i) == '.') {
				length++;
			}
		}
		return length;
	}

	/**
	 * Updates the Target Elements of all Policies included in this PolicySet
	 * 
	 * @param policySet
	 * @return
	 */
	public Document updateTargetElements(Document policySet) {

		NodeList listPolicies = policySet.getElementsByTagName("Policy");

		for (int i = 0; i < listPolicies.getLength(); i++) {

			updatePolicyTargets(listPolicies.item(i));

		}

		return policySet;
	}

	/**
	 * Updates the Target Element of the given Policy
	 * 
	 * @param policy
	 */
	private void updatePolicyTargets(Node policy) {

		Node target = policy.getChildNodes().item(1);

		while (target.hasChildNodes()) {

			target.removeChild(target.getFirstChild());
		}

		Vector<Node> resourceListFromRules = new Vector<Node>();

		for (int i = 0; i < policy.getChildNodes().getLength(); i++) {

			Node n = policy.getChildNodes().item(i);

			if (n.getNodeName().equalsIgnoreCase("Rule")) {

				resourceListFromRules.add(n);
			}
		}

		Vector<Node> resourceListFromTarget = new Vector<Node>();

		for (int i = 0; i < resourceListFromRules.size(); i++) {

			Node n = resourceListFromRules.elementAt(i);
			boolean matches = false;

			for (int k = 0; k < resourceListFromTarget.size(); k++) {

				if (n.getChildNodes()
						.item(3)
						.getChildNodes()
						.item(3)
						.getChildNodes()
						.item(1)
						.getChildNodes()
						.item(1)
						.getChildNodes()
						.item(1)
						.getTextContent()
						.equalsIgnoreCase(
								resourceListFromTarget.elementAt(k)
										.getChildNodes().item(3)
										.getChildNodes().item(3)
										.getChildNodes().item(1)
										.getChildNodes().item(1)
										.getChildNodes().item(1)
										.getTextContent())) {
					matches = true;
				}
			}
			if (!matches) {
				resourceListFromTarget.add(n.cloneNode(true));
			}
		}

		Document ress = null;
		try {
			ress = DocumentBuilderFactory
					.newInstance()
					.newDocumentBuilder()
					.parse(new InputSource(new java.io.StringReader(
							"<Resources></Resources>")));
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		if (resourceListFromTarget.size() != 0) {
			Node copy = target.getOwnerDocument().importNode(
					ress.getDocumentElement(), true);

			target.appendChild(copy);

			for (int f = 0; f < resourceListFromTarget.size(); f++) {

				Node res = policy.getOwnerDocument().importNode(
						resourceListFromTarget.elementAt(f).getChildNodes()
								.item(3).getChildNodes().item(3)
								.getChildNodes().item(1), true);

				copy.appendChild(res);

			}
		}
	}

	/**
	 * 
	 * Constructs a rule using the given data that represents an access rule.
	 * 
	 * @param organisation
	 *            - String representing the full name of the organisation
	 *            affected by this rule.
	 * @param persons
	 *            - The String representation of the persons affected by this
	 *            rule.
	 * @param affectedOID
	 *            - The OID of the persons affected by this rule. If persons
	 *            equals ALL then this is simply the OID of the organisation,
	 *            else it is the OID of the group of persons affected, e.g.
	 *            physicians.
	 * @param documents
	 *            - The String representation of the affected documents, can be
	 *            ALL or a specific documenttype, e.g. lab results.
	 * @param documentsOID
	 *            - The OID of the affected documents, can either be the
	 *            patients whole record or a a documenttype.
	 * @param accessType
	 *            - read, write or readwrite.
	 * @param grantAccess
	 *            - true if access is granted, else false.
	 * @return
	 */
	public Document constructRule(String organisation, String persons,
			String affectedOID, String documents, String documentsOID,
			String accessType, boolean grantAccess) {
		
		Document rule = getRuleSkeleton();

		String ruleEffect = "";

		String description = "";

		String subjectMatchOrga = affectedOID.split("\\^")[0];

		String subjectMatchPersonel = "";

		if (affectedOID.contains("^")) {

			subjectMatchPersonel = affectedOID.split("\\^")[1];
		}

		String resourceMatch = "";

		String action = "";

		if (documentsOID.equalsIgnoreCase(allOID)) {
			documents = "ALL";
		}

		// Rules on organisation level covering all documents of a patient
		if (persons.equalsIgnoreCase("ALL")
				&& !affectedOID.equalsIgnoreCase("")
				&& documents.equalsIgnoreCase("ALL")
				&& !documentsOID.equalsIgnoreCase("")
				&& accessType.equalsIgnoreCase("write") && grantAccess) {

			ruleEffect = "Permit";

			description = "Die Organisation " + organisation
					+ " darf alle meine Dokumente in ISIS einstellen.";

			resourceMatch = documentsOID;

			action = accessType;

		} else if (persons.equalsIgnoreCase("ALL")
				&& !affectedOID.equalsIgnoreCase("")
				&& documents.equalsIgnoreCase("ALL")
				&& !documentsOID.equalsIgnoreCase("")
				&& accessType.equalsIgnoreCase("write") && !grantAccess) {

			ruleEffect = "Deny";

			description = "Die Organisation " + organisation
					+ " darf keine meiner Dokumente in ISIS einstellen.";

			resourceMatch = documentsOID;

			action = accessType;

		} else if (persons.equalsIgnoreCase("ALL")
				&& !affectedOID.equalsIgnoreCase("")
				&& documents.equalsIgnoreCase("ALL")
				&& !documentsOID.equalsIgnoreCase("")
				&& accessType.equalsIgnoreCase("read") && grantAccess) {

			ruleEffect = "Permit";

			description = "Die Organisation " + organisation
					+ " darf alle meine Dokumente in ISIS lesen.";

			resourceMatch = documentsOID;

			action = accessType;

		} else if (persons.equalsIgnoreCase("ALL")
				&& !affectedOID.equalsIgnoreCase("")
				&& documents.equalsIgnoreCase("ALL")
				&& !documentsOID.equalsIgnoreCase("")
				&& accessType.equalsIgnoreCase("read") && !grantAccess) {

			ruleEffect = "Deny";

			description = "Die Organisation " + organisation
					+ " darf keine meiner Dokumente in ISIS lesen.";

			resourceMatch = documentsOID;

			action = accessType;

		} else if (persons.equalsIgnoreCase("ALL")
				&& !affectedOID.equalsIgnoreCase("")
				&& documents.equalsIgnoreCase("ALL")
				&& !documentsOID.equalsIgnoreCase("")
				&& accessType.equalsIgnoreCase("readwrite") && grantAccess) {

			ruleEffect = "Permit";

			description = "Die Organisation "
					+ organisation
					+ " darf alle meine Dokumente in ISIS einstellen und alle in ISIS verfügbaren Dokumente lesen.";

			resourceMatch = documentsOID;

			action = accessType;

		} else if (persons.equalsIgnoreCase("ALL")
				&& !affectedOID.equalsIgnoreCase("")
				&& documents.equalsIgnoreCase("ALL")
				&& !documentsOID.equalsIgnoreCase("")
				&& accessType.equalsIgnoreCase("readwrite") && !grantAccess) {

			ruleEffect = "Deny";

			description = "Die Organisation "
					+ organisation
					+ " darf weder meine Dokumente in ISIS einstellen und noch in ISIS verfügbare Dokumente lesen.";

			resourceMatch = documentsOID;

			action = accessType;

		}
		// Rules on organisation level covering a specific documenttype.
		else if (persons.equalsIgnoreCase("ALL")
				&& !affectedOID.equalsIgnoreCase("")
				&& !documents.equalsIgnoreCase("ALL")
				&& !documentsOID.equalsIgnoreCase("")
				&& accessType.equalsIgnoreCase("read") && grantAccess) {

			ruleEffect = "Permit";

			description = "Die Organisation " + organisation
					+ " darf meine, in ISIS verfügbaren, " + documents
					+ " lesen.";

			resourceMatch = documentsOID;

			action = accessType;

		} else if (persons.equalsIgnoreCase("ALL")
				&& !affectedOID.equalsIgnoreCase("")
				&& !documents.equalsIgnoreCase("ALL")
				&& !documentsOID.equalsIgnoreCase("")
				&& accessType.equalsIgnoreCase("read") && !grantAccess) {

			ruleEffect = "Deny";

			description = "Die Organisation " + organisation
					+ " darf meine, in ISIS verfügbaren, " + documents
					+ " nicht lesen.";

			resourceMatch = documentsOID;

			action = accessType;

		} else if (persons.equalsIgnoreCase("ALL")
				&& !affectedOID.equalsIgnoreCase("")
				&& !documents.equalsIgnoreCase("ALL")
				&& !documentsOID.equalsIgnoreCase("")
				&& accessType.equalsIgnoreCase("write") && grantAccess) {

			ruleEffect = "Permit";

			description = "Die Organisation " + organisation + " darf meine "
					+ documents + " in ISIS einstellen.";

			resourceMatch = documentsOID;

			action = accessType;

		} else if (persons.equalsIgnoreCase("ALL")
				&& !affectedOID.equalsIgnoreCase("")
				&& !documents.equalsIgnoreCase("ALL")
				&& !documentsOID.equalsIgnoreCase("")
				&& accessType.equalsIgnoreCase("write") && !grantAccess) {

			ruleEffect = "Deny";

			description = "Die Organisation " + organisation + " darf meine "
					+ documents + " nicht in ISIS einstellen.";

			resourceMatch = documentsOID;

			action = accessType;

		} else if (persons.equalsIgnoreCase("ALL")
				&& !affectedOID.equalsIgnoreCase("")
				&& !documents.equalsIgnoreCase("ALL")
				&& !documentsOID.equalsIgnoreCase("")
				&& accessType.equalsIgnoreCase("readwrite") && grantAccess) {

			ruleEffect = "Permit";

			description = "Die Organisation "
					+ organisation
					+ " darf meine, in ISIS verfügbaren "
					+ documents
					+ " lesen und anfallende Dokumente diesen Types in ISIS einstellen.";

			resourceMatch = documentsOID;

			action = accessType;

		} else if (persons.equalsIgnoreCase("ALL")
				&& !affectedOID.equalsIgnoreCase("")
				&& !documents.equalsIgnoreCase("ALL")
				&& !documentsOID.equalsIgnoreCase("")
				&& accessType.equalsIgnoreCase("readwrite") && !grantAccess) {

			ruleEffect = "Deny";

			description = "Die Organisation "
					+ organisation
					+ " darf meine, in ISIS verfügbaren, "
					+ documents
					+ " weder lesen noch anfallende Dokumente diesen Types in ISIS einstellen.";

			resourceMatch = documentsOID;

			action = accessType;

		}
		// Rules on staff level covering all documents
		else if (!persons.equalsIgnoreCase("ALL")
				&& !affectedOID.equalsIgnoreCase("")
				&& documents.equalsIgnoreCase("ALL")
				&& !documentsOID.equalsIgnoreCase("")
				&& accessType.equalsIgnoreCase("read") && grantAccess) {

			ruleEffect = "Permit";

			description = "Alle " + persons + " der Organisation "
					+ organisation
					+ " dürfen meine, in ISIS verfügbaren, Dokumente lesen.";

			resourceMatch = documentsOID;

			action = accessType;

		} else if (!persons.equalsIgnoreCase("ALL")
				&& !affectedOID.equalsIgnoreCase("")
				&& documents.equalsIgnoreCase("ALL")
				&& !documentsOID.equalsIgnoreCase("")
				&& accessType.equalsIgnoreCase("read") && !grantAccess) {

			ruleEffect = "Deny";

			description = "Alle "
					+ persons
					+ " der Organisation "
					+ organisation
					+ " dürfen meine, in ISIS verfügbaren, Dokumente nicht lesen.";

			resourceMatch = documentsOID;

			action = accessType;

		}
		// Rules on staff level covering a specific documenttype.
		else if (!persons.equalsIgnoreCase("ALL")
				&& !affectedOID.equalsIgnoreCase("")
				&& !documents.equalsIgnoreCase("ALL")
				&& !documentsOID.equalsIgnoreCase("")
				&& accessType.equalsIgnoreCase("read") && grantAccess) {

			ruleEffect = "Permit";

			description = "Alle " + persons + " der Organisation "
					+ organisation
					+ " dürfen alle meine, in ISIS verfügbaren, " + documents
					+ " lesen.";

			resourceMatch = documentsOID;

			action = accessType;

		} else if (!persons.equalsIgnoreCase("ALL")
				&& !affectedOID.equalsIgnoreCase("")
				&& !documents.equalsIgnoreCase("ALL")
				&& !documentsOID.equalsIgnoreCase("")
				&& accessType.equalsIgnoreCase("read") && !grantAccess) {

			ruleEffect = "Deny";

			description = "Alle " + persons + " der Organisation "
					+ organisation
					+ " dürfen alle meine, in ISIS verfügbaren, " + documents
					+ " nicht lesen.";

			resourceMatch = documentsOID;

			action = accessType;

		} else {
			description = "Nicht unterstützte Anfrage";
		}

		// Setting up the rule skeleton
		rule.getDocumentElement().setAttribute("Effect", ruleEffect);

		rule.getElementsByTagName("Description").item(0)
				.setTextContent(description);

		rule.getElementsByTagName("AttributeValue").item(0)
				.setTextContent(subjectMatchOrga);

		rule.getElementsByTagName("AttributeValue").item(1)
				.setTextContent(subjectMatchPersonel);

		rule.getElementsByTagName("AttributeValue").item(2)
				.setTextContent(resourceMatch);

		Document readAction = null;
		Document writeAction = null;

		try {
			readAction = DocumentBuilderFactory
					.newInstance()
					.newDocumentBuilder()
					.parse(new InputSource(
							new java.io.StringReader(
									"<Action><ActionMatch MatchId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\"><AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">read</AttributeValue><ActionAttributeDesignator AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" DataType=\"http://www.w3.org/2001/XMLSchema#string\"/></ActionMatch></Action>")));

			writeAction = DocumentBuilderFactory
					.newInstance()
					.newDocumentBuilder()
					.parse(new InputSource(
							new java.io.StringReader(
									"<Action><ActionMatch MatchId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\"><AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">write</AttributeValue><ActionAttributeDesignator AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" DataType=\"http://www.w3.org/2001/XMLSchema#string\"/></ActionMatch></Action>")));

		} catch (Exception e) {
			Logger.getLogger(DocumentFactory.class).error(e.getMessage());
		}

		if (action.equalsIgnoreCase("readwrite")) {

			Node copy = rule.importNode(readAction.getDocumentElement(), true);
			rule.getElementsByTagName("Actions").item(0).appendChild(copy);

			Node copy2 = rule
					.importNode(writeAction.getDocumentElement(), true);
			rule.getElementsByTagName("Actions").item(0).appendChild(copy2);
		} else if (action.equalsIgnoreCase("read")) {

			Node copy = rule.importNode(readAction.getDocumentElement(), true);
			rule.getElementsByTagName("Actions").item(0).appendChild(copy);
		} else if (action.equalsIgnoreCase("write")) {

			Node copy2 = rule
					.importNode(writeAction.getDocumentElement(), true);
			rule.getElementsByTagName("Actions").item(0).appendChild(copy2);
		}
		rule.getElementsByTagName("Rule").item(0).getAttributes().item(1)
				.setTextContent(Long.toString(new Date().getTime()));

		return rule;
	}


	/**
	 * Compares the MD5 Hashes of the given Document objects.
	 * 
	 * @param cdaFile
	 * @param orignalCDA
	 * @return
	 */
	private boolean isCDAoriginal(Document cdaFile, Document originalCDA) {

		NodeList nl = cdaFile.getElementsByTagName("Signature");
		if (nl.getLength() == 0) {
			return false;
		}

		(nl.item(0).getParentNode()).removeChild(nl.item(0));

		if (getMD5Hash(cdaFile).equals(getMD5Hash(originalCDA))) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether or not the given Document contains a valid XML Signature
	 * and if it has the exact same content as the original.
	 * 
	 * @param cdaFile
	 * @param originalCDA
	 * @return
	 */
	public boolean isXMLSignatureValid(Document cdaFile, Document originalCDA) {

		boolean coreValidity = false;

		try {

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			Document checkCDA = db.newDocument();

			Node copy = checkCDA.importNode(cdaFile.getDocumentElement(), true);
			checkCDA.appendChild(copy);

			if (!isCDAoriginal(checkCDA, originalCDA)) {
				return false;
			}

			// Find Signature element
			NodeList nl = cdaFile.getElementsByTagNameNS(XMLSignature.XMLNS,
					"Signature");
			if (nl.getLength() == 0) {
				return false;
			}

			// Create a DOM XMLSignatureFactory that will be used to unmarshal
			// the
			// document containing the XMLSignature
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

			// Create a DOMValidateContext and specify a KeyValue KeySelector
			// and document context
			DOMValidateContext valContext = new DOMValidateContext(
					new X509KeySelector(), nl.item(0));

			// unmarshal the XMLSignature
			XMLSignature signature = fac.unmarshalXMLSignature(valContext);

			// Validate the XMLSignature (generated above)
			coreValidity = signature.validate(valContext);

			// Check core validation status
			if (coreValidity) {
				return true;
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
		return coreValidity;
	}
}

class X509KeySelector extends KeySelector {
	public KeySelectorResult select(KeyInfo keyInfo,
			KeySelector.Purpose purpose, AlgorithmMethod method,
			XMLCryptoContext context) throws KeySelectorException {
		Iterator ki = keyInfo.getContent().iterator();
		while (ki.hasNext()) {
			XMLStructure info = (XMLStructure) ki.next();
			if (!(info instanceof X509Data))
				continue;
			X509Data x509Data = (X509Data) info;
			Iterator xi = x509Data.getContent().iterator();
			while (xi.hasNext()) {
				Object o = xi.next();
				if (!(o instanceof X509Certificate))
					continue;
				final PublicKey key = ((X509Certificate) o).getPublicKey();
				// Make sure the algorithm is compatible
				// with the method.
				if (algEquals(method.getAlgorithm(), key.getAlgorithm())) {
					return new KeySelectorResult() {
						public Key getKey() {
							return key;
						}
					};
				}
			}
		}
		Logger.getLogger(this.getClass()).error("No Key found");
		throw new KeySelectorException("No key found!");
	}

	static boolean algEquals(String algURI, String algName) {
		if ((algName.equalsIgnoreCase("DSA") && algURI
				.equalsIgnoreCase(SignatureMethod.DSA_SHA1))
				|| (algName.equalsIgnoreCase("RSA") && algURI
						.equalsIgnoreCase(SignatureMethod.RSA_SHA1))) {
			return true;
		} else {
			return false;
		}
	}
}