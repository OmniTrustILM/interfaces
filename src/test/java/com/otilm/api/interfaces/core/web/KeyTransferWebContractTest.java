package com.otilm.api.interfaces.core.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.client.certificate.CertificateImportEntryDto;
import com.otilm.api.model.client.certificate.CertificateImportRequestDto;
import com.otilm.api.model.client.certificate.CertificateKeystoreRequestDto;
import com.otilm.api.model.client.cryptography.key.KeyExportRequestDto;
import com.otilm.api.model.client.cryptography.key.KeyImportRequestDto;
import com.otilm.api.model.client.inspection.InspectionRequestDto;
import com.otilm.api.model.client.upload.UploadRequestDto;
import com.otilm.api.model.core.certificate.CertificateFormat;
import com.otilm.api.model.core.secret.Passphrase;
import com.otilm.api.model.core.secret.UploadedFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotEmpty;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.otilm.api.testsupport.OpenApiProseAssertions.assertLanguageNeutral;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

/**
 * Pins the operator-facing key import, key export and PKCS#12 endpoints against their annotation values.
 *
 * <p>
 * The properties held here are the ones a later change could quietly undo: an uploaded file must arrive as the type
 * that is never rendered or echoed, a passphrase must never reach a URL, and the certificate content download must not
 * gain a keystore format that would route around the export gates.
 * </p>
 */
class KeyTransferWebContractTest {

    private static final List<Class<?>> PASSPHRASE_BEARING_BODIES = List
            .of(KeyImportRequestDto.class, KeyExportRequestDto.class, InspectionRequestDto.class,
                    CertificateImportRequestDto.class, CertificateKeystoreRequestDto.class);

    /**
     * Reading a file has to stay optional, so an import must never require a value only the platform can mint: a
     * content-derived entry reference is one a caller can compute itself.
     */
    @Test
    void importRequiresNothingOnlyThePlatformCanIssue() {
        List<String> mintedValues = Stream
                .concat(Arrays.stream(CertificateImportRequestDto.class.getDeclaredFields()),
                        Arrays.stream(CertificateImportEntryDto.class.getDeclaredFields()))
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(KeyTransferWebContractTest::isRequiredBySchema)
                .map(Field::getName)
                .filter(name -> {
                    String lower = name.toLowerCase(Locale.ROOT);
                    return lower.contains("token") || lower.contains("digest") || lower.contains("signature")
                            || lower.contains("nonce");
                })
                .toList();

        assertTrue(mintedValues.isEmpty(),
                () -> "an import requiring a value only the platform can issue forces every caller to read the file "
                        + "first: " + mintedValues);
    }

    /**
     * Retry safety is per entry because entries succeed and fail on their own: a repeated request has to be able to
     * return what already succeeded and retry only what did not, which one identifier for the whole batch cannot
     * express.
     */
    @Test
    void retrySafetyIsPerImportedEntry() throws Exception {
        Field entryIdentifier = CertificateImportEntryDto.class.getDeclaredField("importId");

        assertTrue(isRequiredBySchema(entryIdentifier), "each imported entry must carry its own import identifier");
        assertFalse(
                Arrays
                        .stream(CertificateImportRequestDto.class.getDeclaredFields())
                        .filter(field -> !Modifier.isStatic(field.getModifiers()))
                        .anyMatch(field -> field.getName().toLowerCase(Locale.ROOT).contains("importid")),
                "an identifier for the whole request would have to be fanned out to the entries anyway");
    }

    /**
     * An import that could act on entries the caller did not name would take in whatever else a file happened to carry.
     * Naming them is how the caller stays in control of what enters the platform.
     */
    @Test
    void importActsOnlyOnEntriesTheCallerNamed() throws Exception {
        Field entries = CertificateImportRequestDto.class.getDeclaredField("entries");

        assertTrue(isRequiredBySchema(entries), "an import must name the entries it acts on");
        assertNotNull(entries.getAnnotation(NotEmpty.class),
                "an empty selection must be refused rather than treated as everything");
        assertFalse(
                Arrays
                        .stream(CertificateImportRequestDto.class.getDeclaredFields())
                        .filter(field -> !Modifier.isStatic(field.getModifiers()))
                        .anyMatch(field -> field.getName().equals("keyDestination")),
                "a destination for the whole request would need a precedence rule against the entries' own");
    }

