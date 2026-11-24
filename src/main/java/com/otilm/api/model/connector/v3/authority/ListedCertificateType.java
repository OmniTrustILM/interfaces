package com.otilm.api.model.connector.v3.authority;

import lombok.Getter;
import lombok.Setter;

/**
 * An entry of the certificate types a connector lists, as it arrives. The type is kept as its code, so that a code this
 * version does not know can be left out instead of failing the whole response.
 */
@Getter
@Setter
public class ListedCertificateType {

    private String certificateType;

    public static ListedCertificateType of(String certificateType) {
        ListedCertificateType listed = new ListedCertificateType();
        listed.setCertificateType(certificateType);
        return listed;
    }
}
