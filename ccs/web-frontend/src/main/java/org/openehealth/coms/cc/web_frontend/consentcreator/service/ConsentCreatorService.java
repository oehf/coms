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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.OIDObject;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.User;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.llp.LLPException;

/**
 * ConsentCreatorService provides the main functionality for the servlets and
 * functions as and abstraction layer between servlets and the worker classes.
 * 
 * @author Lennart Koester, Markus Birkle, Nilay Yueksekogul
 * @version 1.0 14.02.2011
 * 
 */

public class ConsentCreatorService {

	protected Shipper shipper;

	protected Database database;
	protected DocumentFactory documentFactory;
	private ConsentCreatorUtilities consentCreatorUtilites;
	private DefaultTreeModel treeModel = null;
	private Vector<OIDObject> documentList = new Vector<OIDObject>();

	private static ConsentCreatorService instance = new ConsentCreatorService();

	private ConsentCreatorService() {

		try {
			database = new Database();

			documentFactory = new DocumentFactory();
			consentCreatorUtilites = ConsentCreatorUtilities.getInstance();
			shipper = new Shipper();
			shipper.setEmailProperties(documentFactory.getEmailProperties());
			shipper.setEmailBody(documentFactory.getEmailBody());
			shipper.setShipperProperties(documentFactory.getShipperProperties());
			treeModel = shipper.getTreeOrganisations();
			documentList = documentFactory.getResources();
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
			e.printStackTrace();
		}
	}

	/**
	 * Returns an instance of the ConsentCreatorService
	 * 
	 * @return
	 */
	public static ConsentCreatorService getInstance() {
		return instance;
	}

	/**
	 * Retrieves the consent Document for the user with the given id from the
	 * filesystem and sends the Document to the Consent Manager, then deletes
	 * information which was associated with this uncleared consent from the
	 * database and filesystem.
	 * 
	 * @param id
	 * @return
	 */
	public boolean acceptUnclearedConsent(User user) {

		String filename = database.retrieveUnclearedConsentFilename(user
				.getID());

		if (filename.equalsIgnoreCase("")) {
			return false;
		}

		Document domCDA = documentFactory.getUnclearedConsent(filename);

		if (domCDA != null) {
			if (shipper.sendUnclearedConsent(domCDA, user)) {
				documentFactory.deleteUnclearedConsent(filename);
				database.removeUnclearedConsent(user.getID());
				return true;
			}
		}
		return false;
	}

	/**
	 * Activates the user with the given id in the database.
	 * 
	 * @param id
	 *            - the patient's ID.
	 * @return - true if the user was activated, else false.
	 */
	public boolean activateUser(User user) {

		if (!database.activateUser(user.getID())) {
			return false;
		}

		if (database.existsPasswordRequestForUser(user.getID())) {
			database.removeRefIDForUser(user.getID());
		}

		String s = database.storePasswordRequest(user);
		shipper.sendPasswordLinkToEmail(1, user, s);

		return true;
	}

	/**
	 * Adds the given Rule to the given PolicySet.
	 * 
	 * @param policySet
	 *            - The PolicySet to which the Rule will be added
	 * @param rule
	 *            - The Rule to be added
	 * @param ruleType
	 *            - defines the type of the Rule, the Rule will be added to the
	 *            Policy with the corresponding ID.
	 * @return
	 */
	public Document addRuleToPolicySet(Document policySet, Document rule,
			int ruleType) {

		documentFactory.addRuleToPolicySet(policySet, rule, ruleType);

		return policySet;
	}

	/**
	 * Creates a CDA consent in which a PDF and the PolicySet are stored.
	 * 
	 * @param policySet
	 *            - The PolicySet which will be used as a part of the consent.
	 *            Null if a new PolicySet should be constructed.
	 * @param participation
	 *            - true if the user wants to participate, else false.
	 * @param user
	 *            - The consenting user.
	 * @param author
	 *            - The author of the deactivation request.
	 * @param out
	 *            - The OutputStream on which to write the PDF presentation of
	 *            the consent.
	 * @return
	 */
	public Document createConsent(Document policySet, boolean participation,
			User user, User author, OutputStream out) {

		Document cda = documentFactory.constructCDA(user, policySet, author,
				participation, out);

		return cda;
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

		return documentFactory.constructRule(organisation, persons,
				affectedOID, documents, documentsOID, accessType, grantAccess);
	}

	/**
	 * Deactivates the given in the database, removes possible password requests
	 * and sets a new password for the account.
	 * 
	 * @param user
	 * @return
	 */
	public boolean deactivateUser(User user) {

		if (database.deactivateUser(user.getID())) {

			if (database.existsPasswordRequestForUser(user.getID())) {
				database.removeRefIDForUser(user.getID());
			}
			database.updateUserPassword(user.getID(), documentFactory
					.getMD5Hash(RandomStringUtils.randomNumeric(64)));
			return true;
		}

		return false;
	}

