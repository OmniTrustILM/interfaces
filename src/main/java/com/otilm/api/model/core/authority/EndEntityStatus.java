package com.otilm.api.model.core.authority;

import java.util.Arrays;

public enum EndEntityStatus {
    /** New user **/
    NEW(10),

    /** Generation of user certificate failed **/
    FAILED(11),

    /** User has been initialized **/
    INITIALIZED(20),

    /** Generation of user certificate in process **/
    IN_PROCESS(30),

    /** A certificate has been generated for the user **/
    GENERATED(40),

    /** The user has been revoked and should not have any more certificates issued **/
    REVOKED(50),

    /** The user is old and archived **/
    HISTORICAL(60),

    /** The user is should use key recovery functions in next certificate generation. **/
    KEY_RECOVERY (70),

    /** the operation is waiting to be approved before execution **/
    WAITING_FOR_ADD_APPROVAL(80),
    ;

    private final int code;

    EndEntityStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static EndEntityStatus fromCode(int code) {
        return Arrays.stream(values())
                .filter(e -> e.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Unsupported type %s.", code)));
    }
}
