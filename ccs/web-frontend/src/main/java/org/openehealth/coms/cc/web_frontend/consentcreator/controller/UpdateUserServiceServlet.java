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

import org.apache.log4j.Logger;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.User;
import org.openehealth.coms.cc.web_frontend.consentcreator.service.ServiceException;


/**
 * This servlet allows unprivileged users to edit their data.
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 * 
 */
public class UpdateUserServiceServlet extends AbstractServlet {
	private final static long serialVersionUID = 18L;

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z-]{2,})$";
	private Pattern pattern;
	private Matcher matcher;


	/**
	 * This method does the actual request processing.
	 * 
	 * @throws ServiceException
	 * @throws Exception
	 */
	@Override
	public void doService() throws ServiceException, Exception {

		if (requestType.equals("update")) {

			requestTypeUpdate();

		} else {
			this.writeErrorMessage("Der übergebene Parameter ist unbekannt.");
		}
	}


	/**
	 * This method does update the actual request informations.
	 * 
	 */
	private void requestTypeUpdate() {

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
				Logger.getLogger(this.getClass()).error(e);
				this.writeErrorMessage("Der übergebene Parameter Geburtsdatum ist ungültig.");
				return;
			}

			int zip = -1;

			try {
				zip = Integer.parseInt(request.getParameter("zipcode"));
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).error(e);
				this.writeErrorMessage("Der übergebene Parameter Postleitzahl ist ungültig.");
				return;
			}

			pattern = Pattern.compile(EMAIL_PATTERN);
			matcher = pattern.matcher(request.getParameter("emailaddress"));

			if (!matcher.matches()) {
				this.writeErrorMessage("Ungültige Emailaddresse");
				return;
			}

			User updatedUser = new User();

			updatedUser.setActive(1);
			updatedUser.setPrivileges(0);

			updatedUser.setBirthdate(d);
			updatedUser.setCity(request.getParameter("city"));
			updatedUser.setEmailaddress(request.getParameter("emailaddress"));
			updatedUser.setForename(request.getParameter("forename"));
			updatedUser.setGender(request.getParameter("gender"));
			updatedUser.setName(request.getParameter("name"));
			updatedUser.setStreet(request.getParameter("street"));
			updatedUser.setZipcode(zip);

			updatedUser.setID(user.getID());

			try {

				if (ccService.existsUser(updatedUser)) {

					User foundUser = null;

					try {
						foundUser = ccService.getUser(updatedUser
								.getEmailaddress());

					} catch (Exception e) {
						foundUser = ccService.getUser(updatedUser.getName(),
								updatedUser.getForename(),
								updatedUser.getBirthdate(),
								updatedUser.getGender());
					}

					if (foundUser.getID().equalsIgnoreCase(updatedUser.getID())) {

						try {

							if (ccService.updateUser(updatedUser)) {

								request.getSession().setAttribute("user",
										updatedUser);

								this.writeSuccessMessage("Ihre Daten wurden aktualisiert.");
							} else {
								this.writeErrorMessage("Aktualisierung fehlgeschlagen");
							}
						} catch (Exception e) {
							Logger.getLogger(this.getClass()).error(e);
							this.writeErrorMessage(e.getMessage());
						}
					} else {
						this.writeErrorMessage("Es existiert bereits ein Benutzer mit den angegebenen Daten.");
					}
				} else {
					try {

						if (ccService.updateUser(updatedUser)) {

							request.getSession().setAttribute("user",
									updatedUser);

							this.writeSuccessMessage("Ihre Daten wurden geupdatet.");

						} else {
							this.writeErrorMessage("Update fehlgeschlagen");
						}
					} catch (Exception e) {
						Logger.getLogger(this.getClass()).error(e);
						this.writeErrorMessage(e.getMessage());
					}
				}

			} catch (Exception e) {
				Logger.getLogger(this.getClass()).error(e);
				writeErrorMessage(e.getMessage());
			}
		}
	}
}