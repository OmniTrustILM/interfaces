package com.otilm.api.model.core.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.client.attribute.custom.CustomAttributeDefinitionDetailDto;
import com.otilm.api.model.client.attribute.metadata.GlobalMetadataDefinitionDetailDto;
import com.otilm.api.model.client.signing.profile.SigningProfileDto;
import com.otilm.api.model.client.signing.protocols.tsp.TspBasicCredentialDto;
import com.otilm.api.model.client.signing.protocols.tsp.TspProfileDto;
import com.otilm.api.model.client.signing.timequality.TimeQualityConfigurationDto;
import com.otilm.api.model.core.acme.AcmeProfileDto;
import com.otilm.api.model.core.authority.AuthorityInstanceDto;
import com.otilm.api.model.core.certificate.CertificateDetailDto;
import com.otilm.api.model.core.certificate.group.GroupDto;
import com.otilm.api.model.core.cmp.CmpProfileDetailDto;
import com.otilm.api.model.core.compliance.ComplianceProfileDto;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.model.core.connector.v2.ConnectorDetailDto;
import com.otilm.api.model.core.credential.CredentialDto;
import com.otilm.api.model.core.cryptography.key.KeyDetailDto;
import com.otilm.api.model.core.cryptography.token.TokenInstanceDetailDto;
import com.otilm.api.model.core.cryptography.tokenprofile.TokenProfileDetailDto;
import com.otilm.api.model.core.entity.EntityInstanceDto;
import com.otilm.api.model.core.location.LocationDto;
import com.otilm.api.model.core.proxy.ProxyDto;
import com.otilm.api.model.core.raprofile.RaProfileDto;
import com.otilm.api.model.core.scep.ScepProfileDetailDto;
import com.otilm.api.model.core.secret.SecretDetailDto;
import com.otilm.api.model.core.vault.VaultInstanceDetailDto;
import com.otilm.api.model.core.vaultprofile.VaultProfileDetailDto;
import com.otilm.api.testsupport.OpenApiSchemaTestSupport;
import com.otilm.core.model.auth.ResourceAction;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectActionsAwareTest {

    /** OID detail is addressed by the OID, not a UUID, so no object-level check applies to it. */
    private static final Set<Resource> WITHOUT_OBJECT_DETAIL = Set.of(Resource.OID);

    private static final Map<Resource, List<Class<? extends ObjectActionsAware>>> DETAIL_MODELS = Map
            .ofEntries(entry(Resource.CREDENTIAL, List.of(CredentialDto.class)),
                    entry(Resource.CONNECTOR, List.of(ConnectorDto.class, ConnectorDetailDto.class)),
                    entry(Resource.ATTRIBUTE,
                            List.of(CustomAttributeDefinitionDetailDto.class, GlobalMetadataDefinitionDetailDto.class)),
                    entry(Resource.PROXY, List.of(ProxyDto.class)),
                    entry(Resource.ACME_PROFILE, List.of(AcmeProfileDto.class)),
                    entry(Resource.SCEP_PROFILE, List.of(ScepProfileDetailDto.class)),
                    entry(Resource.CMP_PROFILE, List.of(CmpProfileDetailDto.class)),
                    entry(Resource.AUTHORITY, List.of(AuthorityInstanceDto.class)),
                    entry(Resource.RA_PROFILE, List.of(RaProfileDto.class)),
                    entry(Resource.CERTIFICATE, List.of(CertificateDetailDto.class)),
                    entry(Resource.GROUP, List.of(GroupDto.class)),
                    entry(Resource.COMPLIANCE_PROFILE,
                            List
                                    .of(ComplianceProfileDto.class,
                                            com.otilm.api.model.core.compliance.v2.ComplianceProfileDto.class)),
                    entry(Resource.ENTITY, List.of(EntityInstanceDto.class)),
                    entry(Resource.LOCATION, List.of(LocationDto.class)),
                    entry(Resource.TOKEN_PROFILE, List.of(TokenProfileDetailDto.class)),
                    entry(Resource.TOKEN, List.of(TokenInstanceDetailDto.class)),
                    entry(Resource.CRYPTOGRAPHIC_KEY, List.of(KeyDetailDto.class)),
                    entry(Resource.VAULT, List.of(VaultInstanceDetailDto.class)),
                    entry(Resource.VAULT_PROFILE, List.of(VaultProfileDetailDto.class)),
                    entry(Resource.SECRET, List.of(SecretDetailDto.class)),
                    entry(Resource.SIGNING_PROFILE, List.of(SigningProfileDto.class)),
                    entry(Resource.TIME_QUALITY_CONFIGURATION, List.of(TimeQualityConfigurationDto.class)),
                    entry(Resource.TSP_PROFILE, List.of(TspProfileDto.class)),
                    entry(Resource.TSP_PROFILE_BASIC_CREDENTIAL, List.of(TspBasicCredentialDto.class)));

    private static Stream<Class<? extends ObjectActionsAware>> detailModels() {
        return DETAIL_MODELS.values().stream().flatMap(List::stream);
    }

    @Test
    void coversEveryResourceWhoseObjectsAreAuthorizedIndividually() {
        Set<Resource> expected = Arrays
                .stream(Resource.values())
                .filter(resource -> resource.hasObjectAccess() || resource.hasOwner())
                .filter(resource -> !WITHOUT_OBJECT_DETAIL.contains(resource))
                .collect(Collectors.toSet());

        assertEquals(expected, DETAIL_MODELS.keySet());
    }

    @ParameterizedTest
    @MethodSource("detailModels")
    void publishesAllowedActionsAsAnOptionalListOfActionCodes(Class<? extends ObjectActionsAware> model) {
        Schema<?> schema = OpenApiSchemaTestSupport.openApi31Schema(model);
        Schema<?> property = (Schema<?>) schema.getProperties().get("allowedActions");

        assertTrue(property != null, model.getSimpleName() + ".allowedActions is not published");
        assertEquals("#/components/schemas/ResourceAction", property.getItems().get$ref());
        assertEquals(ObjectActionsAware.ALLOWED_ACTIONS_DESCRIPTION, property.getDescription());
        assertFalse(schema.getRequired() != null && schema.getRequired().contains("allowedActions"),
                "allowedActions must stay optional, since list responses sharing the model leave it out");
    }

    @Test
    void serializesActionsAsTheirWireCodes() throws Exception {
        SecretDetailDto secret = new SecretDetailDto();
        secret.setAllowedActions(List.of(ResourceAction.DETAIL, ResourceAction.GET_SECRET_CONTENT));

        assertTrue(new ObjectMapper()
                .findAndRegisterModules()
                .writeValueAsString(secret)
                .contains("\"allowedActions\":[\"detail\",\"getSecretContent\"]"));
    }

    @Test
    void documentsTheConstraintsAClientCannotInferFromTheTypes() {
        for (String constraint : List
                .of("same authorization check that guards each request", "ownership",
                        "members action on a group the object is assigned to", "detail response only",
                        "absent where the same model is returned in a list", "remains the only gate")) {
            assertTrue(ObjectActionsAware.ALLOWED_ACTIONS_DESCRIPTION.contains(constraint),
                    "the allowedActions description no longer states: " + constraint);
        }
    }
}