	/**
	 * Checks whether or not a user with the data stored in the given user
	 * exists
	 * 
	 * @param user
	 * @return
	 */
	public boolean existsUser(User user) {
		return database.existsUser(user);
	}

	/**
	 * Retrieves the PDF consent with the given ID from the Consent Management
	 * Service.
	 * 
	 * @param id
	 * @return
	 */
	public String getPDFConsent(String oid, OutputStream out) {

		try {
			shipper.getSpecificPDFConsent(oid, out);
		} catch (IOException e) {
			Logger.getLogger(this.getClass()).error(e);
			return "Error occurred during data handling.";
		} catch (HL7Exception e) {
			Logger.getLogger(this.getClass()).error(e);
			return "Error occurred during connection setup.";
		} catch (LLPException e) {
			Logger.getLogger(this.getClass()).error(e);
			return "Lower Layer Protocol error.";
		}
		return "";

	}

	/**
	 * Returns the list containg all available document types.
	 * 
	 * @return
	 */
	public Vector<OIDObject> getDocumentList() {
		return documentList;
	}

	/**
	 * Retrieves the latest PDF consent for the given user ID and writes it onto
	 * the OutputStream.
	 * 
	 * @param id
	 *            - The identifier of a user.
	 * @return
	 */
	public String getLatestPDFConsent(String id, OutputStream out) {

		try {
			shipper.getLatestPDFConsent(id, out);
		} catch (IOException e) {
			Logger.getLogger(this.getClass()).error(e);
			return "Error occurred during data handling.";
		} catch (HL7Exception e) {
			Logger.getLogger(this.getClass()).error(e);
			return "Error occurred during connection setup.";
		} catch (LLPException e) {
			Logger.getLogger(this.getClass()).error(e);
			return "Lower Layer Protocol error.";
		}
		return "";
	}

	/**
	 * Returns a List of users during whose registration a conflict was
	 * encountered. Inside the List<Object> is stored another List<Object>
	 * containing in index 1 - the User Object 2 - the Timestamp Object which
	 * was stored at the time of the registration.
	 * 
	 * @return
	 */
	public List<Object> getListofUserConflicts() {
		return database.showConflicts();
	}

	/**
	 * Retrieves the list of stored uncleared consents from the database.
	 * 
	 * @return
	 */
	public List<Object> getListofUnclearedConsents() {
		return database.showUnclearedConsents();
	}

	/**
	 * Retrieves the consent history of the patient with the given ID.
	 * 
	 * @param id
	 *            - The patient's ID of whom the consent history should be
	 *            retrieved.
	 * @return - A XML document of the patients consents, represented as Strings
	 *         in the format "consentOID, date of creation"
	 */
	public Document getConsentHistory(String id) {

		return shipper.getConsentList(id);
	}

	/**
	 * Retrieves the PolicySet of the user with the corresponding ID.
	 * 
	 * @param user
	 * @return
	 */
	public Document getPolicySet(User user) {

		return shipper.getLatestPolicySet(user.getID());
	}

	/**
	 * Retrieves the standard PolicySet of the user.
	 * 
	 * @param user
	 * @return
	 */
	public Document getStandardPolicySet(User user) {

		return documentFactory.getSkeletonPolicySet(true, user);
	}

	/**
	 * Returns the root of the stored tree.
	 * 
	 * @return
	 */
	public DefaultMutableTreeNode getOIDTreeRoot() {
		return (DefaultMutableTreeNode) treeModel.getRoot();
	}

	/**
	 * Returns all children of the TreeNode with the given ID.
	 * 
	 * @param oid
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Vector<OIDObject> getOIDTreeNodeChildren(String oid) {

		Vector<OIDObject> children = new Vector<OIDObject>();

		DefaultMutableTreeNode soughtNode = consentCreatorUtilites
				.traverseTreeOID(oid, getOIDTreeRoot());

		if (soughtNode != null) {
			for (Enumeration e = soughtNode.children(); e.hasMoreElements();) {

				DefaultMutableTreeNode lon = (DefaultMutableTreeNode) e
						.nextElement();
				OIDObject o = new OIDObject(
						((OIDObject) lon.getUserObject()).getIdentifier(),
						((OIDObject) lon.getUserObject()).getName(),
						((OIDObject) lon.getUserObject()).isActive());
				o.setHasChildren(!lon.isLeaf());
				children.add(o);
			}
		}
		return children;
	}

	/**
	 * Returns a DefaultTreeModel containing leaf-Nodes containing the query
	 * String in their names. 
	 * noch?
	 * 
	 * @param query
	 * @return
	 */
	public DefaultTreeModel getOIDTreeNodes(String query) {

		DefaultMutableTreeNode realRoot = (DefaultMutableTreeNode) getOIDTreeRoot();
		DefaultMutableTreeNode searchRoot = (DefaultMutableTreeNode) getOIDTreeRoot()
				.clone();
		DefaultMutableTreeNode ret = consentCreatorUtilites.traverseTreeString(
				query.toLowerCase(), searchRoot, realRoot);

		return new DefaultTreeModel(searchRoot);
	}

