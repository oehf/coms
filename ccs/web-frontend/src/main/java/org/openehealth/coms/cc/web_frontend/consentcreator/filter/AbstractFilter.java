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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.*;

import org.apache.log4j.Logger;

//TODO Kommentar
/**
 * 
 * 
 * @author Lennart Koester
 * 
 */
public abstract class AbstractFilter implements Filter {

	protected FilterConfig filterConfig;
	protected ServletResponse response;
	protected ServletRequest request;
	protected FilterChain chain;

	/**
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 * @param filterConfig
	 */
	public void init(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		filterConfig = null;
	}

	/**
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 * @param request
	 * @param response
	 * @param chain
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) {

		this.response = response;
		this.request = request;
		this.chain = chain;

		try {
			this.response.setContentType("text/html; charset=UTF-8");
			this.request.setCharacterEncoding("UTF-8");
			doFilter();
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}


	/**
	 * 
	 * 
	 * @throws Exception
	 */
	protected abstract void doFilter() throws Exception;


	/**
	 *  This Method write Error Messages 
	 * @param message
	 */
	protected void writeErrorMessage(String message) {
		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println(message);
			out.close();
		} catch (IOException e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}
}