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

import org.apache.log4j.Logger;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.User;
import org.openehealth.coms.cc.web_frontend.consentcreator.service.ServiceException;


/**
 * This servlet handles requests by unprivileged users regarding the current PDF
 * representation of their consent.
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 * 
 */
public class RetrieveConsentServiceServlet extends AbstractServlet {

	private static final long serialVersionUID = 15L;


	/**
	 * This method does the actual request processing.
	 * 
	 * @throws ServiceException
	 * @throws Exception
	 */
	@Override
	public void doService() throws ServiceException, Exception {

		if (requestType.equals("retrieveConsent")) {
			
			requestTypeRetrieveConsent();
			
		} else {
			
			this.writeErrorMessage("Der übergebene Parameter ist unbekannt.");
		}
	}

	/**
	 * This method does retrieve the consent to the actual request processing.
	 * 
	 */
	private void requestTypeRetrieveConsent() {

		try {
			
			user = (User) request.getSession().getAttribute("user");
			response.setContentType("application/pdf");
			
			if (!ccService.getLatestPDFConsent(user.getID(),
					response.getOutputStream()).equalsIgnoreCase("")) {
				
				this.writeErrorMessage("Die Einwilligung konnte nicht abgefragt werden.");
			}
			
		} catch (Exception e) {
			System.out.println("Dokument nicht vorhanden");
			Logger.getLogger(this.getClass()).error(e);
		}
	}
}