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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;

import org.openehealth.coms.cc.web_frontend.consentcreator.service.ServiceException;


/**
 * This servlet logs the user in and forwards their request to the respective
 * servlet which is normally used to handle each specific request
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 * 
 */
public class QuickServiceServlet extends AbstractServlet {

	private static final long serialVersionUID = 22L;


	/**
	 * This method does the actual request processing.
	 * 
	 * @throws ServiceException
	 * @throws Exception
	 */
	@Override
	public void doService() throws ServiceException, Exception {

		if (ccService.login(request.getParameter("emailaddresslogin"),
				request.getParameter("passwordlogin"))) {

			user = ccService.getUser(request.getParameter("emailaddresslogin")
					.toLowerCase());
			request.getSession().setAttribute("user", user);

			if (user.isActive() != 1) {

				request.getSession().setAttribute("user", null);
				this.writeErrorMessage("Mit den angegebenen Daten war kein Anmelden möglich.");
			} else if (user.getPrivileges() == 0) {

				this.writeErrorMessage("Mit den angegebenen Daten war kein Anmelden möglich.");

			} else if (user.getPrivileges() >= 1 && user.getPrivileges() <= 2) {

				quickService();
			} else {

				this.writeErrorMessage("Unbekannte Priviliegien");
			}
		} else {

			this.writeErrorMessage("Mit den angegebenen Daten war kein Anmelden möglich.");
		}
	}

	/**
	 * 
	 * @throws ServiceException
	 * @throws Exception
	 */
	private void quickService() throws ServiceException, Exception {

		if (request.getParameter("type").equals("registerUser")) {

			// http://localhost:8080/web-frontend/QuickServiceServlet?type=registerUser&emailaddresslogin=root&passwordlogin=123&forename=Donald&name=Duck&emailaddress=asdasd@ghfg.de&street=asdasdf&zipcode=23423&city=LosAngeles&birthdate=12.12.1212&gender=male&privileges=0&active=1

			RequestDispatcher dispatcher = request
					.getRequestDispatcher("PrivilegedRegisterServiceServlet");
			dispatcher.forward(request, response);

		}
	}
}
