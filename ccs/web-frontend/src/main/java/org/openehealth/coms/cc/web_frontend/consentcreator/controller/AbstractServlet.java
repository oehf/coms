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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.User;
import org.openehealth.coms.cc.web_frontend.consentcreator.service.ConsentCreatorService;
import org.openehealth.coms.cc.web_frontend.consentcreator.service.ServiceException;
import org.openehealth.coms.cc.web_frontend.org.json.JSONArray;
import org.openehealth.coms.cc.web_frontend.org.json.JSONObject;
import org.w3c.dom.Document;


/**
 * This servlet forwards pages to unprivileged users upon request.
 * @author Lennart Koester
 * 
 */
public abstract class AbstractServlet extends HttpServlet {

	protected ConsentCreatorService ccService;
	protected HttpServletResponse response;
	protected HttpServletRequest request;
	protected User user;
	protected String requestType;

	// SerialVersionUID
	private static final long serialVersionUID = 1L;

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doService(request, response);
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doService(request, response);
	}


	/**
	 * This method does the actual request processing.
	 * 
	 * @param request
	 * @param response
	 */
	private void doService(HttpServletRequest request,
			HttpServletResponse response) {

		this.request = request;
		this.response = response;

		try {

			this.request.setCharacterEncoding("UTF-8");
			requestType = request.getParameter("type");
			ccService = ConsentCreatorService.getInstance();
			user = (User) request.getSession().getAttribute("user");
			this.doService();

		} catch (ServiceException e) {
			this.writeErrorMessage(e.getMessage());
			Logger.getLogger(this.getClass()).error(e);

		} catch (Exception e) {
			this.writeErrorMessage("Bei der Verarbeitung Ihrer Anfrage ist ein interner Fehler aufgetreten.");
			Logger.getLogger(this.getClass()).error(e);
		}
	}


	/**
	 * 
	 * 
	 * @throws ServiceException
	 * @throws Exception
	 */
	abstract void doService() throws ServiceException, Exception;

	/**
	 * This method dispatches the given page, which is then represented to the
	 * user.
	 * 
	 * @param page
	 */
	protected void dispatch(String page) {

		try {

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher(page);
			dispatcher.forward(request, response);

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}

	/**
	 * This method writes the given JSONArray into the response via a
	 * PrintWriter
	 * 
	 * @param array
	 */
	protected void writeJSONArray(JSONArray array) {

		try {

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.println(array);

		} catch (IOException e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}

	/**
	 * This method writes the given JSONObject into the response via a
	 * PrintWriter
	 * 
	 * @param object
	 */
	protected void writeJSONObject(JSONObject object) {
		try {

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.println(object);

		} catch (IOException e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}

	/**
	 * This method sends the message to the user, presented as a failed
	 * execution.
	 * 
	 * @param message
	 */
	protected void writeErrorMessage(String message) {

		try {

			request.setCharacterEncoding("UTF-8");
			request.setAttribute("show", "messagepage");
			request.setAttribute("type", "error");
			request.setAttribute("message", message);
			this.dispatch("/index.jsp");

		} catch (UnsupportedEncodingException e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}

	/**
	 * This method sends the message to the user, presented as a successful
	 * execution.
	 * 
	 * @param message
	 */
	protected void writeSuccessMessage(String message) {

		try {

			request.setCharacterEncoding("UTF-8");
			request.setAttribute("show", "messagepage");
			request.setAttribute("type", "success");
			request.setAttribute("message", message);
			this.dispatch("/index.jsp");

		} catch (UnsupportedEncodingException e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}

	/**
	 * This method writes the given CDA Document into the response via a
	 * PrintWriter
	 * 
	 * @param cda
	 */
	protected void writeXML(Document cda) {

		try {

			DOMSource domSource = new DOMSource(cda);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);

			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/xml");
			PrintWriter out = response.getWriter();
			out.println(writer.toString());

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}
}