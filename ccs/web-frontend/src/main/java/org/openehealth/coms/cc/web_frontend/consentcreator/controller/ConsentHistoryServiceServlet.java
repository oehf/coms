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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.User;
import org.openehealth.coms.cc.web_frontend.consentcreator.service.ServiceException;
import org.openehealth.coms.cc.web_frontend.org.json.JSONArray;
import org.openehealth.coms.cc.web_frontend.org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * This servlet enables privileged users to obtain the entire consent history of
 * a patient and to retrieve specific documents from the history.
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 * 
 */
public class ConsentHistoryServiceServlet extends AbstractServlet {

	private static final long serialVersionUID = 4L;

	/**
	 * 
	 * @throws ServiceException
	 * @throws Exception
	 */
	@Override
	public void doService() throws ServiceException, Exception {

		if (requestType.equals("consentHistory")) {

			requestTypeConsentHistory();

		} else if (requestType.equals("consent")) {

			requestTypeConsent();

		} else {

			this.writeErrorMessage("Der übergebene Parameter ist unbekannt.");
		}

	}

	/**
	 * This method handles requests which aim to obtain the entire consent
	 * history of a patient.
	 * 
	 */
	private void requestTypeConsentHistory() {

		try {

			if (request.getParameter("emailaddress").trim()
					.equalsIgnoreCase("")) {

				this.writeErrorMessage("Der übergebene Parameter Emailaddresse fehlt.");
				return;
			}

			User soughtUser = ccService.getUser((String) request
					.getParameter("emailaddress"));

			if (soughtUser == null) {

				writeErrorMessage("Die Liste der Dokumente des Patienten konnte nicht gefunden werden.");
				return;
			}

			Document domList = ccService.getConsentHistory(soughtUser.getID());

			if (domList != null) {

				NodeList nl = domList.getElementsByTagName("ConsentDocument");
				Vector<JSONObject> vjo = new Vector<JSONObject>();

				for (int i = 0; i < nl.getLength(); i++) {
					JSONObject jso = new JSONObject();
					jso.put("root", nl.item(i).getChildNodes().item(1)
							.getAttributes().item(1).getNodeValue());
					jso.put("extension", nl.item(i).getChildNodes().item(1)
							.getAttributes().item(0).getNodeValue());

					SimpleDateFormat sdf = new SimpleDateFormat();
					sdf.applyPattern("yyyyMMddHHmmssZ");

					Date d = sdf.parse(nl.item(i).getChildNodes().item(3)
							.getAttributes().item(0).getNodeValue());

					SimpleDateFormat displaysdf = new SimpleDateFormat();
					displaysdf.applyPattern("dd.MM.yyyy HH:mm");

					jso.put("effectiveTime", displaysdf.format(d));
					vjo.add(jso);

				}

				JSONArray ja = new JSONArray(vjo);
				request.setAttribute("showList", "true");
				request.setAttribute("show", "consenthistorypage");
				request.setAttribute("list", ja);
				request.setAttribute("soughtUser", soughtUser);
				this.dispatch("/index.jsp");

			} else {

				this.writeErrorMessage("Die Liste der Dokumente des Patienten konnte nicht gefunden werden.");
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}

	/**
	 * This method handles requests which aim to obtain a specific consent
	 * document.
	 * 
	 */
	private void requestTypeConsent() {

		try {
			if (request.getParameter("oid").trim().equalsIgnoreCase("")) {

				this.writeErrorMessage("Der übergebene Parameter ID fehlt.");
				return;
			}

			String oid = request.getParameter("oid");

			response.setContentType("application/pdf");
			ccService.getPDFConsent(oid, response.getOutputStream());

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}
}