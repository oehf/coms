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

package org.openehealth.coms.cc.web_frontend.consentcreator.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.OIDObject;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.User;
import org.openehealth.coms.cc.web_frontend.consentcreator.service.ConsentCreatorUtilities;
import org.openehealth.coms.cc.web_frontend.consentcreator.service.ServiceException;
import org.openehealth.coms.cc.web_frontend.org.json.JSONArray;
import org.openehealth.coms.cc.web_frontend.org.json.JSONException;
import org.openehealth.coms.cc.web_frontend.org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * This servlet provides data needed for the generation of consents for
 * privileged users as well as the handling of requests which occur during
 * creation.
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 * 
 */
public class PrivilegedCreateConsentServiceServlet extends AbstractServlet {

	private final static long serialVersionUID = 10L;

	/**
	 * This method does the actual request processing.
	 * 
	 * @throws ServiceException
	 *             , Exception
	 */
	@Override
	public void doService() throws ServiceException, Exception {

		if (requestType.equals("newconsentpdf")) {

			requestTypeNewConsentPDF();

		} else if (requestType.equals("consentpdf")) {

			requestTypeConsentPDF();

		} else if (requestType.equals("newconsentcda")) {

			requestTypeNewConsentCDA();

		} else if (requestType.equals("storeunclearedconsent")) {

			requestTypeStoreUnclearedConsent();

		} else if (requestType.equals("storestandardunclearedconsent")) {

			requestTypeStoreStandardUnclearedConsent();

		} else if (requestType.equals("storesignedconsent")) {

			requestTypeStoreSignedConsent();

		} else if (requestType.equals("addRule")) {

			requestTypeAddRule();

		} else if (requestType.equals("removeRule")) {

			requestTypeRemoveRule();

		} else if (requestType.equals("ruleDescription")) {

			requestTypeRuleDescription();

		} else if (requestType.equals("rulelist")) {

			requestTypeRuleList();

		} else if (requestType.equals("treeroot")) {

			requestTypeTreeRoot();

		} else if (requestType.equals("expandtree")) {

			requestTypeExpandTree();

		} else if (requestType.equals("searchtree")) {

			requestTypeSearchTree();

		} else if (requestType.equals("documenttypes")) {

			requestTypeDocumentTypes();

		} else if (requestType.equals("persons")) {

			requestTypePersons();

		} else if (requestType.equals("standardconsentCDA")) {

			requestTypeStandardConsentCDA();

		} else {
			this.writeErrorMessage("Der übergebene Parameter ist unbekannt.");
		}

	}