    private static boolean isRequiredBySchema(Field field) {
        io.swagger.v3.oas.annotations.media.Schema schema = field
                .getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class);
        return schema != null
                && schema.requiredMode() == io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;
    }

    @Test
    void keyImportIsAJsonPostUnderTheTokenProfile() {
        Method importKey = method(CryptographicKeyController.class, "importKey");
        PostMapping mapping = importKey.getAnnotation(PostMapping.class);

        assertNotNull(mapping, "key import must be a POST");
        assertArrayEquals(
                new String[]{"/tokens/{tokenInstanceUuid}/tokenProfiles/{tokenProfileUuid}/keys/{type}/import"},
                mapping.path());
        assertArrayEquals(new String[]{MediaType.APPLICATION_JSON_VALUE}, mapping.consumes());
    }

    /**
     * Every upload in the platform is base64 inside a JSON body, so these three follow suit; what sets them apart is
     * that the file may carry key material, so it is held as an {@link UploadedFile} rather than a string, the type
     * that is never rendered, never echoed in an error, and can be overwritten once used.
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("uploadOperations")
    void everyUploadArrivesInAJsonBodyAsAnUploadedFile(Method operation) throws Exception {
        PostMapping mapping = operation.getAnnotation(PostMapping.class);
        assertNotNull(mapping, operation.getName() + " must be a POST");
        assertArrayEquals(new String[]{MediaType.APPLICATION_JSON_VALUE}, mapping.consumes(), operation.getName());

        Parameter body = Arrays
                .stream(operation.getParameters())
                .filter(parameter -> parameter.isAnnotationPresent(RequestBody.class))
                .findFirst()
                .orElseThrow(() -> new AssertionError(operation.getName() + " must take a request body"));
        assertTrue(UploadRequestDto.class.isAssignableFrom(body.getType()),
                operation.getName() + " must take its file through the shared upload body");
        assertEquals(UploadedFile.class, UploadRequestDto.class.getDeclaredField("file").getType());
    }

    /** One upload shape for the whole platform: a multipart operation would be the first and only one. */
    @Test
    void noOperationTakesAMultipartBody() {
        List<String> multipart = Stream
                .of(CryptographicKeyController.class, CertificateController.class, InspectionController.class)
                .flatMap(controller -> Arrays.stream(controller.getDeclaredMethods()))
                .filter(method -> {
                    PostMapping mapping = method.getAnnotation(PostMapping.class);
                    return mapping != null
                            && Arrays.asList(mapping.consumes()).contains(MediaType.MULTIPART_FORM_DATA_VALUE)
                            || Arrays
                                    .stream(method.getParameters())
                                    .anyMatch(p -> p.isAnnotationPresent(RequestPart.class));
                })
                .map(Method::getName)
                .toList();

        assertTrue(multipart.isEmpty(), () -> "operations taking a multipart body: " + multipart);
    }

    /**
     * An operation that documents 201 but leaves the status to the implementation answers 200 the moment an
     * implementation forgets to set it, and generated clients that check for 201 then fail against a correct import.
     */
    @Test
    void keyImportAnswersCreated() {
        Method importKey = method(CryptographicKeyController.class, "importKey");
        ResponseStatus status = importKey.getAnnotation(ResponseStatus.class);

        assertNotNull(status, "the contract must set the status it documents");
        assertEquals(HttpStatus.CREATED, status.value());
    }

    /**
     * What a file holds is the answer, not the question, so reading one is a single operation rather than one per
     * resource: a caller uploading a container does not yet know whether it carries certificates, a private key, a
     * secret key or a signing request.
     */
    @Test
    void readingAFileIsOneOperationRatherThanOnePerResource() {
        assertArrayEquals(new String[]{"/v1/inspections"},
                InspectionController.class.getAnnotation(RequestMapping.class).value());
        assertFalse(
                Stream
                        .of(CertificateController.class, CryptographicKeyController.class)
                        .flatMap(controller -> Arrays.stream(controller.getDeclaredMethods()))
                        .anyMatch(method -> method.getName().toLowerCase(Locale.ROOT).contains("inspect")),
                "reading a file must not be duplicated onto a resource");
    }

    @Test
    void keyExportIsAPostSoThePassphraseStaysOutOfTheUrl() {
        Method exportKey = method(CryptographicKeyController.class, "exportKey");
        PostMapping mapping = exportKey.getAnnotation(PostMapping.class);

        assertNotNull(mapping, "key export must be a POST");
        assertArrayEquals(new String[]{"/keys/{uuid}/items/{keyItemUuid}/export"}, mapping.path());
        assertArrayEquals(new String[]{MediaType.APPLICATION_JSON_VALUE}, mapping.consumes());
        assertArrayEquals(new String[]{MediaType.APPLICATION_OCTET_STREAM_VALUE}, mapping.produces());
    }

    @Test
    void keystoreDownloadIsAPostAndServesTheContainerMediaType() {
        Method download = method(CertificateController.class, "downloadKeystore");
        PostMapping mapping = download.getAnnotation(PostMapping.class);

        assertNotNull(mapping, "the keystore download must be a POST");
        assertArrayEquals(new String[]{"/{uuid}/keystore"}, mapping.path());
        assertArrayEquals(new String[]{CertificateController.KEYSTORE_MEDIA_TYPE}, mapping.produces());
    }

    /**
     * The two operations that hand a file to a caller declare the protections the service has to set on it. An
     * interface cannot enforce them, so what it can do is publish them as required and keep that list from shrinking:
     * both files carry key material, so a cache that keeps either one keeps the key.
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("keyMaterialDownloads")
    void everyKeyMaterialDownloadDeclaresItsCacheProtections(Method download) {
        ApiResponse success = Arrays
                .stream(download.getAnnotation(ApiResponses.class).value())
                .filter(response -> "200".equals(response.responseCode()))
                .findFirst()
                .orElseThrow(() -> new AssertionError(download.getName() + " must document its successful response"));

        List<String> declared = Arrays.stream(success.headers()).filter(Header::required).map(Header::name).toList();

        for (String required : List
                .of(HttpHeaders.CONTENT_DISPOSITION, HttpHeaders.CACHE_CONTROL, HttpHeaders.PRAGMA,
                        "X-Content-Type-Options")) {
            assertTrue(declared.contains(required),
                    () -> download.getName() + " must declare " + required + " as required, declares " + declared);
        }
    }

    @Test
    void exportCanOnlyBeDisabled() {
        Method disable = method(CryptographicKeyController.class, "disableKeyExport");

        assertNotNull(disable.getAnnotation(PatchMapping.class), "disabling export must be a PATCH");
        assertFalse(
                Arrays
                        .stream(CryptographicKeyController.class.getDeclaredMethods())
                        .anyMatch(m -> m.getName().toLowerCase(Locale.ROOT).contains("enablekeyexport")),
                "there must be no operation that makes a key exportable after it exists");
    }

    @Test
    void attributeSchemasAreReadableWithoutSendingAnything() {
        for (String name : List.of("listImportKeyAttributes", "listExportKeyAttributes")) {
            Method method = method(CryptographicKeyController.class, name);
            assertNotNull(method.getAnnotation(GetMapping.class), name + " must be a GET");
        }
    }

    /**
     * A passphrase in a query string is recorded by proxies, browser history and access logs, so no operation may take
     * one that way.
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("keyTransferOperations")
    void noOperationTakesAPassphraseAsAQueryParameter(Method operation) {
        for (Parameter parameter : operation.getParameters()) {
            if (parameter.getAnnotation(RequestParam.class) == null) {
                continue;
            }
            assertNotEquals(Passphrase.class, parameter.getType(),
                    operation.getName() + " takes a passphrase as a query parameter");
            assertFalse(parameter.getName().toLowerCase(Locale.ROOT).contains("passphrase"),
                    operation.getName() + " takes a passphrase as a query parameter");
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("keyTransferOperations")
    void everyOperationIsDocumented(Method operation) {
        Operation documentation = operation.getAnnotation(Operation.class);

        assertNotNull(documentation, "missing @Operation on " + operation.getName());
        assertFalse(documentation.summary().isBlank(), "blank summary on " + operation.getName());
        assertFalse(documentation.description().isBlank(), "blank description on " + operation.getName());
    }

    /**
     * These operations are published for the administrator frontend and for anyone generating a client, in whatever
     * language. Prose naming a type of the language the platform is written in describes nothing to them.
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("keyTransferOperations")
    void noPublishedProseNamesTheImplementationLanguage(Method operation) {
        Operation documentation = operation.getAnnotation(Operation.class);
        assertLanguageNeutral(operation.getName() + " summary", documentation.summary());
        assertLanguageNeutral(operation.getName() + " description", documentation.description());

        ApiResponses responses = operation.getAnnotation(ApiResponses.class);
        if (responses != null) {
            for (ApiResponse response : responses.value()) {
                assertLanguageNeutral(operation.getName() + " " + response.responseCode() + " response",
                        response.description());
            }
        }
        for (Parameter parameter : operation.getParameters()) {
            io.swagger.v3.oas.annotations.Parameter documented = parameter
                    .getAnnotation(io.swagger.v3.oas.annotations.Parameter.class);
            if (documented != null) {
                assertLanguageNeutral(operation.getName() + " parameter", documented.description());
            }
        }
    }

    /**
     * The certificate content download serves certificates. A keystore format here would be a second way to obtain key
     * material, one that never passes the export gates.
     */
    @Test
    void theCertificateContentFormatsCarryNoKeystore() {
        assertEquals(List.of(CertificateFormat.RAW, CertificateFormat.PKCS7),
                Arrays.asList(CertificateFormat.values()));
        for (CertificateFormat format : CertificateFormat.values()) {
            String code = format.getCode().toLowerCase(Locale.ROOT);
            assertFalse(code.contains("12") || code.contains("keystore") || code.contains("pfx"),
                    "certificate download format " + format + " looks like a keystore format");
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("passphraseBearingBodies")
    void everyPassphraseIsHeldAsAPassphraseRatherThanAString(Class<?> body) {
        List<Field> passphraseFields = Stream
                .concat(Arrays.stream(body.getDeclaredFields()),
                        body.getSuperclass() == null
                                ? Stream.empty()
                                : Arrays.stream(body.getSuperclass().getDeclaredFields()))
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> field.getName().toLowerCase(Locale.ROOT).contains("passphrase"))
                .toList();

        assertFalse(passphraseFields.isEmpty(), body.getSimpleName() + " declares no passphrase field");
        for (Field field : passphraseFields) {
            assertEquals(Passphrase.class, field.getType(),
                    body.getSimpleName() + "." + field.getName() + " must be a Passphrase, not a String");
        }
    }

    /**
     * A request body rendered as JSON — by a log, a trace, an error detail — must not carry its secret fields even as
     * empty objects: the wrapper types hide their content, and the property itself must be write-only.
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("passphraseBearingBodies")
    void secretFieldsAreNeverWrittenBack(Class<?> body) throws Exception {
        Object request = body.getDeclaredConstructor().newInstance();
        List<String> secretProperties = new ArrayList<>();
        for (Class<?> type = body; type != null; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (field.getType() == Passphrase.class) {
                    field.setAccessible(true);
                    field.set(request, new Passphrase("correct horse battery staple".toCharArray()));
                    secretProperties.add(field.getName());
                } else if (field.getType() == UploadedFile.class) {
                    field.setAccessible(true);
                    field.set(request, new UploadedFile(new byte[]{1, 2, 3}));
                    secretProperties.add(field.getName());
                }
            }
        }
        assertFalse(secretProperties.isEmpty(), body.getSimpleName() + " declares no secret field");

        String json = new ObjectMapper().writeValueAsString(request);

        for (String property : secretProperties) {
            assertFalse(json.contains("\"" + property + "\""),
                    () -> body.getSimpleName() + "." + property + " must not be written back, got " + json);
        }
    }

    static Stream<Named<Class<?>>> passphraseBearingBodies() {
        return PASSPHRASE_BEARING_BODIES.stream().map(body -> named(body.getSimpleName(), body));
    }

    static Stream<Named<Method>> uploadOperations() {
        return Stream
                .of(named("importKey", method(CryptographicKeyController.class, "importKey")),
                        named("inspect", method(InspectionController.class, "inspect")),
                        named("importCertificates", method(CertificateController.class, "importCertificates")));
    }

    static Stream<Named<Method>> keyMaterialDownloads() {
        return Stream
                .of(named("exportKey", method(CryptographicKeyController.class, "exportKey")),
                        named("downloadKeystore", method(CertificateController.class, "downloadKeystore")));
    }

    static Stream<Named<Method>> keyTransferOperations() {
        return Stream
                .of(named("importKey", method(CryptographicKeyController.class, "importKey")),
                        named("exportKey", method(CryptographicKeyController.class, "exportKey")),
                        named("listImportKeyAttributes",
                                method(CryptographicKeyController.class, "listImportKeyAttributes")),
                        named("listExportKeyAttributes",
                                method(CryptographicKeyController.class, "listExportKeyAttributes")),
                        named("disableKeyExport", method(CryptographicKeyController.class, "disableKeyExport")),
                        named("inspect", method(InspectionController.class, "inspect")),
                        named("importCertificates", method(CertificateController.class, "importCertificates")),
                        named("downloadKeystore", method(CertificateController.class, "downloadKeystore")));
    }

    private static Method method(Class<?> controller, String name) {
        return Arrays
                .stream(controller.getDeclaredMethods())
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError(controller.getSimpleName() + " declares no method " + name));
    }
}
