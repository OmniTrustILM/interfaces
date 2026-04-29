package com.otilm.api.model.core.acme;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * Set of properties to represent the new Account object from ACME.
 */
public class NewAccountRequest {

    /**
     * Contact field in the ACME Account. This contains the array of URLs that the server can use
     * to contact the client for issues related to this Account.
     * This is an optional parameter.
     */
    @Schema(description = "List of contacts for the Account. Required if the required flag is set in ACME Profile",
            example = "[\"mailto:someadmin@domain.com\"]")
    private List<String> contact;

    /**
     * This field represents the status of the Terms of Service agreed or not. This field will be included in a
     * new Account request indicating the client has agreed to the Terms of Service. This field cannot be updated by the
     * client.
     * This is a non-mandatory field.
     */
    @Schema(description = "Terms of Service agreed flag. true = Yes, false = No. Required if the required flag is set in ACME Profile",
            example = "false")
    private boolean termsOfServiceAgreed;

    /**
     * This field represents if the server should return only the Account that is already available. If the value is set
     * as true and the server cannot find any data, then the server can return error. This field will be included in a
     * new Account request indicating the client has agreed to the Terms of Service. This field cannot be updated by the
     * client.
     * This is a non-mandatory field.
     */
    @Schema(description = "Return existing Account only flag", defaultValue = "false", example = "false")
    private boolean onlyReturnExisting;


    public List<String> getContact() {
        return contact;
    }

    public void setContact(List<String> contact) {
        this.contact = contact;
    }

    public boolean isTermsOfServiceAgreed() {
        return termsOfServiceAgreed;
    }

    public void setTermsOfServiceAgreed(boolean termsOfServiceAgreed) {
        this.termsOfServiceAgreed = termsOfServiceAgreed;
    }

    public boolean isOnlyReturnExisting() {
        return onlyReturnExisting;
    }

    public void setOnlyReturnExisting(boolean onlyReturnExisting) {
        this.onlyReturnExisting = onlyReturnExisting;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("contact", contact)
                .append("termsOfServiceAgreed", termsOfServiceAgreed)
                .append("onlyReturnExisting", onlyReturnExisting)
                .toString();
    }
}
