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

import org.openehealth.coms.cc.web_frontend.consentcreator.model.User;
import org.openehealth.coms.cc.web_frontend.consentcreator.service.ServiceException;


/**
 * This servlet handles requests regarding the uncleared consents of the
 * patients. Consents can either be accepted or rejected.
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 * 
 */
public class ClearConsentServiceServlet extends AbstractServlet {

	private static final long serialVersionUID = 2L;

	
	/**
	 * This method does the actual request processing.
	 * 
	 * @throws ServiceException
	 * @throws Exception
	 */
	@Override
	public void doService() throws ServiceException, Exception {

		if (requestType.equals("rejectconsent")) {

			requestTypeRejectConsent();

		} else if (requestType.equals("acceptconsent")) {

			requestTypeAcceptConsent();

		} else {

			this.writeErrorMessage("Der übergebene Parameter ist unbekannt.");
		}
	}

	/**
	 * This method handles requests which aim to reject an uncleared consent.
	 */
	private void requestTypeRejectConsent() {

		if (request.getParameter("emailaddress").trim().equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Emailaddresse fehlt.");
			return;

		} else {

			User u = ccService.getUser(request.getParameter("emailaddress"));

			if (u != null) {

				if (ccService.rejectUnclearedConsent(u.getID())) {

					this.writeSuccessMessage("Die Einwilligungserklärung wurde gelöscht und nicht freigeschaltet.");

				} else {

					this.writeErrorMessage("Die Einwilligungserklärung konnte nicht gelöscht werden.");
				}
			} else {

				this.writeErrorMessage("Die Einwilligungserklärung konnte nicht gelöscht werden.");
			}
		}
	}

	/**
	 * This method handles requests which aim to accept an uncleared consent.
	 * 
	 */
	private void requestTypeAcceptConsent() {

		if (request.getParameter("emailaddress").trim().equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Emailaddresse fehlt.");

		} else if (request.getParameter("endparticipation").trim()
				.equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Teilnamestatus fehlt.");

		} else {

			boolean endparticipation = false;
			if (request.getParameter("endparticipation").equalsIgnoreCase(
					"true")) {
				endparticipation = true;
			}
			User u = ccService.getUser(request.getParameter("emailaddress"));
			if (u != null) {
				if (ccService.acceptUnclearedConsent(u)) {
					if (endparticipation) {
						if (ccService.deactivateUser(u)) {
							this.writeSuccessMessage("Die Einwilligungserklärung wurde freigeschaltet und der Benutzer deaktiviert.");
						} else {
							this.writeErrorMessage("Die Einwilligungserklärung wurde freigeschaltet, der Benutzer konnte jedoch nicht deaktiviert werden.");
						}
					} else {
						this.writeSuccessMessage("Die Einwilligungserklärung wurde freigeschaltet.");
					}
				} else {
					this.writeErrorMessage("Die Einwilligungserklärung konnte nicht freigeschaltet werden.");
				}
			} else {
				this.writeErrorMessage("Die Einwilligungserklärung konnte nicht freigeschaltet werden.");
			}
		}
	}
}
