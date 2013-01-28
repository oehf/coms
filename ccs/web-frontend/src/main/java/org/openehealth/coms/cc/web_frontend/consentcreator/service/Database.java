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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.User;


/**
 * A Database.
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 * 
 */

public class Database {

	private ConnectionFactory conFact = ConnectionFactory.getInstance();

	public Database() {
	}

	/**
	 * Activates the given user.
	 * 
	 * @param user
	 * @return
	 */
	public boolean activateUser(String id) {

		boolean activated = false;

		String sql = "UPDATE users SET active = 1 WHERE users.id = ?;";
		Connection connection = null;
		PreparedStatement statement = null;
		int result = -1;

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, id);
			result = statement.executeUpdate();
			if (result == 1) {
				activated = true;

			} else if (result != 0) {
				connection.rollback();
				throw new ServiceException(
						"Es wurden mehrere mögliche Benutzer gefunden!");
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		finally {
			try {
				connection.close();
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
		return activated;
	}

	/**
	 * Deactivates the given user.
	 * 
	 * @param user
	 * @return
	 */
	public boolean deactivateUser(String id) {

		boolean deactivated = false;

		String sql = "UPDATE users SET active = 0 WHERE users.id = ?;";
		Connection connection = null;
		PreparedStatement statement = null;
		int result = -1;

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, id);
			result = statement.executeUpdate();
			if (result == 1) {
				deactivated = true;
			} else if (result != 0) {
				connection.rollback();
				throw new ServiceException(
						"Es wurden mehrere mögliche Benutzer gefunden!");
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		finally {
			try {
				connection.close();
			} catch (Exception e) {
				Logger.getLogger(Database.class).error(e);
			}
		}
		return deactivated;
	}

	/**
	 * Checks whether the user with the given data already exists or not.
	 * 
	 * @param user
	 * @return
	 */
	public boolean existsUser(User user) {

		boolean existsUser = true;

		String sql = "SELECT * FROM users WHERE name = ? AND forename = ? AND birthdate = ? AND gender = ?;";
		String sql2 = "SELECT * FROM users WHERE emailaddress = ?;";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;

		try {

			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, user.getName());
			statement.setString(2, user.getForename());
			java.sql.Date sqlDate = new java.sql.Date(user.getBirthdate()
					.getTime());
			statement.setDate(3, sqlDate);
			statement.setString(4, user.getGender());
			result = statement.executeQuery();
			if (!result.next()) {
				existsUser = false;
			}
			if (!existsUser) {

				statement = connection.prepareStatement(sql2);
				statement.setString(1, user.getEmailaddress());
				result = statement.executeQuery();
				if (!result.next()) {
					existsUser = false;
				}
			}

		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
			throw new ServiceException(
					"Bei der Verarbeitung Ihrer Anfrage ist ein Fehler aufgetreten.");
		}

		finally {
			try {
				connection.close();
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
		return existsUser;
	}

	/**
	 * Retrieves the password associated with the given emailaddress from the
	 * database
	 * 
	 * @param emailaddress
	 * @return
	 */
	public String retrievePassword(String emailaddress) {

		String pwd = "";

		String sql = "SELECT password FROM users WHERE emailaddress = ?;";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, emailaddress);
			result = statement.executeQuery();
			if (result.next()) {
				pwd = result.getString("password");
			} else {
				return "";
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		finally {
			try {
				connection.close();
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
		return pwd;
	}

	/**
	 * Retrieves the User associated with the given emailaddress from the
	 * database
	 * 
	 * @param emailaddress
	 * @return - null if no user was found
	 */
	public User retrieveUserByPasswordRequest(String s) {

		String sql = "SELECT * FROM passwordRequests WHERE refid = ?;";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;

		User user = null;

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, s);
			result = statement.executeQuery();
			if (result.next()) {
				user = retrieveUserByEmail(result.getString("emailaddress"));
			} else {
				throw new ServiceException(
						"Es existiert kein Benutzer zur angegebenen RefID");
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		finally {
			try {
				connection.close();
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
		return user;
	}

	/**
	 * Retrieves the user associated with the given emailaddress from the
	 * database.
	 * 
	 * @param emailaddress
	 * @return - null if no User with the given emailaddress was found.
	 */
	public User retrieveUserByEmail(String emailaddress) {

		User user = null;

		String sql = "SELECT * FROM users WHERE emailaddress = ?;";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, emailaddress);
			result = statement.executeQuery();
			if (result.next()) {
				user = new User();
				user.setActive(result.getInt("active"));
				user.setBirthdate(result.getDate("birthdate"));
				user.setCity(result.getString("city"));
				user.setEmailaddress(result.getString("emailaddress"));
				user.setForename(result.getString("forename"));
				user.setGender(result.getString("gender"));
				user.setID(result.getString("id"));
				user.setName(result.getString("name"));
				user.setPrivileges(result.getInt("privileges"));
				user.setStreet(result.getString("street"));
				user.setZipcode(result.getInt("zipcode"));
			} else {
				throw new ServiceException(
						"Zu dieser Emailaddresse existiert kein Benutzer.");
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		finally {
			try {
				connection.close();
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
		return user;
	}

	/**
	 * Retrieves the user associated with the given id from the database.
	 * 
	 * @param id
	 * @return
	 */
	public User retrieveUserByID(String id) {

		User user = null;

		String sql = "SELECT * FROM users WHERE id = ?;";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, id);
			result = statement.executeQuery();
			if (result.next()) {
				user = new User();
				user.setActive(result.getInt("active"));
				user.setBirthdate(result.getDate("birthdate"));
				user.setCity(result.getString("city"));
				user.setEmailaddress(result.getString("emailaddress"));
				user.setForename(result.getString("forename"));
				user.setGender(result.getString("gender"));
				user.setID(result.getString("id"));
				user.setName(result.getString("name"));
				user.setPrivileges(result.getInt("privileges"));
				user.setStreet(result.getString("street"));
				user.setZipcode(result.getInt("zipcode"));
			} else {
				throw new ServiceException(
						"Zu dieser Emailaddresse existiert kein Benutzer.");
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		finally {
			try {
				connection.close();
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
		return user;
	}

	/**
	 * Retrieves the user whose name, forename and birthdate match the given
	 * parameters.
	 * 
	 * @param name
	 * @param forename
	 * @param birthdate
	 * @return - null if no user was found
	 */
	public User retrieveUser(String name, String forename, Date birthdate,
			String gender) {

		User user = null;

		String sql = "SELECT * FROM users WHERE name = ? AND forename = ? AND birthdate = ? AND gender = ?;";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, name);
			statement.setString(2, forename);
			java.sql.Date sqlDate = new java.sql.Date(birthdate.getTime());
			statement.setDate(3, sqlDate);
			statement.setString(4, gender);
			result = statement.executeQuery();
			if (result.next()) {
				user = new User();
				user.setActive(result.getInt("active"));
				user.setBirthdate(result.getDate("birthdate"));
				user.setCity(result.getString("city"));
				user.setEmailaddress(result.getString("emailaddress"));
				user.setForename(result.getString("forename"));
				user.setGender(result.getString("gender"));
				user.setID(result.getString("id"));
				user.setName(result.getString("name"));
				user.setPrivileges(result.getInt("privileges"));
				user.setStreet(result.getString("street"));
				user.setZipcode(result.getInt("zipcode"));
			} else {
				throw new ServiceException(
						"Zu diesen Stammdaten existiert kein Benutzer.");
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		finally {
			try {
				connection.close();
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
		return user;
	}

	/**
	 * Returns the filename for the uncleared consent of the patient with the
	 * given id.
	 * 
	 * @param id
	 *            - patient's id.
	 * @return - filename if found, else "".
	 */
	public String retrieveUnclearedConsentFilename(String id) {

		String sql = "SELECT * FROM unclearedConsents WHERE id = ?;";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;

		String ret = "";

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, id);
			result = statement.executeQuery();
			if (result.next()) {
				ret = result.getString("filename");
			} else {
				throw new ServiceException(
						"Es konnte keine Einwiligungserklärung gefunden werden.");
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		finally {
			try {
				connection.close();
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
		return ret;
	}

	/**
	 * Returns a List of users during whose registration a conflict was
	 * encountered. Inside the List<Object> is stored another List<Object>
	 * containing in index 1 - the User Object 2 - the Timestamp Object which
	 * was stored at the time of the registration.
	 * 
	 * @return - null if an error occurred
	 */
	public List<Object> showConflicts() {

		String sql = "SELECT * FROM registerConflicts;";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet res = null;
		User user = null;

		Vector<Object> vecO = new Vector<Object>();

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			res = statement.executeQuery();

			while (res.next()) {

				Vector<Object> veco = new Vector<Object>();

				user = retrieveUserByID(res.getString("id"));

				veco.add(user);

				veco.add(res.getTimestamp("timestamp"));

				vecO.add(veco);

			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
			throw new ServiceException(
					"Bei der Verarbeitung Ihrer Anfrage ist ein Fehler aufgetreten.");
		}

		finally {
			try {
				connection.close();
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
		return vecO;

	}

	/**
	 * Returns a list of uncleared consents from the database.
	 * 
	 * @return
	 */
	public List<Object> showUnclearedConsents() {

		String sql = "SELECT * FROM unclearedConsents;";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet res = null;
		User user = null;

		Vector<Object> vecO = new Vector<Object>();

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			res = statement.executeQuery();

			while (res.next()) {

				Vector<Object> vecU = new Vector<Object>();

				user = retrieveUserByID(res.getString("id"));

				vecU.add(user);

				vecU.add(res.getString("participation"));

				vecU.add(res.getTimestamp("timestamp"));

				vecO.add(vecU);

			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
			throw new ServiceException(
					"Bei der Verarbeitung Ihrer Anfrage ist ein Fehler aufgetreten.");
		}

		finally {
			try {
				connection.close();
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
		return vecO;
	}

	/**
	 * Stores that there has been a conflict while registering for the user with
	 * the given ID.
	 * 
	 * @param id
	 * @return
	 */
	public boolean storeConflict(String id) {

		boolean stored = false;

		String sql = "INSERT INTO registerconflicts (id, timestamp) VALUES (?,?);";
		Connection connection = null;
		PreparedStatement statement = null;
		int result = -1;

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, id);
			statement.setTimestamp(2, new Timestamp(new Date().getTime()));
			result = statement.executeUpdate();
			if (result == 1) {
				stored = true;
			} else {
				throw new ServiceException(
						"Die Anfrage konnte nicht gespeichert werden!");
			}
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		finally {
			try {
				connection.close();
			} catch (SQLException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}

		return stored;
	}

	/**
	 * Stores information about a uncleared consent.
	 * 
	 * @param id
	 *            - ID of the user associated with the uncleared consent.
	 * @param cdaHash
	 *            - MD5 hash of the consent cda.
	 * @return
	 */
	public String storeUnclearedConsent(String id, String cdaHash,
			int participation) {

		boolean usedNmbr = true;
		String refID = "";

		while (usedNmbr) {

			String s = RandomStringUtils.randomNumeric(64);

			if (!existsRefidUnclearedConsent(s)) {
				refID = s;
				usedNmbr = false;
			}
		}

		boolean stored = false;

		String sql = "INSERT INTO unclearedConsents (id, filename, md5hash, participation, timestamp) VALUES (?, ?, ?, ?, ?);";
		Connection connection = null;
		PreparedStatement statement = null;
		int result = -1;

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, id);
			statement.setString(2, refID);
			statement.setString(3, cdaHash);
			statement.setInt(4, participation);
			statement.setTimestamp(5, new Timestamp(new Date().getTime()));
			result = statement.executeUpdate();
			if (result == 1) {
				stored = true;
			} else {
				throw new ServiceException(
						"Die Anfrage konnte nicht gespeichert werden!");
			}
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		finally {
			try {
				connection.close();
			} catch (SQLException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
		if (stored) {
			return refID;
		}
		return "";
	}

	/**
	 * Stores that a new password for the given user was requested.
	 * 
	 * @param user
	 * @return refID - The ID used to identify the request.
	 */
	public String storePasswordRequest(User user) {

		boolean usedNmbr = true;
		String refID = "";

		while (usedNmbr) {

			String s = RandomStringUtils.randomNumeric(64);

			if (!existsPasswordRequest(s, false)) {
				refID = s;
				usedNmbr = false;
			}
		}

		boolean stored = false;

		String sql = "INSERT INTO passwordRequests (id, refid, timestamp, emailaddress) VALUES (?, ?, ?, ?);";
		Connection connection = null;
		PreparedStatement statement = null;
		int result = -1;

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, user.getID());
			statement.setString(2, refID);
			statement.setTimestamp(3, new Timestamp(new Date().getTime()));
			statement.setString(4, user.getEmailaddress());
			result = statement.executeUpdate();
			if (result == 1) {
				stored = true;
			} else {
				throw new ServiceException(
						"Die Anforderung konnte nicht gespeichert werden!");
			}
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		finally {
			try {
				connection.close();
			} catch (SQLException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
		if (stored) {
			return refID;
		}

		return "";
	}

	/**
	 * Checks whether a password request exists for the given referer or not.
	 * 
	 * @param s
	 * @param external
	 *            - if the request to view this data is internal this is false,
	 *            if true the request will be deleted if it is too old.
	 * @return true if a valid request exists. Requests that have been stored
	 *         for more than three days become invalid.
	 * 
	 */
	public boolean existsPasswordRequest(String s, boolean external) {

		boolean exists = true;

		String sql = "SELECT * FROM passwordRequests WHERE refid = ?;";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, s);
			result = statement.executeQuery();
			if (!result.next()) {
				exists = false;
			}
			Timestamp stamp = result.getTimestamp(3);
			if (stamp.getTime() < new Timestamp(new Date().getTime()).getTime() - 3 * 24 * 3600000
					&& external) {
				removeRefID(s);
				throw new ServiceException("Referer abgelaufen");
			}

		} catch (SQLException e) {
			exists = false;
		}

		finally {
			try {
				connection.close();
			} catch (SQLException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
		return exists;
	}

	/**
	 * Checks whether a password request exists for the given user id or not.
	 * 
	 * @param id
	 * @return
	 */
	public boolean existsPasswordRequestForUser(String id) {

		boolean exists = true;

		String sql = "SELECT * FROM passwordRequests WHERE id = ?;";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, id);
			result = statement.executeQuery();
			if (!result.next()) {
				exists = false;
			}
		} catch (SQLException e) {
			exists = false;
			Logger.getLogger(this.getClass()).error(e);
		}

		finally {
			try {
				connection.close();
			} catch (SQLException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
		return exists;
	}

	/**
	 * Checks if there is a database entry concerning an uncleared consent for
	 * the patient with the given id.
	 * 
	 * @param id
	 * @return
	 */
	public boolean existsUnclearedConsent(String id) {

		boolean exists = true;

		String sql = "SELECT * FROM unclearedconsents WHERE id = ?;";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, id);
			result = statement.executeQuery();
			if (!result.next()) {
				exists = false;
			}
		} catch (SQLException e) {
			exists = false;
			Logger.getLogger(this.getClass()).error(e);
		}

		finally {
			try {
				connection.close();
			} catch (SQLException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
		return exists;

	}

	/**
	 * Checks whether the given String is already used as a RefID on the Table
	 * 
	 * @param s
	 * @return
	 */
	private boolean existsRefidUnclearedConsent(String s) {

		boolean exists = true;

		String sql = "SELECT * FROM unclearedConsents WHERE id = ?;";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, s);
			result = statement.executeQuery();
			if (!result.next()) {
				exists = false;
			}

		} catch (SQLException e) {
			exists = false;
			Logger.getLogger(this.getClass()).error(e);
		}

		finally {
			try {
				connection.close();
			} catch (SQLException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
		return exists;
	}

	/**
	 * Removes the password request associated with the given referer from the
	 * database.
	 * 
	 * @param s
	 * @return
	 */
	public boolean removeRefID(String s) {

		boolean removed = false;

		String sql = "DELETE FROM passwordRequests WHERE refid = ?;";
		Connection connection = null;
		PreparedStatement statement = null;
		int res = -1;

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, s);
			res = statement.executeUpdate();
			if (res == 1) {
				removed = true;
			}
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		finally {
			try {
				connection.close();
			} catch (SQLException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}

		return removed;
	}

	/**
	 * Removes the password request associated with the given user id from the
	 * database.
	 * 
	 * @param s
	 * @return
	 */
	public boolean removeRefIDForUser(String id) {

		boolean removed = false;

		String sql = "DELETE FROM passwordRequests WHERE id = ?;";
		Connection connection = null;
		PreparedStatement statement = null;
		int res = -1;

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, id);
			res = statement.executeUpdate();
			if (res == 1) {
				removed = true;
			}
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		finally {
			try {
				connection.close();
			} catch (SQLException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}

		return removed;
	}

	/**
	 * Removes the entry about a stored uncleared consent for the user with the
	 * given ID from the database.
	 * 
	 * @param id
	 * @return
	 */
	public boolean removeUnclearedConsent(String id) {

		boolean removed = false;

		String sql = "DELETE FROM unclearedConsents WHERE id = ?;";
		Connection connection = null;
		PreparedStatement statement = null;
		int res = -1;

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, id);
			res = statement.executeUpdate();
			if (res == 1) {
				removed = true;
			}
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		finally {
			try {
				connection.close();
			} catch (SQLException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}

		return removed;
	}

	/**
	 * Removes the user from the database.
	 * 
	 * @param id
	 * @return
	 */
	public boolean removeUser(String id) {

		boolean removed = false;

		String sql = "DELETE FROM users WHERE id = ?;";
		Connection connection = null;
		PreparedStatement statement = null;
		int res = -1;

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, id);
			res = statement.executeUpdate();
			if (res == 1) {
				removed = true;
			}
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		finally {
			try {
				connection.close();
			} catch (SQLException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}

		return removed;

	}

	/**
	 * Removes the conflict notice for the user with the given id.
	 * 
	 * @param id
	 * @return
	 */
	public boolean removeConflict(String id) {

		boolean removed = false;

		String sql = "DELETE FROM registerconflicts WHERE id = ?;";
		Connection connection = null;
		PreparedStatement statement = null;
		int res = -1;

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, id);
			res = statement.executeUpdate();
			if (res == 1) {
				removed = true;
			}
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		finally {
			try {
				connection.close();
			} catch (SQLException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}

		return removed;
	}

	/**
	 * Stores the given user to the database.
	 * 
	 * @param user
	 * @return
	 */
	public boolean storeUser(User user) {

		boolean stored = false;

		String sql = "INSERT INTO users (name, forename, birthdate, gender, street, zipcode, city, emailaddress, id, privileges, active, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		Connection connection = null;
		PreparedStatement statement = null;
		int result = -1;

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, user.getName());
			statement.setString(2, user.getForename());
			java.sql.Date sqlDate = new java.sql.Date(user.getBirthdate()
					.getTime());
			statement.setDate(3, sqlDate);
			statement.setString(4, user.getGender());
			statement.setString(5, user.getStreet());
			statement.setInt(6, user.getZipcode());
			statement.setString(7, user.getCity());
			statement.setString(8, user.getEmailaddress().toLowerCase());
			statement.setString(9, user.getID());
			statement.setInt(10, user.getPrivileges());
			statement.setInt(11, user.isActive());
			statement.setString(12, user.getPassword());
			result = statement.executeUpdate();
			if (result == 1) {
				stored = true;
			} else {
				throw new ServiceException(
						"Der Benutzer konnte nicht gespeichert werden!");
			}
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		finally {
			try {
				connection.close();
			} catch (SQLException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
		return stored;
	}

	/**
	 * Updates the given users data. The user is identified by his unique ID.
	 * 
	 * @param user
	 * @return
	 */
	public boolean updateUser(User user) {

		boolean updated = false;

		String sql = "UPDATE users SET name = ?, forename = ?, birthdate = ?, gender = ?, street = ?, zipcode = ?, city = ?, emailaddress = ?, privileges  = ?, active = ? WHERE id = ?;";
		Connection connection = null;
		PreparedStatement statement = null;
		int result = -1;

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, user.getName());
			statement.setString(2, user.getForename());
			java.sql.Date sqlDate = new java.sql.Date(user.getBirthdate()
					.getTime());
			statement.setDate(3, sqlDate);
			statement.setString(4, user.getGender());
			statement.setString(5, user.getStreet());
			statement.setInt(6, user.getZipcode());
			statement.setString(7, user.getCity());
			statement.setString(8, user.getEmailaddress());
			statement.setInt(9, user.getPrivileges());
			statement.setLong(10, user.isActive());
			statement.setString(11, user.getID());

			result = statement.executeUpdate();
			if (result == 1) {
				updated = true;
			} else if (result != 0) {
				connection.rollback();
				throw new ServiceException(
						"Es wurden mehrere mögliche Benutzer gefunden!");
			}
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).error(e);
			throw new ServiceException(
					"Es ist ein Fehler bei der Verarbeitung ihrer Anfrage aufgetreten.");
		}

		finally {
			try {
				connection.close();
			} catch (SQLException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
		return updated;
	}

	/**
	 * Sets a new password for the user with the given ID.
	 * 
	 * @param id
	 * @param pwd
	 * @return
	 */
	public boolean updateUserPassword(String id, String pwd) {

		boolean updated = false;

		String sql = "UPDATE users SET password = ? WHERE id = ?;";
		Connection connection = null;
		PreparedStatement statement = null;
		int result = -1;

		try {
			connection = conFact.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, pwd);
			statement.setString(2, id);

			result = statement.executeUpdate();
			if (result == 1) {
				updated = true;
			} else if (result != 0) {
				connection.rollback();
				throw new ServiceException(
						"Es wurden mehrere mögliche Benutzer gefunden!");
			}
		} catch (SQLException e) {
			Logger.getLogger(this.getClass()).error(e);
		}

		finally {
			try {
				connection.close();
			} catch (SQLException e) {
				Logger.getLogger(this.getClass()).error(e);
			}
		}
		return updated;
	}
}