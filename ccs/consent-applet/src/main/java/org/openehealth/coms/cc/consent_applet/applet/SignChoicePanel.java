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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.openehealth.coms.cc.consent_applet.applet.ConsentApplet.ShowDigitalSignListener;
import org.openehealth.coms.cc.consent_applet.applet.ConsentApplet.ShowWrittenSignListener;
import org.w3c.dom.Document;


/**
 * This Panel displays the user with the Components used to enable him to choose one of two signing options
 * 
 * @author Lennart Koester
 * @version 14.02.2011
 *
 */
@SuppressWarnings("serial")
public class SignChoicePanel extends JPanel {
	
	
	
	Document cda = null;
	
	JButton signWritten = new JButton();
	JButton signDigital = new JButton();
	JLabel spacer = new JLabel();
	

	public SignChoicePanel() {
		
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			
		}

		GridBagLayout panel4Layout = new GridBagLayout();

		
		spacer.setText("            ");

		this.setLayout(panel4Layout);
		this.setBackground(Color.WHITE);

		signWritten.setText("Schriftliche Signatur");
		signDigital.setText("Digitale Signatur");

		this.add(signWritten, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						3, 3, 3, 3), 0, 0));
		
		this.add(spacer, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						3, 3, 3, 3), 0, 0));

		this.add(signDigital, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						3, 3, 3, 3), 0, 0));
		
	}
	
	public void setCDA(Document dom) {
		
		cda = dom;
	}
	public void setDigitalButton(ShowDigitalSignListener showDigitalSignListener) {

		signDigital.addActionListener(showDigitalSignListener);
		
	}
	
	public void setWrittenButton(ShowWrittenSignListener showWrittenSignListener) {

		signWritten.addActionListener(showWrittenSignListener);
		
	}
}
