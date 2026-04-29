package com.otilm.api.model.core.acme;

public enum Problem {

    ACCOUNT_DOES_NOT_EXIST("accountDoesNotExist", "Account not found", "The request specified an account that does not exist"),
    ALREADY_REVOKED("alreadyRevoked", "Certificate already revoked", "The request specified a certificate to be revoked that has already been revoked"),
    BAD_CSR("badCsr", "CSR Invalid", "Provided CSR is unacceptable (e.g., due to a short key"),
    BAD_NONCE("badNonce", "Bad Nonce", "The client sent an unacceptable anti-replay nonce"),
    BAD_PUBLIC_KEY("badPublicKey", "Bad Public Key", "The JWS was signed by a public key the server does not support"),
    BAD_REVOCATION_REASON("badRevocationReason", "Bad Revocation Reason", "The revocation reason provided is not supported by the server"),
    BAD_SIGNATURE_ALGORITHM("badSignatureAlgorithm", "Bad Signature Algorithm", "The JWS was signed with an algorithm the server does not support"),
    CAA("caa", "Certificate Authority Authorization Forbidden", "Certification Authority Authorization (CAA) records forbid the CA from  issuing a certificate"),
    COMPOUND("compound", "Multiple Errors", "Specific error conditions are indicated in the \"subproblem\" array"),
    CONNECTION("connection", "Connection Error", "The server could not connect to the validation target"),
    DNS("dns", "DNS Validation Error", "There was a problem with a DNS query during identifier validation"),
    EXTERNAL_ACCOUNT_REQUIRED("externalAccountRequired", "External Account Required", "The request must incluide a value for \"externalAccountBinding\" field"),
    INCORRECT_RESPONSE("incorrectResponse", "Validation Response Incorrect", "Response received didn't match the challenge's requirements"),
    INVALID_CONTACT("invalidContact", "Invalid Contact", "A contact URL for the account was invalid"),
    MALFORMED("malformed", "Message Malformed", "the request message was malformed"),
    ORDER_NOT_READY("orderNotReady", "Order Not Ready", "The request attempted to finalize an order that is not ready to be finalized"),
    RATE_LIMITED("rateLimited", "Request Rate Limited", "The request exceeds a rate limit"),
    REJECTED_IDENTIFIER("rejectedIdentifier", "Identifier Rejected", "The server will not issue certificate for the identifier"),
    SERVER_INTERNAL("serverInternal", "Internal Server Error", "The server experienced an internal error"),
    TLS("tls", "TLS Error", "The server experienced a TLS error during validation"),
    UNAUTHORIZED("unauthorized", "Unauthorized", "The server lacks sufficient authorization"),
    UNSUPPORTED_CONTACT("unsupportedContact", "Unsupported Contact", "A contact for an account used an unsupported protocol scheme"),
    UNSUPPORTED_IDENTIFIER("unsupportedIdentifier", "Unsupported Identifier", "An identifier is of an unsupported type"),
    USER_ACTION_REQUIRED("userActionRequired", "User Action Required", "Visit the \"instance\" URL and take actions specified there"),
    ARCHIVED("archived", "Archived certificate", "Certificate is archived");

    private String type;
    private String title;
    private String detail;

    Problem(String type, String title, String detail) {
        this.type = "urn:ietf:params:acme:error:" + type;
        this.title = title;
        this.detail = detail;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
