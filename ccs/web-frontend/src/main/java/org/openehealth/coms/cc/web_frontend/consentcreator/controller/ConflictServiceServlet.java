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
 * 
 * This servlet handles requests regarding conflicts which occurred during
 * registration, more precisely, users who registered themselves but where
 * already registered with the user register will need to have their
 * registration confirmed. The servlet allows privileged users to reject or
 * accept the registration.
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 * 
 */
public class ConflictServiceServlet extends AbstractServlet {

	private static final long serialVersionUID = 3L;


	/**
	 * This method does the actual request processing.
	 * 
	 * @throws ServiceException
	 * @throws Exception
	 */
	@Override
	public void doService() throws ServiceException, Exception {

		if (requestType.equals("removeconflict")) {

			requestTypeRemoveConflict();

		} else if (requestType.equals("solveconflict")) {

			requestTypeSolveConflict();
		}
	}

	/**
	 * This method handles requests which aim to reject the registration attempt
	 * of a user.
	 * 
	 */
	private void requestTypeRemoveConflict() {

		if (request.getParameter("emailaddress").trim().equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Emailaddresse fehlt.");
			return;
		}
		User retrievedUser = ccService.getUser(request
				.getParameter("emailaddress"));

		if (retrievedUser != null) {
			if (ccService.removeConflict(retrievedUser.getID())) {

				this.writeSuccessMessage("Der Registrierungsversuch wurde gelöscht.");

			} else {

				this.writeErrorMessage("Der Registrierungsversuch konnte nicht gelöscht werden.");
			}
		} else {

			this.writeErrorMessage("Der Registrierungsversuch konnte nicht gelöscht werden.");
		}
	}

	/**
	 * This method handles requests which aim to accept the registration attempt
	 * of a user. The activation of the user's account then has to be documented
	 * by generating a consent.
	 */
	private void requestTypeSolveConflict() {

		if (request.getParameter("emailaddress").trim().equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Emailaddresse fehlt.");
			return;
		}

		User retrievedUser = ccService.getUser(request
				.getParameter("emailaddress"));

		if (retrievedUser != null) {
			if (ccService.solveConflict(retrievedUser)) {

				request.setAttribute("title", "Benutzer freigeschaltet");
				request.setAttribute("message",
						"Der Benutzer wurde freigeschaltet.");
				request.setAttribute(
						"messageOrder",
						"Bitte erstellen Sie eine Einwilligungserklärung, die diesen Vorgang dokumentiert!");
				request.setAttribute("emailaddress",
						request.getParameter("emailaddress"));
				request.setAttribute("context", "clearedUser");
				request.setAttribute("show", "signchoicepage");
				this.dispatch("/index.jsp");

			} else {

				this.writeErrorMessage("Der Benutzer konnte nicht freigeschaltet werden.");
			}
		} else {

			this.writeErrorMessage("Der Benutzer konnte nicht freigeschaltet werden.");
		}
	}
}
