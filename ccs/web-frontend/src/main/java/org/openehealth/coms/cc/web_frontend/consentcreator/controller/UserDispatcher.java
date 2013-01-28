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
 * This servlet forwards pages to unprivileged users upon request.
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 * 
 */
public class UserDispatcher extends AbstractServlet {

	private static final long serialVersionUID = 19L;


	/**
	 * This method does the actual request processing.
	 * 
	 * @throws ServiceException
	 * @throws Exception
	 */
	@Override
	public void doService() throws ServiceException, Exception {

		if (requestType.equalsIgnoreCase("homepage")) {
			requestTypeHomePage();
		} else if (requestType.equalsIgnoreCase("createconsentpage")) {
			requestTypeConsentPage();
		} else if (requestType.equalsIgnoreCase("revokeconsentpage")) {
			requestTypeRevokeConsentPage();
		} else if (requestType.equalsIgnoreCase("editdetailspage")) {
			requestTypeEditDetailsPage();
		} else if (requestType.equalsIgnoreCase("signdigitalpage")) {
			requestTypeSignDigitalPage();
		} else if (requestType.equalsIgnoreCase("signwrittenpage")) {
			requestTypeSignWrittenPage();
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
	 * set consent page in the actual request processing
	 * 
	 */
	private void requestTypeConsentPage() {
		request.setAttribute("show", "createconsentpage");
		this.dispatch("/index.jsp");
	}


	/**
	 * set revokeconsent page in the actual request processing
	 * 
	 */
	private void requestTypeRevokeConsentPage() {
		request.setAttribute("show", "revokeconsentpage");
		request.setAttribute("endparticipation",
				request.getParameter("endparticipation"));
		this.dispatch("/index.jsp");
	}


	/**
	 * set edit details page in the actual request processing
	 * 
	 */
	private void requestTypeEditDetailsPage() {
		request.setAttribute("show", "editdetailspage");
		this.dispatch("/index.jsp");
	}


	/**
	 * set sign digital page in the actual request processing
	 * 
	 */
	private void requestTypeSignDigitalPage() {
		request.setAttribute("show", "signdigitalpage");
		request.setAttribute("endparticipation",
				request.getParameter("endparticipation"));
		this.dispatch("/index.jsp");
	}


	/**
	 * set sign written page in the actual request processing
	 * 
	 */
	private void requestTypeSignWrittenPage() {
		request.setAttribute("show", "signwrittenpage");
		request.setAttribute("endparticipation",
				request.getParameter("endparticipation"));
		this.dispatch("/index.jsp");
	}
}
