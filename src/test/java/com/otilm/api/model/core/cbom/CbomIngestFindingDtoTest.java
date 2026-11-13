package com.otilm.api.model.core.cbom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.media.Schema;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.otilm.api.testsupport.OpenApiSchemaTestSupport.openApi31Schemas;

/**
 * One line of a CBOM's ingest report: every member but the component name is always present, and the component name is
 * omitted rather than sent as null for a line about the document as a whole.
 */
class CbomIngestFindingDtoTest {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void everyMemberButTheComponentNameIsRequired() {
        Schema row = openApi31Schemas(CbomIngestFindingDto.class).get(CbomIngestFindingDto.class.getSimpleName());
        Assertions.assertNotNull(row);
        List<String> required = List.of("kind", "detail", "occurrences", "recordedAt");
        Assertions.assertEquals(required.stream().sorted().toList(), row.getRequired().stream().sorted().toList());
        Assertions
                .assertEquals(List.of("componentName", "detail", "kind", "occurrences", "recordedAt"),
                        row.getProperties().keySet().stream().sorted().toList(),
                        "no member beyond the documented ones");
    }

    @Test
    void roundTripsThroughJsonWithTheKindAsItsWireCode() throws Exception {
        CbomIngestFindingDto dto = new CbomIngestFindingDto();
        dto.setKind(CbomIngestFindingKind.SKIP);
        dto.setComponentName("a-signing-key");
        dto.setDetail("the component could not be turned into an asset: no cryptographic properties");
        dto.setOccurrences(3);
        dto.setRecordedAt(OffsetDateTime.of(2026, 10, 1, 12, 0, 0, 0, ZoneOffset.UTC));

        JsonNode json = mapper.readTree(mapper.writeValueAsString(dto));
        Assertions.assertEquals("skip", json.get("kind").asText());
        Assertions.assertEquals("a-signing-key", json.get("componentName").asText());
        Assertions.assertEquals(3, json.get("occurrences").asInt());
        Assertions.assertEquals(dto, mapper.readValue(json.toString(), CbomIngestFindingDto.class));
    }

    @Test
    void aLineAboutTheDocumentOmitsTheComponentName() throws Exception {
        CbomIngestFindingDto dto = new CbomIngestFindingDto();
        dto.setKind(CbomIngestFindingKind.FINDING);
        dto.setDetail("bom-ref dup is defined more than once, and CycloneDX requires it to be unique");
        dto.setOccurrences(1);
        dto.setRecordedAt(OffsetDateTime.of(2026, 10, 1, 12, 0, 0, 0, ZoneOffset.UTC));

        JsonNode json = mapper.readTree(mapper.writeValueAsString(dto));
        Assertions.assertFalse(json.has("componentName"), "a document-level line carries no component member");
        Assertions.assertNull(mapper.readValue(json.toString(), CbomIngestFindingDto.class).getComponentName());
    }
}
