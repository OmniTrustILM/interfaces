package com.otilm.api.interfaces.connector.signing.contentsigning;

import com.otilm.api.interfaces.connector.common.v2.AuthProtectedConnectorController;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.error.ProblemDetailExtended;
import com.otilm.api.model.connector.signatures.contentsigning.common.ComputeDtbsRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ComputeDtbsResponseDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ContentSigningFormattingOperation;
import com.otilm.api.model.connector.signatures.contentsigning.common.EmbedSignatureValueRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.EmbedTimestampRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ExtendOperationScopedRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ExtendOperationStatusResponseDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ExtendToLevelRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ExtendToLevelResponseDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.SignedDocumentRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.SignedDocumentResponseDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.TimestampImprintResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/v1/signatureProvider/contentSigning")
@Tag(name = "Content Signing Formatting",
        description = "Formatting of content signatures. v1 serves four AdES families: PAdES, XAdES, CAdES and JAdES. "
                + "Future content kinds arrive as additional families on this same contract. Every content-signing "
                + "formatting connector implements this contract. The family interface codes it advertises on /v2/info "
                + "say which families it is entitled to serve. It MUST reject any other family with 422.\n\nThe seven "
                + "operations form three compute/embed pairs plus the fused extendToLevel. A pair exists wherever the "
                + "connector needs something only the platform can produce. That is a signature value, because private "
                + "keys never leave the platform. It is also a timestamp token, because timestamps are issued "
                + "platform-side. Validation material is deliberately not such an artifact. The connector fetches it "
                + "itself, inside extendToLevel, where the document already is.\n\nThe connector MUST be stateless. It "
                + "holds nothing between operations. Every request carries everything that operation needs. The only "
                + "value that crosses from a compute step to its embed step is formattingContext. The connector "
                + "produces it, and the platform replays it verbatim without inspecting or storing it. The connector "
                + "MUST NOT run timers or background jobs of its own. Any periodic work is a call the platform "
                + "makes.\n\nThe six pure-compute operations MUST be deterministic. An identical request MUST produce "
                + "a bit-identical result across connector replicas and versions. The platform relies on this: it "
                + "resumes an interrupted signing operation by replaying the operation sequence, rather than by "
                + "storing what each step returned. extendToLevel is the exception. It fetches, so its retry semantics "
                + "are refetch, not replay.\n\nThe connector MUST NOT read its own clock for anything that enters "
                + "signature content. The platform supplies signingTime, and the connector uses that value.\n\nEvery "
                + "operation has an attribute schema that configures it, read from that operation's route plus "
                + "/attributes. The platform collects those schemas when a Signing Profile is configured, and replays "
                + "the operator's chosen values in formattingAttributes on every call.\n\nA Signing Profile holds one "
                + "flat set of attribute values, not one set per operation. The platform merges the schemas of every "
                + "operation that profile can reach into a single set, keyed by attribute name. It sends that whole "
                + "set to every operation. Which operations are reachable depends on the level the profile is "
                + "configured up to.\n\nTwo consequences bind a connector. First, an operation MUST ignore any "
                + "attribute name its own schema does not declare, rather than rejecting it: that name belongs to a "
                + "sibling operation. The platform has already refused, at profile save, any name no reachable "
                + "operation declares. Second, an attribute declared by more than one operation MUST carry an "
                + "identical definition in every schema that lists it. The operator configures it once, and both "
                + "operations receive that one value. For the compute/embed twins that is the point rather than a "
                + "limitation. An imprint computed under one digest algorithm must be embedded expecting the same one, "
                + "and a single shared attribute makes that true by construction.")
public interface ContentSigningFormattingController extends AuthProtectedConnectorController {

