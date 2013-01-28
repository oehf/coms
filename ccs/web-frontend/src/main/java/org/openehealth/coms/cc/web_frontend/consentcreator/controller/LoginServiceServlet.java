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

import org.openehealth.coms.cc.web_frontend.consentcreator.service.ServiceException;

/**
 * This servlet handles login-requests.
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 * 
 */
public class LoginServiceServlet extends AbstractServlet {

	private static final long serialVersionUID = 8L;

	/**
	 * @see controller.AbstractServlet#doService()
	 * @throws ServiceException
	 *             , Exception
	 */
	@Override
	public void doService() throws ServiceException, Exception {

		if (requestType.equals("login")) {

			requestTypeLogin();

		} else {

			this.writeErrorMessage("Der übergebene Parameter ist unbekannt.");
		}
	}


	/**
	 * Login user in the actual request processing
	 * 
	 */
	private void requestTypeLogin() {

		if (ccService.login(request.getParameter("emailaddress"),
				request.getParameter("password"))) {

			user = ccService.getUser(request.getParameter("emailaddress")
					.toLowerCase());

			// TODO ID des CDA in die Session für Hochzählen der SetID
			request.getSession().setAttribute("user", user);

			if (user.isActive() != 1) {

				request.getSession().setAttribute("user", null);
				this.writeErrorMessage("Mit den angegebenen Daten war kein Anmelden möglich.");

				// Patient?
			} else if (user.getPrivileges() == 0 ) { // =

				request.getSession().setAttribute("policySet",
						ccService.getPolicySet(user));
				this.dispatch("/index.jsp");

				// care provider or Admin?
			} else if (user.getPrivileges() == 1 ||  user.getPrivileges() == 2) {
				this.dispatch("/index.jsp");
				
			} else {

				this.writeErrorMessage("Unbekannte Priviliegien");
			}
		} else {

			this.writeErrorMessage("Mit den angegebenen Daten war kein Anmelden möglich.");
		}
	}
}