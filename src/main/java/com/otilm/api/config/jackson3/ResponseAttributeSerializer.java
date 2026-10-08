package com.otilm.api.config.jackson3;

import com.otilm.api.model.client.attribute.ResponseAttributeV2;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.content.data.CredentialAttributeContentData;
import com.otilm.api.model.common.attribute.v2.DataAttributeV2;
import com.otilm.api.model.common.attribute.v2.content.BaseAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.CredentialAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.SecretAttributeContentV2;
import java.util.ArrayList;
import java.util.List;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.json.JsonMapper;

/**
 * Jackson 3 twin of {@link com.otilm.api.config.serializer.ResponseAttributeSerializer}: writes the content of a
 * {@link ResponseAttributeV2} with every secret left out.
 */
final class ResponseAttributeSerializer extends ValueSerializer<List<BaseAttributeContentV2<?>>> {

    private static final ObjectMapper CONVERTER = JsonMapper
            .builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    @Override
    public void serialize(List<BaseAttributeContentV2<?>> content, JsonGenerator gen, SerializationContext context) {
        ResponseAttributeV2 attribute = (ResponseAttributeV2) gen.currentValue();
        if (content == null) {
            gen.writeNull();
            return;
        }
        gen.writeStartArray();
        for (BaseAttributeContentV2<?> item : content) {
            context.writeValue(gen, withoutSecrets(attribute.getContentType(), item));
        }
        gen.writeEndArray();
    }

    private static Object withoutSecrets(AttributeContentType contentType, BaseAttributeContentV2<?> item) {
        if (contentType == AttributeContentType.SECRET) {
            SecretAttributeContentV2 secret = CONVERTER.convertValue(item, SecretAttributeContentV2.class);
            secret.setData(null);
            return secret;
        }
        if (contentType == AttributeContentType.CREDENTIAL) {
            CredentialAttributeContentData credential = CONVERTER
                    .convertValue(item, CredentialAttributeContentV2.class)
                    .getData();
            credential.setAttributes(credentialAttributesWithoutSecrets(credential.getAttributes()));
            return new CredentialAttributeContentV2(item.getReference(), credential);
        }
        return item;
    }

    /** A credential loaded as a bare name and UUID has no attributes, and gets an empty list. */
    private static List<DataAttributeV2> credentialAttributesWithoutSecrets(List<DataAttributeV2> attributes) {
        List<DataAttributeV2> result = new ArrayList<>();
        if (attributes == null) {
            return result;
        }
        for (DataAttributeV2 attribute : attributes) {
            if (attribute.getContentType() == AttributeContentType.SECRET) {
                List<BaseAttributeContentV2<?>> contents = new ArrayList<>();
                for (BaseAttributeContentV2<?> content : attribute.getContent()) {
                    SecretAttributeContentV2 secret = CONVERTER.convertValue(content, SecretAttributeContentV2.class);
                    secret.setData(null);
                    contents.add(secret);
                }
                attribute.setContent(contents);
            }
            result.add(attribute);
        }
        return result;
    }
}
