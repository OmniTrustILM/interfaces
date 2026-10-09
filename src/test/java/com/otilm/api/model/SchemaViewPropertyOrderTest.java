package com.otilm.api.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static com.otilm.api.testsupport.ClassScanTestSupport.classesIn;
import static com.otilm.api.testsupport.OpenApiSchemaTestSupport.openApi31Schema;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * The getter order a {@code oneOf} schema view falls back on changes between JVM starts.
 */
class SchemaViewPropertyOrderTest {

    @Test
    void oneOfSchemaViews_nameTheirPropertyOrder() throws Exception {
        // given
        List<Class<?>> views = classesIn(SchemaViewPropertyOrderTest.class.getPackageName())
                .stream()
                .filter(SchemaViewPropertyOrderTest::isOneOfView)
                .filter(view -> schemaPropertyNames(view).size() > 1)
                .toList();

        // when
        List<String> unordered = views
                .stream()
                .filter(view -> !declaredPropertyOrder(view).equals(schemaPropertyNames(view)))
                .map(view -> view.getName() + " generates " + schemaPropertyNames(view))
                .toList();

        // then
        assertFalse(views.isEmpty(), "No oneOf schema views with properties discovered");
        assertEquals(List.of(), unordered,
                "Each oneOf schema view must name all its schema properties in a @JsonPropertyOrder of its own");
    }

    private static boolean isOneOfView(Class<?> type) {
        Schema schema = type.getDeclaredAnnotation(Schema.class);
        return schema != null && schema.oneOf().length > 0;
    }

    private static List<String> declaredPropertyOrder(Class<?> view) {
        JsonPropertyOrder order = view.getDeclaredAnnotation(JsonPropertyOrder.class);
        return order == null ? List.of() : List.of(order.value());
    }

    private static List<String> schemaPropertyNames(Class<?> view) {
        Map<String, ?> properties = openApi31Schema(view).getProperties();
        return properties == null ? List.of() : List.copyOf(properties.keySet());
    }
}
