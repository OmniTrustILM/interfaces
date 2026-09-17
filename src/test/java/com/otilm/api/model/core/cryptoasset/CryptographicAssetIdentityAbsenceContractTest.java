package com.otilm.api.model.core.cryptoasset;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.otilm.api.interfaces.core.web.CryptographicAssetController;
import com.otilm.api.interfaces.core.web.StatisticsController;
import com.otilm.api.model.client.dashboard.CryptographicAssetStatisticsDto;
import com.otilm.api.model.client.dashboard.CryptographicAssetSyncCompletenessDto;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.cbom.CbomAssetSyncState;
import com.otilm.api.model.core.cbom.CbomDto;
import com.otilm.api.model.core.cbom.CbomSyncSkipDto;
import com.otilm.api.model.core.cbom.CbomSyncSkipState;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Proves the asset's internal deduplication key stays off the wire. The key hashes low-entropy material values, so any
 * surface that carries or names it would let a client recover what redaction removed. The DTOs are defined in this
 * artifact, so this is where the guarantee holds or fails. Swept surfaces: declared fields and Jackson wire properties
 * (getters and renames included) with their schema text (description, title, name, example, examples, defaultValue,
 * pattern, allowableValues) at class, field, getter and parameter level; operation names, operationIds, class and
 * method mapping paths, response descriptions with their content schemas and example objects, parameter annotations
 * with their nested schemas, and tags of the inventory controller and the statistics operation; the three inventory
 * enums and the CRYPTO_ASSET resource entry as served by the enums API. Fixtures prove each dimension fails on a
 * violation instead of passing silently. What no static sweep can reach — the searchable-fields catalogue core
 * populates at runtime — is guarded core-side; the operation's own prose states the keys are never offered.
 */
class CryptographicAssetIdentityAbsenceContractTest {

    /**
     * "identity" catches the key under its own name and any of its spellings; "fingerprint" catches the historical name
     * the investigation used for the same value. Nothing this inventory legitimately serves needs either word:
     * certificates are referenced by serial number and DN, assets by UUID.
     */
    private static final List<String> BANNED_TOKENS = List.of("identity", "fingerprint");

