package com.otilm.api.model.common.attribute.validation;

import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.AttributeVersion;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.MetadataAttributeProperties;
import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import com.otilm.api.model.common.attribute.v2.content.IntegerAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.StringAttributeContentV2;
import com.otilm.api.model.common.attribute.v3.MetadataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.IntegerAttributeContentV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

class MetadataAttributeValidatorTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    @Test
    void validate_hasNoViolations_forValidMetadataAttribute() {
        // given
        MetadataListHolder holder = holderWith(validMetadataAttribute());

        // when
        Set<ConstraintViolation<MetadataListHolder>> violations = VALIDATOR.validate(holder);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_hasNoViolations_forSupportedV3MetadataAttribute() {
        // given
        MetadataAttributeV3 supportedV3Metadata = validV3MetadataAttribute();
        MetadataListHolder holder = holderWith(supportedV3Metadata);

        // when
        Set<ConstraintViolation<MetadataListHolder>> violations = VALIDATOR.validate(holder);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_hasNoViolations_forUppercaseUuidHex() {
        // given
        String canonicalUuidWithUppercaseHex = "00000000-0000-0000-0000-00000000000A";
        MetadataAttributeV2 metadata = validMetadataAttribute();
        metadata.setUuid(canonicalUuidWithUppercaseHex);
        MetadataListHolder holder = holderWith(metadata);

        // when
        Set<ConstraintViolation<MetadataListHolder>> violations = VALIDATOR.validate(holder);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_rejectsMismatchedV3SchemaVersion() {
        // given
        MetadataAttributeV3 metadata = validV3MetadataAttribute();
        metadata.setSchemaVersion(AttributeVersion.V2);
        MetadataListHolder holder = holderWith(metadata);

        // when
        Set<ConstraintViolation<MetadataListHolder>> violations = VALIDATOR.validate(holder);

        // then
        assertHasViolation(violations, "metadata[0].<list element>.schemaVersion", "schemaVersion must match version");
    }

    @Test
    void validate_hasNoViolations_forUsableNonTextData() {
        // given
        int usableIntegerData = 42;
        MetadataAttributeV2 metadata = validMetadataAttribute();
        metadata.setContentType(AttributeContentType.INTEGER);
        metadata.setContent(List.of(new IntegerAttributeContentV2(usableIntegerData)));
        MetadataListHolder holder = holderWith(metadata);

        // when
        Set<ConstraintViolation<MetadataListHolder>> violations = VALIDATOR.validate(holder);

        // then
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("metadataWithMismatchedDtoVersion")
    void validate_rejectsVersionThatDoesNotMatchMetadataDto(MetadataAttribute metadata) {
        // given
        MetadataListHolder holder = holderWith(metadata);

        // when
        Set<ConstraintViolation<MetadataListHolder>> violations = VALIDATOR.validate(holder);

        // then
        assertHasViolation(violations, indexedPath("version"), "version must match metadata attribute DTO");
    }

    static Stream<Named<MetadataAttribute>> metadataWithMismatchedDtoVersion() {
        MetadataAttributeV2 v2MetadataDeclaringV3 = validMetadataAttribute();
        v2MetadataDeclaringV3.setVersion(AttributeVersion.V3.getVersion());
        MetadataAttributeV3 v3MetadataDeclaringV2 = validV3MetadataAttribute();
        v3MetadataDeclaringV2.setVersion(AttributeVersion.V2.getVersion());
        v3MetadataDeclaringV2.setSchemaVersion(AttributeVersion.V2);

        return Stream
                .of(named("V2 DTO declaring V3", v2MetadataDeclaringV3),
                        named("V3 DTO declaring V2", v3MetadataDeclaringV2));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("metadataWithUnsupportedContentType")
    void validate_rejectsContentTypeUnsupportedByAttributeVersion(MetadataAttribute metadata) {
        // given
        MetadataListHolder holder = holderWith(metadata);

        // when
        Set<ConstraintViolation<MetadataListHolder>> violations = VALIDATOR.validate(holder);

        // then
        assertHasViolation(violations, indexedPath("contentType"),
                "contentType is not supported for attribute version");
    }

    static Stream<Named<MetadataAttribute>> metadataWithUnsupportedContentType() {
        MetadataAttributeV2 v2MetadataWithV3OnlyContentType = validMetadataAttribute();
        v2MetadataWithV3OnlyContentType.setContentType(AttributeContentType.RESOURCE);
        MetadataAttributeV3 v3MetadataWithV2OnlyContentType = validV3MetadataAttribute();
        v3MetadataWithV2OnlyContentType.setContentType(AttributeContentType.SECRET);

        return Stream
                .of(named("V2 resource content", v2MetadataWithV3OnlyContentType),
                        named("V3 secret content", v3MetadataWithV2OnlyContentType));
    }

    @Test
    void validate_doesNotValidateV2Data() {
        // We don't want to validate V2 data, as when deserialized, the data object is always BaseAttributeContentV2
        // which would not match the concrete classes.
        // given
        int usableIntegerData = 42;
        MetadataAttributeV2 metadata = validMetadataAttribute(); // String
        metadata.setContent(List.of(new IntegerAttributeContentV2(usableIntegerData)));
        MetadataListHolder holder = holderWith(metadata);

        // when
        Set<ConstraintViolation<MetadataListHolder>> violations = VALIDATOR.validate(holder);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_reportsIndexedPath_forV3ContentClassThatDoesNotMatchContentType() {
        // given
        int usableIntegerData = 42;
        MetadataAttributeV3 metadata = validV3MetadataAttribute();
        metadata.setContent(List.of(new IntegerAttributeContentV3(usableIntegerData)));
        MetadataListHolder holder = holderWith(metadata);

        // when
        Set<ConstraintViolation<MetadataListHolder>> violations = VALIDATOR.validate(holder);

        // then
        assertHasViolation(violations, indexedPath("content[0].<list element>"),
                "content must match contentType and attribute version");
    }

    @Test
    void validate_rejectsV3Content_forV2Metadata() {
        // given
        String usableContent = "usable";
        MetadataAttributeV2 metadata = validMetadataAttribute();
        metadata.setContent(List.of(new StringAttributeContentV3(usableContent)));
        MetadataListHolder holder = holderWith(metadata);

        // when
        Set<ConstraintViolation<MetadataListHolder>> violations = VALIDATOR.validate(holder);

        // then
        assertHasViolation(violations, indexedPath("content[0].<list element>"),
                "content must match contentType and attribute version");
    }

    @Test
    void validate_hasNoViolations_forV2ContentInV3Metadata() {
        // given
        String usableContent = "usable";
        MetadataAttributeV3 metadata = validV3MetadataAttribute();
        metadata.setContent(List.of(new StringAttributeContentV2(usableContent)));
        MetadataListHolder holder = holderWith(metadata);

        // when
        Set<ConstraintViolation<MetadataListHolder>> violations = VALIDATOR.validate(holder);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_hasNoViolations_forNullValueHandledOnlyByCustomConstraint() {
        // given
        MetadataHolder holderWithNullMetadata = new MetadataHolder(null);

        // when
        Set<ConstraintViolation<MetadataHolder>> violations = VALIDATOR.validate(holderWithNullMetadata);

        // then
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidMetadataProperties")
    void validate_hasExpectedViolation_forInvalidMetadataProperty(InvalidMetadata invalidMetadata) {
        // given
        MetadataListHolder holder = holderWith(invalidMetadata.metadata());

        // when
        Set<ConstraintViolation<MetadataListHolder>> violations = VALIDATOR.validate(holder);

        // then
        assertHasViolation(violations, indexedPath(invalidMetadata.path()), invalidMetadata.message());
    }

    static Stream<Named<InvalidMetadata>> invalidMetadataProperties() {
        return Stream
                .of(invalidMetadata("missing UUID", MetadataAttributeV2::setUuid, null, "uuid",
                        "uuid is required and must be a valid UUID"),
                        invalidMetadata("blank UUID", MetadataAttributeV2::setUuid, "   ", "uuid",
                                "uuid is required and must be a valid UUID"),
                        invalidMetadata("malformed UUID", MetadataAttributeV2::setUuid, "not-a-uuid", "uuid",
                                "uuid is required and must be a valid UUID"),
                        invalidMetadata("non-canonical UUID", MetadataAttributeV2::setUuid, "0-0-0-0-a", "uuid",
                                "uuid is required and must be a valid UUID"),
                        invalidMetadata("missing name", MetadataAttributeV2::setName, null, "name",
                                "name must not be blank"),
                        invalidMetadata("empty name", MetadataAttributeV2::setName, "", "name",
                                "name must not be blank"),
                        invalidMetadata("blank name", MetadataAttributeV2::setName, "   ", "name",
                                "name must not be blank"),
                        invalidMetadata("missing type", MetadataAttributeV2::setType, null, "type",
                                "type must be meta"),
                        invalidMetadata("incorrect type", MetadataAttributeV2::setType, AttributeType.DATA, "type",
                                "type must be meta"),
                        invalidMetadata("missing version", MetadataAttributeV2::setVersion, 0, "version",
                                "version is required and must be supported"),
                        invalidMetadata("unsupported version", MetadataAttributeV2::setVersion, 4, "version",
                                "version is required and must be supported"),
                        invalidMetadata("missing content type", MetadataAttributeV2::setContentType, null,
                                "contentType", "contentType is required"),
                        invalidMetadata("missing properties", MetadataAttributeV2::setProperties, null, "properties",
                                "properties is required"),
                        invalidMetadata("missing content", MetadataAttributeV2::setContent, null, "content",
                                "content is required and must not be empty"),
                        invalidMetadata("empty content", MetadataAttributeV2::setContent, List.of(), "content",
                                "content is required and must not be empty"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("unusableContent")
    void validate_reportsIndexedPath_forUnusableMetadataContent(AttributeContent content) {
        // given
        MetadataAttributeV2 metadata = validMetadataAttribute();
        metadata.setContent(List.of(new StringAttributeContentV2("usable"), content));
        MetadataListHolder holder = holderWith(metadata);

        // when
        Set<ConstraintViolation<MetadataListHolder>> violations = VALIDATOR.validate(holder);

        // then
        assertHasViolation(violations, "metadata[0].<list element>.content[1].<list element>",
                "content must contain a non-blank reference or usable data");
    }

    static Stream<Named<AttributeContent>> unusableContent() {
        return Stream
                .of(named("null reference and data", new StringAttributeContentV2()),
                        named("blank reference and null data", new StringAttributeContentV2("   ", null)),
                        named("blank string data", new StringAttributeContentV2("   ")));
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void validate_reportsIndexedPath_forIncorrectMetadataContentElementType() {
        // given
        MetadataAttributeV2 metadata = validMetadataAttribute();
        List incorrectlyTypedContent = List.of(new StringAttributeContentV2("usable"), "not attribute content");
        metadata.setContent(incorrectlyTypedContent);
        MetadataListHolder holder = holderWith(metadata);

        // when
        Set<ConstraintViolation<MetadataListHolder>> violations = VALIDATOR.validate(holder);

        // then
        assertHasViolation(violations, "metadata[0].<list element>.content[1].<list element>",
                "content must contain a non-blank reference or usable data");
    }

    @Test
    void validate_reportsIndexedPath_forNullMetadataContentElement() {
        // given
        MetadataAttributeV2 metadata = validMetadataAttribute();
        metadata.setContent(java.util.Arrays.asList(new StringAttributeContentV2("usable"), null));
        MetadataListHolder holder = holderWith(metadata);

        // when
        Set<ConstraintViolation<MetadataListHolder>> violations = VALIDATOR.validate(holder);

        // then
        assertHasViolation(violations, "metadata[0].<list element>.content[1].<list element>",
                "content must contain a non-blank reference or usable data");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("usableContent")
    void validate_hasNoViolations_forMetadataContentWithUsableReferenceOrData(AttributeContent content) {
        // given
        MetadataAttributeV2 metadata = validMetadataAttribute();
        metadata.setContent(List.of(content));
        MetadataListHolder holder = holderWith(metadata);

        // when
        Set<ConstraintViolation<MetadataListHolder>> violations = VALIDATOR.validate(holder);

        // then
        assertTrue(violations.isEmpty());
    }

    static Stream<Named<AttributeContent>> usableContent() {
        return Stream
                .of(named("reference only", new StringAttributeContentV2("provider-reference", null)),
                        named("data only", dataOnlyContent()));
    }

    private static StringAttributeContentV2 dataOnlyContent() {
        StringAttributeContentV2 content = new StringAttributeContentV2("provider-data");
        content.setReference(null);
        return content;
    }

    private static MetadataAttributeV3 validV3MetadataAttribute() {
        String validV3Uuid = "00000000-0000-0000-0000-000000000003";
        String metadataName = "v3 provider handle";
        String providerKeyData = "provider-key-3";
        MetadataAttributeV3 metadata = new MetadataAttributeV3();
        metadata.setUuid(validV3Uuid);
        metadata.setName(metadataName);
        metadata.setContentType(AttributeContentType.STRING);
        metadata.setProperties(new MetadataAttributeProperties());
        metadata.setContent(List.of(new StringAttributeContentV3(providerKeyData)));
        return metadata;
    }

    private static <V> Named<InvalidMetadata> invalidMetadata(String name, BiConsumer<MetadataAttributeV2, V> setter,
            V value, String path, String message) {
        MetadataAttributeV2 metadata = validMetadataAttribute();
        setter.accept(metadata, value);
        return named(name, new InvalidMetadata(metadata, path, message));
    }

    private static MetadataAttributeV2 validMetadataAttribute() {
        MetadataAttributeV2 metadata = new MetadataAttributeV2();
        metadata.setUuid("00000000-0000-0000-0000-000000000002");
        metadata.setName("metadata handle");
        metadata.setContentType(AttributeContentType.STRING);
        metadata.setProperties(new MetadataAttributeProperties());
        metadata.setContent(List.of(new StringAttributeContentV2("metadata-value")));
        return metadata;
    }

    private static MetadataListHolder holderWith(MetadataAttribute metadata) {
        return new MetadataListHolder(List.of(metadata));
    }

    private static String indexedPath(String nestedProperty) {
        return "metadata[0].<list element>." + nestedProperty;
    }

    private static void assertHasViolation(Set<? extends ConstraintViolation<?>> violations, String path,
            String message) {
        assertTrue(
                violations
                        .stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals(path)
                                && violation.getMessage().equals(message)),
                () -> "Expected " + path + ": " + message + ", got "
                        + violations.stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList());
    }

    private record InvalidMetadata(MetadataAttributeV2 metadata, String path, String message) {
    }

    private record MetadataHolder(@ValidMetadataAttribute MetadataAttribute metadata) {
    }

    private record MetadataListHolder(List<@ValidMetadataAttribute MetadataAttribute> metadata) {
    }
}
