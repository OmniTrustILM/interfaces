package com.otilm.api.model.connector.v3.authority;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.MetadataAttributeProperties;
import com.otilm.api.model.common.attribute.v3.MetadataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuthorityConnectionResponseDtoTest {

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @Test
    void roundTripsMetadata() throws Exception {
        MetadataAttributeV3 attribute = new MetadataAttributeV3();
        attribute.setUuid("6f0d2a52-6a4b-4c1e-9d1a-3b1e2c4d5e6f");
        attribute.setName("acmeAccountUrl");
        attribute.setContentType(AttributeContentType.STRING);
        MetadataAttributeProperties properties = new MetadataAttributeProperties();
        properties.setLabel("ACME account URL");
        attribute.setProperties(properties);
        attribute.setContent(List.of(new StringAttributeContentV3("https://ca.example/acct/1")));
        AuthorityConnectionResponseDto dto = new AuthorityConnectionResponseDto();
        dto.setMetadata(List.of(attribute));

        AuthorityConnectionResponseDto back = mapper
                .readValue(mapper.writeValueAsString(dto), AuthorityConnectionResponseDto.class);

        MetadataAttributeV3 backAttribute = assertInstanceOf(MetadataAttributeV3.class, back.getMetadata().get(0));
        assertEquals("acmeAccountUrl", backAttribute.getName());
        assertEquals("https://ca.example/acct/1",
                ((StringAttributeContentV3) backAttribute.getContent().get(0)).getData());
    }

    @Test
    void readsEmptyObjectWithoutMetadata() throws Exception {
        AuthorityConnectionResponseDto back = mapper.readValue("{}", AuthorityConnectionResponseDto.class);

        assertNull(back.getMetadata());
    }
}
