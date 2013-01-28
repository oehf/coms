//
// Generated stub from file:/D:/Worspaces/OSS-Demonstrator/Consent_Manager/cm_trunk/ipf-core/src/main/groovy/org/openehealth/coms/cms/ipf_core/routes/mdm/transmogrifier/MDM_T91_MDM_T02Transmogrifier.groovy
//

package org.openehealth.coms.cms.ipf_core.routes.mdm.transmogrifier;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import groovy.util.XmlSlurper;
import java.io.File;
import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import net.iharder.Base64;
import org.openehealth.ipf.commons.core.modules.api.Transmogrifier;
import org.openehealth.ipf.modules.hl7.AckTypeCode;
import org.openehealth.ipf.modules.hl7.HL7v2Exception;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.openehealth.coms.cms.ipf_core.IPFCore;

/**
 * MDM T91 MDM T02 Transmogrifier
 *
 * @author nyueksekogul
 */
public class MDM_T91_MDM_T02Transmogrifier
    extends java.lang.Object
    implements groovy.lang.GroovyObject, Transmogrifier
{
    /**
     * Saves the hash into a Hash-document that has the file name as the PID
     *
     * @param msg
     * @param params
     */
    public java.lang.Object zap(java.lang.Object msg, java.lang.Object[] params) {
        throw new InternalError("Stubbed method");
    }

    /**
     * Returns true if the CDA document is successfully find
     *
     * @param path
     * @param filename
     * @return true
     * @author mbirkle
     */
    private boolean searchFile(java.lang.String path, java.lang.String filename) {
        throw new InternalError("Stubbed method");
    }

    /**
     * Returns true if the CDA document not exist and it is successfully written into file
     *
     * @param cda
     * @param filename
     * @return true
     * @author mbirkle
     */
    private boolean writeToFile(java.lang.String cda, java.lang.String filename) {
        throw new InternalError("Stubbed method");
    }

    public groovy.lang.MetaClass getMetaClass() {
        throw new InternalError("Stubbed method");
    }

    public void setMetaClass(groovy.lang.MetaClass metaClass) {
        throw new InternalError("Stubbed method");
    }

    public java.lang.Object invokeMethod(java.lang.String name, java.lang.Object args) {
        throw new InternalError("Stubbed method");
    }

    public java.lang.Object getProperty(java.lang.String name) {
        throw new InternalError("Stubbed method");
    }

    public void setProperty(java.lang.String name, java.lang.Object value) {
        throw new InternalError("Stubbed method");
    }
}
