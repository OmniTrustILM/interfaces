package com.otilm.api.model.client.connector.v2;

import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(enumAsRef = true)
public enum ConnectorVersion implements IPlatformEnum {

    V1(Codes.V1, 1),
    V2(Codes.V2, 2);

    private final String code;

    @Getter
    private final int version;

    ConnectorVersion(String code, int version) {
        this.code = code;
        this.version = version;
    }

    @Override
    @JsonValue
    public String getCode() {
        return code;
    }

    @Override
    public String getLabel() {
        return code;
    }

    @Override
    public String getDescription() {
        return null;
    }

    public static class Codes {
        private Codes(){}
        public static final String V1 = "v1";
        public static final String V2 = "v2";
    }
}
