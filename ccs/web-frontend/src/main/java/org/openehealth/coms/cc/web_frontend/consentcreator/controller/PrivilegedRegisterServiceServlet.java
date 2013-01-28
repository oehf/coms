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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.User;
import org.openehealth.coms.cc.web_frontend.consentcreator.service.ServiceException;


/**
 * 
 * This servlet handles registration requests issued by privileged users for
 * unregistered users.
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 * 
 */
public class PrivilegedRegisterServiceServlet extends AbstractServlet {

	private static final long serialVersionUID = 12L;

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z-]{2,})$";
	private Pattern pattern;
	private Matcher matcher;


	/**
	 * This method does the actual request processing.
	 * 
	 * @throws ServiceException
	 *             , Exception
	 */
	@Override
	public void doService() throws ServiceException, Exception {

		if (requestType.equals("registerUser")) {

			requestTypeRegisterUser();

		} else {

			this.writeErrorMessage("Der übergebene Parameter ist unbekannt.");
		}
	}


	/**
	 * Update registered user in the actual request processing
	 * 
	 */
	private void requestTypeRegisterUser() {

		if (request.getParameter("forename").trim().equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Vorname fehlt.");

		} else if (request.getParameter("name").trim().equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Name fehlt.");

		} else if (request.getParameter("emailaddress").trim()
				.equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Emailaddresse fehlt.");

		} else if (request.getParameter("street").trim().equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Straße fehlt.");

		} else if (request.getParameter("zipcode").trim().equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Postleitzahl fehlt.");

		} else if (request.getParameter("city").trim().equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Stadt fehlt.");

		} else if (request.getParameter("birthdate").trim()
				.equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Geburtsdatum fehlt.");

		} else if (request.getParameter("gender").trim().equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Geschlecht fehlt.");

		} else if (request.getParameter("privileges").trim()
				.equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Privilegien fehlt.");

		} else if (request.getParameter("active").trim().equalsIgnoreCase("")) {

			this.writeErrorMessage("Der übergebene Parameter Teilnahemstatus fehlt.");

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
				return;
			}

			int zip = -1;

			try {

				zip = Integer.parseInt(request.getParameter("zipcode"));

			} catch (Exception e) {
				this.writeErrorMessage("Der übergebene Parameter Postleitzahl ist ungültig.");
				return;
			}

			pattern = Pattern.compile(EMAIL_PATTERN);
			matcher = pattern.matcher(request.getParameter("emailaddress"));

			if (!matcher.matches()) {

				this.writeErrorMessage("Ungültige Emailaddresse");
				return;
			}

			User newUser = new User();
			newUser.setActive(Integer.parseInt(request.getParameter("active")));
			newUser.setPrivileges(Integer.parseInt(request
					.getParameter("privileges")));
			newUser.setBirthdate(d);
			newUser.setCity(request.getParameter("city"));
			newUser.setEmailaddress(request.getParameter("emailaddress"));
			newUser.setForename(request.getParameter("forename"));
			newUser.setGender(request.getParameter("gender"));
			newUser.setName(request.getParameter("name"));
			newUser.setStreet(request.getParameter("street"));
			newUser.setZipcode(zip);
			newUser.setPassword(RandomStringUtils.randomNumeric(64));

			try {

				if (newUser.getPrivileges() >= 1) {

					if (ccService.registerUser(newUser, false)) {
						this.writeSuccessMessage("Der privilegierte Benutzer wurde erfolgreich registriert.");

					} else {

						this.writeErrorMessage("Der Benutzer konnte nicht registriert werden.");
					}
				} else {

					if (ccService.registerUser(newUser, false)) {

						request.setAttribute("message",
								"Der Benutzer wurde erfolgreich gespeichert.");

					} else {

						request.setAttribute(
								"message",
								"Es existiert bereits ein ISIS Benutzerprofil mit diesen Daten, das Profil wurde mit dem neu angelegten Benutzer verknüpft.");
					}

					request.setAttribute("title", "Benutzer gespeichert");

					request.setAttribute(
							"messageOrder",
							"Bitte erstellen Sie eine Einwilligungserklärung, die diesen Vorgang dokumentiert!");
					request.setAttribute("emailaddress",
							request.getParameter("emailaddress"));

					if (newUser.isActive() == 1) {

						request.setAttribute("context", "registered");

					} else {

						request.setAttribute("context",
								"registerednoparticipation");
					}

					request.setAttribute("show", "signchoicepage");
					this.dispatch("/index.jsp");
				}

			} catch (Exception e) {
				Logger.getLogger(this.getClass()).error(e);
				this.writeErrorMessage(e.getMessage());
			}
		}
	}
}
