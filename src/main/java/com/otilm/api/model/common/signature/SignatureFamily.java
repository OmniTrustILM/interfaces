package com.otilm.api.model.common.signature;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.connector.v2.ConnectorInterface;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * The signature family a signature belongs to. v1 ships the four AdES families; future content kinds arrive as
 * additional families rather than as a separate workflow type.
 */
@Schema(enumAsRef = true, description = "Signature family, naming the standard and document format a signature follows")
public enum SignatureFamily implements IPlatformEnum {

    PADES(Codes.PADES, "PAdES", "PDF Advanced Electronic Signatures, ETSI EN 319 142",
            ConnectorInterface.PADES_FORMATTING, EnumSet.of(DocumentTransferMode.INLINE),
            SignatureLevelNames.etsiBaseline("PAdES")),
    XADES(Codes.XADES, "XAdES", "XML Advanced Electronic Signatures, ETSI EN 319 132",
            ConnectorInterface.XADES_FORMATTING,
            EnumSet.of(DocumentTransferMode.INLINE, DocumentTransferMode.DIGEST_ONLY),
            SignatureLevelNames.etsiBaseline("XAdES")),
    CADES(Codes.CADES, "CAdES", "CMS Advanced Electronic Signatures, ETSI EN 319 122",
            ConnectorInterface.CADES_FORMATTING,
            EnumSet.of(DocumentTransferMode.INLINE, DocumentTransferMode.DIGEST_ONLY),
            SignatureLevelNames.etsiBaseline("CAdES")),
    JADES(Codes.JADES, "JAdES", "JSON Advanced Electronic Signatures, ETSI TS 119 182",
            ConnectorInterface.JADES_FORMATTING,
            EnumSet.of(DocumentTransferMode.INLINE, DocumentTransferMode.DIGEST_ONLY),
            SignatureLevelNames.etsiBaseline("JAdES"));

    public static class Codes {

        private Codes() {
        }

        public static final String PADES = "pades";
        public static final String XADES = "xades";
        public static final String CADES = "cades";
        public static final String JADES = "jades";
    }

    private static final SignatureFamily[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;
    private final ConnectorInterface connectorInterface;
    private final Set<DocumentTransferMode> transferModes;
    private final SignatureLevelNames levelNames;

    SignatureFamily(String code, String label, String description, ConnectorInterface connectorInterface,
            Set<DocumentTransferMode> transferModes, SignatureLevelNames levelNames) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.connectorInterface = connectorInterface;
        this.transferModes = transferModes;
        this.levelNames = levelNames;
    }

    /** A connector advertises this interface to serve the family. */
    public ConnectorInterface getConnectorInterface() {
        return this.connectorInterface;
    }

    /**
     * True when this family can sign a document sent in this mode. A family that writes its signature into the
     * document, as PAdES does, needs the document inline.
     */
    public boolean accepts(DocumentTransferMode mode) {
        return this.transferModes.contains(mode);
    }

    /** How this family writes the level, such as {@code PAdES-B-T} for {@link SignatureLevel#TIMESTAMPED}. */
    public String getLevelName(SignatureLevel level) {
        if (level == null) {
            throw new IllegalArgumentException("Signature level must be provided");
        }
        return this.levelNames.nameOf(level);
    }

    @Override
    @JsonValue
    public String getCode() {
        return this.code;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @JsonCreator
    public static SignatureFamily findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(
                        () -> new ValidationException(ValidationError.create("Unknown signature family {}", code)));
    }
}