	/**
	 * This method handles requests aiming to view the current state of the
	 * consent of the person stored in the session in the request as a PDF
	 */
	private void requestTypeNewConsentPDF() {

		try {

			User consentUser = (User) request.getSession().getAttribute(
					"consentUser");
			Document policySet = (Document) request.getSession().getAttribute(
					"policySetForUser");
			response.setContentType("application/pdf");
			ccService.createConsent(policySet, true, consentUser, user,
					response.getOutputStream());

		} catch (IOException e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}

	/**
	 * This method handles requests aiming to obtain the current consent of the
	 * person associated with the emailaddress given in the request as
	 * XML-Document
	 */
	private void requestTypeConsentPDF() {

		User consentUser = ccService.getUser(request
				.getParameter("emailaddress"));

		if (consentUser == null) {

			return;
		}

		String participation = request.getParameter("participation");

		boolean part = false;
		if (participation.equalsIgnoreCase("true")) {

			part = true;
		}

		try {

			response.setContentType("application/pdf");
			ccService.createConsent(null, part, consentUser, user,
					response.getOutputStream());

		} catch (IOException e) {
			Logger.getLogger(this.getClass()).error(e);
		}

	}

	/**
	 * This method handles requests aiming to obtain the current consent of the
	 * person stored in the session as XML-Document
	 */
	private void requestTypeNewConsentCDA() {

		User consentUser = (User) request.getSession().getAttribute(
				"consentUser");
		Document policySet = (Document) request.getSession().getAttribute(
				"policySetForUser");
		Document cda = ccService.createConsent(policySet, true, consentUser,
				user, null);
		writeXML(cda);
		request.getSession().setAttribute("originalCDA", cda);

	}

	/**
	 * This method handles requests aiming to store the consent of the person
	 * stored in the session in it's current status without an electronic
	 * signature
	 */
	private void requestTypeStoreUnclearedConsent() {

		User consentUser = (User) request.getSession().getAttribute(
				"consentUser");
		Document policySet = (Document) request.getSession().getAttribute(
				"policySetForUser");
		Document domCDA = ccService.createConsent(policySet, true, consentUser,
				user, null);

		if (ccService.storeUnclearedConsent(domCDA, user.getID(), 1)) {

			JSONObject jso = new JSONObject();

			try {

				jso.put("success", true);
				jso.put("message",
						"Ihre Einwilligungserklärung wird bis zum Eingang Ihrer schriftlichen Form vorgehalten.");
				writeJSONObject(jso);

			} catch (JSONException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		} else {

			JSONObject jso = new JSONObject();

			try {

				jso.put("success", false);
				jso.put("message",
						"Ihre Einwillgungserklärung konnte nicht abgelegt werden.");
				writeJSONObject(jso);

			} catch (JSONException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
	}

	/**
	 * This method handles requests aiming to store the consent of the person
	 * stored in the session in it's current state without an electronic
	 * signature
	 */
	private void requestTypeStoreStandardUnclearedConsent() {

		User consentUser = ccService.getUser(request
				.getParameter("emailaddress"));
		String participation = request.getParameter("participation");

		if (consentUser == null) {

			writeErrorMessage("Bei der Bearbeitung Ihrer Anfrage ist ein Fehler aufgetreten.");
		}

		boolean part = false;
		int partint = 0;
		if (participation.equalsIgnoreCase("true")) {

			part = true;
			partint = 1;
		}

		if (ccService.storeUnclearedConsent(
				ccService.createConsent(null, part, consentUser, user, null),
				consentUser.getID(), partint)) {

			this.writeSuccessMessage("Die unsignierte Einwilligungserklärung wird bis zu ihrer Freischaltung vorgehalten.");

		} else {

			this.writeErrorMessage("Die unsignierte Einwilligungserklärung konnte nicht gespeichert werden.");
		}
	}

	/**
	 * This methods handles requests with an attached, electronically signed
	 * consent, which are to be stored to the Consent Management Service for the
	 * user stored in the session
	 */
	private void requestTypeStoreSignedConsent() {

		ObjectInputStream inputStream = null;
		Object obj = null;

		try {

			// Construct the ObjectInputStream object
			inputStream = new ObjectInputStream(request.getInputStream());

			while ((obj = inputStream.readObject()) != null) {

			}
		} catch (Exception e) {

		}
		Document signedCDA = (Document) obj;

		User consentUser = (User) request.getSession().getAttribute(
				"consentUser");
		Document originalCDA = (Document) request.getSession().getAttribute(
				"originalCDA");

		if (ccService.storeSignedConsent(signedCDA, originalCDA, consentUser)) {

			JSONObject jso = new JSONObject();

			try {

				jso.put("success", true);
				jso.put("message",
						"Ihre Einwilligungserklärung wurde erfolgreich gespeichert.");
				writeJSONObject(jso);

			} catch (JSONException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		} else {

			JSONObject jso = new JSONObject();

			try {

				jso.put("success", false);
				jso.put("message",
						"Ihre Einwillgungserklärung konnte nicht abgelegt werden.");
				writeJSONObject(jso);

			} catch (JSONException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
	}

	/**
	 * This method handles requests regarding the description of a newly
	 * constructed rule by utilization of the provided parameters of the
	 * request.
	 */
	private void requestTypeRuleDescription() {

		try {

			String organisation = request.getParameter("organisation");
			String persons = request.getParameter("persons");
			String affectedOID = request.getParameter("affectedOID");
			String documents = request.getParameter("documents");
			String documentsOID = request.getParameter("documentsOID");
			String accessType = request.getParameter("accessType");
			boolean grantAccess = Boolean.parseBoolean(request
					.getParameter("grantAccess"));

			Document rule = ccService.constructRule(organisation, persons,
					affectedOID, documents, documentsOID, accessType,
					grantAccess);

			String desc = rule.getElementsByTagName("Description").item(0)
					.getTextContent();

			JSONObject jso = new JSONObject();
			jso.put("description", desc);

			writeJSONObject(jso);

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}

	/**
	 * 
	 * This method adds a, from the parameters of the request, newly constructed
	 * Rule to the PolicySet of the person stored in the session
	 */
	private void requestTypeAddRule() {

		try {

			String organisation = request.getParameter("organisation");
			String persons = request.getParameter("persons");
			String affectedOID = request.getParameter("affectedOID");
			String documents = request.getParameter("documents");
			String documentsOID = request.getParameter("documentsOID");
			String accessType = request.getParameter("accessType");
			boolean grantAccess = Boolean.parseBoolean(request
					.getParameter("grantAccess"));

			Document rule = ccService.constructRule(organisation, persons,
					affectedOID, documents, documentsOID, accessType,
					grantAccess);
			Document policySet = (Document) request.getSession().getAttribute(
					"policySetForUser");

			int ruleType = -1;

			if (documents.equalsIgnoreCase("ALL")) {

				ruleType = 2;

			} else {

				ruleType = 1;
			}
			ccService.addRuleToPolicySet(policySet, rule, ruleType);

			request.getSession().setAttribute("policySetForUser", policySet);
			JSONObject ret = new JSONObject();
			ret.put("added", true);

			this.writeJSONObject(ret);

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}

	/**
	 * This method handles requests aiming to remove a certain Rule from the
	 * PolicySet of the person stored in the session
	 */
	private void requestTypeRemoveRule() {

		JSONObject ret = new JSONObject();
		try {

			Document policySet = (Document) request.getSession().getAttribute(
					"policySetForUser");

			if (ccService.removeRule(request.getParameter("id"), policySet)) {

				request.getSession()
						.setAttribute("policySetForUser", policySet);
				ret.put("removed", true);
				this.writeJSONObject(ret);

			}
		} catch (Exception e) {

			try {

				ret.put("removed", false);

			} catch (JSONException e1) {

			}

			this.writeJSONObject(ret);
			Logger.getLogger(this.getClass()).error(e);
		}
	}

	/**
	 * This method handles request aiming to receive a complete list of the
	 * descriptions of the Rules of the person stored in the session
	 */
	private void requestTypeRuleList() {

		try {

			User consentUser = (User) request.getSession().getAttribute(
					"consentUser");

			Document domPolicySet = (Document) request.getSession()
					.getAttribute("policySetForUser");

			if (domPolicySet == null) {

				String newlyRegistered = (String) request.getSession()
						.getAttribute("newlyregistered");

				if (newlyRegistered.equalsIgnoreCase("true")) {

					domPolicySet = ccService.getStandardPolicySet(consentUser);

				} else {

					domPolicySet = ccService.getPolicySet(consentUser);
				}
			}

			request.getSession().setAttribute("policySetForUser", domPolicySet);

			Vector<JSONObject> vjbo = new Vector<JSONObject>();

			NodeList listPolicies = domPolicySet.getElementsByTagName("Policy");

			for (int j = 0; j < listPolicies.getLength(); j++) {

				Node policy = listPolicies.item(j);

				for (int i = 0; i < policy.getChildNodes().getLength(); i++) {

					if (policy.getChildNodes().item(i).getNodeName()
							.equalsIgnoreCase("Rule")) {

						JSONObject jbo = new JSONObject();
						jbo.put("ruleID", policy.getChildNodes().item(i)
								.getAttributes().item(1).getTextContent());
						jbo.put("description", policy.getChildNodes().item(i)
								.getChildNodes().item(1).getTextContent());

						vjbo.add(jbo);
					}
				}
			}
			writeJSONArray(new JSONArray(vjbo));

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}

	/**
	 * This method provides upon request the root of the organisation-tree and
	 * the first batch of it's children.
	 */
	@SuppressWarnings("rawtypes")
	private void requestTypeTreeRoot() {

		try {

			DefaultMutableTreeNode node = ccService.getOIDTreeRoot();

			Vector<JSONObject> vjbo = new Vector<JSONObject>();

			for (Enumeration e = node.children(); e.hasMoreElements();) {

				DefaultMutableTreeNode n = (DefaultMutableTreeNode) e
						.nextElement();
				JSONObject jso = new JSONObject();
				jso.put("name", ((OIDObject) n.getUserObject()).getName());
				jso.put("identifier",
						((OIDObject) n.getUserObject()).getIdentifier());
				jso.put("hasChildren", !n.isLeaf());
				jso.put("isActive", ((OIDObject) n.getUserObject()).isActive());

				vjbo.add(jso);
			}

			JSONObject root = new JSONObject();
			root.put("name", ((OIDObject) node.getUserObject()).getName());
			root.put("identifier",
					((OIDObject) node.getUserObject()).getIdentifier());
			root.put("hasChildren", !node.isLeaf());
			root.put("isActive", ((OIDObject) node.getUserObject()).isActive());
			root.put("children", new JSONArray(vjbo));

			writeJSONObject(root);

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}

	/**
	 * This method returns to the requester the children of the
	 * organisation-tree node with the given identifier.
	 */
	private void requestTypeExpandTree() {

		try {

			Vector<OIDObject> vtn = ccService.getOIDTreeNodeChildren(request
					.getParameter("identifier"));
			Vector<JSONObject> vjso = new Vector<JSONObject>();

			for (int i = 0; i < vtn.size(); i++) {

				JSONObject jso = new JSONObject();
				jso.put("name", vtn.elementAt(i).getName());
				jso.put("identifier", vtn.elementAt(i).getIdentifier());
				jso.put("hasChildren", vtn.elementAt(i).hasChildren());
				jso.put("isActive", vtn.elementAt(i).isActive());

				vjso.add(jso);
			}
			writeJSONArray(new JSONArray(vjso));

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}

	/**
	 * This method handles requests asking for all nodes containing the given
	 * String
	 * 
	 */
	private void requestTypeSearchTree() {

		String searchString = request.getParameter("searchstring").trim();

		if (searchString.equalsIgnoreCase("")) {

			requestTypeTreeRoot();

		} else {

			try {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) ccService
						.getOIDTreeNodes(searchString).getRoot();

				ConsentCreatorUtilities consentCreatorUtilities = ConsentCreatorUtilities
						.getInstance();

				Vector<JSONObject> vjbo = consentCreatorUtilities
						.traverseTreeForExpand(node);

				JSONObject root = new JSONObject();
				root.put("name", ((OIDObject) node.getUserObject()).getName());
				root.put("identifier",
						((OIDObject) node.getUserObject()).getIdentifier());
				root.put("hasChildren", !node.isLeaf());
				root.put("isActive",
						((OIDObject) node.getUserObject()).isActive());
				root.put("children", new JSONArray(vjbo));

				writeJSONObject(root);

			} catch (Exception e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
	}

	/**
	 * This method upon request returns the list of currently available
	 * document-types.
	 */
	private void requestTypeDocumentTypes() {

		try {

			Vector<JSONObject> vjso = new Vector<JSONObject>();

			Vector<OIDObject> voo = ccService.getDocumentList();

			for (int i = 0; i < voo.size(); i++) {

				JSONObject jso = new JSONObject();

				jso.put("name", ((OIDObject) voo.elementAt(i)).getName());
				jso.put("identifier",
						((OIDObject) voo.elementAt(i)).getIdentifier());

				vjso.add(jso);
			}
			writeJSONArray(new JSONArray(vjso));

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

	}

	/**
	 * This method upon request returns the list of currently available roles.
	 * 
	 */
	private void requestTypePersons() {

		// FIXME remove stub
		try {

			Vector<JSONObject> vjso = new Vector<JSONObject>();

			JSONObject jso = new JSONObject();
			jso.put("name", "Alle");
			jso.put("identifier", "ALL");

			JSONObject jso2 = new JSONObject();
			jso2.put("name", "Ärzte");
			jso2.put("identifier", "1.1.1");

			JSONObject jso3 = new JSONObject();
			jso3.put("name", "Krankenschwestern");
			jso3.put("identifier", "1.1.2");

			vjso.add(jso);
			vjso.add(jso2);
			vjso.add(jso3);
			writeJSONArray(new JSONArray(vjso));

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}

	/**
	 * This method upon request constructs a Document with the given
	 * participation parameter and returns this Document to the requesting
	 * person.
	 */
	private void requestTypeStandardConsentCDA() {

		User consentUser = (User) request.getSession().getAttribute(
				"consentUser");
		String part = request.getParameter("participation");

		writeXML(ccService.createConsent(null, Boolean.parseBoolean(part),
				consentUser, user, null));

	}
}