	/**
	 * Retrieves the user with the given emailaddress from the database.
	 * 
	 * @param emailaddress
	 * @return
	 */
	public User getUser(String emailaddress) {
		return database.retrieveUserByEmail(emailaddress);
	}

	/**
	 * Retrieves the user whose data corresponds to the given data from the
	 * database.
	 * 
	 * @param name
	 * @param forename
	 * @param birthdate
	 * @param gender
	 * @return
	 */
	public User getUser(String name, String forename, Date birthdate,
			String gender) {
		return database.retrieveUser(name, forename, birthdate, gender);
	}

	/**
	 * Checks whether or not a login with the given data is possible.
	 * 
	 * @param emailaddress
	 * @param password
	 * @return
	 */
	public boolean login(String emailaddress, String password) {

		String pwd = database.retrievePassword(emailaddress);

		String passwordMD5 = documentFactory.getMD5Hash(password);

		return (pwd.equals(passwordMD5));
	}

	/**
	 * Sends an email with a link to set a new password to the given
	 * emailaddress.
	 * 
	 * @param emailaddress
	 * @return
	 */
	public boolean requestPassword(String emailaddress) {

		boolean send = false;
		User user = database.retrieveUserByEmail(emailaddress);

		if (user == null) {

			return false;

		} else if (user.isActive() == 1) {

			database.removeRefIDForUser(database.retrieveUserByEmail(
					emailaddress).getID());
			String s = database.storePasswordRequest(user);
			shipper.sendPasswordLinkToEmail(1, user, s);
			send = true;
		}

		return send;

	}

	/**
	 * Registers the given User with the local database of the service as well
	 * as with the external patient register.
	 * 
	 * @param user
	 * @param out
	 * @param orphanRegister
	 *            - true if the user is the one registering himself, else he is
	 *            being registered by personnel, then this is false.
	 * @throws ServiceException
	 *             - Throws an exception if the user is already registered with
	 *             the service.
	 * @return - true if the user was newly registered. false if the user was
	 *         only registered to the local database and existed in the external
	 *         register already.
	 */
	public boolean registerUser(User user, boolean orphanRegister)
			throws ServiceException {

		User author = null;

		// Check whether the user is already registered or not
		if (!database.existsUser(user)) {
			// If the user is a privileged user register him only locally
			if (user.getPrivileges() >= 1) {

				// Generate a random id considerably larger then the ids
				// received from the register
				user.setID(RandomStringUtils.randomNumeric(96));
				database.storeUser(user);
				String s = database.storePasswordRequest(user);
				shipper.sendPasswordLinkToEmail(0, user, s);
				return true;
			}

			// Check with the external register whether the user is already
			// registered or not
			String id = shipper.existsUser(user);

			// User is not already present in the register
			if (id.equalsIgnoreCase("-1")) {

				if (orphanRegister) {
					author = user;
				}

				user.setID(RandomStringUtils.randomNumeric(64));
				if (!shipper.registerUser(user)) {
					throw new ServiceException(
							"Der Benutzer konnte nicht registriert werden.");
				}
				database.storeUser(user);
				String s = database.storePasswordRequest(user);
				shipper.sendPasswordLinkToEmail(0, user, s);

				if (orphanRegister) {
					Document cda = documentFactory.constructCDA(user, null,
							author, false, null);
					if (!shipper.sendCDA(cda, user)) {
						return false; // TODO Hier sollte was passieren wenn der
										// CDA nicht erfolgreich gesendet werden
										// kann.
					}
				}

				return true;
			}
			// User is only going to be registered locally
			else {
				// Set participation to false so no documents are transmitted
				// until the conflict is resolved.
				boolean participation = false;

				// If the user registers himself and is already registered with
				// the patient register store him as inactive and
				// mark him
				if (orphanRegister) {
					user.setActive(0);
					author = user;
				}
				// If the user is being registered by a trusted care provider,
				// send him a password and activate the account.
				else {
					user.setActive(1);
					participation = true;

				}
				user.setID(id);
				database.storeUser(user);

				if (orphanRegister) {
					database.storeConflict(id);
					Document cda = documentFactory.constructCDA(user, null,
							author, participation, null);
					shipper.sendCDA(cda, user);
				} else {
					String s = database.storePasswordRequest(user);
					shipper.sendPasswordLinkToEmail(0, user, s);
				}

				return false;
			}
		}
		// Can't register if a user with corresponding data already exists.
		else {
			throw new ServiceException(
					"Unter den angegebenen Daten existiert bereits ein Benutzer.");
		}
	}

