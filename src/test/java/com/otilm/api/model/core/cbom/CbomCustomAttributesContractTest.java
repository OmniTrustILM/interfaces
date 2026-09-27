package com.otilm.api.model.core.cbom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.cryptoasset.CryptographicAssetDetailDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CbomCustomAttributesContractTest {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void cbomsAndCryptographicAssetsAcceptCustomAttributeDefinitions() {
        assertTrue(Resource.CBOM.hasCustomAttributes());
        assertTrue(Resource.CRYPTO_ASSET.hasCustomAttributes());
        assertTrue(Resource.getCustomAttributesResources().contains(Resource.CBOM));
        assertTrue(Resource.getCustomAttributesResources().contains(Resource.CRYPTO_ASSET));
    }

    @Test
    void uploadAndBothDetailsExposeCustomAttributesOnTheWire() throws Exception {
        CbomUploadRequestDto upload = mapper
                .readValue("{\"content\":{},\"customAttributes\":[]}", CbomUploadRequestDto.class);
        CbomDetailDto cbom = mapper.readValue("{\"customAttributes\":[]}", CbomDetailDto.class);
        CryptographicAssetDetailDto asset = mapper
                .readValue("{\"customAttributes\":[]}", CryptographicAssetDetailDto.class);

        assertEquals(0, upload.getCustomAttributes().size());
        assertEquals(0, cbom.getCustomAttributes().size());
        assertEquals(0, asset.getCustomAttributes().size());
        assertTrue(mapper.readTree(mapper.writeValueAsString(upload)).has("customAttributes"));
        assertTrue(mapper.readTree(mapper.writeValueAsString(cbom)).has("customAttributes"));
        assertTrue(mapper.readTree(mapper.writeValueAsString(asset)).has("customAttributes"));
        assertEquals(0, new CbomDetailDto().getCustomAttributes().size());
        assertEquals(0, new CryptographicAssetDetailDto().getCustomAttributes().size());
    }
}
