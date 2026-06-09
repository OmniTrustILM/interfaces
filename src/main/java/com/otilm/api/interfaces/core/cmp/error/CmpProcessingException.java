package com.otilm.api.interfaces.core.cmp.error;

import com.otilm.api.interfaces.core.cmp.PkiMessageError;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIFailureInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This exception is created if a any processing problem (scope: cmp request/response)
 * is raised.
 */
public class CmpProcessingException extends CmpBaseException {

    private static final Logger LOG = LoggerFactory.getLogger(CmpProcessingException.class.getName());
    protected ImplFailureInfo implFailureInfo;

    /**
     * @param failureInfo protocol-based error by rfc4120 (type as ${@link PKIFailureInfo})
     * @param implFailureInfo implementation-based error (platform scope, see ${@link ImplFailureInfo})
     */
    public CmpProcessingException(int failureInfo, ImplFailureInfo implFailureInfo) {
        this(new DEROctetString("n/a".getBytes()), failureInfo,implFailureInfo, null);
    }

    /**
     * @param failureInfo protocol-based error by rfc4120 (type as ${@link PKIFailureInfo})
     * @param implFailureInfo implementation-based error (platform scope, see ${@link ImplFailureInfo})
     * @param ex failure reason
     */
    public CmpProcessingException(PKIFailureInfo failureInfo, ImplFailureInfo implFailureInfo, Exception ex) {
        this(new DEROctetString("n/a".getBytes()), failureInfo.intValue(),implFailureInfo, ex);
    }

    /**
     * @param failureInfo protocol-based error by rfc4120 (type as ${@link PKIFailureInfo})
     * @param implFailureInfo implementation-based error (platform scope, see ${@link ImplFailureInfo})
     */
    public CmpProcessingException(ASN1OctetString tid, int failureInfo, ImplFailureInfo implFailureInfo) {
        this(tid, failureInfo, implFailureInfo, null);
    }

    /**
     * @param failureInfo protocol-based error by rfc4120 (type as integer, see ${@link PKIFailureInfo})
     * @param errorDetails string description of error
     */
    public CmpProcessingException(int failureInfo, String errorDetails) {
        super(new DEROctetString("n/a".getBytes()), failureInfo, errorDetails, null);
    }

    /**
     * @param failureInfo protocol-based error by rfc4120 (type as integer, see ${@link PKIFailureInfo})
     * @param errorDetails string description of error
     */
    public CmpProcessingException(ASN1OctetString tid, int failureInfo, String errorDetails) {
        super(tid, failureInfo,errorDetails, null);
    }

    /**
     * @param failureInfo protocol-based error by rfc4120 (type as integer, see ${@link PKIFailureInfo})
     * @param errorDetails string description of error
     * @param ex failure reason
     */
    public CmpProcessingException(ASN1OctetString tid, int failureInfo, String errorDetails, Exception ex) {
        super(tid, failureInfo,errorDetails, ex);
    }

    /**
     * @param failureInfo protocol-based error by rfc4120 (type as integer, see ${@link PKIFailureInfo})
     * @param implFailureInfo implementation-based error (platform scope, see ${@link ImplFailureInfo})
     * @param ex failure reason
     */
    public CmpProcessingException(ASN1OctetString tid, int failureInfo, ImplFailureInfo implFailureInfo, Exception ex) {
        super(tid, failureInfo, implFailureInfo.name() + "("+implFailureInfo.getCode()+"): "+ implFailureInfo.getDescription(), ex);
        this.failureInfo = failureInfo;
        this.implFailureInfo = implFailureInfo;
    }

    public ImplFailureInfo getImplFailureInfo() {
        return implFailureInfo;
    }

    public int getFailureInfo() {
        return failureInfo;
    }

    /**
     * @return help to build {@link PKIBody} for response flow
     */
    public PKIBody toPKIBody() {
        if(implFailureInfo != null) {
            return PkiMessageError.generateBody(failureInfo, implFailureInfo);
        }
        return PkiMessageError.generateBody(failureInfo, errorDetails);
    }
}
