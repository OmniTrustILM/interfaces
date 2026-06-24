package com.otilm.api.model.client.connector.v2.attribute;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.attribute.RequestAttributeV2;
import com.otilm.api.model.client.attribute.RequestAttributeV3;
import com.otilm.api.model.client.connector.v2.ConnectorInterface;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.common.callback.AttributeCallback;
import com.otilm.api.model.common.attribute.common.callback.RequestAttributeCallback;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.common.attribute.v3.GroupAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.core.auth.Resource;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AttributeV2DtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    // ---- fixtures (NON-EMPTY, mixed v2/v3 — exercise the real polymorphism) ----

    private static DataAttributeV3 dataDefinition() {
        DataAttributeV3 d = new DataAttributeV3();
        d.setUuid(UUID.randomUUID().toString());
        d.setName("country");
        d.setType(AttributeType.DATA);
        d.setContentType(AttributeContentType.STRING);          // else BaseAttributeSerializer can't write the DATA arm
        d.setContent(List.of(new StringAttributeContentV3("CZ")));
        return d;
    }

    private static GroupAttributeV3 groupDefinition() {
        GroupAttributeV3 g = new GroupAttributeV3();            // no-arg ctor sets type = GROUP
        g.setName("profileGroup");
        g.setContent(List.of());
        return g;
    }

    private static RequestAttributeV3 currentV3() {
        RequestAttributeV3 r = new RequestAttributeV3();
        r.setName("raProfile");
        r.setContentType(AttributeContentType.STRING);
        r.setContent(List.of(new StringAttributeContentV3("issuing")));
        return r;
    }

    private static RequestAttributeV2 currentV2() {
        RequestAttributeV2 r = new RequestAttributeV2();
        r.setName("legacyField");
        return r;
    }

    // ---- Task 1: AttributeCallback.dependsOn ----

    @Test
    void attributeCallback_dependsOn_roundTrips() throws Exception {
        AttributeCallback cb = new AttributeCallback();
        cb.setDependsOn(List.of("endEntityProfile", "certificateProfile"));

        AttributeCallback back = mapper.readValue(mapper.writeValueAsString(cb), AttributeCallback.class);

        assertEquals(List.of("endEntityProfile", "certificateProfile"), back.getDependsOn());
        assertTrue(cb.toString().contains("dependsOn"), "toString must render dependsOn");
    }

    // ---- Task 2: AttributeDefinitionsDto + ScopedAttributes ----

    @Test
    void attributeDefinitionsDto_roundTrips_withMixedDataAndGroupDefinitions() throws Exception {
        AttributeDefinitionsDto dto = new AttributeDefinitionsDto();
        dto.setConnectorVersion("1.4.2-ejbca");
        dto.setDefinitions(List.of(dataDefinition(), groupDefinition()));

        AttributeDefinitionsDto back =
                mapper.readValue(mapper.writeValueAsString(dto), AttributeDefinitionsDto.class);

        assertEquals("1.4.2-ejbca", back.getConnectorVersion());
        List<BaseAttribute> defs = back.getDefinitions();
        assertEquals(2, defs.size());
        assertInstanceOf(DataAttributeV3.class, defs.get(0));
        assertInstanceOf(GroupAttributeV3.class, defs.get(1));
    }

    @Test
    void attributeDefinitionsDto_roundTrips_withNonEmptyGroupChildren() throws Exception {
        // A GROUP definition whose content carries child attributes: the children are BaseAttributeV3
        // (polymorphic-typed), so serializing them goes through serializeWithType — which must not throw.
        GroupAttributeV3 child = new GroupAttributeV3();
        child.setName("childGroup");
        child.setContent(List.of());
        GroupAttributeV3 parent = new GroupAttributeV3();
        parent.setName("parentGroup");
        parent.setContent(List.of(child));

        AttributeDefinitionsDto dto = new AttributeDefinitionsDto();
        dto.setConnectorVersion("1.0");
        dto.setDefinitions(List.of(parent));

        AttributeDefinitionsDto back =
                mapper.readValue(mapper.writeValueAsString(dto), AttributeDefinitionsDto.class);

        assertInstanceOf(GroupAttributeV3.class, back.getDefinitions().get(0));
        GroupAttributeV3 backParent = (GroupAttributeV3) back.getDefinitions().get(0);
        assertEquals(1, backParent.getContent().size());
        assertInstanceOf(GroupAttributeV3.class, backParent.getContent().get(0));
        assertEquals("childGroup", backParent.getContent().get(0).getName());
    }

    @Test
    void scopedAttributes_roundTrips() throws Exception {
        ScopedAttributes scope = new ScopedAttributes();
        scope.setScope(Resource.AUTHORITY);
        scope.setObjectUuid(UUID.randomUUID());
        scope.setAttributes(List.of(currentV3()));

        ScopedAttributes back = mapper.readValue(mapper.writeValueAsString(scope), ScopedAttributes.class);

        assertEquals(Resource.AUTHORITY, back.getScope());
        assertEquals(1, back.getAttributes().size());
        assertInstanceOf(RequestAttributeV3.class, back.getAttributes().get(0));
    }

    @Test
    void scopedAttributes_scopeSerializesToPluralCode() throws Exception {
        ScopedAttributes scope = new ScopedAttributes();
        scope.setScope(Resource.AUTHORITY);
        scope.setAttributes(List.of());

        assertTrue(mapper.writeValueAsString(scope).contains("\"scope\":\"authorities\""),
                "Resource serializes via @JsonValue getCode() to the PLURAL code");
    }

    // ---- Task 3: AttributeCallbackResponseDto + AttributeCallbackRequestDto ----

    @Test
    void callbackResponse_isExactlyOneArmSet_trueForOneArm_falseForBothOrNeither() {
        AttributeCallbackResponseDto contentArm = new AttributeCallbackResponseDto();
        contentArm.setContent(List.of(new StringAttributeContentV3("x")));
        assertTrue(contentArm.isExactlyOneArmSet());

        AttributeCallbackResponseDto attrArm = new AttributeCallbackResponseDto();
        attrArm.setAttributes(List.of(dataDefinition()));
        assertTrue(attrArm.isExactlyOneArmSet());

        AttributeCallbackResponseDto both = new AttributeCallbackResponseDto();
        both.setContent(List.of(new StringAttributeContentV3("x")));
        both.setAttributes(List.of(dataDefinition()));
        assertFalse(both.isExactlyOneArmSet());

        assertFalse(new AttributeCallbackResponseDto().isExactlyOneArmSet());
    }

    @Test
    void callbackResponse_contentArm_roundTrips_withV3Content() throws Exception {
        AttributeCallbackResponseDto dto = new AttributeCallbackResponseDto();
        dto.setContent(List.of(new StringAttributeContentV3("issuing-ca")));
        dto.setTotalItems(1L);

        AttributeCallbackResponseDto back =
                mapper.readValue(mapper.writeValueAsString(dto), AttributeCallbackResponseDto.class);

        assertEquals(1, back.getContent().size());
        assertInstanceOf(StringAttributeContentV3.class, back.getContent().get(0));
        assertEquals(1L, back.getTotalItems());
    }

    @Test
    void callbackRequest_roundTrips_withMixedV2V3CurrentAttributes() throws Exception {
        AttributeCallbackRequestDto dto = new AttributeCallbackRequestDto();
        dto.setConnectorInterface(ConnectorInterface.AUTHORITY);
        dto.setInterfaceVersion("v3");
        dto.setAttributeUuid(UUID.randomUUID());
        dto.setAttributeName("raProfile");
        ScopedAttributes scope = new ScopedAttributes();
        scope.setScope(Resource.AUTHORITY);
        scope.setAttributes(List.of(currentV3()));
        dto.setContextAttributes(List.of(scope));
        dto.setCurrentAttributes(List.of(currentV2(), currentV3()));

        AttributeCallbackRequestDto back =
                mapper.readValue(mapper.writeValueAsString(dto), AttributeCallbackRequestDto.class);

        assertEquals(ConnectorInterface.AUTHORITY, back.getConnectorInterface());
        assertEquals(1, back.getContextAttributes().size());
        List<RequestAttribute> current = back.getCurrentAttributes();
        assertEquals(2, current.size());
        assertInstanceOf(RequestAttributeV2.class, current.get(0));
        assertInstanceOf(RequestAttributeV3.class, current.get(1));
    }

    @Test
    void callbackRequest_versionlessAttribute_defaultsToV2() throws Exception {
        // hand-authored JSON with NO "version" on the attribute → fail-open to V2 (documents the hazard)
        String json = """
                {"connectorInterface":"authority","interfaceVersion":"v3",
                 "attributeUuid":"%s","attributeName":"x",
                 "contextAttributes":[],
                 "currentAttributes":[{"name":"legacyField"}]}
                """.formatted(UUID.randomUUID());

        AttributeCallbackRequestDto back = mapper.readValue(json, AttributeCallbackRequestDto.class);

        assertInstanceOf(RequestAttributeV2.class, back.getCurrentAttributes().get(0));
    }

    // ---- Task 10: RequestAttributeCallback.attributes ----

    @Test
    void requestAttributeCallback_attributes_roundTrips() throws Exception {
        RequestAttributeCallback cb = new RequestAttributeCallback();
        cb.setName("raProfile");
        cb.setAttributes(List.of(currentV3()));

        RequestAttributeCallback back =
                mapper.readValue(mapper.writeValueAsString(cb), RequestAttributeCallback.class);

        assertEquals(1, back.getAttributes().size());
        assertEquals("raProfile", back.getAttributes().get(0).getName());
        assertTrue(cb.toString().contains("attributes"), "toString must render attributes");
    }
}
