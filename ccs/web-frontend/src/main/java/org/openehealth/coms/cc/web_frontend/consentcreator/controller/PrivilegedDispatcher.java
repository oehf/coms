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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.User;
import org.openehealth.coms.cc.web_frontend.consentcreator.service.ServiceException;
import org.openehealth.coms.cc.web_frontend.org.json.JSONArray;
import org.openehealth.coms.cc.web_frontend.org.json.JSONObject;


/**
 * This servlet dispatches pages to users with the needed privileges.
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 * 
 */
public class PrivilegedDispatcher extends AbstractServlet {

	private static final long serialVersionUID = 11L;

	/**
	 * @see controller.AbstractServlet#doService()
	 * @throws ServiceException
	 *             , Exception
	 */
	public void doService() throws ServiceException, Exception {

		if (requestType.equals("findUser")) {

			requestTypeFindUser();

		} else if (requestType.equals("registeruserpage")) {

			requestTypeRegisterUserPage();

		} else if (requestType.equals("finduserpage")) {

			requestTypeFindUserPage();

		} else if (requestType.equals("userconflictspage")) {

			requestTypeUserConflictsPage();

		} else if (requestType.equals("clearconsentpage")) {

			requestTypeClearConsentPage();

		} else if (requestType.equals("createconsentpage")) {

			requestTypeCreateConsentPage();

		} else if (requestType.equalsIgnoreCase("signdigitalpage")) {

			requestTypeSignDigitalPage();

		} else if (requestType.equalsIgnoreCase("signwrittenpage")) {

			requestTypeSignWrittenPage();
		
		} else {
			// TODO helwprittensignature & helpdigitalsignature
			this.writeErrorMessage("Der übergebene Parameter ist unbekannt.");
		}
		
	}

