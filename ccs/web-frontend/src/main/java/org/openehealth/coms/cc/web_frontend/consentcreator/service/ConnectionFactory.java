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

package org.openehealth.coms.cc.web_frontend.consentcreator.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;

/**
 * Klasse bietet Methoden zum Aufbau der Verbindung mit der Datenbank
 * 
 * @author Frederik Reifschneider [Reifschneider@stud.uni-heidelberg.de]
 * @version $Id: ConnectionFactory.java 4220 2007-06-27 08:50:41Z jthoenes $
 * 
 */
public class ConnectionFactory {

	private static ConnectionFactory aConFac = null;

	/**
	 * Konstruktor
	 */
	private ConnectionFactory() {
		try {
			URL url = ConnectionFactory.class
					.getResource("../files/config/proxool_cfg.xml");
			JAXPConfigurator.configure(new InputStreamReader(url.openStream()),
					false);
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}

	/**
	 * Liefert ein Objekt der Klasse {@link ConnectionFactory}
	 * 
	 * @return
	 */
	public static ConnectionFactory getInstance() {
		if (aConFac == null) {
			aConFac = new ConnectionFactory();
		}
		return aConFac;
	}

	/**
	 * Baut Verbindung zur Datenbank auf
	 * 
	 * @return Connectionobjekt welches Zugriff auf die Datenbank ermoeglicht.
	 * @throws DatenbankExceptions
	 * @throws SQLException
	 *             Falls ein Fehler beim Verbindungsaufbau auftritt.
	 */
	public Connection getConnection() {
		Connection con = null;
		try {
			con = DriverManager.getConnection("proxool.consentCreator");
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		return con;
	}

	/**
	 * Trennt Verbindung zur Datenbank.
	 * 
	 * @param con
	 *            das Connection Objekt.
	 * @throws DatenbankExceptions
	 * @throws SQLException
	 *             Falls ein Fehler bei der Verbindungstrennung auftritt.
	 */
	public void closeConnection(Connection con) {
		try {
			if (con != null && !con.isClosed()) {
				con.close();
				con = null;
			}
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}
}
