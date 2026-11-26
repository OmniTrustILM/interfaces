package com.otilm.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.otilm.api.model.common.attribute.v1.AttributeDefinition;
import com.otilm.api.model.common.attribute.v1.AttributeType;
import com.otilm.api.model.common.attribute.v1.content.JsonAttributeContent;
import com.otilm.api.model.common.attribute.v2.content.CredentialAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.StringAttributeContentV2;
import com.otilm.core.util.V2AttributeMigrationUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class V2AttributeMigrationUtilsTest {
    private static final String CANARY = "synthetic-migration-canary";

    @Test
    @ResourceLock(Resources.SYSTEM_ERR)
    void serializationFailureKeepsCredentialMetadataWithoutExposingDiagnosticData() {
        var captured = new ByteArrayOutputStream();
        var previous = System.err;
        var logger = assertInstanceOf(Logger.class, LoggerFactory.getLogger(V2AttributeMigrationUtils.class));
        var previousLevel = logger.getLevel();
        var recorder = new ListAppender<ILoggingEvent>();
        recorder.setContext(logger.getLoggerContext());
        recorder.start();
        logger.setLevel(Level.WARN);
        logger.addAppender(recorder);
        try (var errorStream = new PrintStream(captured, true, StandardCharsets.UTF_8)) {
            System.setErr(errorStream);
            var converted = V2AttributeMigrationUtils.getNewAttributes(credential(new FailingAttributes()));
            var content = assertInstanceOf(CredentialAttributeContentV2.class, converted.getContent().getFirst());
            assertEquals("credential-name", content.getData().getName());
            assertEquals("00000000-0000-0000-0000-000000000001", content.getData().getUuid());
            assertEquals("Basic", content.getData().getKind());
            assertTrue(content.getData().getAttributes().isEmpty());
        } finally {
            System.setErr(previous);
            logger.detachAppender(recorder);
            logger.setLevel(previousLevel);
            recorder.stop();
        }
        String diagnostic = captured.toString(StandardCharsets.UTF_8);
        assertFalse(diagnostic.contains(CANARY));
        assertFalse(diagnostic.contains("credential-name"));
        assertFalse(diagnostic.contains("00000000-0000-0000-0000-000000000001"));
        assertEquals(1, recorder.list.size());
        var warning = recorder.list.getFirst();
        assertEquals(Level.WARN, warning.getLevel());
        assertEquals("Credential attributes could not be migrated.", warning.getFormattedMessage());
        assertNull(warning.getThrowableProxy());
    }

    @Test
    void validCredentialAttributesAreConverted() {
        var nested = new AttributeDefinition();
        nested.setName("username");
        nested.setType(AttributeType.STRING);
        nested.setContent(Map.of("value", "retained-value"));
        var converted = V2AttributeMigrationUtils.getNewAttributes(credential(List.of(nested)));
        var content = assertInstanceOf(CredentialAttributeContentV2.class, converted.getContent().getFirst());
        assertEquals(1, content.getData().getAttributes().size());
        var attribute = content.getData().getAttributes().getFirst();
        assertEquals("username", attribute.getName());
        var value = assertInstanceOf(StringAttributeContentV2.class, attribute.getContent().getFirst());
        assertEquals("retained-value", value.getData());
    }

    private static AttributeDefinition credential(Object attributes) {
        var data = new LinkedHashMap<String, Object>();
        data.put("name", "credential-name");
        data.put("uuid", "00000000-0000-0000-0000-000000000001");
        data.put("kind", "Basic");
        data.put("attributes", attributes);
        var attribute = new AttributeDefinition();
        attribute.setName("credential");
        attribute.setType(AttributeType.CREDENTIAL);
        attribute.setContent(new JsonAttributeContent("reference", data));
        return attribute;
    }

    private static final class FailingAttributes extends JsonSerializable.Base {
        @Override
        public void serialize(JsonGenerator generator, SerializerProvider provider) throws IOException {
            if (generator instanceof TokenBuffer) {
                generator.writeEmbeddedObject(this);
            } else {
                throw JsonMappingException.from(generator, CANARY);
            }
        }

        @Override
        public void serializeWithType(JsonGenerator generator, SerializerProvider provider, TypeSerializer serializer)
                throws IOException {
            serialize(generator, provider);
        }
    }
}
