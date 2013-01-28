//
// Generated stub from file:/D:/Worspaces/OSS-Demonstrator/Consent_Manager/cm_trunk/ipf-core/src/main/groovy/org/openehealth/coms/cms/ipf_core/routes/qbp/transmogrifier/QBP_Q91_QBP_Q11Transmogrifier.groovy
//

package org.openehealth.coms.cms.ipf_core.routes.qbp.transmogrifier;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.io.File;
import java.text.SimpleDateFormat;
import net.iharder.Base64;
import org.openehealth.ipf.commons.core.modules.api.Transmogrifier;
import org.openehealth.ipf.modules.hl7.AckTypeCode;
import ca.uhn.hl7v2.model.v251.message.QBP_Q11;
import ca.uhn.hl7v2.model.v251.segment.QPD;
import ca.uhn.hl7v2.parser.GenericParser;
import org.openehealth.coms.cms.ipf_core.IPFCore;
import org.openehealth.coms.cms.util.FileUtilities;

/**
 * QBP Q91 Transmogrifier class handles with QBP Q91 messages
 * and retrieves the current consent document. 
 * Search criterion is patient id.
 *
 * @author nkarakus
 */
public class QBP_Q91_QBP_Q11Transmogrifier
    extends java.lang.Object
    implements groovy.lang.GroovyObject, Transmogrifier
{
    private SimpleDateFormat hl7DateTime = null;

    private GenericParser p = null;

    /**
     * @param msg
     * @param params
     * @return
     */
    public java.lang.Object zap(java.lang.Object msg, java.lang.Object[] params) {
        throw new InternalError("Stubbed method");
    }

    /**
     * Returns the date from the filename.
     *
     * @param filename
     * @return
     * @author mbirkle
     */
    private java.lang.String getDateFromFilename(java.lang.String filename) {
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