	/**
	 * This method handles requests regarding the page showing user conflicts.
	 * After the request, needed data for presentation is obtained and the user
	 * then forwarded to the requested page.
	 */
	@SuppressWarnings("unchecked")
	private void requestTypeUserConflictsPage() {

		try {

			request.setAttribute("show", "userconflictspage");

			SimpleDateFormat displaysdf = new SimpleDateFormat();
			displaysdf.applyPattern("dd.MM.yyyy");

			List<Object> ul = ccService.getListofUserConflicts();
			Vector<JSONObject> vjo = new Vector<JSONObject>();

			for (int i = 0; i < ul.size(); i++) {

				Vector<Object> veco = (Vector<Object>) ul.get(i);
				User u = (User) veco.get(0);

				JSONObject jso = new JSONObject();
				jso.put("name", u.getName());
				jso.put("forename", u.getForename());
				jso.put("birthdate", displaysdf.format(u.getBirthdate()));
				jso.put("emailaddress", u.getEmailaddress());

				if (u.getGender().equalsIgnoreCase("male")) {

					jso.put("gender", "Männlich");

				} else {

					jso.put("gender", "Weiblich");

				}

				jso.put("timestamp", displaysdf.format(new Date(
						((Timestamp) veco.get(1)).getTime())));

				vjo.add(jso);
			}
			request.setAttribute("conflictlist", new JSONArray(vjo));
			this.dispatch("/index.jsp");

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}

	/**
	 * Open FindUser page in the actual request processing
	 * 
	 */
	private void requestTypeFindUserPage() {

		request.setAttribute("show", "finduserpage");
		request.setAttribute("context",
				(String) request.getParameter("context"));
		this.dispatch("/index.jsp");
	}

	/**
	 * Open RegisterUser page in the actual request processing
	 * 
	 */
	private void requestTypeRegisterUserPage() {

		request.setAttribute("show", "registeruserpage");
		this.dispatch("/index.jsp");
	}

	/**
	 * This method handles requests aiming to find a specific user. If the user
	 * is found, the page which was requested in the context of the request will
	 * then be forwarded.
	 */
	private void requestTypeFindUser() {

		if (request.getParameter("name").trim().equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Name fehlt.");

		} else if (request.getParameter("forename").trim().equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Vorname fehlt.");

		} else if (request.getParameter("birthdate").trim()
				.equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Geburtsdatum fehlt.");

		} else if (request.getParameter("gender").trim().equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Geburtsdatum fehlt.");

		} else {

			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			Date d = null;
			try {

				d = sdf.parse(request.getParameter("birthdate"));

				if (!sdf.format(d).equals(request.getParameter("birthdate"))) {

					this.writeErrorMessage("Der übergebene Parameter Geburtsdatum ist ungültig.");
					return;
				}

			} catch (Exception e) {

				this.writeErrorMessage("Der übergebene Parameter Geburtsdatum ist ungültig.");
			}

			User retrievedUser = ccService.getUser(request.getParameter("name")
					.trim(), request.getParameter("forename").trim(), d,
					request.getParameter("gender"));

			if (retrievedUser != null) {

				request.setAttribute("soughtUser", retrievedUser);
				request.setAttribute("show", request.getParameter("context"));
				request.setAttribute("context", request.getParameter("context"));
				this.dispatch("/index.jsp");

			} else {

				this.writeErrorMessage("Der gesuchte Benutzer wurde nicht gefunden");
			}
		}
	}

	/**
	 * 
	 * This method handles requests regarding the clear consent page. After
	 * needed data has been gathered, the requesting person is then forwarded to
	 * the requested page.
	 */
	@SuppressWarnings("unchecked")
	private void requestTypeClearConsentPage() {

		try {

			request.setAttribute("show", "clearconsentpage");

			SimpleDateFormat displaysdf = new SimpleDateFormat();
			displaysdf.applyPattern("dd.MM.yyyy");

			List<Object> ul = ccService.getListofUnclearedConsents();
			Vector<JSONObject> vjo = new Vector<JSONObject>();

			for (int i = 0; i < ul.size(); i++) {

				JSONObject jso = new JSONObject();

				Vector<Object> vecU = (Vector<Object>) ul.get(i);
				User u = (User) vecU.get(0);

				jso.put("name", u.getName());
				jso.put("forename", u.getForename());
				jso.put("birthdate", displaysdf.format(u.getBirthdate()));
				jso.put("emailaddress", u.getEmailaddress());

				if (u.getGender().equalsIgnoreCase("male")) {

					jso.put("gender", "Männlich");

				} else {

					jso.put("gender", "Weiblich");
				}

				if (((String) vecU.get(1)).equalsIgnoreCase("1")) {

					jso.put("participation", "true");

				} else {

					jso.put("participation", "false");
				}

				jso.put("timestamp", displaysdf.format(new Date(
						((Timestamp) vecU.get(2)).getTime())));

				vjo.add(jso);

			}
			request.setAttribute("consentlist", new JSONArray(vjo));
			this.dispatch("/index.jsp");

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}

	/**
	 * This method forwards the requesting person to the createconsentpage and
	 * stores the user for whom a consent will be created in the session of the
	 * requesting person.
	 */
	private void requestTypeCreateConsentPage() {

		request.setAttribute("show", "createconsentpage");

		User consentUser = ccService.getUser(request
				.getParameter("emailaddress"));

		if (consentUser == null) {

			writeErrorMessage("Bei der Bearbeitung Ihrer Anfrage ist ein Fehler aufgetreten.");
			return;
		}

		request.getSession().setAttribute("consentUser", consentUser);
		request.getSession().setAttribute("newlyregistered",
				request.getParameter("newlyregistered"));
		this.dispatch("/index.jsp");
	}

	/**
	 * Forwards the User to the signdigitalpage and adds data regarding the user
	 * for whom a consent shall be created to the session
	 */
	private void requestTypeSignDigitalPage() {

		request.setAttribute("context", request.getParameter("context"));
		request.setAttribute("emailaddress",
				request.getParameter("emailaddress"));

		User consentUser = ccService.getUser(request
				.getParameter("emailaddress"));

		if (consentUser == null) {

			writeErrorMessage("Bei der Bearbeitung Ihrer Anfrage ist ein Fehler aufgetreten.");
			return;
		}

		request.getSession().setAttribute("consentUser", consentUser);
		request.setAttribute("show", "signdigitalprivpage");
		this.dispatch("/index.jsp");

	}

	/**
	 * Forwards the User to the signwrittenpage and adds data regarding the user
	 * for whom a consent shall be created to the session
	 */
	private void requestTypeSignWrittenPage() {

		request.setAttribute("context", request.getParameter("context"));
		request.setAttribute("emailaddress",
				request.getParameter("emailaddress"));
		User consentUser = ccService.getUser(request
				.getParameter("emailaddress"));

		if (consentUser == null) {

			writeErrorMessage("Bei der Bearbeitung Ihrer Anfrage ist ein Fehler aufgetreten.");
			return;
		}

		request.getSession().setAttribute("consentUser", consentUser);
		request.setAttribute("show", "signwrittenprivpage");
		this.dispatch("/index.jsp");

	}

}