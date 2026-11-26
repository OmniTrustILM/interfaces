package com.otilm.api.model.client.signing.nativesigning;

import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Schema(name = "SignRawResponse", description = "Signatures of every request item, with what verifies them")
public class SignRawResponseDto {

    @Schema(description = "Signature algorithm every signature of the request was made with",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private SignatureAlgorithm signatureAlgorithm;

    @ToString.Exclude
    @ArraySchema(arraySchema = @Schema(
            description = "Signing certificate and its chain, signing certificate first; each a DER-encoded X.509 "
                    + "certificate, base64-encoded in JSON.",
            requiredMode = Schema.RequiredMode.REQUIRED), schema = @Schema(format = "byte"), minItems = 1)
    private List<byte[]> signerCertificateChain;

    @ArraySchema(arraySchema = @Schema(description = "One result per request item, in request order",
            requiredMode = Schema.RequiredMode.REQUIRED), minItems = 1)
    private List<SignRawResultDto> results;
}