	/**
	 * Removes the rule with the given ID from the PolicySet
	 * 
	 * @param id
	 * @param policySet
	 * @return
	 */
	public boolean removeRule(String id, Document policySet) {

		NodeList rules = policySet.getElementsByTagName("Rule");

		for (int i = 0; i < rules.getLength(); i++) {

			NamedNodeMap map = rules.item(i).getAttributes();

			String val = map.getNamedItem("RuleId").getNodeValue();

			if (val.equalsIgnoreCase(id)) {

				Node parent = rules.item(i).getParentNode();
				parent.removeChild(rules.item(i));
				i = rules.getLength();
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes the uncleared consent for the patient with the given id from the
	 * file system and removes the corresponding database entry.
	 * 
	 * @param id
	 * @return
	 */
	public boolean rejectUnclearedConsent(String id) {

		String filename = database.retrieveUnclearedConsentFilename(id);

		if (filename.equalsIgnoreCase("")) {
			return false;
		}

		if (documentFactory.deleteUnclearedConsent(filename)) {
			if (shipper.rejectUnclearedConsent(id)) {
				return database.removeUnclearedConsent(id);
			}
		}
		return false;
	}

	/**
	 * Removes the user with the given id from the database. Also removes the
	 * stored conflict.
	 * 
	 * @param id
	 * @return
	 */
	public boolean removeConflict(String id) {

		return database.removeUser(id);

	}

	/**
	 * Sets the password for the given user, invalidates the referer ID
	 * 
	 * @param emailaddress
	 * @param password
	 * @return
	 */
	public boolean setPassword(User user, String password, String ref) {
		if (!database.removeRefID(ref)) {
			return false;
		}
		return database.updateUserPassword(user.getID(),
				documentFactory.getMD5Hash(password));
	}

	/**
	 * Activates the user with the given id and removes the conflict.
	 * 
	 * @param id
	 * @return
	 */
	public boolean solveConflict(User user) {

		if (activateUser(user)) {
			database.removeConflict(user.getID());
			return true;
		}
		return false;
	}

	/**
	 * Updates the Target-Elements of the given PolicySet.
	 * 
	 * @param policySet
	 */
	public void updatePolicySetTargets(Document policySet) {

		documentFactory.updateTargetElements(policySet);

	}

	/**
	 * Stores the signed CDA document of the patient with the given id to the
	 * CM.
	 * 
	 * @param cdaFile
	 * @param id
	 * @return - true if operation was successfull.
	 */
	public boolean storeSignedConsent(Document cdaFile, Document originalCDA,
			User user) {

		if (documentFactory.isXMLSignatureValid(cdaFile, originalCDA)) {
			return shipper.storeSignedConsent(cdaFile, user);
		} else {
			return false;
		}

	}

	/**
	 * Stores the given Document to the local file system and sends a MD5 Hash
	 * of its content to the Consent Manager.
	 * 
	 * @param cdaFile
	 * @param id
	 * @return
	 */
	public boolean storeUnclearedConsent(Document cdaFile, String id,
			int participation) {

		boolean ret = false;

		if (database.existsUnclearedConsent(id)) {
			String filename = database.retrieveUnclearedConsentFilename(id);
			if (documentFactory.deleteUnclearedConsent(filename)) {
				if (!database.removeUnclearedConsent(id)) {
					return false;
				}
			} else {
				return false;
			}
		}

		String md5hash = documentFactory.getMD5Hash(cdaFile);
		String filename = database.storeUnclearedConsent(id, md5hash,
				participation);
		if (!documentFactory.storeUnclearedConsent(cdaFile, filename)) {
			database.removeUnclearedConsent(filename);
		} else {
			ret = shipper.sendUnclearedConsentHash(id, md5hash);
		}
		return ret;
	}

	/**
	 * Updates the data for the given user.
	 * 
	 * @param user
	 * @return
	 */
	public boolean updateUser(User user) {

		return database.updateUser(user);
	}

	/**
	 * Checks whether or not a valid request for the given referer exists.
	 * 
	 * @param s
	 * @return
	 */
	public boolean validPasswordRequest(String s, boolean external) {
		return database.existsPasswordRequest(s, external);
	}

	/**
	 * Retrieves the user associated with the given referer.
	 * 
	 * @param s
	 * @return - null if no user was found
	 */
	public User getUserByPasswordRequest(String s) {
		return database.retrieveUserByPasswordRequest(s);
	}
}