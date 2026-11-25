package com.otilm.api.model.client.metadata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.AttributeVersion;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v2.content.StringAttributeContentV2;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ResponseMetadataJsonTest {

    private static final String CONNECTOR_METADATA = """
            {
              "connectorUuid": "6f1c8a2e-0000-4000-8000-000000000001",
              "connectorName": "ACME",
              "items": [{
                "sourceObjects": [],
                "uuid": "6f1c8a2e-0000-4000-8000-000000000002",
                "name": "acmeOrderUrl",
                "label": "ACME Order URL",
                "type": "meta",
                "contentType": "string",
                "content": [{"reference": null, "data": "https://acme.example.com/order/1", "contentType": "string"}],
                "version": "v3"
              }]
            }
            """;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void readsTheConnectorMetadataCoreSends() throws Exception {
        // when
        MetadataResponseDto read = mapper.readValue(CONNECTOR_METADATA, MetadataResponseDto.class);

        // then
        ResponseMetadataV3 item = assertInstanceOf(ResponseMetadataV3.class, read.getItems().get(0));
        assertEquals("acmeOrderUrl", item.getName());
        assertEquals(AttributeVersion.V3, item.getVersion());
        StringAttributeContentV3 content = assertInstanceOf(StringAttributeContentV3.class, item.getContent().get(0));
        assertEquals("https://acme.example.com/order/1", content.getData());
    }

    @Test
    void writesAndReadsBackAV3Item() throws Exception {
        // given
        ResponseMetadata written = new ResponseMetadataV3(List.of(), UUID.randomUUID(), "acmeOrderUrl", "Order URL",
                AttributeType.META, AttributeContentType.STRING,
                List.of(new StringAttributeContentV3("https://acme.example.com/order/1")));

        // when
        String json = mapper.writeValueAsString(written);
        ResponseMetadata read = mapper.readValue(json, ResponseMetadata.class);

        // then
        assertEquals("v3", mapper.readTree(json).get("version").asText());
        ResponseMetadataV3 item = assertInstanceOf(ResponseMetadataV3.class, read);
        assertEquals(written.getUuid(), item.getUuid());
        assertEquals("https://acme.example.com/order/1", item.getContent().get(0).getData());
    }

    @Test
    void writesAndReadsBackAV2Item() throws Exception {
        // given
        ResponseMetadata written = new ResponseMetadataV2(List.of(), UUID.randomUUID(), "acmeOrderUrl", "Order URL",
                AttributeType.META, AttributeContentType.STRING,
                List.of(new StringAttributeContentV2("https://acme.example.com/order/1")));

        // when
        String json = mapper.writeValueAsString(written);
        ResponseMetadata read = mapper.readValue(json, ResponseMetadata.class);

        // then
        assertEquals("v2", mapper.readTree(json).get("version").asText());
        ResponseMetadataV2 item = assertInstanceOf(ResponseMetadataV2.class, read);
        assertEquals(written.getUuid(), item.getUuid());
        assertEquals("https://acme.example.com/order/1", item.getContent().get(0).getData());
    }

    @Test
    void readsAnItemWithoutAVersionAsV3() throws Exception {
        // given
        JsonNode item = mapper.readTree(CONNECTOR_METADATA).get("items").get(0);
        ((ObjectNode) item).remove("version");

        // when
        ResponseMetadata read = mapper.treeToValue(item, ResponseMetadata.class);

        // then
        assertInstanceOf(ResponseMetadataV3.class, read);
    }
}
