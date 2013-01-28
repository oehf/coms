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

package org.openehealth.coms.cc.web_frontend.consentcreator.filter;

import javax.servlet.http.HttpServletRequest;

import org.openehealth.coms.cc.web_frontend.consentcreator.model.User;


/**
 * Filter to assure that the requesting user has sufficient privileges.
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 * 
 */
public class PrivilegedUserFilter extends AbstractFilter {

	/**
	 * @see chat.filter.AbstractFilter#doFilter()
	 * @throws Exception
	 */
	@Override
	public void doFilter() throws Exception {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		// Attribute is available
		User user = (User) httpRequest.getSession().getAttribute("user");
		// Attribute is valid
		if (user.getPrivileges() >= 1) {
			chain.doFilter(request, response);
		} else {
			this.writeErrorMessage("Diese Funktion ist für Sie nicht verfügbar.");
		}
	}
}