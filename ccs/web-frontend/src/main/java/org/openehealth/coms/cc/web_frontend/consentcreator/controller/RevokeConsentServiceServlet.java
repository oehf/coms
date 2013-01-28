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

import java.io.ObjectInputStream;

import org.apache.log4j.Logger;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.User;
import org.openehealth.coms.cc.web_frontend.consentcreator.service.ServiceException;
import org.openehealth.coms.cc.web_frontend.org.json.JSONException;
import org.openehealth.coms.cc.web_frontend.org.json.JSONObject;
import org.w3c.dom.Document;



/**
 * This servlet is used users wishing to either end their participation or to
 * reset their consent.
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 * 
 */
public class RevokeConsentServiceServlet extends AbstractServlet {

	private static final long serialVersionUID = 17L;


	/**
	 * This method does the actual request processing.
	 * 
	 * @throws ServiceException
	 * @throws Exception
	 */
	@Override
	public void doService() throws ServiceException, Exception {

		if (requestType.equals("revokeconsentpdf")) {

			requestTypeRevokeConsentPDF();

		} else if (requestType.equals("revokeconsentcda")) {

			requestTypeRevokeConsentCDA();

		} else if (requestType.equals("revokeconsent")) {

			if (request.getParameter("context").equalsIgnoreCase("")) {

				this.writeErrorMessage("Der übergebene Parameter ist unbekannt.");
				
			} else {

				requestTypeRevokeConsent();
			}
		} else if (requestType.equals("endparticipation")) {

			requestTypeEndParticipation();

		} else {

			this.writeErrorMessage("Der übergebene Parameter ist unbekannt.");
		}

	}

