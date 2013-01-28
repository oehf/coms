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
 * This servlet handles requests aiming to set a new password.
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 * 
 */
public class RetrievePasswordServiceServlet extends AbstractServlet {

	private final static long serialVersionUID = 16L;


	/**
	 * This method does the actual request processing.
	 * 
	 * @throws ServiceException
	 * @throws Exception
	 */
	@Override
	public void doService() throws ServiceException, Exception {

		// Checks if a valid password request exists and then dispatches the
		// menu
		if (requestType.equals("newPassword")) {

			requestTypeNewPassword();

			// Sets the password for the user
		} else if (requestType.equals("setPassword")) {

			requestTypeSetPassword();

			// Stores a password request for the given emailaddress and sends an
			// email
		} else if (requestType.equals("requestPassword")) {

			requestTypeRequestPassword();
		} else {

			this.writeErrorMessage("Der übergebene Parameter ist unbekannt.");
		}
	}

	/**
	 * This method handles requests aiming to be forwarded to the
	 * setpasswordpage in order to set a new password. The given referer is
	 * checked before forwarding the request.
	 */
	private void requestTypeNewPassword() {

		if (request.getParameter("ref").trim().equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Referer fehlt.");
			return;
		}

		String s = request.getParameter("ref");

		if (ccService.validPasswordRequest(s, true)) {

			User user = ccService.getUserByPasswordRequest(s);

			if (user != null) {

				request.setAttribute("show", "setpasswordpage");
				request.setAttribute("ref", request.getParameter("ref"));
				request.setAttribute("emailaddress", user.getEmailaddress());
				this.dispatch("/index.jsp");
			} else {

				this.writeErrorMessage("Die Anfrage konnte nicht bearbeitet werden.");
			}
		} else {

			this.writeErrorMessage("Die Anfrage konnte nicht bearbeitet werden.");
		}
	}

	/**
	 * This method sets a new password to the user associated with the given
	 * emailaddress and referrer.
	 */
	private void requestTypeSetPassword() {

		if (request.getParameter("emailaddress").trim().equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Emailaddresse fehlt.");
			return;

		} else if (request.getParameter("password").trim().equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Passwort fehlt.");
			return;

		} else if (request.getParameter("ref").trim().equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Referer fehlt.");
			return;
		}

		String emailaddress = request.getParameter("emailaddress");
		String password = request.getParameter("password");
		String s = request.getParameter("ref");

		User oUser = ccService.getUserByPasswordRequest(s);

		User user = ccService.getUser(emailaddress);

		if (!(oUser.getEmailaddress().equalsIgnoreCase(user.getEmailaddress()))) {

			this.writeErrorMessage("Bitte versuchen Sie nur Ihr eigenes Passwort zu ändern.");
			return;

		}

		if (ccService.validPasswordRequest(s, true)) {

			if (ccService.setPassword(user, password, s)) {

				this.writeSuccessMessage("Ihr Passwort wurde erfolgreich neu gesetzt.");

			} else {

				this.writeErrorMessage("Das Passwort konnte nicht gesetzt werden.");
			}
		} else {

			this.writeErrorMessage("Die Anfrage konnte nicht bearbeitet werden.");
		}
	}

	/**
	 * This method handles requests aiming to receive an email with a link which
	 * can be used to set a new password.
	 */
	private void requestTypeRequestPassword() {

		if (request.getParameter("emailaddress").trim().equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Emailaddresse fehlt.");
			return;
		}

		String emailaddress = request.getParameter("emailaddress");

		if (ccService.requestPassword(emailaddress)) {

			this.writeSuccessMessage("Es wurde eine Email mit weiteren Informationen bezüglich eines neuen Passwortes an Sie versendet.");

		} else {

			this.writeErrorMessage("Es konnte keine Email mit weiteren Informationen bezüglich eines neuen Passwortes an Sie versendet werden.");
		}
	}
}