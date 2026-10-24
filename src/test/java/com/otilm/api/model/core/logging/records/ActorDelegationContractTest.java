package com.otilm.api.model.core.logging.records;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.core.logging.enums.ActorType;
import com.otilm.api.model.core.logging.enums.AuthMethod;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActorDelegationContractTest {

    private static final UUID USER_UUID = UUID.fromString("61d9bda2-f2b7-41b4-99cc-537ea41b4cbc");
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void theExistingConstructorOmitsDelegationAndPreservesTheExistingWireMembers() {
        ActorRecord actor = new ActorRecord(ActorType.USER, AuthMethod.TOKEN, USER_UUID, null);
        JsonNode json = mapper.valueToTree(actor);

        assertEquals("user", json.path("type").asText());
        assertEquals("token", json.path("authMethod").asText());
        assertEquals(USER_UUID.toString(), json.path("uuid").asText());
        assertTrue(json.has("name"));
        assertTrue(json.get("name").isNull());
        assertFalse(json.has("delegate"));
    }

    @Test
    void existingRecordsDeserializeAndRemainWithoutDelegation() throws Exception {
        ActorRecord actor = mapper.readValue("""
                {"type":"user", "authMethod":"token", "uuid":"61d9bda2-f2b7-41b4-99cc-537ea41b4cbc", "name":"Alice"}
                """, ActorRecord.class);

        assertEquals(ActorType.USER, actor.type());
        assertEquals(AuthMethod.TOKEN, actor.authMethod());
        assertEquals(USER_UUID, actor.uuid());
        assertEquals("Alice", actor.name());
        assertFalse(mapper.valueToTree(actor).has("delegate"));
    }

    @Test
    void theExistingBuilderLeavesDelegationAbsent() {
        ActorRecord actor = ActorRecord
                .builder()
                .type(ActorType.USER)
                .authMethod(AuthMethod.TOKEN)
                .uuid(USER_UUID)
                .name("Alice")
                .build();

        assertFalse(mapper.valueToTree(actor).has("delegate"));
    }

    @Test
    void theDelegateRoundTripsWithoutReplacingTheUserActor() throws Exception {
        ActorRecord actor = mapper.readValue("""
                {"type":"user", "authMethod":"token", "name":"Alice",
                 "delegate":{"identifier":"automation", "subject":"service-account-42"}}
                """, ActorRecord.class);
        JsonNode json = mapper.readTree(mapper.writeValueAsString(actor));

        assertEquals(ActorType.USER, actor.type());
        assertEquals(AuthMethod.TOKEN, actor.authMethod());
        assertEquals("Alice", actor.name());
        assertEquals("automation", json.path("delegate").path("identifier").asText());
        assertEquals("service-account-42", json.path("delegate").path("subject").asText());
    }

    @Test
    void theDelegateSubjectMayBeAbsent() throws Exception {
        ActorRecord actor = mapper.readValue("""
                {"type":"user", "authMethod":"token", "delegate":{"identifier":"automation"}}
                """, ActorRecord.class);
        JsonNode delegate = mapper.valueToTree(actor).get("delegate");

        assertNotNull(delegate);
        assertEquals("automation", delegate.path("identifier").asText());
        assertFalse(delegate.has("subject"));
    }

    @Test
    void aFutureNestedDelegateMemberDoesNotPreventReadingTheImmediateDelegate() throws Exception {
        ActorRecord actor = mapper.readValue("""
                {"type":"user", "authMethod":"token", "delegate":{"identifier":"automation",
                 "subject":"service-account-42", "act":{"identifier":"upstream-service"}, "futureMember":true}}
                """, ActorRecord.class);
        JsonNode delegate = mapper.valueToTree(actor).get("delegate");

        assertEquals("automation", delegate.path("identifier").asText());
        assertEquals("service-account-42", delegate.path("subject").asText());
    }

    @Test
    void theSchemaKeepsExistingActorRequirementsAndMakesOnlyTheDelegateIdentifierRequired() {
        Map<String, Schema<?>> schemas = readAll(ActorRecord.class);
        Schema<?> actor = schemas.get("ActorRecord");
        assertEquals(List.of("authMethod", "type"), actor.getRequired());
        assertEquals("#/components/schemas/ActorType", actor.getProperties().get("type").get$ref());
        assertEquals("#/components/schemas/AuthMethod", actor.getProperties().get("authMethod").get$ref());
        assertEquals("string", actor.getProperties().get("uuid").getType());
        assertEquals("uuid", actor.getProperties().get("uuid").getFormat());
        assertEquals("string", actor.getProperties().get("name").getType());
        Schema<?> delegate = actor.getProperties().get("delegate");
        assertNotNull(delegate);
        assertEquals("#/components/schemas/DelegateRecord", delegate.get$ref());
        Schema<?> delegateRecord = schemas.get("DelegateRecord");
        assertNotNull(delegateRecord);
        assertEquals(List.of("identifier"), delegateRecord.getRequired());
        assertEquals("string", delegateRecord.getProperties().get("identifier").getType());
        assertEquals("string", delegateRecord.getProperties().get("subject").getType());
        assertNull(delegateRecord.getAdditionalProperties());
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Schema<?>> readAll(Class<?> type) {
        return (Map<String, Schema<?>>) (Map<String, ?>) ModelConverters.getInstance().readAll(type);
    }
}
