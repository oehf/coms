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
 * This servlet handles requests regarding the de- and activation of users.
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 * 
 */
public class DeActivateMemberServiceServlet extends AbstractServlet {

	private static final long serialVersionUID = 6L;

	/**
	 * This method does the actual request processing.
	 * 
	 * @throws ServiceException
	 *             , Exception
	 */
	@Override
	public void doService() throws ServiceException, Exception {

		if (requestType.equals("deactivateUser")) {

			requestTypeDeactivateUser();

		} else if (requestType.equals("activateUser")) {

			requestTypeActivateUser();

		} else {

			this.writeErrorMessage("Der übergebene Parameter ist unbekannt.");
		}
	}

	/**
	 * This method deactivates the user associated with the emailaddress given
	 * in the request.
	 */
	private void requestTypeDeactivateUser() {

		if (request.getParameter("emailaddress").equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Name fehlt.");

		} else {

			User retrievedUser = ccService.getUser(request
					.getParameter("emailaddress"));

			if (retrievedUser != null) {
				if (ccService.deactivateUser(retrievedUser)) {

					request.setAttribute("title", "Benutzer deaktiviert");
					request.setAttribute("message",
							"Der Benutzer wurde erfolgreich deaktiviert.");
					request.setAttribute(
							"messageOrder",
							"Bitte erstellen Sie eine Einwilligungserklärung, die diesen Vorgang dokumentiert!");
					request.setAttribute("emailaddress",
							request.getParameter("emailaddress"));
					request.setAttribute("context", "deactivateUser");
					request.setAttribute("show", "signchoicepage");
					this.dispatch("/index.jsp");

				} else {

					this.writeErrorMessage("Der Benutzer konnte nicht deaktiviert werden.");
				}
			} else {

				this.writeErrorMessage("Der gesuchte Benutzer wurde nicht gefunden");
			}
		}
	}

	/**
	 * This method activates the user associated with the emailaddress given in
	 * the request.
	 */
	private void requestTypeActivateUser() {

		if (request.getParameter("emailaddress").equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Name fehlt.");

		} else {

			User retrievedUser = ccService.getUser(request
					.getParameter("emailaddress"));

			if (retrievedUser != null) {
				if (ccService.activateUser(retrievedUser)) {

					request.setAttribute("title", "Benutzer aktiviert");
					request.setAttribute("message",
							"Der Benutzer wurde erfolgreich aktiviert.");
					request.setAttribute(
							"messageOrder",
							"Bitte erstellen Sie eine Einwilligungserklärung, die diesen Vorgang dokumentiert!");
					request.setAttribute("emailaddress",
							request.getParameter("emailaddress"));
					request.setAttribute("context", "activateUser");
					request.setAttribute("show", "signchoicepage");
					this.dispatch("/index.jsp");

				} else {

					this.writeErrorMessage("Der Benutzer konnte nicht aktiviert werden.");
				}
			} else {

				this.writeErrorMessage("Der gesuchte Benutzer wurde nicht gefunden");
			}
		}
	}
}