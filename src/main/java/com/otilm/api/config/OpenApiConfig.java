package com.otilm.api.config;

public class OpenApiConfig {
        public static final String NO_SECURITY_SCHEME_NAME = "NoAuth";
        public static final String CERTIFICATE_SECURITY_SCHEME_NAME = "CertificateAuth";
        public static final String CERTIFICATE_TLS_SECURITY_SCHEME_NAME = "CertificateTLSAuth";
        public static final String CONNECTOR_API_KEY_SECURITY_SCHEME_NAME = "ConnectorAPIKeyAuth";
        public static final String BASIC_SECURITY_SCHEME_NAME = "BasicAuth";
        public static final String BEARER_JWT_SECURITY_SCHEME_NAME = "BearerJWTAuth";
        public static final String SESSION_SECURITY_SCHEME_NAME = "SessionAuth";

        private OpenApiConfig() {}
}
