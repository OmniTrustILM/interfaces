package com.otilm.api.interfaces.core.cmp.error;

import com.otilm.api.exception.PlatformException;
import com.otilm.api.interfaces.core.cmp.PkiMessageError;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIFailureInfo;

/**
 * Exception for error handling for cmp failure scenarios.
 * <p>
 * citation (from RFC 4210): This message MAY be generated at any time during a PKI transaction.
 * <p>
 * Building one logs nothing: the code that handles the failure logs it, and only that code knows whether the cause
 * carries text a connector wrote, which can repeat secrets the request carried.
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc4210#section-5.3.21">...</a>
 */
public class CmpBaseException extends Exception implements PlatformException {

    protected int failureInfo;
    protected String errorDetails;

    protected CmpBaseException(Exception ex) {
        super(ex);
        if (ex instanceof CmpBaseException) {
            this.failureInfo = ((CmpBaseException) ex).failureInfo;
            this.errorDetails = ((CmpBaseException) ex).errorDetails;
        } else {
            this.failureInfo = PKIFailureInfo.systemFailure;
            this.errorDetails = ex.getLocalizedMessage();
        }
    }

    /**
     * @param failureInfo cmp failureInfo for CMP error message
     * @param errorDetails description of details related to the error
     * @param ex the underlying exception
     */
    protected CmpBaseException(ASN1OctetString tid, int failureInfo, String errorDetails, Throwable ex) {
        super(ex == null || ex.getMessage() == null
                ? "(TID=" + (tid == null ? "n/a" : tid.toString()) + ") " + errorDetails
                : "(TID=" + (tid == null ? "n/a" : tid.toString()) + ") " + ex.getMessage(),
                ex == null || ex.getCause() == null ? ex : ex.getCause());
        if (ex instanceof CmpBaseException) {
            this.failureInfo = ((CmpBaseException) ex).failureInfo;
            this.errorDetails = ((CmpBaseException) ex).errorDetails;
        } else {
            this.failureInfo = failureInfo;
            this.errorDetails = errorDetails;
        }
    }

    /**
     * @return help to build {@link PKIBody} for response flow
     */
    public PKIBody toPKIBody() {
        return PkiMessageError.generateBody(failureInfo, errorDetails);
    }

    /**
     * The details the {@link PKIBody} of {@link #toPKIBody()} carries, as the code raising the exception wrote them.
     * {@link #getMessage()} repeats its cause's message instead, which a connector may have written.
     *
     * @return the details of the error
     */
    public String getErrorDetails() {
        return errorDetails;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [failureInfo=" + failureInfo + ", errorDetails=" + errorDetails
                + "]";
    }
}
