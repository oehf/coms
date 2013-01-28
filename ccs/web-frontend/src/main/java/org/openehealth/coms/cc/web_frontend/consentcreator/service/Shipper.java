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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.Base64;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.OIDObject;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.ConnectionHub;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v231.message.ADT_A01;
import ca.uhn.hl7v2.model.v25.message.QBP_Q21;
import ca.uhn.hl7v2.model.v25.message.RSP_K21;
import ca.uhn.hl7v2.model.v251.message.DOC_T12;
import ca.uhn.hl7v2.model.v251.message.MDM_T02;
import ca.uhn.hl7v2.model.v251.message.QBP_Q11;
import ca.uhn.hl7v2.model.v251.message.RSP_K25;
import ca.uhn.hl7v2.model.v251.message.RTB_Knn;
import ca.uhn.hl7v2.model.v251.segment.Hxx;
import ca.uhn.hl7v2.model.v251.segment.OBX;
import ca.uhn.hl7v2.parser.GenericParser;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * Provides connectivity to other services for the Consent Creator.
 * 
 * @author Lennart Koester, Markus Birkle, Nilay Yueksekogul
 * @version 1.0 14.02.2011
 * 
 */
public class Shipper {

	// The connection hub connects to listening servers MPI and PORS
	private ConnectionHub connectionHubMPI = ConnectionHub.getInstance();
	private String mpiIP;
	private int mpiPortPDQ;
	private int mpiPortPIX;

	private String porsIP;
	private int porsPort;
	private String porsUser;
	private String porsPassword;

	private ConnectionHub connectionHubCM = ConnectionHub.getInstance();
	private String cmIP;
	private int cmPort;

	private String serviceID = "";

	private Logger logging = Logger.getLogger(Shipper.class);

	private Properties emailProperties;
	private String emailBody;

	private Properties shipperProperties;

	private ConsentCreatorUtilities consentCreatorUtilities;

	public Shipper() {

	}

	/**
	 * Sets the given Properties for the utilized connections.
	 * 
	 * @param shipperProps
	 */
	public void setShipperProperties(Properties shipperProps) {
		shipperProperties = shipperProps;

		mpiIP = shipperProperties.getProperty("mpi.ip");
		mpiPortPDQ = Integer.parseInt(shipperProperties
				.getProperty("mpi.port.pdq"));
		mpiPortPIX = Integer.parseInt(shipperProperties
				.getProperty("mpi.port.pix"));

		porsIP = shipperProperties.getProperty("pors.ip");
		porsPort = Integer.parseInt(shipperProperties.getProperty("pors.port"));
		porsUser = shipperProps.getProperty("pors.user");
		porsPassword = shipperProps.getProperty("pors.password");

		cmIP = shipperProperties.getProperty("cm.ip");
		cmPort = Integer.parseInt(shipperProperties.getProperty("cm.port"));

		serviceID = shipperProperties.getProperty("cc.serviceid");

		consentCreatorUtilities = ConsentCreatorUtilities.getInstance();
	}