    @Operation(summary = "List the attributes configuring an operation",
            description = "Returns the attribute schema configuring how this connector performs the named operation. A "
                    + "value the operator fixes once per Signing Profile belongs here. A value that varies per signing "
                    + "request is a field on that operation's request instead. The platform collects these schemas "
                    + "when a Signing Profile is configured. It replays the operator's chosen values in "
                    + "formattingAttributes on every call to that operation.",
            operationId = "listContentSigningFormattingAttributes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Attribute schema retrieved"),
            @ApiResponse(responseCode = "400",
                    description = "The path segment names no content signing formatting operation (errorCode BAD_REQUEST)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @GetMapping(path = "/{operation}/attributes", produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listFormattingAttributes(@Parameter(
            description = "Operation, identified by its wire code (e.g. \"computeDtbs\")") @PathVariable ContentSigningFormattingOperation operation);

    @Operation(summary = "Compute data-to-be-signed bytes",
            description = "Builds the signature structure for the requested family and returns the bytes to be signed. "
                    + "It also returns the formattingContext needed to complete the signature once the signature value "
                    + "comes back. This is the only operation whose request varies by family.\n\n**Signing time.** "
                    + "The connector MUST put the supplied signingTime into the signature, not its own "
                    + "clock.\n\n**Signature algorithm.** The platform also supplies signatureAlgorithm, from the "
                    + "signer it resolved for this request. Only the platform sees both that key and the padding "
                    + "scheme its provider will apply. The connector MUST build the bytes for exactly that algorithm, "
                    + "and MUST NOT substitute one of its own.\n\n**Document digest.** The connector MUST embed the "
                    + "digest the request commits to. An inline transfer carries the document, and the connector "
                    + "digests it. A digestOnly transfer carries that digest already. The platform pins both to the "
                    + "digest signatureAlgorithm commits to, and checks that before it calls, so an algorithm whose "
                    + "paired digest is not a DigestAlgorithm value is never sent here. Either way the connector "
                    + "MUST echo that digest in documentDigest. The platform compares that echo against the digest "
                    + "the user authorized before it releases the signing key.",
            operationId = "computeDtbs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Data-to-be-signed bytes computed"),
            @ApiResponse(responseCode = "400",
                    description = "Request body cannot be read (errorCode BAD_REQUEST). This covers a family that is "
                            + "absent, null, or outside the published signature families. It also covers a "
                            + "transferMode that is absent, or is neither inline nor digestOnly. An entitlement "
                            + "refusal is the 422 below instead.",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "413",
                    description = "Document exceeds the size this connector accepts (errorCode DOCUMENT_TOO_LARGE)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = "- `DOCUMENT_MALFORMED` — the document cannot be parsed as the declared format.\n- "
                            + "`DOCUMENT_SIGNING_FORBIDDEN` — the document forbids the requested signature, such as a "
                            + "certification of a document already signed.\n- "
                            + "`PARAMETER_UNSUPPORTED` — the requested family or a supplied parameter is unsupported "
                            + "by the connector.\n- "
                            + "`SIGNATURE_PLACEMENT_REJECTED` — the document cannot hold the visible signature at the "
                            + "requested place.\n- "
                            + "`VALIDATION_FAILED` — the body is readable but breaks a field rule, such as an absent "
                            + "document or a digest transfer missing its algorithm.",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/computeDtbs", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ComputeDtbsResponseDto computeDtbs(@Valid @RequestBody ComputeDtbsRequestDto request);

    @Operation(summary = "Embed the signature value",
            description = "Completes a SIGNED-level signature by embedding the signature value the platform's signer "
                    + "produced over the data-to-be-signed bytes. The formattingContext returned by computeDtbs is "
                    + "replayed here unchanged. The connector MUST NOT expect to have kept any state of its own "
                    + "between the two calls.\n\n**Signature algorithm.** signatureAlgorithm is the same value the "
                    + "platform gave computeDtbs. The connector MUST embed the value as a signature under that "
                    + "algorithm, and MUST refuse a request whose algorithm disagrees with the one its "
                    + "formattingContext committed to, answering 422 with errorCode CONTEXT_MISMATCH. That turns "
                    + "compute-to-embed drift into one diagnosable error rather than a structurally valid signature "
                    + "no validator accepts.",
            operationId = "embedSignatureValue")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Signature value embedded; the signed document is at SIGNED level"),
            @ApiResponse(responseCode = "400",
                    description = "Request body cannot be read (errorCode BAD_REQUEST), including a family value outside the "
                            + "published signature families",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "413",
                    description = "Replayed formattingContext exceeds the size this connector accepts (errorCode DOCUMENT_TOO_LARGE)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = "- `DOCUMENT_MALFORMED` — the replayed formattingContext cannot be interpreted.\n- "
                            + "`PARAMETER_UNSUPPORTED` — the requested family or a supplied parameter is unsupported "
                            + "by the connector.\n- `CONTEXT_MISMATCH` — the request contradicts the formattingContext "
                            + "it replays, such as a signatureAlgorithm that disagrees with the one that context "
                            + "committed to.\n- `VALIDATION_FAILED` — the body is readable but breaks a field rule.",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/embedSignatureValue", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    SignedDocumentResponseDto embedSignatureValue(@Valid @RequestBody EmbedSignatureValueRequestDto request);

    @Operation(summary = "Compute the signature timestamp imprint",
            description = "Returns the message imprint over which the platform is to obtain a signature timestamp, "
                    + "together with the digest algorithm that produced it. The platform issues the token itself and brings "
                    + "it back to embedSignatureTimestamp.",
            operationId = "computeSignatureTimestampImprint")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imprint computed"),
            @ApiResponse(responseCode = "400",
                    description = "Request body cannot be read (errorCode BAD_REQUEST), including a family value outside the "
                            + "published signature families",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "413",
                    description = "Signed document exceeds the size this connector accepts (errorCode DOCUMENT_TOO_LARGE)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = "- `SIGNATURE_NOT_FOUND` — the document carries no signature to timestamp.\n- "
                            + "`DOCUMENT_MALFORMED` — the document cannot be parsed as the declared format.\n- "
                            + "`PARAMETER_UNSUPPORTED` — the requested family or a supplied parameter is unsupported "
                            + "by the connector.\n- `VALIDATION_FAILED` — the body is readable but breaks a field "
                            + "rule.",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/computeSignatureTimestampImprint", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    TimestampImprintResponseDto computeSignatureTimestampImprint(@Valid @RequestBody SignedDocumentRequestDto request);

    @Operation(summary = "Embed the signature timestamp",
            description = "Embeds the timestamp token the platform obtained over the imprint from "
                    + "computeSignatureTimestampImprint, raising the signature to TIMESTAMPED level.",
            operationId = "embedSignatureTimestamp")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Timestamp embedded; the signed document is at TIMESTAMPED level"),
            @ApiResponse(responseCode = "400",
                    description = "Request body cannot be read (errorCode BAD_REQUEST), including a family value outside the "
                            + "published signature families",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "413",
                    description = "Signed document exceeds the size this connector accepts (errorCode DOCUMENT_TOO_LARGE)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = "- `SIGNATURE_NOT_FOUND` — the document carries no signature to attach the timestamp "
                            + "to.\n- `DOCUMENT_MALFORMED` — the document or token cannot be parsed.\n- "
                            + "`PARAMETER_UNSUPPORTED` — the requested family or a supplied parameter is unsupported "
                            + "by the connector.\n- `VALIDATION_FAILED` — the body is readable but breaks a field "
                            + "rule.",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/embedSignatureTimestamp", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    SignedDocumentResponseDto embedSignatureTimestamp(@Valid @RequestBody EmbedTimestampRequestDto request);

    @Operation(summary = "Compute the archive timestamp imprint",
            description = "Returns the message imprint over which the platform is to obtain an archive timestamp. "
                    + "The archive timestamp covers the signature together with its embedded validation material, which is "
                    + "why this step follows extendToLevel rather than preceding it.",
            operationId = "computeArchiveTimestampImprint")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imprint computed"),
            @ApiResponse(responseCode = "400",
                    description = "Request body cannot be read (errorCode BAD_REQUEST), including a family value outside the "
                            + "published signature families",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "413",
                    description = "Signed document exceeds the size this connector accepts (errorCode DOCUMENT_TOO_LARGE)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = "- `SIGNATURE_NOT_FOUND` — the document carries no signature to timestamp.\n- "
                            + "`DOCUMENT_MALFORMED` — the document cannot be parsed as the declared format.\n- "
                            + "`PARAMETER_UNSUPPORTED` — the requested family or a supplied parameter is unsupported "
                            + "by the connector.\n- `VALIDATION_FAILED` — the body is readable but breaks a field "
                            + "rule.",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/computeArchiveTimestampImprint", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    TimestampImprintResponseDto computeArchiveTimestampImprint(@Valid @RequestBody SignedDocumentRequestDto request);

    @Operation(summary = "Embed the archive timestamp",
            description = "Embeds the timestamp token the platform obtained over the imprint from "
                    + "computeArchiveTimestampImprint, raising the signature to ARCHIVAL level.",
            operationId = "embedArchiveTimestamp")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Timestamp embedded; the signed document is at ARCHIVAL level"),
            @ApiResponse(responseCode = "400",
                    description = "Request body cannot be read (errorCode BAD_REQUEST), including a family value outside the "
                            + "published signature families",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "413",
                    description = "Signed document exceeds the size this connector accepts (errorCode DOCUMENT_TOO_LARGE)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = "- `SIGNATURE_NOT_FOUND` — the document carries no signature to attach the timestamp "
                            + "to.\n- `DOCUMENT_MALFORMED` — the document or token cannot be parsed.\n- "
                            + "`PARAMETER_UNSUPPORTED` — the requested family or a supplied parameter is unsupported "
                            + "by the connector.\n- `VALIDATION_FAILED` — the body is readable but breaks a field "
                            + "rule.",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/embedArchiveTimestamp", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    SignedDocumentResponseDto embedArchiveTimestamp(@Valid @RequestBody EmbedTimestampRequestDto request);

    @Operation(summary = "Extend the signature to a higher level",
            description = "Fetches the validation material the signature needs and embeds it in the same call. That "
                    + "material is CRLs, OCSP responses, and any certificates missing from the chain. Embedding it "
                    + "raises the signature to LONG_TERM, so it stays verifiable after the signing certificate "
                    + "expires.\n\nThis is the one operation that reaches the network on its own, and it MUST run on a "
                    + "deployment allowed that egress. It is correspondingly the one operation exempt from the "
                    + "determinism rule. It fetches, so a retry refetches rather than replays. Two runs may "
                    + "legitimately embed different material. Its response reports every artifact fetched, because "
                    + "that manifest is the platform's only account of traffic it did not make "
                    + "itself.\n\ncertificateChain.trustAnchors tells the connector where a chain stops. That is what "
                    + "decides whether revocation data is demanded for that certificate. The platform may send it "
                    + "empty. No certificate is then designated an anchor, and the connector treats every one of them "
                    + "as untrusted.\n\nThe operation carries an execution-mode envelope for a future asynchronous "
                    + "path. The platform always sends SYNCHRONOUS today, and treats a 202 as a contract violation. "
                    + "The capability flag that will gate the asynchronous path is not published yet. A connector that "
                    + "does not advertise LEVEL_LONG_TERM does not implement this operation at all and answers 422.",
            operationId = "extendToLevel")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Material fetched and embedded synchronously; the signed document is at LONG_TERM level "
                            + "and the fetch manifest accompanies it"),
            @ApiResponse(responseCode = "202",
                    description = "Extension accepted asynchronously; the body carries the tracking handle and no document"),
            @ApiResponse(responseCode = "400",
                    description = "Request body cannot be read (errorCode BAD_REQUEST), including a family value outside the "
                            + "published signature families",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "413",
                    description = "Signed document exceeds the size this connector accepts (errorCode DOCUMENT_TOO_LARGE)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = "- `PARAMETER_UNSUPPORTED` — the connector never advertised LEVEL_LONG_TERM, so it "
                            + "does not implement the extension.\n- `PARAMETER_UNSUPPORTED` — the target level asked "
                            + "for is one this connector does not reach.\n- `SIGNATURE_NOT_FOUND` — the document "
                            + "carries no signature to extend.\n- `DOCUMENT_MALFORMED` — the document cannot be "
                            + "parsed.\n- `VALIDATION_FAILED` — the body is readable but breaks a field rule, such as "
                            + "an absent chain.",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/extendToLevel", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ExtendToLevelResponseDto> extendToLevel(@Valid @RequestBody ExtendToLevelRequestDto request);

    @Operation(summary = "Get the status of an asynchronous extension",
            description = "Reports where an asynchronous extension stands, and carries its result once it has "
                    + "completed. A connector that runs the extension synchronously mints no tracking handle. It "
                    + "therefore answers 404 to every call here — truthfully, rather than through a stub.",
            operationId = "getExtendToLevelStatus")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Extension status retrieved"),
            @ApiResponse(responseCode = "400", description = "Request body cannot be read (errorCode BAD_REQUEST)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "404", description = "Operation is not tracked",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = "The body is readable but breaks a field rule, such as an absent tracking handle "
                            + "(errorCode VALIDATION_FAILED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/extendToLevel/status", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ExtendOperationStatusResponseDto getExtendToLevelStatus(
            @Valid @RequestBody ExtendOperationScopedRequestDto request);

    @Operation(summary = "Cancel an asynchronous extension",
            description = "Abandons an extension that is still in flight. A connector that runs the extension "
                    + "synchronously mints no tracking handle and therefore answers 404 to every call here.",
            operationId = "cancelExtendToLevel")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Aborted"),
            @ApiResponse(responseCode = "400", description = "Request body cannot be read (errorCode BAD_REQUEST)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "404",
                    description = "Operation not tracked; the cancellation outcome is unknown",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = "- The operation is already terminal, or past the point of no return.\n- "
                            + "`VALIDATION_FAILED` — the body is readable but breaks a field rule, such as an absent "
                            + "tracking handle.",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/extendToLevel/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> cancelExtendToLevel(@Valid @RequestBody ExtendOperationScopedRequestDto request);
}
