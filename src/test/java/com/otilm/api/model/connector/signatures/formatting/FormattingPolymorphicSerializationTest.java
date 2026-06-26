package com.otilm.api.model.connector.signatures.formatting;

import com.otilm.api.model.client.signing.profile.workflow.SigningWorkflowType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormattingPolymorphicSerializationTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // -------------------------------------------------------------------------
    // FormatDtbsRequestDto — TimestampingFormatDtbsRequestDto
    // -------------------------------------------------------------------------

    @Test
    void timestampingFormatDtbsRequestDto_serializesDiscriminator() throws Exception {
        TimestampingFormatDtbsRequestDto dto = new TimestampingFormatDtbsRequestDto();
        dto.setData(new byte[]{1, 2, 3});

        JsonNode json = mapper.valueToTree(dto);

        assertEquals(SigningWorkflowType.Codes.TIMESTAMPING, json.get("type").asText());
    }

    @Test
    void timestampingFormatDtbsRequestDto_deserializesViaBaseClass() throws Exception {
        String json = """
                {
                  "type": "timestamping",
                  "data": "AQID",
                  "policy": "1.2.3.4.5",
                  "includeSignerCertificate": true,
                  "qualifiedTimestamp": false
                }
                """;

        FormatDtbsRequestDto base = mapper.readValue(json, FormatDtbsRequestDto.class);

        assertInstanceOf(TimestampingFormatDtbsRequestDto.class, base);
        TimestampingFormatDtbsRequestDto result = (TimestampingFormatDtbsRequestDto) base;
        assertEquals(SigningWorkflowType.TIMESTAMPING, result.getType());
        assertEquals("1.2.3.4.5", result.getPolicy());
        assertTrue(result.isIncludeSignerCertificate());
        assertFalse(result.isQualifiedTimestamp());
    }

    @Test
    void timestampingFormatDtbsRequestDto_roundTrip() throws Exception {
        TimestampingFormatDtbsRequestDto original = new TimestampingFormatDtbsRequestDto();
        original.setData(new byte[]{9, 8, 7});
        original.setPolicy("1.2.3.4.5");
        original.setIncludeSignerCertificate(true);

        String json = mapper.writeValueAsString(original);
        FormatDtbsRequestDto deserialized = mapper.readValue(json, FormatDtbsRequestDto.class);

        assertInstanceOf(TimestampingFormatDtbsRequestDto.class, deserialized);
        assertEquals(original, deserialized);
    }

    // -------------------------------------------------------------------------
    // FormatDtbsRequestDto — ContentSigningFormatDtbsRequestDto
    // -------------------------------------------------------------------------

    @Test
    void contentSigningFormatDtbsRequestDto_serializesDiscriminator() throws Exception {
        ContentSigningFormatDtbsRequestDto dto = new ContentSigningFormatDtbsRequestDto();
        dto.setData(new byte[]{1, 2, 3});

        JsonNode json = mapper.valueToTree(dto);

        assertEquals(SigningWorkflowType.Codes.CONTENT_SIGNING, json.get("type").asText());
    }

    @Test
    void contentSigningFormatDtbsRequestDto_deserializesViaBaseClass() throws Exception {
        String json = """
                {
                  "type": "content_signing",
                  "data": "AQID"
                }
                """;

        FormatDtbsRequestDto base = mapper.readValue(json, FormatDtbsRequestDto.class);

        assertInstanceOf(ContentSigningFormatDtbsRequestDto.class, base);
        assertEquals(SigningWorkflowType.CONTENT_SIGNING, base.getType());
    }

    @Test
    void contentSigningFormatDtbsRequestDto_roundTrip() throws Exception {
        ContentSigningFormatDtbsRequestDto original = new ContentSigningFormatDtbsRequestDto();
        original.setData(new byte[]{4, 5, 6});

        String json = mapper.writeValueAsString(original);
        FormatDtbsRequestDto deserialized = mapper.readValue(json, FormatDtbsRequestDto.class);

        assertInstanceOf(ContentSigningFormatDtbsRequestDto.class, deserialized);
        assertEquals(original, deserialized);
    }

    // -------------------------------------------------------------------------
    // Unknown/missing type guards — FormatDtbsRequestDto
    // -------------------------------------------------------------------------

    @Test
    void unknownFormatDtbsType_throwsOnDeserialization() {
        String json = """
                {
                  "type": "unknown_workflow_type"
                }
                """;

        assertThrows(InvalidTypeIdException.class, () -> mapper.readValue(json, FormatDtbsRequestDto.class));
    }

    @Test
    void missingFormatDtbsType_throwsOnDeserialization() {
        String json = """
                {
                  "data": "AQID"
                }
                """;

        assertThrows(InvalidTypeIdException.class, () -> mapper.readValue(json, FormatDtbsRequestDto.class));
    }

    // -------------------------------------------------------------------------
    // FormatResponseRequestDto — TimestampingFormatResponseRequestDto
    // -------------------------------------------------------------------------

    @Test
    void timestampingFormatResponseRequestDto_serializesDiscriminator() throws Exception {
        TimestampingFormatResponseRequestDto dto = new TimestampingFormatResponseRequestDto();
        dto.setSignature(new byte[]{1, 2, 3});

        JsonNode json = mapper.valueToTree(dto);

        assertEquals(SigningWorkflowType.Codes.TIMESTAMPING, json.get("type").asText());
    }

    @Test
    void timestampingFormatResponseRequestDto_deserializesViaBaseClass() throws Exception {
        String json = """
                {
                  "type": "timestamping",
                  "signature": "AQID",
                  "dtbs": "BAUG",
                  "policy": "1.2.3.4.5",
                  "includeSignerCertificate": true,
                  "qualifiedTimestamp": true
                }
                """;

        FormatResponseRequestDto base = mapper.readValue(json, FormatResponseRequestDto.class);

        assertInstanceOf(TimestampingFormatResponseRequestDto.class, base);
        TimestampingFormatResponseRequestDto result = (TimestampingFormatResponseRequestDto) base;
        assertEquals(SigningWorkflowType.TIMESTAMPING, result.getType());
        assertEquals("1.2.3.4.5", result.getPolicy());
        assertTrue(result.isIncludeSignerCertificate());
        assertTrue(result.isQualifiedTimestamp());
    }

    @Test
    void timestampingFormatResponseRequestDto_roundTrip() throws Exception {
        TimestampingFormatResponseRequestDto original = new TimestampingFormatResponseRequestDto();
        original.setSignature(new byte[]{1, 2, 3});
        original.setDtbs(new byte[]{4, 5, 6});
        original.setPolicy("1.2.3.4.5");
        original.setIncludeSignerCertificate(true);

        String json = mapper.writeValueAsString(original);
        FormatResponseRequestDto deserialized = mapper.readValue(json, FormatResponseRequestDto.class);

        assertInstanceOf(TimestampingFormatResponseRequestDto.class, deserialized);
        assertEquals(original, deserialized);
    }

    // -------------------------------------------------------------------------
    // FormatResponseRequestDto — ContentSigningFormatResponseRequestDto
    // -------------------------------------------------------------------------

    @Test
    void contentSigningFormatResponseRequestDto_serializesDiscriminator() throws Exception {
        ContentSigningFormatResponseRequestDto dto = new ContentSigningFormatResponseRequestDto();
        dto.setSignature(new byte[]{1, 2, 3});

        JsonNode json = mapper.valueToTree(dto);

        assertEquals(SigningWorkflowType.Codes.CONTENT_SIGNING, json.get("type").asText());
    }

    @Test
    void contentSigningFormatResponseRequestDto_deserializesViaBaseClass() throws Exception {
        String json = """
                {
                  "type": "content_signing",
                  "signature": "AQID",
                  "dtbs": "BAUG"
                }
                """;

        FormatResponseRequestDto base = mapper.readValue(json, FormatResponseRequestDto.class);

        assertInstanceOf(ContentSigningFormatResponseRequestDto.class, base);
        assertEquals(SigningWorkflowType.CONTENT_SIGNING, base.getType());
    }

    @Test
    void contentSigningFormatResponseRequestDto_roundTrip() throws Exception {
        ContentSigningFormatResponseRequestDto original = new ContentSigningFormatResponseRequestDto();
        original.setSignature(new byte[]{1, 2, 3});
        original.setDtbs(new byte[]{4, 5, 6});

        String json = mapper.writeValueAsString(original);
        FormatResponseRequestDto deserialized = mapper.readValue(json, FormatResponseRequestDto.class);

        assertInstanceOf(ContentSigningFormatResponseRequestDto.class, deserialized);
        assertEquals(original, deserialized);
    }

    // -------------------------------------------------------------------------
    // Unknown/missing type guards — FormatResponseRequestDto
    // -------------------------------------------------------------------------

    @Test
    void unknownFormatResponseType_throwsOnDeserialization() {
        String json = """
                {
                  "type": "unknown_workflow_type"
                }
                """;

        assertThrows(InvalidTypeIdException.class, () -> mapper.readValue(json, FormatResponseRequestDto.class));
    }

    @Test
    void missingFormatResponseType_throwsOnDeserialization() {
        String json = """
                {
                  "signature": "AQID"
                }
                """;

        assertThrows(InvalidTypeIdException.class, () -> mapper.readValue(json, FormatResponseRequestDto.class));
    }
}