	/**
	 * Retrieves the latest PolicySet of the user with the given ID from the
	 * Consent Management Service.
	 * 
	 * @param string
	 *            - User's ID.
	 * @return - null if an error occurred.
	 */
	public Document getLatestPolicySet(String pid) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;

		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			Logger.getLogger(this.getClass()).error(e);
		}
		Document cda = getLatestCDADocument(pid);

		Document domRule = null;
		String cda_string = "";
		try {
			cda_string = getStringFromNode(cda
					.getElementsByTagName("PolicySet").item(0));
		} catch (IOException e2) {
			Logger.getLogger(this.getClass()).error(e2);
		}

		try {
			domRule = db.parse(new InputSource(new ByteArrayInputStream(
					cda_string.getBytes("UTF8"))));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return domRule;
	}

	/**
	 * Retrieves the latest PDF representation of a user's consent for the user
	 * with the given ID from the Consent Management Service and writes it onto
	 * the provided OutputSteam.
	 * 
	 * @FIXME Remove stub, remove throws
	 * 
	 * @param id
	 *            - User's ID.
	 * @throws IOException
	 *             - If there is a problem
	 */
	public void getLatestPDFConsent(String pid, OutputStream out)
			throws IOException, HL7Exception, LLPException {

		Document cdaDocument = getLatestCDADocument(pid);

		// get Base64 encoded PDF
		NodeList textNodeList = cdaDocument.getElementsByTagName("text");
		Node textNode = textNodeList.item(0);
		Element textElement = (Element) textNode;

		String b64PDF = textElement.getTextContent();

		out.write(Base64.decode(b64PDF));
	}

	/**
	 * Retrieves the latest CDA Document of the user with the given ID from the
	 * Consent Manager
	 * 
	 * 
	 * @param pid
	 * @return
	 */
	private Document getLatestCDADocument(String pid) {

		Connection connection = null;
		GenericParser parser = new GenericParser();

		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestamp = sd.format(new Date());
		long controllid = new Date().getTime();
		String msgControllID = new StringBuffer(String.valueOf(controllid))
				.reverse().toString();

		String msg = "MSH|^~\\&|COC|UKHD|PORS||" + timestamp
				+ "||QBP^Q91^QBP_Q11|" + msgControllID + "|P|2.5.1\r"
				+ "QPD|Q91^Get Latest CDA Document^UKHD0002|" + controllid
				+ "|@PID.3^" + pid + "&\r" + "RCP|I||R";

		GenericParser p = new GenericParser();
		QBP_Q11 qdp = null;
		try {
			qdp = (QBP_Q11) p.parse(msg);
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		Initiator initiator = null;
		Message response = null;
		String responseString = "";

		try {
			connection = connectionHubCM.attach(cmIP, cmPort, new PipeParser(),
					MinLowerLayerProtocol.class);
			// The initiator is used to transmit unsolicited messages
			initiator = connection.getInitiator();
			response = initiator.sendAndReceive(qdp);
			responseString = parser.encode(response);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.getLogger(this.getClass()).error(e);
		}

		connectionHubCM.discard(connection);

		logging.debug("ResponseString: " + responseString);

		PipeParser pipe = new PipeParser();
		DOC_T12 rsp;

		try {
			Message message = pipe.parse(responseString);
			if (message instanceof DOC_T12) {
				rsp = (DOC_T12) message;
				if (rsp.getRESULT().getOBX() != null) {
					OBX obx = rsp.getRESULT().getOBX();

					String obx5 = obx.getObx5_ObservationValue(0).encode();

					int startPos = obx5
							.indexOf("application/x-hl7-cda-level-three+xml")
							+ "application/x-hl7-cda-level-three+xml".length()
							+ 1;

					String temp = obx5.substring(startPos);
					String b64encoded = temp.substring(0, temp.indexOf(" "));

					String cdaString = new String(Base64.decode(b64encoded));

					// create org.w3c.dom.Document out of String
					Document cdaDocument = DocumentBuilderFactory
							.newInstance()
							.newDocumentBuilder()
							.parse(new InputSource(new java.io.StringReader(
									cdaString)));

					return cdaDocument;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.getLogger(this.getClass()).error(e);
		}
		return null;
	}

	/**
	 * Retrieves a List of all consents in the history of the user with the
	 * given ID from the Consent Management Service
	 * 
	 * @param id
	 *            - User's ID.
	 * @return - null if no List could be received
	 */
	public Document getConsentList(String pid) {

		Connection connection = null;
		GenericParser parser = new GenericParser();

		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestamp = sd.format(new Date());
		long controllid = new Date().getTime();
		String msgControllID = new StringBuffer(String.valueOf(controllid))
				.reverse().toString();

		String msg = "MSH|^~\\&|COC|UKHD|PORS||" + timestamp
				+ "||QBP^Q92^QBP_Q11|" + msgControllID + "|P|2.5.1\r"
				+ "QPD|Q92^Get Consent List^UKHD0002|" + controllid
				+ "|@PID.3^" + pid + "&\r" + "RCP|I||R";

		GenericParser p = new GenericParser();
		QBP_Q11 qdp = null;
		try {
			qdp = (QBP_Q11) p.parse(msg);
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		Initiator initiator = null;
		Message response = null;
		String responseString = "";

		try {
			connection = connectionHubCM.attach(cmIP, cmPort, new PipeParser(),
					MinLowerLayerProtocol.class);
			// The initiator is used to transmit unsolicited messages
			initiator = connection.getInitiator();
			response = initiator.sendAndReceive(qdp);
			responseString = parser.encode(response);
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		connectionHubCM.discard(connection);

		logging.debug("ResponseString: " + responseString);

		PipeParser pipe = new PipeParser();
		DOC_T12 rsp;

		try {
			Message message = pipe.parse(responseString);
			if (message instanceof DOC_T12) {
				rsp = (DOC_T12) message;
				if (rsp.getRESULT().getOBX() != null) {
					OBX obx = rsp.getRESULT().getOBX();

					String obx5 = obx.getObx5_ObservationValue(0).encode();

					int startPos = obx5
							.indexOf("application/x-hl7-cda-level-three+xml")
							+ "application/x-hl7-cda-level-three+xml".length()
							+ 1;

					String temp = obx5.substring(startPos);
					String b64encoded = temp.substring(0, temp.indexOf(" "));

					String cdaString = new String(Base64.decode(b64encoded));

					// create org.w3c.dom.Document out of String
					Document consentDocumentList = DocumentBuilderFactory
							.newInstance()
							.newDocumentBuilder()
							.parse(new InputSource(new java.io.StringReader(
									cdaString)));

					return consentDocumentList;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.getLogger(this.getClass()).error(e);
		}
		return null;
	}

	/**
	 * Retrieves the tree of organisations from the Provider and Organization
	 * Registry Service.
	 * 
	 * FIXME Remove stub
	 * 
	 * @return - null if no organisations could be obtained
	 * @throws HL7Exception
	 */
	public DefaultTreeModel getTreeOrganisations() throws HL7Exception {

		OIDObject o = new OIDObject("1.2", "Organisationen", true);

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(o);


		Vector<OIDObject> vOrg = getOrganisations();

		if (vOrg == null) {
			throw new ServiceException(
					"Die benötigten Daten zur Darstellung der Organisationen konnten nicht abgerufen werden.");
		}

		for (int i = 0; i < vOrg.size(); i++) {
			
			consentCreatorUtilities.addNodeToModel(new DefaultMutableTreeNode(
					vOrg.elementAt(i)), root);
		}

		consentCreatorUtilities.sortTree(root);

		return new DefaultTreeModel(root);

	}

	/**
	 * Retrieves all available organisations from the Provider and Organisation
	 * Registry Service.
	 * 
	 * @return - null if no list could be obtained
	 * @throws HL7Exception
	 */
	private Vector<OIDObject> getProvider() throws HL7Exception {

		GenericParser parser = new GenericParser();

		Vector<OIDObject> voo = new Vector<OIDObject>();

		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmm");
		String timestamp = sd.format(new Date());
		long controllid = new Date().getTime();
		String msgControllID = new StringBuffer(String.valueOf(controllid))
				.reverse().toString();

		URI uri;
		String responseString = "";
		Message response = null;
		try {
			uri = new URI("http", (porsIP + ":" + porsPort),
					"/communication-servlet/PorsServlet", "user=" + porsUser
							+ "&password=" + porsPassword
							+ "&msg=MSH|^~\\&|COC|UKHD|PORS||" + timestamp
							+ "||QBP^Q81^QBP_Q21|" + msgControllID
							+ "|P|2.5.1\rQPD|Q81^Find Provider^UKHD0002|"
							+ controllid + "|@STF.3.1^*\rRCP|I||R", null);

			System.out.println(uri);

			URL url = consentCreatorUtilities.formatURI(uri);

			System.out.println(url);

			URLConnection conn = url.openConnection();
			conn.setDoInput(true);

			System.out.println(conn);

			InputStream is = conn.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(new InputStreamReader(is, "UTF-8"), writer);
			responseString = writer.toString();
			response = parser.parse(responseString);
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
			e.printStackTrace();
		}

		logging.debug("ResponseString: " + responseString);

		if (response instanceof RSP_K25) {
			RSP_K25 k25 = (RSP_K25) response;
			if (k25.getQAK().getQak2_QueryResponseStatus().toString()
					.equals("OK")) {

				for (int i = 0; i < k25.getSTAFFReps(); i++) {

					try {
						boolean ac = false;
						String active = String.valueOf(k25.getSTAFF(i).getSTF()
								.getStf7_ActiveInactiveFlag());
						if (active.equalsIgnoreCase("A")) {
							ac = true;
						}

						String name = k25.getSTAFF(i).getSTF()
								.getStf3_StaffName(0).getXpn1_FamilyName()
								.getFn1_Surname().toString();
						String forename = k25.getSTAFF(i).getSTF()
								.getStf3_StaffName(0).getXpn2_GivenName()
								.toString();
						String title = k25.getSTAFF(i).getSTF()
								.getStf3_StaffName(0).getXpn5_PrefixEgDR()
								.toString();

						String orgaIdentifier = k25.getSTAFF(i).getSTF()
								.getStf1_PrimaryKeyValueSTF()
								.getCe3_NameOfCodingSystem()
								.getExtraComponents().getComponent(0).getData()
								.toString();
						String staffIdentifier = k25.getSTAFF(i).getSTF()
								.getStf1_PrimaryKeyValueSTF()
								.getCe1_Identifier().toString();

						voo.add(new OIDObject(orgaIdentifier + "^"
								+ staffIdentifier, title + " " + forename + " "
								+ name, ac));

					} catch (Exception e) {
						Logger.getLogger(this.getClass()).error(e);
					}
				}
				return voo;
			}
		}
		return null;
	}

	/**
	 * Retrieves all available organisations from the Provider and Organisation
	 * Registry Service.
	 * 
	 * @TODO Correct?
	 * 
	 * @return - null if no list could be obtained
	 * @throws HL7Exception
	 */
	private Vector<OIDObject> getOrganisations() throws HL7Exception {

		GenericParser parser = new GenericParser();

		Vector<OIDObject> voo = new Vector<OIDObject>();

		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmm");
		String timestamp = sd.format(new Date());
		long controllid = new Date().getTime();
		String msgControllID = new StringBuffer(String.valueOf(controllid))
				.reverse().toString();

		URI uri;
		String responseString = "";
		Message response = null;
		try {
			uri = new URI("http", (porsIP + ":" + porsPort),
					"/communication-servlet/PorsServlet", "user=" + porsUser
							+ "&password=" + porsPassword
							+ "&msg=MSH|^~\\&|COC|UKHD|PORS||" + timestamp
							+ "||QBP^Q82^QBP_Q21|" + msgControllID
							+ "|P|2.5.1\rQPD|Q82^Find Organisation^UKHD0002|"
							+ controllid + "|@STF.3.1^*\rRCP|I||R", null);

			URL url = consentCreatorUtilities.formatURI(uri);

			URLConnection conn = url.openConnection();
			conn.setDoInput(true);

			InputStream is = conn.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(new InputStreamReader(is, "UTF-8"), writer);
			responseString = writer.toString();
			response = parser.parse(responseString);
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		logging.debug("ResponseString: " + responseString);

		if (response instanceof RSP_K25) {
			RSP_K25 k25 = (RSP_K25) response;
			if (k25.getQAK().getQak2_QueryResponseStatus().toString()
					.equals("OK")) {

				for (int i = 0; i < k25.getSTAFFReps(); i++) {

					try {
						boolean ac = false;
						String active = String.valueOf(k25.getSTAFF(i).getSTF()
								.getStf7_ActiveInactiveFlag());
						if (active.equalsIgnoreCase("A")) {
							ac = true;
						}

						String name = k25.getSTAFF(i).getSTF()
								.getStf3_StaffName(0).getXpn1_FamilyName()
								.getFn1_Surname().toString();
						String forename = k25.getSTAFF(i).getSTF()
								.getStf3_StaffName(0).getXpn2_GivenName()
								.toString();

						String orgaIdentifier = k25.getSTAFF(i).getSTF()
								.getStf1_PrimaryKeyValueSTF()
								.getCe1_Identifier().toString();
						String staffIdentifier = k25.getSTAFF(i).getSTF()
								.getStf1_PrimaryKeyValueSTF()
								.getCe1_Identifier().toString();

						voo.add(new OIDObject(orgaIdentifier + "^"
								+ staffIdentifier, forename + " " + name, ac));

					} catch (Exception e) {
						Logger.getLogger(this.getClass()).error(e);

					}
				}

				return voo;

			}
		}

		return null;
	}

	/**
	 * Retrieves the PDF representation of a user's consent corresponding to the
	 * given document ID from the Consent Management Service.
	 * 
	 * @FIXME Remove stub
	 * 
	 * @param oid
	 * @return - Returns null if the File could not be retrieved.
	 */
	public void getSpecificPDFConsent(String oid, OutputStream out)
			throws IOException, HL7Exception, LLPException {

		Document cdaDocument = getSpecificCDADocument(oid);

		// get Base64 encoded PDF
		NodeList textNodeList = cdaDocument.getElementsByTagName("text");
		Node textNode = textNodeList.item(0);
		Element textElement = (Element) textNode;

		String b64PDF = textElement.getTextContent();

		out.write(Base64.decode(b64PDF));
	}

	/**
	 * Retrieves the CDA Document with the given OID from the Consent Manager
	 * 
	 * 
	 * @param oid
	 * @return
	 */
	private Document getSpecificCDADocument(String oid) {

		Connection connection = null;
		GenericParser parser = new GenericParser();

		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestamp = sd.format(new Date());
		long controllid = new Date().getTime();
		String msgControllID = new StringBuffer(String.valueOf(controllid))
				.reverse().toString();

		String msg = "MSH|^~\\&|COC|UKHD|PORS||" + timestamp
				+ "||QBP^Q93^QBP_Q11|" + msgControllID + "|P|2.5.1\r"
				+ "QPD|Q93^Get Specific CDA Document^UKHD0002|" + controllid
				+ "|@TXA.12^" + oid + "\r" + "RCP|I||R";

		GenericParser p = new GenericParser();
		QBP_Q11 qdp = null;
		try {
			qdp = (QBP_Q11) p.parse(msg);
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		Initiator initiator = null;
		Message response = null;
		String responseString = "";

		try {
			connection = connectionHubCM.attach(cmIP, cmPort, new PipeParser(),
					MinLowerLayerProtocol.class);
			// The initiator is used to transmit unsolicited messages
			initiator = connection.getInitiator();
			response = initiator.sendAndReceive(qdp);
			responseString = parser.encode(response);
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		connectionHubCM.discard(connection);

		logging.debug("ResponseString: " + responseString);

		PipeParser pipe = new PipeParser();
		DOC_T12 rsp;

		try {
			Message message = pipe.parse(responseString);
			if (message instanceof DOC_T12) {
				rsp = (DOC_T12) message;
				if (rsp.getRESULT().getOBX() != null) {
					OBX obx = rsp.getRESULT().getOBX();

					String obx5 = obx.getObx5_ObservationValue(0).encode();

					int startPos = obx5
							.indexOf("application/x-hl7-cda-level-three+xml")
							+ "application/x-hl7-cda-level-three+xml".length()
							+ 1;

					String temp = obx5.substring(startPos);
					String b64encoded = temp.substring(0, temp.indexOf(" "));

					String cdaString = new String(Base64.decode(b64encoded));

					// create org.w3c.dom.Document out of String
					Document cdaDocument = DocumentBuilderFactory
							.newInstance()
							.newDocumentBuilder()
							.parse(new InputSource(new java.io.StringReader(
									cdaString)));

					return cdaDocument;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.getLogger(this.getClass()).error(e);
		}
		return null;
	}

	/**
	 * Sends a message to the Consent Manager requesting that the stored
	 * uncleared consent for the patient with the given id be deleted.
	 * 
	 * FIXME Remove stub
	 * 
	 * @param id
	 * @return
	 */
	public boolean rejectUnclearedConsent(String pid) {

		// Z06 QBP^Q11^QBP_Q11 -> RSP^K11^RSP_K11

		/*
		 * Connection connection = null; GenericParser parser = new
		 * GenericParser();
		 * 
		 * SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss"); String
		 * timestamp = sd.format(new Date()); long controllid = new
		 * Date().getTime(); String msgControllID = new
		 * StringBuffer(String.valueOf(controllid)) .reverse().toString();
		 * 
		 * String msg = "MSH|^~\\&|COC||CMS||"+ timestamp +"||QBP^Q11^QBP_Q11|"+
		 * msgControllID +"|P|2.5.1\r"+
		 * "QPD|Z06^CMS reject uncleared consent document query^CMS0001|"+
		 * controllid +"|||"+ pid +"\r"+ "RCP|I||R\r";
		 * 
		 * GenericParser p = new GenericParser(); QBP_Q11 qdp = null; try { qdp
		 * = (QBP_Q11) p.parse(msg); } catch (Exception e) {
		 * Logger.getLogger(this.getClass()).error(e); }
		 * 
		 * Initiator initiator = null; Message response = null; String
		 * responseString = "";
		 * 
		 * try { connection = connectionHubCM.attach(cmIP, cmPort, new
		 * PipeParser(), MinLowerLayerProtocol.class); // The initiator is used
		 * to transmit unsolicited messages initiator =
		 * connection.getInitiator(); response = initiator.sendAndReceive(qdp);
		 * responseString = parser.encode(response); } catch (Exception e) {
		 * Logger.getLogger(this.getClass()).error(e); }
		 * 
		 * connectionHubCM.discard(connection);
		 * 
		 * logging.debug("ResponseString: "+responseString);
		 * 
		 * PipeParser pipe =new PipeParser(); RSP_K11 rsp;
		 * 
		 * 
		 * try { Message message = pipe.parse(responseString); if(message
		 * instanceof RSP_K11){ rsp = (RSP_K11) message;
		 * if((rsp.getMSA().getMessageControlID
		 * ().toString().equals(msgControllID)) &&
		 * rsp.getMSA().getAcknowledgmentCode().toString().equals("AA") &&
		 * (rsp.getQAK
		 * ().getQak2_QueryResponseStatus().toString().equals("OK"))&&
		 * (Integer.valueOf
		 * (rsp.getQAK().getHitCount().getValue()).intValue()==1)){
		 * 
		 * return true; } } } catch (Exception e) {
		 * Logger.getLogger(this.getClass()).error(e); }
		 * return false;
		 */

		return true; 
	}

	/**
	 * converts Node to String 
	 * 
	 * @param cda
	 *            - The cda document
	 * @param id
	 *            - ID associated with the user.
	 * @return - Returns false if the file exchange was unsuccessful, else true.
	 */
	public String getStringFromNode(Node root) throws IOException {

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
				result.append(getStringFromNode(node));
			}

			if (root.getNodeType() != 9) {
				result.append("</").append(root.getNodeName()).append(">");
			}
		}

		return result.toString();
	}

	/**
	 * Sends the CDA document of the user with the given ID to the Consent
	 * Management Service.
	 * 
	 * @param cda
	 *            - The cda document
	 * @param id
	 *            - ID associated with the user.
	 * @return - Returns false if the file exchange was unsuccessful, else true.
	 */
	public boolean sendCDA(Document cda, User user) {

		Connection connection = null;
		GenericParser parser = new GenericParser();

		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestamp = sd.format(new Date());
		long controllid = new Date().getTime();
		String msgControllID = new StringBuffer(String.valueOf(controllid))
				.reverse().toString();

		String pid = user.getID();
		String name = user.getName().toUpperCase();
		String forename = user.getForename().toUpperCase();

		String cda_string = "";
		try {
			cda_string = getStringFromNode(cda);
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		String cda_base64 = "";
		try {
			cda_base64 = Base64.encodeBytes(cda_string.getBytes("UTF8"));
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}

		String oid = cda.getElementsByTagName("id").item(0).getAttributes()
				.item(0).getNodeValue();

		String msg = "MSH|^~\\&|COC|OSS-Demonstrator|CMS|OSS-Demonstrator|"
				+ timestamp
				+ "||MDM^T02^MDM_T02|"
				+ msgControllID
				+ "|P|2.5.1\r"
				+ "PID|||"
				+ pid
				+ "^^^UKHD&1.2.276.0.76.3.1.78.1.0.10.20&ISO||"
				+ name
				+ "^"
				+ forename
				+ "\r"
				+ "TXA|1|CCDA^Consent CDA|multipart||||||||"
				+ oid
				+ "|||||LA\r"
				+ "OBX||ED|59284-0^Consent Document Patient^LN||^multipart^x-hl7-cda-level-three^A^MIME-Version: 1.0 Content-Type: multipart/mixed; boundary=\"HL7-CDA-boundary\" Content-Transfer-Encoding: Base64 --HL7-CDA-boundary Content-Type: application/x-hl7-cda-level-three+xml "
				+ cda_base64 + " --HL7-CDA-boundary||||||F";

		GenericParser p = new GenericParser();
		MDM_T02 mdm = null;

		Initiator initiator = null;
		Message response = null;
		String responseString = "";

		try {
			mdm = (MDM_T02) p.parse(msg);
			connection = connectionHubCM.attach(cmIP, cmPort, new PipeParser(),
					MinLowerLayerProtocol.class);

			// The initiator is used to transmit unsolicited messages
			initiator = connection.getInitiator();
			response = initiator.sendAndReceive(mdm);
			responseString = parser.encode(response);

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		connectionHubCM.detach(connection);

		logging.debug("ResponseString: " + responseString);

		PipeParser pipe = new PipeParser();
		ca.uhn.hl7v2.model.v251.message.ACK ack;
		try {
			Message message = pipe.parse(responseString);
			if (message instanceof ca.uhn.hl7v2.model.v251.message.ACK) {
				ack = (ca.uhn.hl7v2.model.v251.message.ACK) message;
				if (ack.getMSA().getAcknowledgmentCode().toString()
						.equals("AA")) {
					return true;
				}
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
		return false;
	}

	/**
	 * Sends the consent of the patient with the given id to the Consent
	 * Manager.
	 * 
	 * @param id
	 * @param dom
	 * @return
	 */
	public boolean sendUnclearedConsent(Document cda, User user) {

		Connection connection = null;
		GenericParser parser = new GenericParser();

		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestamp = sd.format(new Date());
		long controllid = new Date().getTime();
		String msgControllID = new StringBuffer(String.valueOf(controllid))
				.reverse().toString();

		String pid = user.getID();
		String name = user.getName().toUpperCase();
		String forename = user.getForename().toUpperCase();

		String cda_string = "";
		try {
			cda_string = getStringFromNode(cda);
		} catch (IOException e2) {

			e2.printStackTrace();
		}

		String cda_base64 = "";
		try {
			cda_base64 = Base64.encodeBytes(cda_string.getBytes("UTF8"));

		} catch (UnsupportedEncodingException e2) {

			e2.printStackTrace();
		}

		String oid = cda.getElementsByTagName("id").item(0).getAttributes()
				.item(0).getNodeValue();

		String msg = "MSH|^~\\&|COC|OSS-Demonstrator|CMS|OSS-Demonstrator|"
				+ timestamp
				+ "||MDM^T02^MDM_T02|"
				+ msgControllID
				+ "|P|2.5.1\r"
				+ "PID|||"
				+ pid
				+ "^^^UKHD&1.2.276.0.76.3.1.78.1.0.10.20&ISO||"
				+ name
				+ "^"
				+ forename
				+ "\r"
				+ "TXA|1|CCDA^Consent CDA|multipart||||||||"
				+ oid
				+ "|||||AU\r"
				+ "OBX||ED|59284-0^Consent Document Patient^LN||^multipart^x-hl7-cda-level-three^A^MIME-Version: 1.0 Content-Type: multipart/mixed; boundary=\"HL7-CDA-boundary\" Content-Transfer-Encoding: Base64 --HL7-CDA-boundary Content-Type: application/x-hl7-cda-level-three+xml "
				+ cda_base64 + " --HL7-CDA-boundary||||||F";

		GenericParser p = new GenericParser();
		MDM_T02 mdm = null;

		Initiator initiator = null;
		Message response = null;
		String responseString = "";

		try {
			mdm = (MDM_T02) p.parse(msg);
			connection = connectionHubCM.attach(cmIP, cmPort, new PipeParser(),
					MinLowerLayerProtocol.class);

			// The initiator is used to transmit unsolicited messages
			initiator = connection.getInitiator();
			response = initiator.sendAndReceive(mdm);
			responseString = parser.encode(response);

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		connectionHubCM.detach(connection);

		logging.debug("ResponseString: " + responseString);

		PipeParser pipe = new PipeParser();
		ca.uhn.hl7v2.model.v251.message.ACK ack;
		try {
			Message message = pipe.parse(responseString);
			if (message instanceof ca.uhn.hl7v2.model.v251.message.ACK) {
				ack = (ca.uhn.hl7v2.model.v251.message.ACK) message;
				if (ack.getMSA().getAcknowledgmentCode().toString()
						.equals("AA")) {
					return true;
				}
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
		return false;
	}

	/**
	 * Sends the generated md5hash of the patient's uncleared consent to the
	 * Consent Manager
	 * 
	 * @param pid
	 *            - id of the patient
	 * @param md5hash
	 *            - md5 hash of the uncleared consent
	 * @return
	 */
	public boolean sendUnclearedConsentHash(String pid, String md5hash) {

		Connection connection = null;
		GenericParser parser = new GenericParser();

		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestamp = sd.format(new Date());
		long controllid = new Date().getTime();
		String msgControllID = new StringBuffer(String.valueOf(controllid))
				.reverse().toString();

		String msg = "MSH|^~\\&|COC|OSS-Demonstrator|CMS|OSS-Demonstrator|"
				+ timestamp + "||MDM^T91^MDM_T02|" + msgControllID
				+ "|P|2.5.1\r" + "PID|||" + pid
				+ "^^^UKHD&1.2.276.0.76.3.1.78.1.0.10.20&ISO||\r"
				+ "TXA|1|CDH^Consent Document Hash|TEXT|||||||||||||LA\r"
				+ "OBX||TX|Consent Document Hash||" + md5hash + "||||||F";

		GenericParser p = new GenericParser();
		MDM_T02 mdm = null;

		Initiator initiator = null;
		Message response = null;
		String responseString = "";

		try {
			mdm = (MDM_T02) p.parse(msg);
			connection = connectionHubCM.attach(cmIP, cmPort, new PipeParser(),
					MinLowerLayerProtocol.class);

			// The initiator is used to transmit unsolicited messages
			initiator = connection.getInitiator();
			response = initiator.sendAndReceive(mdm);
			responseString = parser.encode(response);

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		connectionHubCM.detach(connection);

		logging.debug("ResponseString: " + responseString);

		PipeParser pipe = new PipeParser();
		ca.uhn.hl7v2.model.v251.message.ACK ack;
		try {
			Message message = pipe.parse(responseString);
			if (message instanceof ca.uhn.hl7v2.model.v251.message.ACK) {
				ack = (ca.uhn.hl7v2.model.v251.message.ACK) message;
				if (ack.getMSA().getAcknowledgmentCode().toString()
						.equals("AA")) {
					return true;
				}
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
		return false;
	}

	/**
	 * Sends the given Document associated with the given patient-ID to the
	 * Consent Management Service
	 * 
	 * @param cdaFile
	 *            - an electronically signed consent Document
	 * @param user
	 * @return
	 */
	public boolean storeSignedConsent(Document cdaFile, User user) {

		return sendCDA(cdaFile, user);
	}

	/**
	 * Checks with the user register whether or not a user is already registered
	 * with the register.
	 * 
	 * @param user
	 * @return - Returns -1 if the user does not exist in the user register ,
	 *         otherwise the ID associated with the user is returned.
	 */
	public String existsUser(User user) {

		Connection connection = null;
		GenericParser parser = new GenericParser();

		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestamp = sd.format(new Date());
		long controllid = new Date().getTime();
		String msgControllID = new StringBuffer(String.valueOf(controllid))
				.reverse().toString();

		String name = user.getName().toUpperCase();
		String forename = user.getForename().toUpperCase();
		String gender = user.getGender().substring(0, 1).toUpperCase();
		SimpleDateFormat dTDOB = new SimpleDateFormat("yyyyMMdd");
		String dob = dTDOB.format(user.getBirthdate());

		String msg = "MSH|^~\\&|COC|OSS-Demonstrator|UKHD-MPI|OSS-Demonstrator|"
				+ timestamp
				+ "||QBP^Q22^QBP_Q21|"
				+ msgControllID
				+ "|P|2.5\r"
				+ "QPD|Q22^Find Candidates^HL70471||@PID.5.1^"
				+ name
				+ "~@PID.5.2^"
				+ forename
				+ "~@PID.7^"
				+ dob
				+ "~@PID.8^"
				+ gender + "|||||^^^" + serviceID + "\r" + "RCP|I";

		GenericParser p = new GenericParser();
		QBP_Q21 qdp = null;

		Initiator initiator = null;
		Message response = null;
		String responseString = "";

		try {
			qdp = (QBP_Q21) p.parse(msg);
			connection = connectionHubMPI.attach(mpiIP, mpiPortPDQ,
					new PipeParser(), MinLowerLayerProtocol.class);

			// The initiator is used to transmit unsolicited messages
			initiator = connection.getInitiator();
			response = initiator.sendAndReceive(qdp);
			responseString = parser.encode(response);

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		connectionHubMPI.detach(connection);

		logging.debug("ResponseString: " + responseString);

		PipeParser pipe = new PipeParser();
		RSP_K21 rsp;
		try {
			Message message = pipe.parse(responseString);
			if (message instanceof RSP_K21) {
				rsp = (RSP_K21) message;
				if ((rsp.getQAK().getQak2_QueryResponseStatus().toString()
						.equals("OK"))
						&& (Integer.valueOf(
								rsp.getQAK().getHitCount().getValue())
								.intValue() == 1)) {
					String pid = rsp.getQUERY_RESPONSE().getPID()
							.getPatientIdentifierList()[0].getCx1_IDNumber()
							.toString();
					logging.debug("PID: " + pid);
					return (pid);
				}
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
		return "-1";
	}

	/**
	 * Registers the given user with the user register.
	 * 
	 * @param user
	 * @return - Returns false if registration was unsuccessful
	 */
	public boolean registerUser(User user) {

		Connection connection = null;
		GenericParser parser = new GenericParser();

		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestamp = sd.format(new Date());
		long controllid = new Date().getTime();
		String msgControllID = new StringBuffer(String.valueOf(controllid))
				.reverse().toString();

		String name = user.getName().toUpperCase();
		String forename = user.getForename().toUpperCase();
		String gender = user.getGender().substring(0, 1).toUpperCase();
		SimpleDateFormat dTDOB = new SimpleDateFormat("yyyyMMdd");
		String dob = dTDOB.format(user.getBirthdate());

		String msg = "MSH|^~\\&|COC|OSS-Demonstrator|UKHD-MPI|OSS-Demonstrator|"
				+ timestamp
				+ "||ADT^A01^ADT_A01|"
				+ msgControllID
				+ "|P|2.3.1\r"
				+ "EVN||"
				+ timestamp
				+ "\r"
				+ "PID|||"
				+ user.getID()
				+ "^^^"
				+ serviceID
				+ "&ISO||"
				+ name
				+ "^"
				+ forename + "||" + dob + "|" + gender + "\r" + "PV1||I\r";

		GenericParser p = new GenericParser();
		ADT_A01 qdp = null;
		try {
			qdp = (ADT_A01) p.parse(msg);
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		Initiator initiator = null;
		Message response = null;
		String responseString = "";

		try {
			connection = connectionHubMPI.attach(mpiIP, mpiPortPIX,
					new PipeParser(), MinLowerLayerProtocol.class);
			// The initiator is used to transmit unsolicited messages
			initiator = connection.getInitiator();
			response = initiator.sendAndReceive(qdp);
			responseString = parser.encode(response);
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		connectionHubCM.discard(connection);

		logging.debug("ResponseString: " + responseString);

		PipeParser pipe = new PipeParser();
		ca.uhn.hl7v2.model.v231.message.ACK ack;

		try {
			Message message = pipe.parse(responseString);
			if (message instanceof ca.uhn.hl7v2.model.v231.message.ACK) {
				ack = (ca.uhn.hl7v2.model.v231.message.ACK) message;
				if ((ack.getMSA().getMessageControlID().toString()
						.equals(msgControllID))
						&& ack.getMSA().getAcknowledgementCode().toString()
								.equals("AA")) {
					return true;
				}
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
		return false;
	}

	/**
	 * Sends an email to the given id including a link to the password page. The
	 * link contains the refID.
	 * 
	 * @param emailType
	 *            - refers to the email that shall be sent, 0 sends an email for
	 *            newly registered users, 1 sends a standard password-reset
	 *            email
	 * @param emailaddress
	 * @param refID
	 * @return
	 */
	public boolean sendPasswordLinkToEmail(int emailType, User user,
			String refID) {

		String salutation = "";

		if (user.getGender().equalsIgnoreCase("male")) {
			salutation = "geehrter Herr " + user.getName();
		} else {
			salutation = "geehrte Frau " + user.getName();
		}

		MailAuthenticator auth = new MailAuthenticator(
				emailProperties.getProperty("mail.user"),
				emailProperties.getProperty("mail.user.password")); // Login

		Properties props = (Properties) emailProperties.clone();
		props.remove("mail.user");
		props.remove("mail.user.password");
		Session session = Session.getInstance(props, auth);

		String htmlContent = emailBody;
		htmlContent = htmlContent.replaceFirst(
				"TITLE",
				emailProperties.getProperty("mail.skeleton.type." + emailType
						+ ".title"));
		htmlContent = htmlContent.replaceFirst(
				"MESSAGESUBJECT",
				emailProperties.getProperty("mail.skeleton.type." + emailType
						+ ".messagesubject"));
		htmlContent = htmlContent.replaceFirst(
				"HEADER1",
				emailProperties.getProperty("mail.skeleton.type." + emailType
						+ ".header1"));
		htmlContent = htmlContent.replaceFirst(
				"MESSAGE",
				emailProperties.getProperty("mail.skeleton.type." + emailType
						+ ".message"));
		htmlContent = htmlContent.replaceFirst("SALUTATION", salutation);
		htmlContent = htmlContent.replaceFirst("TOPLEVELDOMAIN",
				emailProperties.getProperty("mail.service.domain"));
		htmlContent = htmlContent.replaceFirst("SERVICENAME",
				emailProperties.getProperty("mail.service.name"));
		htmlContent = htmlContent.replaceFirst("REFERER", refID);
		htmlContent = htmlContent.replaceFirst("BCKRGIMAGE",
				"'cid:header-image'");
		htmlContent = htmlContent.replaceFirst("DASH", "'cid:dash'");

		String textContent = emailProperties.getProperty("mail.skeleton.type."
				+ emailType + ".title")
				+ " - "
				+ emailProperties.getProperty("mail.skeleton.type." + emailType
						+ ".messagesubject")
				+ "\n \n"
				+ emailProperties.getProperty("mail.skeleton.type." + emailType
						+ ".message");

		textContent = textContent.replaceFirst("TOPLEVELDOMAIN",
				emailProperties.getProperty("mail.service.domain"));
		textContent = textContent.replaceFirst("SERVICENAME",
				emailProperties.getProperty("mail.service.name"));
		textContent = textContent.replaceFirst("REFERER", refID);
		textContent = textContent.replaceFirst("SALUTATION", salutation);
		textContent = textContent.replaceAll("<br>", "\n");

		try {

			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(emailProperties
					.getProperty("mail.user")));
			msg.setRecipients(javax.mail.Message.RecipientType.TO,
					user.getEmailaddress());
			msg.setSentDate(new Date());
			msg.setSubject(emailProperties.getProperty("mail.skeleton.type."
					+ emailType + ".subject"));

			Multipart mp = new MimeMultipart("alternative");

			// plaintext
			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText(textContent);
			mp.addBodyPart(textPart);

			// html
			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");
			mp.addBodyPart(htmlPart);

			MimeBodyPart imagePart = new MimeBodyPart();
			DataSource fds = null;
			try {
				fds = new FileDataSource(new File(new URL(
						(String) emailProperties
								.get("mail.image.headerbackground")).getFile()));
			} catch (MalformedURLException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
			imagePart.setDataHandler(new DataHandler(fds));
			imagePart.setHeader("Content-ID", "header-image");
			mp.addBodyPart(imagePart);

			MimeBodyPart imagePart2 = new MimeBodyPart();
			DataSource fds2 = null;
			try {
				fds2 = new FileDataSource(
						new File(
								new URL((String) emailProperties
										.get("mail.image.dash")).getFile()));
			} catch (MalformedURLException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
			imagePart2.setDataHandler(new DataHandler(fds2));
			imagePart2.setHeader("Content-ID", "dash");
			mp.addBodyPart(imagePart2);

			msg.setContent(mp);

			Transport.send(msg);
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
			return false;
		}

		return true;
	}

	/**
	 * Sets the needed Properties for email communication
	 * 
	 * @param emailProperties
	 */
	public void setEmailProperties(Properties emailProperties) {

		this.emailProperties = emailProperties;

	}

	/**
	 * Sets the emails used for user-interaction
	 * 
	 * @param emails
	 */
	public void setEmailBody(String email) {

		emailBody = email;
	}

}

class MailAuthenticator extends Authenticator {

	private final String user;

	private final String password;

	public MailAuthenticator(String user, String password) {
		this.user = user;
		this.password = password;
	}

	/**
	 * @see javax.mail.Authenticator#getPasswordAuthentication()
	 */
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(this.user, this.password);
	}
}