package com.otilm.api.clients.signing.contentsigning;

import com.otilm.api.model.connector.signatures.contentsigning.common.ContentSigningFormattingOperation;

/**
 * The content-signing formatting connector routes, defined once so the REST and MQ clients cannot drift apart.
 */
@SuppressWarnings("java:S1075") // contract paths, not configurable URIs
public final class ContentSigningFormattingPaths {

    private static final String ATTRIBUTES_SEGMENT = "/attributes";

    public static final String BASE = "/v1/signatureProvider/contentSigning";

    public static final String DECLARATION = BASE + "/declaration";

    /** The status and cancel companions of {@code extendToLevel}, which are not ladder operations of their own. */
    public static final String EXTEND_TO_LEVEL_STATUS = BASE + "/extendToLevel/status";

    public static final String EXTEND_TO_LEVEL_CANCEL = BASE + "/extendToLevel/cancel";

    private ContentSigningFormattingPaths() {
    }

    /**
     * The route an operation is posted to. Several operations share a Java signature, so a hand-written route naming
     * the wrong one would compile and return the expected type.
     */
    public static String operation(ContentSigningFormattingOperation operation) {
        return BASE + "/" + operation.getCode();
    }

    /** The attribute schema route for an operation, always that operation's own route plus {@code /attributes}. */
    public static String attributes(ContentSigningFormattingOperation operation) {
        return operation(operation) + ATTRIBUTES_SEGMENT;
    }
}
