package com.otilm.api.model.core.acme;

import io.swagger.v3.oas.annotations.media.Schema;

public class KeyRollover {
    @Schema(description = "Account URL", examples = {"https://example.com/v1/protocols/acme/acct/evOfKhNU60wg"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String account;
    @Schema(description = "Old key of the Account", examples = {"<Account old key content>"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldKey;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getOldKey() {
        return oldKey;
    }

    public void setOldKey(String oldKey) {
        this.oldKey = oldKey;
    }
}
