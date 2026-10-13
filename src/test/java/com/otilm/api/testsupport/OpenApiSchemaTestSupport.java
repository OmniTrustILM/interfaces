package com.otilm.api.testsupport;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Map;

/**
 * Generates schemas in the OpenAPI 3.1 mode used by the published specification.
 */
public final class OpenApiSchemaTestSupport {

    private OpenApiSchemaTestSupport() {
    }

    public static Map<String, Schema> openApi31Schemas(Class<?> root) {
        return ModelConverters.getInstance(true).readAll(root);
    }

    public static Schema<?> openApi31Schema(Class<?> type) {
        return ModelConverters.getInstance(true).resolveAsResolvedSchema(new AnnotatedType(type)).schema;
    }
}
