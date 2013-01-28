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
 * This servlet handles basic page-view requests for pages which do not require
 * users to be logged in.
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 * 
 */
public class Dispatcher extends AbstractServlet {

	private static final long serialVersionUID = 7L;


	/**
	 * This method does the actual request processing.
	 * 
	 * @throws ServiceException
	 *             , Exception
	 */
	@Override
	public void doService() throws ServiceException, Exception {

		if (requestType.equalsIgnoreCase("homepage")) {

			requestTypeHomePage();

		} else if (requestType.equalsIgnoreCase("loginpage")) {

			requestTypeLoginPage();

		} else if (requestType.equalsIgnoreCase("registrationpage")) {

			requestTypeRegistrationPage();

		} else if (requestType.equalsIgnoreCase("passwordpage")) {

			requestTypePasswordPage();

		} else if (requestType.equalsIgnoreCase("contactpage")) {

			requestTypeContactPage();

		} else if (requestType.equalsIgnoreCase("legalnoticepage")) {

			requestTypeLegalNoticePage();

		} else {

			this.writeErrorMessage("Der übergebene Parameter ist unbekannt.");
		}
	}


	/**
	 * set home page in the actual request processing
	 * 
	 */
	private void requestTypeHomePage() {

		request.setAttribute("show", "");
		this.dispatch("/index.jsp");
	}


	/**
	 * set login page in the actual request processing
	 * 
	 */
	private void requestTypeLoginPage() {

		request.setAttribute("show", "loginpage");
		this.dispatch("/index.jsp");
	}


	/**
	 * set registration page in the actual request processing
	 * 
	 */
	private void requestTypeRegistrationPage() {

		request.setAttribute("show", "registrationpage");
		this.dispatch("/index.jsp");
	}


	/**
	 * set password page in the actual request processing
	 * 
	 */
	private void requestTypePasswordPage() {

		request.setAttribute("show", "passwordpage");
		this.dispatch("/index.jsp");
	}


	/**
	 * set contact page in the actual request processing
	 * 
	 */
	private void requestTypeContactPage() {

		request.setAttribute("show", "contactpage");
		this.dispatch("/index.jsp");
	}


	/**
	 * Open legalnotice page in the actual request processing
	 * 
	 */
	private void requestTypeLegalNoticePage() {

		request.setAttribute("show", "legalnoticepage");
		this.dispatch("/index.jsp");
	}
}
