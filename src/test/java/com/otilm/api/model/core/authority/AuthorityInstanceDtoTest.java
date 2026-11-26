package com.otilm.api.model.core.authority;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.client.metadata.MetadataResponseDto;
import com.otilm.api.model.client.metadata.ResponseMetadataV3;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthorityInstanceDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void roundTripsMetadata() throws Exception {
        // given
        MetadataResponseDto metadata = new MetadataResponseDto();
        metadata.setConnectorName("ACME");
        metadata
                .setItems(List
                        .of(new ResponseMetadataV3(List.of(), UUID.randomUUID(), "acmeAccountUrl", "ACME account URL",
                                AttributeType.META, AttributeContentType.STRING,
                                List.of(new StringAttributeContentV3("https://ca.example/acct/1")))));
        AuthorityInstanceDto dto = new AuthorityInstanceDto();
        dto.setName("acme");
        dto.setMetadata(List.of(metadata));

        // when
        String json = mapper.writeValueAsString(dto);
        AuthorityInstanceDto back = mapper.readValue(json, AuthorityInstanceDto.class);

        // then
        assertTrue(json.contains("\"metadata\":[{"));
        assertEquals("ACME", back.getMetadata().get(0).getConnectorName());
        ResponseMetadataV3 item = assertInstanceOf(ResponseMetadataV3.class,
                back.getMetadata().get(0).getItems().get(0));
        assertEquals("acmeAccountUrl", item.getName());
        assertTrue(dto.toString().contains("acmeAccountUrl"));
    }
}