	/**
	 * Handles requests aiming to receive a PDF representation of the consent
	 * created by utilizing the parameters given in the request.
	 */
	private void requestTypeRevokeConsentPDF() {

		try {
			
			user = (User) request.getSession().getAttribute("user");
			String participation = (String) request
					.getParameter("participation");

			boolean part = true;
			
			if (participation.equalsIgnoreCase("endparticipation")) {
				
				part = false;
			}
			response.setContentType("application/pdf");
			ccService.createConsent(null, part, user, user,
					response.getOutputStream());
			
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}

	/**
	 * This method handles requests aiming to receive a CDA Document
	 * representation of the consent created by utilizing the parameters given
	 * in the request.
	 */
	private void requestTypeRevokeConsentCDA() {

		user = (User) request.getSession().getAttribute("user");
		boolean participation = (Boolean) request.getAttribute("participation");
		Document cda = ccService.createConsent(null, participation, user, user,
				null);
		writeXML(cda);
		request.getSession().setAttribute("originalCDA", cda);
	}

	
	/**
	 * This method handles requests to aiming to reset the consent of the
	 * requesting person.
	 */
	private void requestTypeRevokeConsent() {

		user = (User) request.getSession().getAttribute("user");

		if (request.getParameter("context").equalsIgnoreCase("written")) {

			Document cda = ccService
					.createConsent(null, true, user, user, null);
			
			if (ccService.storeUnclearedConsent(cda, user.getID(), 1)) {
				
				this.writeSuccessMessage("Ihre Anfrage wurde erfolgreich bearbeitet und Ihre Einwilligungserklärung wird bis zum Eintreffen Ihrer schriftlichen Form vorgehalten.");
		
			} else {
				
				this.writeErrorMessage("Ihre Anfrage verursachte einen Fehler. Ihre Einwilligungserklärung konnte nicht gespeichert werden.");
			}
		} else if (request.getParameter("context").equalsIgnoreCase("digital")) {

			ObjectInputStream inputStream = null;
			Object obj = null;
			
			try {

				// Construct the ObjectInputStream object
				inputStream = new ObjectInputStream(request.getInputStream());

				while ((obj = inputStream.readObject()) != null) {

				}

			} catch (Exception e) {
				Logger.getLogger(this.getClass()).error(e);
			}

			Document signedCDA = (Document) obj;

			Document originalCDA = (Document) request.getSession()
					.getAttribute("originalCDA");
			if (ccService.storeSignedConsent(signedCDA, originalCDA,
					user)) {

				JSONObject jso = new JSONObject();

				try {
					jso.put("success", true);
					jso.put("message",
							"Ihre Anfrage wurde erfolgreich bearbeitet und Ihre Einwilligungserklärung wurde gespeichert.");
					writeJSONObject(jso);
				} catch (JSONException e2) {
					Logger.getLogger(this.getClass()).error(e2);
				}
			} else {

				JSONObject jso = new JSONObject();

				try {
					jso.put("success", false);
					jso.put("message",
							"Ihre Anfrage verursachte einen Fehler. Ihre Einwilligungserklärung konnte nicht gespeichert werden.");
					writeJSONObject(jso);
				} catch (JSONException e) {
					Logger.getLogger(this.getClass()).error(e);
				}
			}

		} else {
			this.writeErrorMessage("Der übergebene Parameter ist unbekannt.");
		}
	}

	/**
	 * This method handles requests to aiming to end the participation of the
	 * requesting person.
	 */
	private void requestTypeEndParticipation() {

		user = (User) request.getSession().getAttribute("user");

		if (request.getParameter("context").equalsIgnoreCase("written")) {

			Document cda = ccService.createConsent(null, false, user, user,
					null);
			if (ccService.storeUnclearedConsent(cda, user.getID(), 0)) {

				JSONObject jso = new JSONObject();

				try {
					jso.put("success", true);
					jso.put("message",
							"Ihre Anfrage wurde erfolgreich bearbeitet und Ihre Einwilligungserklärung wird bis zum Eintreffen Ihrer schriftlichen Form vorgehalten.");
					writeJSONObject(jso);
				} catch (JSONException e) {
					Logger.getLogger(this.getClass()).error(e);
				}

			} else {

				JSONObject jso = new JSONObject();

				try {
					jso.put("success", false);
					jso.put("message",
							"Ihre Anfrage verursachte einen Fehler. Ihre Einwilligungserklärung konnte nicht gespeichert werden.");
					writeJSONObject(jso);
				} catch (JSONException e) {
					Logger.getLogger(this.getClass()).error(e);
				}
			}
		} else if (request.getParameter("context").equalsIgnoreCase("digital")) {

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

			if (signedCDA == null) {

				JSONObject jso = new JSONObject();
				try {
					jso.put("success", false);
					jso.put("message",
							"Ihre Anfrage verursachte einen Fehler. Ihre Einwilligungserklärung konnte nicht gespeichert werden.");
					writeJSONObject(jso);
				} catch (JSONException e2) {
					Logger.getLogger(this.getClass()).error(e2);
				}
			}

			Document originalCDA = (Document) request.getSession()
					.getAttribute("originalCDA");

			if (ccService.storeSignedConsent(signedCDA, originalCDA,
					user)) {
				if (ccService.deactivateUser(user)) {
					request.getSession().invalidate();

					JSONObject jso = new JSONObject();

					try {
						jso.put("success", true);
						jso.put("message",
								"Ihre Teilnahme an ISIS wurde erfolgreich beendet.");
						writeJSONObject(jso);
					} catch (JSONException e) {
						Logger.getLogger(this.getClass()).error(e);
					}
				} else {

					JSONObject jso = new JSONObject();

					try {
						jso.put("success", false);
						jso.put("message",
								"Ihre Anfrage verursachte einen Fehler. Ihre Einwilligungserklärung konnte nicht gespeichert werden.");
						writeJSONObject(jso);
					} catch (JSONException e) {
						Logger.getLogger(this.getClass()).error(e);
					}
				}
			} else {

				JSONObject jso = new JSONObject();

				try {
					jso.put("success", false);
					jso.put("message",
							"Ihre Anfrage verursachte einen Fehler. Ihre Einwilligungserklärung konnte nicht gespeichert werden.");
					writeJSONObject(jso);
				} catch (JSONException e) {
					Logger.getLogger(this.getClass()).error(e);
				}
			}
		} else {
			this.writeErrorMessage("Der übergebene Parameter ist unbekannt.");
		}
	}
}