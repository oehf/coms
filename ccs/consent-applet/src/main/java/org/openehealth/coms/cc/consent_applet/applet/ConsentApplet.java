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

package org.openehealth.coms.cc.consent_applet.applet;

import java.applet.AppletContext;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JApplet;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.openehealth.coms.cc.consent_applet.applet.ConsentAppletPanel.OIDRadioButton;
import org.openehealth.coms.cc.consent_applet.model.CheckTreeManager;
import org.openehealth.coms.cc.consent_applet.model.LazyOIDTreeNode;
import org.openehealth.coms.cc.consent_applet.model.OIDObject;
import org.openehealth.coms.cc.consent_applet.org.json.JSONArray;
import org.openehealth.coms.cc.consent_applet.org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;




/**
 * This class represents the controller of the applet used for consent creation,
 * contains all methods needed for Applet-startup
 * 
 * @author Lennart Koester
 * @version 1.0 14.02.2011
 *
 */
@SuppressWarnings("serial")
public class ConsentApplet extends JApplet {
	
	private DefaultTreeModel oidTreeModel = null;
	private DefaultListModel ruleListModel = null;
	private DefaultComboBoxModel personsComboxBoxModel = null;
	private DefaultComboBoxModel documentComboBoxModel = null;
	private ConsentAppletPanel cap = new ConsentAppletPanel();
	
	
	private SignChoicePanel scp = new SignChoicePanel();
	private SmartCardSignerPanel smcsa = new SmartCardSignerPanel();
	private SignWrittenPanel swp = new SignWrittenPanel();
	
	String organisation = "";
	String persons = "ALL";
	String affectedOID = "1.2.276.0.76.3.1.78";
	String personsOID = "ALL";
	String organisationOID = "1.2.276.0.76.3.1.78";
	String documents = "ALL";
	String documentsOID = "1.0.10.1.101.3";
	String accessType = "read";
	boolean grantAccess = true;
	
	
	
	String strRelURL = "";
	String strCookieName = "";
	String strCookieValue = "";
	String strCookie = "";
	
	String privilegedServlet = "";
	
	
	String userName = "";
	String userForename = "";
	String userEmailaddress = "";
	
	Document cda = null;
	
	String mode = "";
	String endparticipation = "";
	
	int privInt = -1;

