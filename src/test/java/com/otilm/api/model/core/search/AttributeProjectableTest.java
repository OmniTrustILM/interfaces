package com.otilm.api.model.core.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.client.discovery.DiscoveryListDto;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.core.cbom.CbomDto;
import com.otilm.api.model.core.certificate.CertificateDto;
import com.otilm.api.model.core.connector.v2.ConnectorDto;
import com.otilm.api.model.core.cryptoasset.CryptographicAssetDto;
import com.otilm.api.model.core.cryptography.key.KeyItemDto;
import com.otilm.api.model.core.secret.SecretDto;
import com.otilm.api.model.core.signing.signingrecord.SigningRecordListDto;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Every listing that supports configurable columns must carry projected attribute values the same way, so the
 * projection in Core can populate any of them through {@link AttributeProjectable} instead of a branch per resource.
 */
class AttributeProjectableTest {

    private final ObjectMapper mapper = new ObjectMapper();

    static Stream<Named<Supplier<AttributeProjectable>>> listings() {
        return Stream
                .of(Named.of("certificates", CertificateDto::new), Named.of("keys", KeyItemDto::new),
                        Named.of("discoveries", DiscoveryListDto::new), Named.of("connectors", ConnectorDto::new),
                        Named.of("secrets", SecretDto::new), Named.of("cboms", CbomDto::new),
                        Named.of("signingRecords", SigningRecordListDto::new),
                        Named.of("cryptographicAssets", CryptographicAssetDto::new));
    }

    @ParameterizedTest
    @MethodSource("listings")
    void omitsProjectedValuesWhenNoColumnsWereRequested(Supplier<AttributeProjectable> factory) throws Exception {
        // given — the compatibility path every supported listing has to keep
        var dto = factory.get();

        // then
        assertNull(dto.getAttributeValues());
        assertFalse(mapper.writeValueAsString(dto).contains("attributeValues"));
    }

    @ParameterizedTest
    @MethodSource("listings")
    void roundTripsProjectedValuesKeyedBySourceThenIdentifier(Supplier<AttributeProjectable> factory) throws Exception {
        // given
        var dto = factory.get();
        dto
                .setAttributeValues(Map
                        .of(FilterFieldSource.CUSTOM,
                                Map.of("department", List.of(new StringAttributeContentV3("PKI Ops")))));

        // when
        AttributeProjectable back = mapper.readValue(mapper.writeValueAsString(dto), dto.getClass());

        // then
        assertEquals("PKI Ops",
                back.getAttributeValues().get(FilterFieldSource.CUSTOM).get("department").get(0).getData());
    }
}