    private static final List<Class<?>> WIRE_TYPES = List
            .of(CryptographicAssetDto.class, CryptographicAssetDetailDto.class, CryptographicAssetVerdictDto.class,
                    CryptographicAssetNormalizedFieldsDto.class, CryptographicAssetSourceDto.class,
                    CryptographicAssetEvidenceDto.class, CryptographicAssetOidDto.class,
                    CryptographicAssetStatisticsDto.class, CryptographicAssetSyncCompletenessDto.class, CbomDto.class,
                    CbomSyncSkipDto.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void noWireTypeCarriesAnIdentityKeyFieldOrMentionsOneInItsSchema() {
        List<String> problems = new ArrayList<>();
        for (Class<?> type : WIRE_TYPES) {
            sweep(type, problems);
            sweepJacksonProperties(type, problems);
        }
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    void noInventoryOperationProseMentionsTheIdentityKey() {
        List<String> problems = new ArrayList<>();
        sweepTag(CryptographicAssetController.class, problems);
        sweepTag(StatisticsController.class, problems);
        for (Method method : CryptographicAssetController.class.getDeclaredMethods()) {
            sweepOperation(method, problems);
        }
        for (Method method : StatisticsController.class.getDeclaredMethods()) {
            if (method.getName().equals("getCryptographicAssetStatistics")) {
                sweepOperation(method, problems);
            }
        }
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    void noEnumConstantExposesTheIdentityKeyInItsCodeLabelOrDescription() {
        List<String> problems = new ArrayList<>();
        sweepEnum(CryptographicAssetType.class, problems);
        sweepEnum(PqcVerdict.class, problems);
        sweepEnum(CbomAssetSyncState.class, problems);
        sweepEnum(CbomSyncSkipState.class, problems);
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    void theResourceEntryCarriesNoBannedToken() {
        List<String> problems = new ArrayList<>();
        reportBannedTokens("Resource.CRYPTO_ASSET name", Resource.CRYPTO_ASSET.name(), problems);
        reportBannedTokens("Resource.CRYPTO_ASSET code", Resource.CRYPTO_ASSET.getCode(), problems);
        reportBannedTokens("Resource.CRYPTO_ASSET label", Resource.CRYPTO_ASSET.getLabel(), problems);
        if (Resource.CRYPTO_ASSET.getDescription() != null) {
            reportBannedTokens("Resource.CRYPTO_ASSET description", Resource.CRYPTO_ASSET.getDescription(), problems);
        }
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    void theFieldWalkCatchesEveryViolationShape() {
        List<String> problems = new ArrayList<>();
        sweep(ViolatingFixture.class, problems);
        List<String> expected = List
                .of("class schema", "identityKey field name", "field schema description", "field schema example",
                        "field schema title", "field schema examples");
        for (String fragment : expected) {
            assertTrue(problems.stream().anyMatch(p -> p.contains(fragment)),
                    "field walk missed: " + fragment + "\n" + String.join("\n", problems));
        }
    }

    @Test
    void thePropertyWalkCatchesGettersAndRenames() {
        List<String> problems = new ArrayList<>();
        sweepJacksonProperties(ViolatingFixture.class, problems);
        assertTrue(problems.stream().anyMatch(p -> p.contains("derived") && p.contains("getter schema")),
                "getter-level schema text missed\n" + String.join("\n", problems));
        assertTrue(problems.stream().anyMatch(p -> p.contains("wire property name")),
                "renamed wire property missed\n" + String.join("\n", problems));
    }

    @Test
    void theOperationSweepCatchesEveryViolationShape() {
        List<String> problems = new ArrayList<>();
        for (Method method : ViolatingControllerFixture.class.getDeclaredMethods()) {
            sweepOperation(method, problems);
        }
        sweepTag(ViolatingControllerFixture.class, problems);
        List<String> expected = List
                .of("operationId", "find mapping path", "example object", "parameter example",
                        "parameter nested schema", "content schema", "class mapping path");
        for (String fragment : expected) {
            assertTrue(problems.stream().anyMatch(p -> p.contains(fragment)),
                    "operation sweep missed: " + fragment + "\n" + String.join("\n", problems));
        }
    }

    private static void sweep(Class<?> type, List<String> problems) {
        for (Class<?> current = type; current != null && current != Object.class; current = current.getSuperclass()) {
            Schema classSchema = current.getAnnotation(Schema.class);
            if (classSchema != null) {
                sweepSchema(current.getSimpleName() + " class schema", classSchema, problems);
            }
            for (Field field : current.getDeclaredFields()) {
                if (field.isSynthetic() || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                String location = current.getSimpleName() + "." + field.getName();
                reportBannedTokens(location + " field name", field.getName(), problems);
                Schema schema = field.getAnnotation(Schema.class);
                if (schema != null) {
                    sweepSchema(location + " field schema", schema, problems);
                }
            }
        }
    }

    private static void sweepSchema(String location, Schema schema, List<String> problems) {
        reportBannedTokens(location + " description", schema.description(), problems);
        reportBannedTokens(location + " title", schema.title(), problems);
        reportBannedTokens(location + " name", schema.name(), problems);
        reportBannedTokens(location + " example", schema.example(), problems);
        for (String example : schema.examples()) {
            reportBannedTokens(location + " examples", example, problems);
        }
        reportBannedTokens(location + " defaultValue", schema.defaultValue(), problems);
        reportBannedTokens(location + " pattern", schema.pattern(), problems);
        for (String value : schema.allowableValues()) {
            reportBannedTokens(location + " allowableValues", value, problems);
        }
    }

    private static void sweepJacksonProperties(Class<?> type, List<String> problems) {
        BeanDescription description = MAPPER.getSerializationConfig().introspect(MAPPER.constructType(type));
        for (BeanPropertyDefinition property : description.findProperties()) {
            String location = type.getSimpleName() + "." + property.getName();
            reportBannedTokens(location + " wire property name", property.getName(), problems);
            if (property.getField() != null) {
                Schema schema = property.getField().getAnnotation(Schema.class);
                if (schema != null) {
                    sweepSchema(location + " field schema", schema, problems);
                }
            }
            if (property.getGetter() != null) {
                Schema schema = property.getGetter().getAnnotation(Schema.class);
                if (schema != null) {
                    sweepSchema(location + " getter schema", schema, problems);
                }
            }
        }
    }

    private static void sweepOperation(Method method, List<String> problems) {
        reportBannedTokens(method.getName() + " method name", method.getName(), problems);

        Operation operation = method.getAnnotation(Operation.class);
        if (operation != null) {
            reportBannedTokens(method.getName() + " operationId", operation.operationId(), problems);
            reportBannedTokens(method.getName() + " summary", operation.summary(), problems);
            reportBannedTokens(method.getName() + " description", operation.description(), problems);
        }

        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            sweepMappingPaths(method.getName(), getMapping.path(), getMapping.value(), problems);
        }
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            sweepMappingPaths(method.getName(), postMapping.path(), postMapping.value(), problems);
        }
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            sweepMappingPaths(method.getName(), requestMapping.path(), requestMapping.value(), problems);
        }

        for (ApiResponse response : method.getAnnotationsByType(ApiResponse.class)) {
            String location = method.getName() + " " + response.responseCode() + " response";
            reportBannedTokens(location + " description", response.description(), problems);
            for (Content content : response.content()) {
                reportBannedTokens(location + " content mediaType", content.mediaType(), problems);
                sweepSchema(location + " content schema", content.schema(), problems);
                sweepSchema(location + " content array schema", content.array().schema(), problems);
                for (ExampleObject example : content.examples()) {
                    String exampleLocation = location + " example object";
                    reportBannedTokens(exampleLocation + " name", example.name(), problems);
                    reportBannedTokens(exampleLocation + " summary", example.summary(), problems);
                    reportBannedTokens(exampleLocation + " description", example.description(), problems);
                    reportBannedTokens(exampleLocation + " value", example.value(), problems);
                }
            }
        }

        for (Parameter parameter : method.getParameters()) {
            // Fully qualified deliberately: java.lang.reflect.Parameter is already imported for the reflected method
            // parameter this loop walks, so the annotation type has to stay unimported to avoid shadowing it.
            io.swagger.v3.oas.annotations.Parameter param = parameter
                    .getAnnotation(io.swagger.v3.oas.annotations.Parameter.class);
            if (param != null) {
                String location = method.getName() + " parameter";
                reportBannedTokens(location + " name", param.name(), problems);
                reportBannedTokens(location + " description", param.description(), problems);
                reportBannedTokens(location + " example", param.example(), problems);
                sweepSchema(location + " nested schema", param.schema(), problems);
            }
            PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
            if (pathVariable != null) {
                reportBannedTokens(method.getName() + " @PathVariable value", pathVariable.value(), problems);
            }
            RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
            if (requestParam != null) {
                reportBannedTokens(method.getName() + " @RequestParam value", requestParam.value(), problems);
                reportBannedTokens(method.getName() + " @RequestParam defaultValue", requestParam.defaultValue(),
                        problems);
            }
            Schema schema = parameter.getAnnotation(Schema.class);
            if (schema != null) {
                sweepSchema(method.getName() + " parameter schema", schema, problems);
            }
        }
    }

    private static void sweepMappingPaths(String methodName, String[] paths, String[] values, List<String> problems) {
        for (String path : paths) {
            reportBannedTokens(methodName + " mapping path", path, problems);
        }
        for (String value : values) {
            reportBannedTokens(methodName + " mapping path", value, problems);
        }
    }

    private static void sweepTag(Class<?> controller, List<String> problems) {
        Tag tag = controller.getAnnotation(Tag.class);
        if (tag != null) {
            reportBannedTokens(controller.getSimpleName() + " @Tag name", tag.name(), problems);
            reportBannedTokens(controller.getSimpleName() + " @Tag description", tag.description(), problems);
        }
        RequestMapping mapping = controller.getAnnotation(RequestMapping.class);
        if (mapping != null) {
            sweepMappingPaths(controller.getSimpleName() + " class", mapping.path(), mapping.value(), problems);
        }
    }

    private static void sweepEnum(Class<? extends IPlatformEnum> enumType, List<String> problems) {
        Schema classSchema = enumType.getAnnotation(Schema.class);
        if (classSchema != null) {
            sweepSchema(enumType.getSimpleName() + " class schema", classSchema, problems);
        }
        for (IPlatformEnum constant : enumType.getEnumConstants()) {
            String location = enumType.getSimpleName() + "." + constant.name();
            reportBannedTokens(location + " name", constant.name(), problems);
            reportBannedTokens(location + " code", constant.getCode(), problems);
            reportBannedTokens(location + " label", constant.getLabel(), problems);
            if (constant.getDescription() != null) {
                reportBannedTokens(location + " description", constant.getDescription(), problems);
            }
        }
    }

    private static void reportBannedTokens(String location, String text, List<String> problems) {
        String lower = text.toLowerCase(Locale.ROOT);
        for (String token : BANNED_TOKENS) {
            if (lower.contains(token)) {
                problems.add(location + " must not mention \"" + token + "\"");
            }
        }
    }

    /** Default access on purpose: a private field would trip Sonar's unused-private-field rule (java:S1068). */
    @Schema(description = "keyed by identity")
    private static final class ViolatingFixture {

        String identityKey;

        @Schema(description = "described by its fingerprint", example = "fingerprint of the material")
        String value;

        @Schema(title = "the identity title", examples = {"a fingerprint example"})
        String texts;

        @JsonProperty("identityKey")
        String renamed;

        @Schema(description = "derived fingerprint text")
        public String getDerived() {
            return null;
        }

        public String getRenamed() {
            return renamed;
        }
    }

    @RequestMapping("/identityFixtureRoot")
    private interface ViolatingControllerFixture {

        @Operation(operationId = "findByIdentityKey", summary = "clean summary", description = "clean description")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "clean",
                        content = @Content(schema = @Schema(title = "a fingerprint title"),
                                examples = {@ExampleObject(value = "{\"key\":\"a fingerprint\"}")}))})
        @GetMapping(path = "/byIdentity")
        String find(@io.swagger.v3.oas.annotations.Parameter(description = "clean", example = "an identity example",
                schema = @Schema(description = "an identity schema")) String query);
    }
}
