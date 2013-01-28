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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultTreeModel;

import org.openehealth.coms.cc.consent_applet.applet.ConsentApplet.OIDTreeSelectionListener;
import org.openehealth.coms.cc.consent_applet.model.CheckTreeManager;
import org.openehealth.coms.cc.consent_applet.model.OIDObject;



/**
 * This class contains the Components necessary for the presentation of the consent applet
 * 
 * @author Lennart Koester
 * @version 14.02.2011
 *
 */
@SuppressWarnings("serial")
public class ConsentAppletPanel extends JPanel {
	
	BorderLayout layout = new BorderLayout();
	
	MainPanel mainPanel = new MainPanel();
	StoreButtonsPanel storeButtons = new StoreButtonsPanel();
	CheckTreeManager checkTreeManager = null;
	
	public ConsentAppletPanel () {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			
		}
		
		this.setLayout(layout);
		this.setBackground(Color.WHITE);
		this.setPreferredSize(new Dimension(750, 800));
		this.setMaximumSize(new Dimension(750, 800));
		this.add(mainPanel, BorderLayout.NORTH);
		this.add(storeButtons, BorderLayout.CENTER);
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(), "Einwilligungserklärung erstellen"));
	}
	
	/**
	 * Sets the Listener used for changes in the comboboxes and radiobuttons
	 * 
	 * @param bal
	 */
	@SuppressWarnings("rawtypes")
	public void setChangeListener(ActionListener bal) {
		
		for (Enumeration e = mainPanel.mp.cp.scp.bgAccessType.getElements() ; e.hasMoreElements() ;) {
			
			OIDRadioButton n = (OIDRadioButton) e.nextElement();
			n.addActionListener(bal);
			
		}
		
		
		for (Enumeration e = mainPanel.mp.cp.scp.bgGrantAccess.getElements() ; e.hasMoreElements() ;) {
			
			OIDRadioButton n = (OIDRadioButton) e.nextElement();
			n.addActionListener(bal);
			
		}
		
		mainPanel.mp.cp.scp.jcbDocuments.addActionListener(bal);
		mainPanel.mp.cp.scp.jcbPersons.addActionListener(bal);
		
		mainPanel.mp.cp.tp.orgaTree.addTreeSelectionListener(checkTreeManager);
	
		
	}
	/**
	 * Sets the headline with the given data
	 * 
	 * @param userName
	 * @param userForename
	 * @param userEmailaddress
	 */
	public void setUserData(String userName, String userForename, String userEmailaddress) {
		
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(), "Einwilligungserklärung erstellen für " + userForename + " " + userName + " - " + userEmailaddress));
		
	}
	
	/**
	 * Sets the given DefaulListeModel to the presentation
	 * 
	 * @param ruleListModel
	 * @param ruleListMouseAdapter
	 */
	public void setRuleList(DefaultListModel ruleListModel, MouseListener ruleListMouseAdapter) {
		
		mainPanel.listPanel.ruleList.setModel(ruleListModel);
				
		if (ruleListMouseAdapter!= null) {
			mainPanel.listPanel.ruleList.addMouseListener(ruleListMouseAdapter);
		}
	}
	
	/**
	 * Sets data to the organisation tree
	 * 
	 * @param oidTreeModel
	 * @param mTEL
	 * @param otsl
	 */
	public void setOIDTree(DefaultTreeModel oidTreeModel, TreeExpansionListener mTEL, OIDTreeSelectionListener otsl) {
		
		mainPanel.mp.cp.tp.orgaTree.setModel(oidTreeModel);
		
		
		if (mTEL != null) {
			mainPanel.mp.cp.tp.orgaTree.addTreeExpansionListener(mTEL);
			
		}
		if (checkTreeManager == null) {
			checkTreeManager = new CheckTreeManager(mainPanel.mp.cp.tp.orgaTree, otsl);
		}
	}
	
	public CheckTreeManager getCheckTreeManager() {
		return checkTreeManager;
	}
	
	/**
	 * Sets data to the personnel combobox
	 * 
	 * @param personsComboxBoxModel
	 */
	public void setPersonnelComboBox(DefaultComboBoxModel personsComboxBoxModel) {
		
		mainPanel.mp.cp.scp.jcbPersons.setModel(personsComboxBoxModel);
	}
	
	/**
	 * Sets data to the document combobox
	 * 
	 * @param documentComboBoxModel
	 */
	public void setDocumentComboBox(DefaultComboBoxModel documentComboBoxModel) {
		
		mainPanel.mp.cp.scp.jcbDocuments.setModel(documentComboBoxModel);
	}
	
	/**
	 * Sets the given text to the rule desciption textarea
	 * 
	 * @param requestRuleDescription
	 */
	public void setRuleDescription(String requestRuleDescription) {
		mainPanel.descriptionPanel.description.setText(requestRuleDescription);
		
	}
	
	/**
	 * Adds the listener enabling the adding of rules to the respective button
	 * @param marl
	 */
	public void setAddRuleListener(ActionListener marl) {
		mainPanel.mp.addRule.addActionListener(marl);
	}
	
	
	/**
	 * Adds the create consent listener to the respective button
	 * @param mynpcl
	 */
	public void setCreateConsentListener(ActionListener mynpcl) {
		storeButtons.storeConsent.addActionListener(mynpcl);
	}
	
	/**
	 * Adds the searchfield listener to the searchfield
	 * @param searchFieldListener
	 */
	public void setSearchFieldListener(KeyListener searchFieldListener) {
		mainPanel.mp.cp.searchField.addKeyListener(searchFieldListener);
	}
	
	
	/**
	 * This Panel contains all other Panels and their subcomponents
	 *
	 */
	class MainPanel extends JPanel {
		
		public BorderLayout mainLayout = new BorderLayout();
		public ListPanel listPanel = new ListPanel();
		public MidPanel mp = new MidPanel();
		public DescriptionPanel descriptionPanel = new DescriptionPanel();
		
		public MainPanel() {
			
			this.setSize(750, 800);
			this.setLayout(mainLayout);
			this.add(listPanel, BorderLayout.NORTH);
			this.add(mp, BorderLayout.CENTER);
			this.add(descriptionPanel, BorderLayout.SOUTH);
			this.setBackground(Color.WHITE);
			
		}
	}
	
	/**
	 * This Panel contains the List containing the description of the currently constructed Rules
	 *
	 */
	class ListPanel extends JPanel {
		
		FlowLayout panel1Layout = new FlowLayout();
		JList ruleList = new JList ();
		JScrollPane pane = new JScrollPane(ruleList);
		
		public ListPanel() {
			
			pane.setPreferredSize(new Dimension(720, 200));
			this.setLayout(panel1Layout);
			this.setBackground(Color.WHITE);
			this.add(pane);
			this.setBorder(BorderFactory.createTitledBorder(BorderFactory
					.createEtchedBorder(), "Datenschutzregeln"));
		}
	}
	
	/**
	 * This Panel contains the mid-section of the applet's presentation, the ChoicePanel and the buttons to add or cancel a Rule
	 *
	 */
	class MidPanel extends JPanel {
		
		GridBagLayout gbl = new GridBagLayout();
		
		ChoicePanel cp = new ChoicePanel();
		
		JButton addRule = new JButton();
		JButton reset = new JButton();
		
		
		public MidPanel() {
			this.setLayout(gbl);
			this.setBackground(Color.WHITE);
			
			this.setBorder(BorderFactory.createTitledBorder(BorderFactory
					.createEtchedBorder(), "Datenschutzregel erstellen"));
			
			addRule.setText("Datenschutzregel hinzufügen");
			reset.setText("Verwerfen");
			
			this.add(cp, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.BOTH,
					new Insets(3, 3, 3, 3), 0, 0));
			
			JPanel b = new JPanel();
			b.setLayout(new GridBagLayout());
			
			b.add(addRule, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.NONE,
					new Insets(3, 3, 3, 3), 0, 0));
			
			b.add(reset, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.NONE,
					new Insets(3, 3, 3, 3), 0, 0));
			
			b.setBackground(Color.WHITE);
			
			this.add(b, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.BOTH,
					new Insets(3, 3, 3, 3), 0, 0));
		}
	}
	
	/**
	 * This Panel contains both the TreePanel as well as the SubChoicePanel and the SearchField
	 *
	 */
	class ChoicePanel extends JPanel {
		
		GridBagLayout gbl = new GridBagLayout();
		
		JTextField searchField = new JTextField();
		TreePanel tp = new TreePanel();
		SubChoicePanel scp = new SubChoicePanel();
		
	
		public ChoicePanel() {
			
			
			this.setLayout(gbl);
			this.setBackground(Color.WHITE);
			
			this.add(searchField, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.BOTH,
					new Insets(3, 3, 3, 3), 0, 0));
		
			
			
			this.add(tp, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.BOTH,
					new Insets(3, 3, 3, 3), 0, 0));
			
			this.add(scp, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.NORTH, GridBagConstraints.NONE,
					new Insets(3, 3, 3, 3), 0, 0));
			
			
		}	
		/**
		 * This Panel contains the JTree used for presentation of the organisations
		 * 
		 */
		class TreePanel extends JPanel {
			
			GridBagLayout layout = new GridBagLayout();
			
			JLabel lbOrganisation = new JLabel();
			JTree orgaTree = new JTree();
			JScrollPane pane = new JScrollPane(orgaTree);
			
						
			
			public TreePanel() {
				this.setLayout(layout);
				this.setBackground(Color.WHITE);
				lbOrganisation.setText("Organisation");
				
				
				
				pane.setPreferredSize(new Dimension(300, 300));
				
				this.add(lbOrganisation, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(3, 3, 3, 3), 0, 0));
				
				this.add(pane, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.BOTH,
						new Insets(3, 3, 3, 3), 0, 0));
			}
		}
		/**
		 * This Panel contains all remaining Components used to display parameters used for Rule-generation besides the JTree
		 *
		 */
		class SubChoicePanel extends JPanel {
			
			private GridBagLayout panel2Layout = new GridBagLayout();
			
			JLabel lbDocuments = new JLabel();
			JLabel lbGrantAccess = new JLabel();
			JLabel lbPersons = new JLabel();
			JLabel lbAccessType = new JLabel();
			
			OIDRadioButton jrbAccessTypeRead = new OIDRadioButton(new OIDObject("read", "Lesen", true));
			OIDRadioButton jrbAccessTypeWrite = new OIDRadioButton(new OIDObject("write", "Schreiben", true));
			OIDRadioButton jrbAccessTypeReadWrite = new OIDRadioButton(new OIDObject("readwrite", "Lesen und Schreiben", true));
		
			JComboBox jcbDocuments = new JComboBox();
			JComboBox jcbPersons = new JComboBox();
			
			OIDRadioButton jrbAccessTrue = new OIDRadioButton(new OIDObject("true", "Ja", true));
			OIDRadioButton jrbAccessFalse = new OIDRadioButton(new OIDObject("false", "Nein", true));
			
			ButtonGroup bgAccessType = new ButtonGroup();
			ButtonGroup bgGrantAccess = new ButtonGroup();

			
			public SubChoicePanel() {
				this.setLayout(panel2Layout);
				this.setBackground(Color.WHITE);
				
				
				lbAccessType.setText("Zugriffsart");
				lbDocuments.setText("Dokumente");
				lbGrantAccess.setText("Zugriff zulassen");
				lbPersons.setText("Personen");
				
				
				jrbAccessTypeRead.setName("accesstype");
				jrbAccessTypeWrite.setName("accesstype");
				jrbAccessTypeReadWrite.setName("accesstype");
				
				
				jrbAccessTrue.setName("grantaccess");
				jrbAccessFalse.setName("grantaccess");
				
				
				jcbDocuments.setName("documents");
				jcbPersons.setName("persons");
				
				
				bgAccessType.add(jrbAccessTypeRead);
				bgAccessType.add(jrbAccessTypeWrite);
				bgAccessType.add(jrbAccessTypeReadWrite);
				
				
				bgGrantAccess.add(jrbAccessTrue);
				bgGrantAccess.add(jrbAccessFalse);
				
				jrbAccessTypeRead.setBackground(Color.WHITE);
				jrbAccessTypeWrite.setBackground(Color.WHITE);
				jrbAccessTypeReadWrite.setBackground(Color.WHITE);
				
				jrbAccessTrue.setBackground(Color.WHITE);
				jrbAccessFalse.setBackground(Color.WHITE);
				
				
				jrbAccessTypeRead.setSelected(true);
				jrbAccessTrue.setSelected(true);
				
			
				
				lbPersons.setPreferredSize(new Dimension(120, 17));
				
				this.add(lbPersons, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.NORTH, GridBagConstraints.NONE,
						new Insets(3, 3, 3, 3), 0, 0));
				
				this.add(jcbPersons, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(3, 3, 3, 3), 0, 0));
				
				this.add(lbDocuments, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(3, 3, 3, 3), 0, 0));
				
				this.add(jcbDocuments, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(3, 3, 3, 3), 0, 0));
				
				this.add(lbAccessType, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(3, 3, 3, 3), 0, 0));
				
				this.add(jrbAccessTypeRead, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(3, 3, 3, 3), 0, 0));
				
				this.add(jrbAccessTypeWrite, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(3, 3, 3, 3), 0, 0));
				
				this.add(jrbAccessTypeReadWrite, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(3, 3, 3, 3), 0, 0));
				
				this.add(lbGrantAccess, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(3, 3, 3, 3), 0, 0));
				
				this.add(jrbAccessTrue, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(3, 3, 3, 3), 0, 0));
				
				this.add(jrbAccessFalse, new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(3, 3, 3, 3), 0, 0));
			}
		}
	}
	/**
	 * This Panel contains the textarea used to display the description of the currently constructed Rule
	 *
	 */
	class DescriptionPanel extends JPanel {
		
		FlowLayout panel3Layout = new FlowLayout();
		JTextArea description = new JTextArea();
		
		public DescriptionPanel() {
			
			description.setSize(600, 80);
			description.setLineWrap(true);
			description.setWrapStyleWord(true);
			
			
			this.setLayout(panel3Layout);
			this.add(description);
			this.setBackground(Color.WHITE);
			this.setBorder(BorderFactory.createTitledBorder(BorderFactory
					.createEtchedBorder(), "Aktuelle Datenschutzregel"));
		}
	}
	/**
	 * This Panel contains the buttons used to store or cancel the current consent
	 *
	 */
	class StoreButtonsPanel extends JPanel {
		
		GridBagLayout panel4Layout = new GridBagLayout();
		
		JButton storeConsent = new JButton();
		JButton cancel = new JButton();
		
		public StoreButtonsPanel() {
			this.setLayout(panel4Layout);
			this.setBackground(Color.WHITE);
			
			
			storeConsent.setText("Einwilligungserklärung erstellen");
			cancel.setText("Abbrechen");
			
			this.add(storeConsent, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(3, 3, 3, 3), 0, 0));
			
			this.add(cancel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(3, 3, 3, 3), 0, 0));
		}
	}
	/**
	 * Class used to display an OIDObject in a JRadioButton
	 *
	 */
	class OIDRadioButton extends JRadioButton {
		
		private OIDObject oidObject = new OIDObject("hello", "hello", true);
		
		public String toString() {
			return oidObject.getName();
		}
		
		public String getText() {
			
			if (oidObject == null) {
				return "hello";
			}
			
			return oidObject.getName();
		}
		
		public String getIdentifier() {
			return oidObject.getIdentifier();
		}
			
		public void setOIDObject(OIDObject oido) {
			oidObject = oido;
		}
		public OIDObject getOIDObject() {
			return oidObject;
		}
		
		public OIDRadioButton(OIDObject oido) {
			oidObject = oido;
		}
	}
}
