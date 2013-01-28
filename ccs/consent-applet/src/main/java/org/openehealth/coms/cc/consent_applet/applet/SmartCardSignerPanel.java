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
import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openehealth.coms.cc.consent_applet.applet.ConsentApplet.StoreSignedConsentListener;
import org.openehealth.coms.cc.consent_applet.model.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Node;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.security.*;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.lang.reflect.Constructor;


/**
 * Applet for digital signing documents with a smart card. The applet is
 * intended to be placed in a HTML document containing a single HTML form that
 * is used for applet input/output. The applet accepts several parameters - the
 * name of the field in the HTML form that contains the file name to be signed
 * and the names of the fields in the HTML form, where the certification chain
 * and signature should be stored.
 * 
 * If the signing process is successful, the signature and certification chain
 * fields in the HTML form are filled. Otherwise, an error message explaining
 * the failure reason is shown.
 * 
 * The applet asks the user to locate in his local file system the PKCS#11
 * implementation library that is part of software that come with the smart card
 * and the smart card reader. Usually this is a Windows .DLL file located in
 * Windows system32 directory or .so library (e.g.
 * C:\windows\system32\pkcs201n.dll).
 * 
 * The applet also asks the user to enter his PIN code for accessing the smart
 * card. If the smart card contains a certificate and a corresponding private
 * key, the signature of the file is calculated and is placed in the HTML form.
 * In addition to the calculated signature the certificate with its full
 * certification chain is extracted from the smart card and is placed in the
 * HTML form too. The digital signature is placed as Base64-encoded sequence of
 * bytes. The certification chain is placed as ASN.1 DER-encoded sequence of
 * bytes, additionally encoded in Base64. In case the smart card contains only
 * one certificate without its full certification chain, a chain consisting of
 * this single certificate is extracted and stored in the HTML form instead of a
 * full certification chain.
 * 
 * Digital signature algorithm used is SHA1withRSA. The length of the calculated
 * signature depends on the length of the private key on the smart card.
 * 
 * The applet should be able to access the local machine's file system for
 * reading and writing. Reading the local file system is required for the applet
 * to access the file that should be signed. Writing the local file system is
 * required for the applet to save its settings in the user's home directory.
 * Accessing the local file system is not possible by default, but if the applet
 * is digitally signed (with jarsigner), it runs with no security restrictions
 * and can do anything. This applet should be signed in order to run.
 * 
 * Java Plug-In version 1.5 or higher is required for accessing the PKCS#11
 * smart card functionality, so the applet will not run in any other Java
 * runtime environment.
 * 
 * This file is part of NakovDocumentSigner digital document signing framework
 * for Java-based Web applications: http://www.nakov.com/documents-signing/
 * 
 * Copyright (c) 2005 by Svetlin Nakov - http://www.nakov.com All rights
 * reserved. This code is freeware. It can be used for any purpose as long as
 * this copyright statement is not removed or modified.
 */
public class SmartCardSignerPanel extends JPanel {

	private static final String PKCS11_KEYSTORE_TYPE = "PKCS11";
	private static final String X509_CERTIFICATE_TYPE = "X.509";
	private static final String CERTIFICATION_CHAIN_ENCODING = "PkiPath";
	private static final String SUN_PKCS11_PROVIDER_CLASS = "sun.security.pkcs11.SunPKCS11";

	private Button mSignButton;
	
	private Document cdaDocument = null;
	private Document saveDocument = null;
	
	private StoreSignedConsentListener sscl = null;
	
	Certificate[] certChain = null;

	/**
	 * Initializes the applet - creates and initializes its graphical user
	 * interface. Actually the applet consists of a single button, that fills
	 * its all surface. The button's caption is taken from the applet parameter
	 * SIGN_BUTTON_CAPTION_PARAM.
	 */

	public SmartCardSignerPanel() {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			
		}

		GridBagLayout panel4Layout = new GridBagLayout();

