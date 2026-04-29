package com.otilm.api.config.serializer;

import com.otilm.api.model.client.attribute.ResponseAttributeV2;
import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v2.DataAttributeV2;
import com.otilm.api.model.common.attribute.v2.content.*;
import com.otilm.api.model.common.attribute.v2.content.BaseAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.CredentialAttributeContentV2;
import com.otilm.api.model.common.attribute.common.content.data.CredentialAttributeContentData;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResponseAttributeSerializer extends StdSerializer<List<BaseAttributeContentV2<?>>> {

    public ResponseAttributeSerializer() {
        super((Class<List<BaseAttributeContentV2<?>>>) null);
    }

    @Override
    public void serialize(List<BaseAttributeContentV2<?>> response, JsonGenerator gen, SerializerProvider provider) throws IOException {
        ResponseAttributeV2 responseAttribute = (ResponseAttributeV2) gen.getCurrentValue();
        if (response == null) {
            gen.writeNull();
            return;
        }
        if (responseAttribute.getContentType() == null) {
            gen.writeStartArray();
            for (AttributeContent content : response) {
                gen.writeObject(content);
            }
            gen.writeEndArray();
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        if (responseAttribute.getContentType().equals(AttributeContentType.SECRET)) {
            writeSecretAttributeContent(gen, responseAttribute, objectMapper);
        } else if (responseAttribute.getContentType().equals(AttributeContentType.CREDENTIAL)) {
            writeCredentialAttributeContent(gen, responseAttribute, objectMapper);
        } else {
            gen.writeStartArray();
            for (AttributeContent content : response) {
                gen.writeObject(content);
            }
        }
        gen.writeEndArray();
    }

    private static void writeCredentialAttributeContent(JsonGenerator gen, ResponseAttributeV2 responseAttribute, ObjectMapper objectMapper) throws IOException {
        gen.writeStartArray();
        for (BaseAttributeContentV2<?> credential : responseAttribute.getContent()) {
            CredentialAttributeContentV2 credentialAttributeContent = objectMapper.convertValue(credential, CredentialAttributeContentV2.class);
            List<DataAttributeV2> credentialAttributes = new ArrayList<>();
            CredentialAttributeContentData credentialDto = credentialAttributeContent.getData();

            // attributes can be null when serializing credential content not loaded with full credentials but as NameAndUuidDto
            if (credentialDto.getAttributes() != null) {
                addCredentialAttributes(objectMapper, credentialDto, credentialAttributes);
            }


            credentialDto.setAttributes(credentialAttributes);
            CredentialAttributeContentV2 credentialAttributeContentV2 = new CredentialAttributeContentV2(credential.getReference(),credentialDto);
            gen.writeObject(credentialAttributeContentV2);
        }
    }

    private static void addCredentialAttributes(ObjectMapper objectMapper, CredentialAttributeContentData credentialDto, List<DataAttributeV2> credentialAttributes) {
        for (DataAttributeV2 dataAttributeV2 : credentialDto.getAttributes()) {
            List<BaseAttributeContentV2<?>> credentialAttributeContents = new ArrayList<>();
            if (dataAttributeV2.getContentType().equals(AttributeContentType.SECRET)) {
                for (BaseAttributeContentV2<?> baseAttributeContent : dataAttributeV2.getContent()) {
                    SecretAttributeContentV2 secretAttributeContent = objectMapper.convertValue(baseAttributeContent, SecretAttributeContentV2.class);
                    secretAttributeContent.setData(null);
                    credentialAttributeContents.add(secretAttributeContent);
                }
            } else {
                credentialAttributeContents.addAll(dataAttributeV2.getContent());
            }
            dataAttributeV2.setContent(credentialAttributeContents);
            credentialAttributes.add(dataAttributeV2);
        }
    }

    private static void writeSecretAttributeContent(JsonGenerator gen, ResponseAttributeV2 responseAttribute, ObjectMapper objectMapper) throws IOException {
        gen.writeStartArray();
        for (Object content : responseAttribute.getContent()) {
            SecretAttributeContentV2 secretAttributeContent = objectMapper.convertValue(content, SecretAttributeContentV2.class);
            secretAttributeContent.setData(null);
            gen.writeObject(secretAttributeContent);
        }
    }
}