	/**
	 * Setting up the data fields and retrieving parameters from the applet attributes.
	 * 
	 */
	public void init() {
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					
					smcsa = new SmartCardSignerPanel();
					
					createGUI();
					strRelURL = getParameter("relURL");
					
					strCookieName = getParameter("cookieName");
				    strCookieValue = getParameter("cookieValue");
				    strCookie = strCookieName + "=" + strCookieValue;
				    
				    
				    userName = getParameter( "name" );
				    userForename = getParameter( "forename" );
				    userEmailaddress = getParameter( "emailaddress" );
				    
				    String strPrivileges = getParameter("privileges");
				    
				    privInt = Integer.parseInt(strPrivileges);
				    
				    if (privInt >=1) {
				    	
				    	privilegedServlet = "Privileged";
				    	
				    }
				    
				    mode = getParameter("mode");
				    endparticipation = getParameter("endparticipation");
				    
				    //If intended use is to sign a standard-consent
				    if (mode.equalsIgnoreCase("signonly")) {
				    	
				    	requestStandardConsentDocument(!Boolean.parseBoolean(endparticipation));
				    	
				    	smcsa.setDocument(cda, new StoreSignedConsentListener());
						
						remove(scp);
						
						setContentPane(smcsa);
						
						validate();
				    	
				    }
				    else {
				    	requestData();
						setData();
				    }
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("createGUI didn't successfully complete");
		}
	}

	public void start() {
		this.setSize(750, 800);
	}

	public void stop() {

	}

	public String getAppletInfo() {
		return "";
	}

	private void createGUI() {
		setContentPane(cap);
	}	
	
	/**
	 * Sets the data to the fields and adds the respective listeners to the components.
	 */
	private void setData() {
		
		cap.setUserData(userName, userForename, userEmailaddress);
		cap.setChangeListener(getBoxActionListener());
		cap.setAddRuleListener(getMyAddRuleListener());
		cap.setCreateConsentListener(getMyNewPDFConsentListener());
		cap.setRuleList(ruleListModel, getRuleListMouseAdapter());
		cap.setOIDTree(oidTreeModel, getMyTreeExpansionListener(), new OIDTreeSelectionListener());
		cap.setPersonnelComboBox(personsComboxBoxModel);
		cap.setDocumentComboBox(documentComboBoxModel);
		cap.setRuleDescription(requestRuleDescription());
		cap.setSearchFieldListener(getSearchFieldListener());
		
	}

	/**
	 * Requests data needed to populate the data fields from the server
	 * 
	 */
	private void requestData() {

		requestTreeRoot();
		requestRuleList();
		requestPersonnelList();
		requestDocumentList();
		
	}

	/**
	 * Requests the root of the organisations tree and it's children
	 */
	private void requestTreeRoot() {
		try {
			URL url = new URL(strRelURL+"/"+privilegedServlet+"CreateConsentServiceServlet?type=treeroot");
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("cookie", strCookie );
			conn.setDoInput(true);

			InputStream is = conn.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(new InputStreamReader(is, "UTF8"), writer);
			String s = writer.toString();
			
			is.close();
			
			JSONObject jso = new JSONObject(s);

			OIDObject root = new OIDObject(jso.getString("identifier"), jso.getString("name"), jso.getBoolean("isActive"));
			
			root.setHasChildren(jso.getBoolean("hasChildren"));

			LazyOIDTreeNode rootNode = new LazyOIDTreeNode(root);

			JSONArray jsa = jso.getJSONArray("children");

			for (int i = 0; i < jsa.length(); i++) {

				JSONObject jsoo = (JSONObject) jsa.get(i);

				OIDObject oi = new OIDObject(jsoo.getString("identifier"), jsoo.getString("name"), jsoo.getBoolean("isActive"));

				oi.setHasChildren(jsoo.getBoolean("hasChildren"));
				LazyOIDTreeNode oin = new LazyOIDTreeNode(oi);

				rootNode.add(oin);
			}
			
			oidTreeModel = new DefaultTreeModel(rootNode);
			cap.setOIDTree(oidTreeModel, null, new OIDTreeSelectionListener());
			cap.mainPanel.mp.cp.tp.orgaTree.collapseRow(1);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Requests the children of the given treenode from the server
	 * 
	 * @param lzynde
	 */
	private void requestTreeNode(LazyOIDTreeNode lzynde) {
		
		try {
			URL url = new URL(strRelURL+"/"+privilegedServlet+"CreateConsentServiceServlet?type=expandtree&identifier="+((OIDObject) lzynde.getUserObject()).getIdentifier());
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("cookie", strCookie );
			conn.setDoInput(true);

			InputStream is = conn.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(new InputStreamReader(is, "UTF8"), writer);
			String s = writer.toString();

			is.close();
			
			JSONArray jsa = new JSONArray(s);

			for (int i = 0; i < jsa.length(); i++) {

				JSONObject jsoo = (JSONObject) jsa.get(i);

				OIDObject oi = new OIDObject(jsoo.getString("identifier"),
						jsoo.getString("name"), jsoo.getBoolean("isActive"));
				
				oi.setHasChildren(jsoo.getBoolean("hasChildren"));

				LazyOIDTreeNode oin = new LazyOIDTreeNode(oi);
				
				lzynde.add(oin);
				oidTreeModel.insertNodeInto(oin, lzynde, i);
			}
			
			cap.setOIDTree(oidTreeModel, null, new OIDTreeSelectionListener());

			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Requests all TreeNodes containing the given String in their Name-attribute
	 *  
	 * @param searchString
	 */
	private void requestSearchTree(String searchString) {
		
		if (searchString.trim().equalsIgnoreCase("")) {
			requestTreeRoot();
		}
		else {
			
			try {
				String toBeURL = strRelURL+"/"+privilegedServlet+"CreateConsentServiceServlet?type=searchtree&searchstring="+searchString;
				
				URL url = new URL(encodeString(toBeURL));
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("cookie", strCookie );
				conn.setDoInput(true);

				InputStream is = conn.getInputStream();
				StringWriter writer = new StringWriter();
				IOUtils.copy(new InputStreamReader(is, "UTF8"), writer);
				String s = writer.toString();
				
				is.close();

				JSONObject jso = new JSONObject(s);

				OIDObject root = new OIDObject(jso.getString("identifier"), jso.getString("name"), jso.getBoolean("isActive"));
				
				root.setHasChildren(jso.getBoolean("hasChildren"));

				DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root);

				reconstructTree(rootNode, jso);

				oidTreeModel = new DefaultTreeModel(rootNode);				
				cap.setOIDTree(oidTreeModel, null, new OIDTreeSelectionListener());
				
				int row = 0;
			    while (row < cap.mainPanel.mp.cp.tp.orgaTree.getRowCount()) {
			    	cap.mainPanel.mp.cp.tp.orgaTree.expandRow(row);
			    	row++;
			    }
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Reconstructs the requested tree from the given JSONObjects and adds it to the given Node
	 * 
	 * @param node
	 * @param jsoTree
	 */
	private void reconstructTree(DefaultMutableTreeNode node, JSONObject jsoTree) {
		
		try {
			
			JSONArray children = new JSONArray();
			
			try {
				children = jsoTree.getJSONArray("children");
			}
			catch (Exception e) {
			}
			
			
			for (int i = 0; i < children.length(); i++) {

				JSONObject jsoo = (JSONObject) children.get(i);

				OIDObject oi = new OIDObject(jsoo.getString("identifier"), jsoo.getString("name"), jsoo.getBoolean("isActive"));

				oi.setHasChildren(jsoo.getBoolean("hasChildren"));
				DefaultMutableTreeNode oin = new DefaultMutableTreeNode(oi);
				
				JSONArray tejso = new JSONArray();
				
				try {
					tejso = jsoo.getJSONArray("children");
				}
				catch (Exception e) {
				}

				if (tejso.length() != 0) {
					
					reconstructTree(oin, jsoo);
				}
				node.add(oin);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	/**
	 * Requests the list of Rule descriptions belonging to the currently worked on user
	 */
	private void requestRuleList() {
		try {
			URL url = new URL(strRelURL+"/"+privilegedServlet+"CreateConsentServiceServlet?type=rulelist");
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("cookie", strCookie );
			conn.setDoInput(true);
			
			
			InputStream is = conn.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(new InputStreamReader(is, "UTF8"), writer);
			String s = writer.toString();
            
			
			JSONArray jsa = new JSONArray(s);

			ruleListModel = new DefaultListModel();

			for (int i = 0; i < jsa.length(); i++) {

				JSONObject jsoo = (JSONObject) jsa.get(i);

				OIDObject oi = new OIDObject(jsoo.getString("ruleID"), jsoo.getString("description"), true);

				ruleListModel.addElement(oi);
			}

			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Requests a Rule description for the currently selected data from the server
	 * @return
	 */
	private String requestRuleDescription() {
		try {
			String toBeUrl = strRelURL+"/"+privilegedServlet+"CreateConsentServiceServlet?type=ruleDescription" +
			"&organisation="+organisation+ "&persons="+persons+"&affectedOID="+affectedOID+"&documents="+documents+
			"&documentsOID="+documentsOID+"&accessType="+accessType+"&grantAccess="+grantAccess;
			
			URL url = new URL(encodeString(toBeUrl));
			
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("cookie", strCookie );
			conn.setDoInput(true);
			
			InputStream is = conn.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(new InputStreamReader(is, "UTF8"), writer);
			String s = writer.toString();
            
			is.close();
		
			JSONObject jsoo = new JSONObject(s);

			return jsoo.getString("description");

			
		} catch (Exception e) {
			//e.printStackTrace();
			return "";
		}
	}

	/**
	 * Removes the given Rule from the PolicySet which is stored on the server
	 * 
	 * @param oo
	 * @return
	 */
	private boolean requestRemoveRule(OIDObject oo) {
		
		try {
			URL url = new URL(strRelURL+"/"+privilegedServlet+"CreateConsentServiceServlet?type=removeRule&id="+oo.getIdentifier());
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("cookie", strCookie );
			conn.setDoInput(true);
			
			
			InputStream is = conn.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(new InputStreamReader(is, "UTF8"), writer);
			String s = writer.toString();
            
			is.close();
			
			JSONObject ack = new JSONObject(s);
			
			return ack.getBoolean("removed");
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * Requests that the Rule with the current parameter selection be added to the PolicySet of the user
	 * 
	 * @return
	 */
	private boolean requestAddRule() {
		
		try {
			String toBeURL = strRelURL+"/"+privilegedServlet+"CreateConsentServiceServlet?type=addRule&"+
			"&organisation="+organisation+ "&persons="+persons+"&affectedOID="+affectedOID+"&documents="+documents+
			"&documentsOID="+documentsOID+"&accessType="+accessType+"&grantAccess="+grantAccess;
			
			System.out.println(organisation);
			System.out.println(persons);
			System.out.println(affectedOID);
			System.out.println(documents);
			System.out.println(documentsOID);
			System.out.println(accessType);
			System.out.println(grantAccess);
			
			toBeURL = encodeString(toBeURL);
			URL url = new URL(toBeURL);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("cookie", strCookie );
			conn.setDoInput(true);
			
			
			InputStream is = conn.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(new InputStreamReader(is, "UTF8"), writer);
			String s = writer.toString();
            
			is.close();
			
			JSONObject ss = new JSONObject(s);
			
			if (ss.getBoolean("added")) {
				requestRuleList();
				
				cap.setRuleList(ruleListModel, null);
				
				return true;
			}
			
		} catch (Exception e) {
			return false;
			
		}
		return false;
	}

	/**
	 * Requests the currently supported personnel-types from the server
	 * 
	 */
	private void requestPersonnelList() {
		try {
			
			URL url = new URL(strRelURL+"/"+privilegedServlet+"CreateConsentServiceServlet?type=persons");
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("cookie", strCookie );
			conn.setDoInput(true);

			
			InputStream is = conn.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(new InputStreamReader(is, "UTF8"), writer);
			String s = writer.toString();

			JSONArray jsa = new JSONArray(s);

			personsComboxBoxModel = new DefaultComboBoxModel();

			for (int i = 0; i < jsa.length(); i++) {

				JSONObject jsoo = (JSONObject) jsa.get(i);

				OIDObject oi = new OIDObject(jsoo.getString("identifier"),
						jsoo.getString("name"), true);

				personsComboxBoxModel.addElement(oi);
			}

			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Opens a new window and shows the current consent as a PDF within
	 * 
	 */
	private void requestConsentPDF() {
		
		 try
		    {
		      AppletContext a = getAppletContext();
		      URL url = new URL(strRelURL+"/"+privilegedServlet+"CreateConsentServiceServlet?type=newconsentpdf");
		      a.showDocument(url,"_blank");
		    }
		    catch (MalformedURLException e){
		      System.out.println(e.getMessage());
		    }
	}

	/**
	 * Requests the currently available document-types
	 * 
	 */
	private void requestDocumentList() {
		try {
			URL url = new URL(strRelURL+"/"+privilegedServlet+"CreateConsentServiceServlet?type=documenttypes");
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("cookie", strCookie );
			conn.setDoInput(true);

			
			InputStream is = conn.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(new InputStreamReader(is, "UTF8"), writer);
			String s = writer.toString();

			JSONArray jsa = new JSONArray(s);

			documentComboBoxModel = new DefaultComboBoxModel();

			for (int i = 0; i < jsa.length(); i++) {

				JSONObject jsoo = (JSONObject) jsa.get(i);

				OIDObject oi = new OIDObject(jsoo.getString("identifier"),
						jsoo.getString("name"), true);

				documentComboBoxModel.addElement(oi);
			}
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Requests a newly created CDA Document from the server
	 * 
	 */
	private void requestDocument() {
		try {
			
			URL url = new URL(strRelURL+"/"+privilegedServlet+"CreateConsentServiceServlet?type=newconsentcda");
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("cookie", strCookie );
			conn.setDoInput(true);

			
			InputStream is = conn.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(new InputStreamReader(is, "UTF8"), writer);
			String s = writer.toString();
			is.close();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			cda = db.parse(new InputSource(new ByteArrayInputStream(s.getBytes("utf-8"))));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Requests that the created consent be stored as an uncleared consent
	 */
	private void requestStoreUnclearedConsent() {
		
		try {
			URL url = new URL(strRelURL+"/"+privilegedServlet+"CreateConsentServiceServlet?type=storeunclearedconsent");
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("cookie", strCookie );
			conn.setDoInput(true);
			
			
			InputStream is = conn.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(new InputStreamReader(is, "UTF8"), writer);
			String s = writer.toString();
			
			is.close();
			
			JSONObject jso = new JSONObject(s);

			boolean success = jso.getBoolean("success");
			String message = jso.getString("message");
			
			if (success) {
				
				JOptionPane
				.showMessageDialog(
						null,
						message,
						"Erfolg" ,
						JOptionPane.INFORMATION_MESSAGE);
				
			}
			else {
				
				JOptionPane
				.showMessageDialog(
						null,
						message,
						"Fehler",
						JOptionPane.ERROR_MESSAGE);
				
			}
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Requests that a electronically signed CDA Document object be stored
	 * 
	 * @param storeUrl
	 */
	private void requestStoreSignedConsent(String storeUrl) {
		try {
			
			URL url = new URL(strRelURL+storeUrl);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("cookie", strCookie );
			conn.setRequestProperty("Content-Type", "text/xml");
			conn.setRequestProperty("Character-Encoding", "UTF-8");
			
			conn.setDoOutput(true);
			conn.setDoInput(true);

			DOMSource domSource = new DOMSource(cda);
			StringWriter owriter = new StringWriter();
			StreamResult result = new StreamResult(owriter);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			ObjectOutputStream oStream = new ObjectOutputStream( bStream );
			oStream.writeObject ( cda );
			byte[] byteVal = bStream. toByteArray();
			
			
			OutputStream ops = conn.getOutputStream();
			ops.write(byteVal);
			
			
			InputStream is = conn.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(new InputStreamReader(is, "UTF8"), writer);
			String s = writer.toString();
			
			is.close();
			
			JSONObject jso = new JSONObject(s);

			boolean success = jso.getBoolean("success");
			String message = jso.getString("message");
			
			if (success) {
				
				JOptionPane
				.showMessageDialog(
						this,
						message,
						"Erfolg" ,
						JOptionPane.INFORMATION_MESSAGE);
				
			}
			else {
				
				JOptionPane
				.showMessageDialog(
						this,
						message,
						"Fehler",
						JOptionPane.ERROR_MESSAGE);
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	/**
	 * Requests a standard CDA Document object for the given participation status
	 * 
	 * @param participation
	 */
	private void requestStandardConsentDocument(boolean participation) {
		try {
			
			URL url = new URL(strRelURL+"/"+privilegedServlet+"CreateConsentServiceServlet?type=standardconsentCDA&participation="+participation);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("cookie", strCookie );
			conn.setDoInput(true);

			
			InputStream is = conn.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(new InputStreamReader(is, "UTF8"), writer);
			String s = writer.toString();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			cda = db.parse(new InputSource(new ByteArrayInputStream(s.getBytes("utf-8"))));
			
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Encodes a String to be UTF-8 compatible
	 * 
	 * @param s
	 * @return
	 */
	private String encodeString (String s) {
		
		s = s.replace(" ", "%20");
		s = s.replace("Ä", "%C3%84");
		s = s.replace("Ö", "%C3%96");
		s = s.replace("Ü", "%C3%9C");
		s = s.replace("ä", "%C3%A4");
		s = s.replace("ö", "%C3%B6");
		s = s.replace("ü", "%C3%BC");
		
		return s;
	}
	
	/**
	 * Listener which handles the expansion of TreeNodes
	 * 
	 * @return
	 */
	private TreeExpansionListener getMyTreeExpansionListener() {

		return new TreeExpansionListener() {
			
			public void treeCollapsed(TreeExpansionEvent arg0) {	
			}

			public void treeExpanded(TreeExpansionEvent arg0) {
								
				//Normal Tree, load child Nodes from the server
				if (arg0.getPath().getLastPathComponent() instanceof LazyOIDTreeNode) {
					LazyOIDTreeNode node = (LazyOIDTreeNode) arg0.getPath().getLastPathComponent();
					
					if (!node.isLeaf() && !node.hasBeenExpanded()) {
						node.setHasBeenExpanded(true);
						requestTreeNode(node);
					}
				}
				//Search Tree, expand as usual
				else if (arg0.getPath().getLastPathComponent() instanceof DefaultMutableTreeNode) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) arg0.getPath().getLastPathComponent();
					
					JTree tree = (JTree) arg0.getSource();
					tree.expandPath(new TreePath(node.getPath()));
					
				}
			};
		};
	}
	
	/**
	 * MouseListener used to select items from the list of Rule descriptions, allows for deletion of Rules
	 * 
	 * @return
	 */
	private MouseListener getRuleListMouseAdapter() {
		
		return new MouseListener() {
			
			private JList list;
			
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					list = ((JList) e.getSource());
					list.setSelectedIndex(getRow(e.getPoint()));
					
					JPopupMenu menu = new JPopupMenu();
					menu.add(new RemoveRule(list));

					Point pt = SwingUtilities.convertPoint(e.getComponent(),
							e.getPoint(), list);
					menu.show(list, pt.x, pt.y);
				}
			}

			private int getRow(Point point)
			{
				return list.locationToIndex(point);
			}

			public void mouseClicked(MouseEvent arg0) {
				
			}

			public void mouseEntered(MouseEvent arg0) {
				
			}

			public void mouseExited(MouseEvent arg0) {
				
			}

			public void mouseReleased(MouseEvent arg0) {
				
			}
		};	
	}
	
	/**
	 * Rightclick contextmenu for Rule deletion
	 *
	 */
	class RemoveRule extends AbstractAction{ 
	    private JList list;
	    
	    public RemoveRule(JList list){ 
	        super("Löschen"); 
	        this.list = list;
	    } 
	 
	    public void actionPerformed(ActionEvent e){ 
	        if (requestRemoveRule((OIDObject) ruleListModel.get(list.getSelectedIndex()))) {
	        	ruleListModel.remove(list.getSelectedIndex());
	        }
	    } 
	}
	
	/**
	 * Listener which handles state changes for the Components displaying parameters for Rule-generation
	 * 
	 * @return
	 */
	private ActionListener getBoxActionListener() {
		
		return new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				if (e.getSource() instanceof JComboBox) {
					
					String name = (((JComboBox)e.getSource()).getName());
					
					
					if (name.equalsIgnoreCase("documents")) {
						documents = ((OIDObject) ((JComboBox)e.getSource()).getSelectedItem()).getName();
						documentsOID = ((OIDObject) ((JComboBox)e.getSource()).getSelectedItem()).getIdentifier();
					}
					else if (name.equalsIgnoreCase("persons")) {
						
						if (!((OIDObject) ((JComboBox)e.getSource()).getSelectedItem()).getIdentifier().equalsIgnoreCase("ALL")) {
							personsOID = ((OIDObject) ((JComboBox)e.getSource()).getSelectedItem()).getIdentifier();
							affectedOID = organisationOID + "^" + personsOID;
							persons = ((OIDObject) ((JComboBox)e.getSource()).getSelectedItem()).getName();
							
						}
						else {
							personsOID = ((OIDObject) ((JComboBox)e.getSource()).getSelectedItem()).getIdentifier();
							affectedOID = organisationOID;
							persons = "ALL";
						}
					}
				}
				else if (e.getSource() instanceof OIDRadioButton) {
					
					String name = (((OIDRadioButton)e.getSource()).getName());
					
					if (name.equalsIgnoreCase("accesstype")) {
						accessType = ((OIDObject) ((OIDRadioButton)e.getSource()).getOIDObject()).getIdentifier();
					}
					else if (name.equalsIgnoreCase("grantaccess")) {
						grantAccess = Boolean.parseBoolean(((OIDRadioButton)e.getSource()).getOIDObject().getIdentifier());
					}
				}
				cap.setRuleDescription(requestRuleDescription());
			}
		};
	}
	
	/**
	 * Listener used for detection of selection changes within the organisation tree
	 *
	 */
	public class OIDTreeSelectionListener {

		public void valueChanged() {
			
			CheckTreeManager c = cap.getCheckTreeManager();

			if (c.getSelectionModel().getSelectionPath() != null) {
				
				TreePath selectedPath = c.getSelectionModel().getSelectionPath();
				c.deselectAll();
				c.getSelectionModel().setSelectionPath(selectedPath);
				
				String orga = "";
				String orgaOID = "";
				
				
				if (c.getSelectionModel().getSelectionPath().getLastPathComponent() instanceof LazyOIDTreeNode) {
					LazyOIDTreeNode selectedNode = (LazyOIDTreeNode) c.getSelectionModel().getSelectionPath().getLastPathComponent();
					
					orga = selectedNode.getUserObject().getName();
					orgaOID = selectedNode.getUserObject().getIdentifier();
				}
				else if (c.getSelectionModel().getSelectionPath().getLastPathComponent() instanceof DefaultMutableTreeNode) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) c.getSelectionModel().getSelectionPath().getLastPathComponent();
					
					orga = ((OIDObject) selectedNode.getUserObject()).getName();
					orgaOID = ((OIDObject) selectedNode.getUserObject()).getIdentifier();
				}
				
				
				organisation = orga;
				organisationOID = orgaOID;
				cap.setRuleDescription(requestRuleDescription());
				
			}
			else {
				cap.setRuleDescription("Bitte wählen Sie alle Parameter aus");
			}
		}
	}
	
	/**
	 * Listener used to add Rules using the "add rule" button
	 * 
	 * @return
	 */
	private ActionListener getMyAddRuleListener() {
		
		return new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				requestAddRule();
			}
		};
	}
	
	/**
	 * Listener used to handle search input for the organisations tree
	 * 
	 * @return
	 */
	private KeyListener getSearchFieldListener() {
		
		return new KeyListener() {
			
			public void keyTyped(KeyEvent e) {
			}
			
			public void keyReleased(KeyEvent e) {
				requestSearchTree(((JTextField) e.getSource()).getText());
			}
			
			public void keyPressed(KeyEvent e) {
			}
		};
	}
	
	/**
	 * Requests a PDF showing the consent and opens the PDF in a new window while forwarding the user to the
	 * page which allows him to choose a signing option.
	 * 
	 * @return
	 */
	private ActionListener getMyNewPDFConsentListener() {
		
		return new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				remove(cap);
				setContentPane(scp);
				
				requestDocument();
				
				validate();
				
				scp.setCDA(cda);
				
				scp.setDigitalButton(new ShowDigitalSignListener());
				
				scp.setWrittenButton(new ShowWrittenSignListener());
				
				
				requestConsentPDF();
			}
		};
	}
	
	/**
	 * Listener for the SignDigital button
	 *
	 */
	class ShowDigitalSignListener implements ActionListener {

		
		public void actionPerformed(ActionEvent e) {
			
			smcsa.setDocument(cda, new StoreSignedConsentListener());
			
			remove(scp);
			
			setContentPane(smcsa);
			
			validate();
			
		}
	}
	
	/**
	 * Listener for the SignWritten button
	 *
	 */
	class ShowWrittenSignListener implements ActionListener {

		
		public void actionPerformed(ActionEvent e) {
			
			remove(scp);
			
			setContentPane(swp);
			
			swp.signWritten.addActionListener(new StoreUnclearedConsentListener());
			
			validate();
		}
	}
	
	/**
	 * 
	 * Listener for the store unsigned consent button
	 *
	 */
	class StoreUnclearedConsentListener implements ActionListener {

		
		public void actionPerformed(ActionEvent e) {
			requestStoreUnclearedConsent();
			
		}
	}
	
	/**
	 * Listener for the store signed consent button
	 *
	 */
	class StoreSignedConsentListener implements ActionListener {

		String createConsentURL = "/"+privilegedServlet+"CreateConsentServiceServlet?type=storesignedconsent";
		String revokeConsentURL = "/"+privilegedServlet+"RevokeConsentServiceServlet?type=";
		
		
		public void actionPerformed(ActionEvent e) {
			
			if (privInt >= 1) {
				requestStoreSignedConsent(createConsentURL);
			}
			else {
				if (!mode.equalsIgnoreCase("signonly")) {
					requestStoreSignedConsent(createConsentURL);
				}
				else {
					if (Boolean.parseBoolean(endparticipation)) {
						requestStoreSignedConsent(revokeConsentURL+"endparticipation&context=digital");
					}
					else {
						requestStoreSignedConsent(revokeConsentURL+"revokeconsent&context=digital");
					}
				}
			}
		}
	}
}