		this.setLayout(panel4Layout);
		this.setBackground(Color.WHITE);
		
		
		String signButtonCaption = "Signieren";
		mSignButton = new Button(signButtonCaption);
		mSignButton.setLocation(0, 0);
		Dimension appletSize = this.getSize();
		mSignButton.setSize(appletSize);
		mSignButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				signSelectedFile();
			}
		});
		this.add(mSignButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						3, 3, 3, 3), 0, 0));
	}
	
	public void setDocument(Document cda, StoreSignedConsentListener sscl) {
		
		
		cdaDocument = cda;
		
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			Document doc = db.newDocument();


			Node copy = doc.importNode(cdaDocument.getDocumentElement(), true);
			doc.appendChild(copy);
			
			saveDocument = doc;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		if (sscl != null) {
			this.sscl = sscl;
		}
		
	}

	/**
	 * Signs the selected file. The file name comes from a field in the HTML
	 * document. The result consists of the calculated digital signature and
	 * certification chain, both placed in fields in the HTML document, encoded
	 * in Base64 format. The HTML document should contain only one HTML form.
	 * The name of the field, that contains the name of the file to be signed is
	 * obtained from FILE_NAME_FIELD_PARAM applet parameter. The names of the
	 * output fields for the signature and the certification chain are obtained
	 * from the parameters CERT_CHAIN_FIELD_PARAM and SIGNATURE_FIELD_PARAM. The
	 * user is asked to choose a PKCS#11 implementation library and a PIN code
	 * for accessing the smart card.
	 */

	@SuppressWarnings("static-access")
	private void signSelectedFile() {
		try {
		
			JOptionPane pane = new JOptionPane();
			// Perform the actual file signing
			boolean signingResult = signFile(cdaDocument);
			
		
			if (signingResult) {
				
				JOptionPane
				.showMessageDialog(
						this,
						"Die Signierung war erfolgreich",
						"Erfolg",
						JOptionPane.INFORMATION_MESSAGE);
				
				
				sscl.actionPerformed(new ActionEvent(X509_CERTIFICATE_TYPE, getX(), null));
				mSignButton.setEnabled(false);
				
				
			} else {
				pane
				.showMessageDialog(
						this,
						"Die Signierung war nicht erfolgreich",
						"Fehler",
						JOptionPane.ERROR_MESSAGE);
				
			
				
				try {
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					
					Document doc = db.newDocument();

					Node copy = doc.importNode(saveDocument.getDocumentElement(), true);
					doc.appendChild(copy);
					
					cdaDocument = doc;
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		} catch (DocumentSignException dse) {
			// Document signing failed. Display error message
			String errorMessage = dse.getMessage();
			JOptionPane.showMessageDialog(this, errorMessage);
		} catch (SecurityException se) {
			se.printStackTrace();
			JOptionPane
					.showMessageDialog(
							this,
							"Unable to access the local file system.\n"
									+ "This applet should be started with full security  permissions.\n"
									+ "Please accept to trust this applet when the Java  Plug-In asks you.");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Unexpected error: " + e.getMessage());
		}
	}

	/**
	 * Signs given local file. The certificate and private key to be used for
	 * signing come from the locally attached smart card. The user is requested
	 * to provide a PKCS#11 implementation library and the PIN code for
	 * accessing the smart card. @param aFileName the name of the file to be
	 * signed.
	 * 
	 * @return the digital signature of the given file and the certification
	 *         chain of the certificatie used for signing the file, both
	 *         Base64-encoded or null if the signing process is canceled by the
	 *         user.
	 * @throws DocumentSignException
	 *             when a problem arised during the singing process (e.g. smart
	 *             card access problem, invalid certificate, invalid PIN code,
	 *             etc.)
	 */

	private boolean signFile(Document cda)
			throws DocumentSignException {

		
		// Show a dialog for choosing PKCS#11 implementation library
		// and smart card PIN
		PKCS11LibraryFileAndPINCodeDialog pkcs11Dialog = new PKCS11LibraryFileAndPINCodeDialog();
		boolean dialogConfirmed;
		try {
			dialogConfirmed = pkcs11Dialog.run();
		} finally {
			pkcs11Dialog.dispose();
		}

		if (dialogConfirmed) {
			String oldButtonLabel = mSignButton.getLabel();
			mSignButton.setLabel("Working...");
			mSignButton.setEnabled(false);
			try {
				String pkcs11LibraryFileName = pkcs11Dialog
						.getLibraryFileName();
				String pinCode = pkcs11Dialog.getSmartCardPINCode();

				// Do the actual signing of the document with the
				// smart card
				boolean signingResult = signDocument(
						cda, pkcs11LibraryFileName, pinCode);
				return signingResult;
			} finally {
				mSignButton.setLabel(oldButtonLabel);
				mSignButton.setEnabled(true);
			}
		} else {
			return false;
		}
	}

	private boolean signDocument(
			Document cda, String aPkcs11LibraryFileName,
			String aPinCode) throws DocumentSignException {
		if (aPkcs11LibraryFileName.length() == 0) {
			String errorMessage = "It is mandatory to choose "
					+ "a PCKS#11 native implementation library for "
					+ "smart card (.dll or .so file)!";
			throw new DocumentSignException(errorMessage);
		}

		// Load the keystore from the smart card using the specified
		// PIN code
		KeyStore userKeyStore = null;
		try {
			userKeyStore = loadKeyStoreFromSmartCard(aPkcs11LibraryFileName,
					aPinCode);
		} catch (Exception ex) {
			String errorMessage = "Cannot read the keystore from "
					+ "the smart card.\n" + "Possible reasons:\n"
					+ " - The smart card reader in not connected.\n"
					+ " - The smart card is not inserted.\n"
					+ " - The PKCS#11 implementation library is invalid.\n"
					+ " - The PIN for the smart card is incorrect.\n"
					+ "Problem details: " + ex.getMessage();
			throw new DocumentSignException(errorMessage, ex);
		}

		// Get the private key and its certification chain from the
		// keystore
		PrivateKeyAndCertChain privateKeyAndCertChain = null;
		try {
			privateKeyAndCertChain = getPrivateKeyAndCertChain(userKeyStore);
		} catch (GeneralSecurityException gsex) {
			String errorMessage = "Cannot extract the private key "
					+ "and certificate from the smart card. Reason: "
					+ gsex.getMessage();
			throw new DocumentSignException(errorMessage, gsex);
		}

		// Check if the private key is available
		PrivateKey privateKey = privateKeyAndCertChain.mPrivateKey;
		if (privateKey == null) {
			String errorMessage = "Cannot find the private key on "
					+ "the smart card.";
			throw new DocumentSignException(errorMessage);
		}

		// Check if X.509 certification chain is available
		certChain = privateKeyAndCertChain.mCertificationChain;
		if (certChain == null) {
			String errorMessage = "Cannot find the certificate on "
					+ "the smart card.";
			throw new DocumentSignException(errorMessage);
		}

		
		boolean signingResult = false;
		try {
			signingResult = signDocument(cda, privateKey);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		
		return signingResult;
	}

	/**
	 * Loads the keystore from the smart card using its PKCS#11 implementation
	 * library and the Sun PKCS#11 security provider. The PIN code for accessing
	 * the smart card is required.
	 */

	private KeyStore loadKeyStoreFromSmartCard(String aPKCS11LibraryFileName,
			String aSmartCardPIN) throws GeneralSecurityException, IOException {
		// First configure the Sun PKCS#11 provider. It requires a
		// stream (or file) containing the configuration parameters -
		// "name" and "library".
		String pkcs11ConfigSettings = "name = SmartCard\n" + "library = "
				+ aPKCS11LibraryFileName +"\n"
				+ "slot = 1";
		byte[] pkcs11ConfigBytes = pkcs11ConfigSettings.getBytes();
		ByteArrayInputStream confStream = new ByteArrayInputStream(
				pkcs11ConfigBytes);

		// Instantiate the provider dynamically with Java reflection
		Provider pkcs11Provider = null;
		try {
			Class sunPkcs11Class = Class.forName(SUN_PKCS11_PROVIDER_CLASS);
			Constructor pkcs11Constr = sunPkcs11Class.getConstructor(java.io.InputStream.class);
			pkcs11Provider = (Provider) pkcs11Constr
					.newInstance(confStream);
			Security.addProvider(pkcs11Provider);
		} catch (Exception e) {
			throw new KeyStoreException("Can initialize "
					+ "Sun PKCS#11 security provider. Reason: "
					+ e.getCause().getMessage());
		}

		// Read the keystore form the smart card
		char[] pin = aSmartCardPIN.toCharArray();
		KeyStore keyStore = KeyStore.getInstance(PKCS11_KEYSTORE_TYPE);
		keyStore.load(null, pin);
		return keyStore;
	}

	/**
	 * @return private key and certification chain corresponding to it,
	 *         extracted from the given keystore. The keystore is considered to
	 *         have only one entry that contains both certification chain and
	 *         its corresponding private key. If the keystore has no entries, an
	 *         exception is thrown.
	 */

	private PrivateKeyAndCertChain getPrivateKeyAndCertChain(KeyStore aKeyStore)
			throws GeneralSecurityException {
		Enumeration aliasesEnum = aKeyStore.aliases();
		if (aliasesEnum.hasMoreElements()) {
			String alias = (String) aliasesEnum.nextElement();
			Certificate[] certificationChain = aKeyStore
					.getCertificateChain(alias);
			PrivateKey privateKey = (PrivateKey) aKeyStore.getKey(alias, null);
			PrivateKeyAndCertChain result = new PrivateKeyAndCertChain();
			result.mPrivateKey = privateKey;
			result.mCertificationChain = certificationChain;
			return result;
		} else {
			throw new KeyStoreException("The keystore is empty!");
		}
	}

	/**
	 * @return Base64-encoded ASN.1 DER representation of given X.509
	 *         certification chain.
	 */

	private String encodeX509CertChainToBase64(Certificate[] aCertificationChain)
			throws CertificateException {
		List certList = Arrays.asList(aCertificationChain);
		CertificateFactory certFactory = CertificateFactory
				.getInstance(X509_CERTIFICATE_TYPE);
		CertPath certPath = certFactory.generateCertPath(certList);
		byte[] certPathEncoded = certPath
				.getEncoded(CERTIFICATION_CHAIN_ENCODING);
		String base64encodedCertChain = Base64.encodeBytes(certPathEncoded);
		return base64encodedCertChain;
	}

	/**
	 * Reads the specified file into a byte array.
	 */

	private byte[] readFileInByteArray(String aFileName) throws IOException {
		File file = new File(aFileName);
		FileInputStream fileStream = new FileInputStream(file);
		try {
			int fileSize = (int) file.length();
			byte[] data = new byte[fileSize];
			int bytesRead = 0;
			while (bytesRead < fileSize) {
				bytesRead += fileStream.read(data, bytesRead, fileSize
						- bytesRead);
			}
			return data;
		} finally {
			fileStream.close();
		}
	}

	/**
	 * Signs given document with a given private key.
	 */

	private boolean signDocument(Document cda, PrivateKey aPrivateKey)
			throws GeneralSecurityException {
		
		XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM");
		
		Reference ref = factory.newReference("", factory.newDigestMethod(DigestMethod.SHA1, null), Collections.singletonList(factory.newTransform
		    (Transform.ENVELOPED, (TransformParameterSpec) null)), null, null);
		
		SignedInfo si = factory.newSignedInfo(factory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
		    factory.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(ref));
		
		KeyInfoFactory kif = factory.getKeyInfoFactory();
		
		X509Certificate signerCertificate = (X509Certificate) certChain[0];
		
		X509Data x509data = kif.newX509Data(Collections.nCopies(1, signerCertificate));
		
		KeyInfo ki = kif.newKeyInfo(Collections.nCopies(1, x509data));
		
		XMLSignature signature = factory.newXMLSignature(si, ki);
		
		DOMSignContext context = new DOMSignContext(aPrivateKey, cda.getDocumentElement());
		
	    try {
			signature.sign(context);
		} catch (MarshalException e) {
			e.printStackTrace();
		} catch (XMLSignatureException e) {
			e.printStackTrace();
		}
		
		
		return true;
	}

	/**
	 * Data structure that holds a pair of private key and certification chain
	 * corresponding to this private key.
	 */

	static class PrivateKeyAndCertChain {
		public PrivateKey mPrivateKey;
		public Certificate[] mCertificationChain;
	}

	/**
	 * Data structure that holds a pair of Base64-encoded certification chain
	 * and digital signature.
	 */

	static class CertificationChainAndSignatureBase64 {
		public String mCertificationChain = null;
		public String mSignature = null;
	}

	/**
	 * Exception class used for document signing errors.
	 */

	static class DocumentSignException extends Exception {
		public DocumentSignException(String aMessage) {
			super(aMessage);
		}

		public DocumentSignException(String aMessage, Throwable aCause) {
			super(aMessage, aCause);
		}
	}
}