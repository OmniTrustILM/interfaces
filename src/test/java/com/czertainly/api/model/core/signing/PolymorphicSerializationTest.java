package com.czertainly.api.model.core.signing;

import com.czertainly.api.model.common.NameAndUuidDto;
import com.czertainly.api.model.common.enums.cryptography.DigestAlgorithm;
import com.czertainly.api.model.core.signing.signatureprofile.DelegatedSigningDto;
import com.czertainly.api.model.core.signing.signatureprofile.DelegatedSigningRequestDto;
import com.czertainly.api.model.core.signing.signatureprofile.ManagedSigningDto;
import com.czertainly.api.model.core.signing.signatureprofile.ManagedSigningRequestDto;
import com.czertainly.api.model.core.signing.signatureprofile.SigningScheme;
import com.czertainly.api.model.core.signing.signatureprofile.SigningSchemeDto;
import com.czertainly.api.model.core.signing.signatureprofile.SigningSchemeRequestDto;
import com.czertainly.api.model.core.signing.workflow.SigningWorkflowConfigurationCreateRequestDto;
import com.czertainly.api.model.core.signing.workflow.SigningWorkflowConfigurationDto;
import com.czertainly.api.model.core.signing.workflow.SigningWorkflowConfigurationUpdateRequestDto;
import com.czertainly.api.model.core.signing.workflow.SigningWorkflowType;
import com.czertainly.api.model.core.signing.workflow.TimestampingConfigurationCreateRequestDto;
import com.czertainly.api.model.core.signing.workflow.TimestampingConfigurationDto;
import com.czertainly.api.model.core.signing.workflow.TimestampingConfigurationUpdateRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PolymorphicSerializationTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // -------------------------------------------------------------------------
    // TimestampingConfigurationDto
    // -------------------------------------------------------------------------

    @Test
    void timestampingConfigurationDto_serializesDiscriminator() throws Exception {
        TimestampingConfigurationDto dto = new TimestampingConfigurationDto();
        dto.setQualifiedTimestamp(true);

        JsonNode json = mapper.valueToTree(dto);

        assertEquals(SigningWorkflowType.Codes.TIMESTAMPING, json.get("type").asText());
    }

    @Test
    void timestampingConfigurationDto_deserializesViaBaseClass() throws Exception {
        String json = """
                {
                  "uuid": "11111111-0000-0000-0000-000000000001",
                  "name": "My TSA Workflow",
                  "type": "timestamping",
                  "qualifiedTimestamp": true,
                  "defaultPolicyId": "1.2.3.4.5",
                  "allowedPolicyIds": ["1.2.3.4.5", "1.2.3.4.6"],
                  "allowedDigestAlgorithms": ["SHA-256", "SHA-512"]
                }
                """;

        SigningWorkflowConfigurationDto base = mapper.readValue(json, SigningWorkflowConfigurationDto.class);

        assertInstanceOf(TimestampingConfigurationDto.class, base);
        TimestampingConfigurationDto result = (TimestampingConfigurationDto) base;
        assertEquals(SigningWorkflowType.TIMESTAMPING, result.getType());
        assertEquals("11111111-0000-0000-0000-000000000001", result.getUuid());
        assertEquals("My TSA Workflow", result.getName());
        assertTrue(result.getQualifiedTimestamp());
        assertEquals("1.2.3.4.5", result.getDefaultPolicyId());
        assertEquals(List.of("1.2.3.4.5", "1.2.3.4.6"), result.getAllowedPolicyIds());
        assertEquals(List.of(DigestAlgorithm.SHA_256, DigestAlgorithm.SHA_512), result.getAllowedDigestAlgorithms());
    }

    @Test
    void timestampingConfigurationDto_roundTrip() throws Exception {
        TimestampingConfigurationDto original = new TimestampingConfigurationDto();
        original.setUuid("11111111-0000-0000-0000-000000000001");
        original.setName("My TSA Workflow");
        original.setQualifiedTimestamp(false);
        original.setDefaultPolicyId("1.2.3.4.5");
        original.setAllowedPolicyIds(List.of("1.2.3.4.5", "1.2.3.4.6"));
        original.setAllowedDigestAlgorithms(List.of(DigestAlgorithm.SHA_256, DigestAlgorithm.SHA_384));

        String json = mapper.writeValueAsString(original);
        SigningWorkflowConfigurationDto deserialized = mapper.readValue(json, SigningWorkflowConfigurationDto.class);

        assertInstanceOf(TimestampingConfigurationDto.class, deserialized);
        assertEquals(original, deserialized);
    }

    // -------------------------------------------------------------------------
    // TimestampingConfigurationCreateRequestDto
    // -------------------------------------------------------------------------

    @Test
    void timestampingCreateRequestDto_serializesDiscriminator() throws Exception {
        TimestampingConfigurationCreateRequestDto dto = new TimestampingConfigurationCreateRequestDto();
        dto.setName("New Workflow");
        dto.setQualifiedTimestamp(true);

        JsonNode json = mapper.valueToTree(dto);

        assertEquals(SigningWorkflowType.Codes.TIMESTAMPING, json.get("type").asText());
    }

    @Test
    void timestampingCreateRequestDto_deserializesViaBaseClass() throws Exception {
        String json = """
                {
                  "name": "New TSA Workflow",
                  "type": "timestamping",
                  "qualifiedTimestamp": true,
                  "defaultPolicyId": "1.2.3.4.5",
                  "allowedDigestAlgorithms": ["SHA-256"]
                }
                """;

        SigningWorkflowConfigurationCreateRequestDto base =
                mapper.readValue(json, SigningWorkflowConfigurationCreateRequestDto.class);

        assertInstanceOf(TimestampingConfigurationCreateRequestDto.class, base);
        TimestampingConfigurationCreateRequestDto result = (TimestampingConfigurationCreateRequestDto) base;
        assertEquals(SigningWorkflowType.TIMESTAMPING, result.getType());
        assertEquals("New TSA Workflow", result.getName());
        assertTrue(result.getQualifiedTimestamp());
        assertEquals("1.2.3.4.5", result.getDefaultPolicyId());
        assertEquals(List.of(DigestAlgorithm.SHA_256), result.getAllowedDigestAlgorithms());
    }

    @Test
    void timestampingCreateRequestDto_roundTrip() throws Exception {
        TimestampingConfigurationCreateRequestDto original = new TimestampingConfigurationCreateRequestDto();
        original.setName("New TSA Workflow");
        original.setQualifiedTimestamp(true);
        original.setDefaultPolicyId("1.2.3.4.5");
        original.setAllowedPolicyIds(List.of("1.2.3.4.5"));
        original.setAllowedDigestAlgorithms(List.of(DigestAlgorithm.SHA_256, DigestAlgorithm.SHA_512));
        UUID tqUuid = UUID.randomUUID();
        original.setTimeQualityConfigurationUuid(tqUuid);

        String json = mapper.writeValueAsString(original);
        SigningWorkflowConfigurationCreateRequestDto deserialized = mapper.readValue(json, SigningWorkflowConfigurationCreateRequestDto.class);

        assertInstanceOf(TimestampingConfigurationCreateRequestDto.class, deserialized);
        assertEquals(original, deserialized);
    }

    // -------------------------------------------------------------------------
    // TimestampingConfigurationUpdateRequestDto
    // -------------------------------------------------------------------------

    @Test
    void timestampingUpdateRequestDto_serializesDiscriminator() throws Exception {
        TimestampingConfigurationUpdateRequestDto dto = new TimestampingConfigurationUpdateRequestDto();
        dto.setName("Updated Workflow");
        dto.setQualifiedTimestamp(false);

        JsonNode json = mapper.valueToTree(dto);

        assertEquals(SigningWorkflowType.Codes.TIMESTAMPING, json.get("type").asText());
    }

    @Test
    void timestampingUpdateRequestDto_roundTrip() throws Exception {
        TimestampingConfigurationUpdateRequestDto original = new TimestampingConfigurationUpdateRequestDto();
        original.setName("Updated TSA Workflow");
        original.setQualifiedTimestamp(false);
        original.setAllowedDigestAlgorithms(List.of(DigestAlgorithm.SHA_384));

        String json = mapper.writeValueAsString(original);
        SigningWorkflowConfigurationUpdateRequestDto deserialized = mapper.readValue(json, SigningWorkflowConfigurationUpdateRequestDto.class);

        assertInstanceOf(TimestampingConfigurationUpdateRequestDto.class, deserialized);
        assertEquals(original, deserialized);
    }

    // -------------------------------------------------------------------------
    // SigningSchemeDto — ManagedSigningDto
    // -------------------------------------------------------------------------

    @Test
    void managedSigningDto_serializesDiscriminator() throws Exception {
        ManagedSigningDto dto = new ManagedSigningDto();
        dto.setTokenProfile(new NameAndUuidDto("aaaa-bbbb", "Token"));
        dto.setCryptographicKey(new NameAndUuidDto("cccc-dddd", "Key"));

        JsonNode json = mapper.valueToTree(dto);

        assertEquals(SigningScheme.Codes.MANAGED, json.get("signingScheme").asText());
    }

    @Test
    void managedSigningDto_deserializesViaBaseClass() throws Exception {
        String json = """
                {
                  "signingScheme": "managed",
                  "tokenProfile": {"uuid": "aaaa-bbbb", "name": "Token"},
                  "cryptographicKey": {"uuid": "cccc-dddd", "name": "Key"}
                }
                """;

        SigningSchemeDto base = mapper.readValue(json, SigningSchemeDto.class);

        assertInstanceOf(ManagedSigningDto.class, base);
        ManagedSigningDto result = (ManagedSigningDto) base;
        assertEquals(SigningScheme.MANAGED, result.getSigningScheme());
        assertEquals("aaaa-bbbb", result.getTokenProfile().getUuid());
        assertEquals("cccc-dddd", result.getCryptographicKey().getUuid());
    }

    @Test
    void managedSigningDto_roundTrip() throws Exception {
        ManagedSigningDto original = new ManagedSigningDto();
        original.setTokenProfile(new NameAndUuidDto("aaaa-bbbb", "Token Profile"));
        original.setCryptographicKey(new NameAndUuidDto("cccc-dddd", "RSA Key"));

        String json = mapper.writeValueAsString(original);
        SigningSchemeDto deserialized = mapper.readValue(json, SigningSchemeDto.class);

        assertInstanceOf(ManagedSigningDto.class, deserialized);
        assertEquals(original, deserialized);
    }

    // -------------------------------------------------------------------------
    // SigningSchemeDto — DelegatedSigningDto
    // -------------------------------------------------------------------------

    @Test
    void delegatedSigningDto_serializesDiscriminator() throws Exception {
        DelegatedSigningDto dto = new DelegatedSigningDto();
        dto.setConnector(new NameAndUuidDto("eeee-ffff", "Connector"));

        JsonNode json = mapper.valueToTree(dto);

        assertEquals(SigningScheme.Codes.DELEGATED, json.get("signingScheme").asText());
    }

    @Test
    void delegatedSigningDto_deserializesViaBaseClass() throws Exception {
        String json = """
                {
                  "signingScheme": "delegated",
                  "connector": {"uuid": "eeee-ffff", "name": "My Connector"}
                }
                """;

        SigningSchemeDto base = mapper.readValue(json, SigningSchemeDto.class);

        assertInstanceOf(DelegatedSigningDto.class, base);
        DelegatedSigningDto result = (DelegatedSigningDto) base;
        assertEquals(SigningScheme.DELEGATED, result.getSigningScheme());
        assertEquals("eeee-ffff", result.getConnector().getUuid());
    }

    @Test
    void delegatedSigningDto_roundTrip() throws Exception {
        DelegatedSigningDto original = new DelegatedSigningDto();
        original.setConnector(new NameAndUuidDto("eeee-ffff", "My Connector"));

        String json = mapper.writeValueAsString(original);
        SigningSchemeDto deserialized = mapper.readValue(json, SigningSchemeDto.class);

        assertInstanceOf(DelegatedSigningDto.class, deserialized);
        assertEquals(original, deserialized);
    }

    // -------------------------------------------------------------------------
    // SigningSchemeRequestDto — ManagedSigningRequestDto
    // -------------------------------------------------------------------------

    @Test
    void managedSigningRequestDto_serializesDiscriminator() throws Exception {
        ManagedSigningRequestDto dto = new ManagedSigningRequestDto();
        dto.setTokenProfileUuid(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        dto.setCryptographicKeyUuid(UUID.fromString("22222222-2222-2222-2222-222222222222"));

        JsonNode json = mapper.valueToTree(dto);

        assertEquals(SigningScheme.Codes.MANAGED, json.get("signingScheme").asText());
    }

    @Test
    void managedSigningRequestDto_roundTrip() throws Exception {
        ManagedSigningRequestDto original = new ManagedSigningRequestDto();
        original.setTokenProfileUuid(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        original.setCryptographicKeyUuid(UUID.fromString("22222222-2222-2222-2222-222222222222"));

        String json = mapper.writeValueAsString(original);
        SigningSchemeRequestDto deserialized = mapper.readValue(json, SigningSchemeRequestDto.class);

        assertInstanceOf(ManagedSigningRequestDto.class, deserialized);
        assertEquals(original, deserialized);
    }

    // -------------------------------------------------------------------------
    // SigningSchemeRequestDto — DelegatedSigningRequestDto
    // -------------------------------------------------------------------------

    @Test
    void delegatedSigningRequestDto_serializesDiscriminator() throws Exception {
        DelegatedSigningRequestDto dto = new DelegatedSigningRequestDto();
        dto.setConnectorUuid(UUID.fromString("33333333-3333-3333-3333-333333333333"));

        JsonNode json = mapper.valueToTree(dto);

        assertEquals(SigningScheme.Codes.DELEGATED, json.get("signingScheme").asText());
    }

    @Test
    void delegatedSigningRequestDto_roundTrip() throws Exception {
        DelegatedSigningRequestDto original = new DelegatedSigningRequestDto();
        original.setConnectorUuid(UUID.fromString("33333333-3333-3333-3333-333333333333"));

        String json = mapper.writeValueAsString(original);
        SigningSchemeRequestDto deserialized = mapper.readValue(json, SigningSchemeRequestDto.class);

        assertInstanceOf(DelegatedSigningRequestDto.class, deserialized);
        assertEquals(original, deserialized);
    }

    // -------------------------------------------------------------------------
    // Unknown type discriminator guard
    // -------------------------------------------------------------------------

    @Test
    void unknownSigningWorkflowType_throwsOnDeserialization() {
        String json = """
                {
                  "type": "unknown_workflow_type",
                  "qualifiedTimestamp": true
                }
                """;

        assertThrows(InvalidTypeIdException.class, () -> mapper.readValue(json, SigningWorkflowConfigurationDto.class));
    }

    @Test
    void unknownSigningByType_throwsOnDeserialization() {
        String json = """
                {
                  "signingScheme": "unknown_signing_scheme"
                }
                """;

        assertThrows(InvalidTypeIdException.class, () -> mapper.readValue(json, SigningSchemeDto.class));
    }
}
