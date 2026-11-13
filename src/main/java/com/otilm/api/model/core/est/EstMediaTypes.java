package com.otilm.api.model.core.est;

/**
 * Media types of EST request and response bodies (RFC 7030 §4, RFC 8951).
 */
public final class EstMediaTypes {

    public static final String PKCS10 = "application/pkcs10";
    public static final String PKCS7_MIME = "application/pkcs7-mime";
    public static final String PKCS7_CERTS_ONLY = "application/pkcs7-mime; smime-type=certs-only";
    public static final String CSR_ATTRS = "application/csrattrs";
    public static final String PKCS8 = "application/pkcs8";
    public static final String MULTIPART_MIXED = "multipart/mixed";

    private EstMediaTypes() {
    }
}
