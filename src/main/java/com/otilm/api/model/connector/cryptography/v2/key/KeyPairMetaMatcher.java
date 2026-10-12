package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The equality {@code keyPairMetaFilter} promises: the same metadata names in any order, each with the same data in the
 * same order. A handle with a repeated name or a content item without data is ambiguous and matches nothing.
 */
public final class KeyPairMetaMatcher {

    private KeyPairMetaMatcher() {
    }

    public static boolean matches(List<MetadataAttribute> handle, List<MetadataAttribute> other) {
        Optional<Map<String, List<Object>>> handleData = dataByName(handle);
        return handleData.isPresent() && handleData.equals(dataByName(other));
    }

    public static boolean isUnambiguous(List<MetadataAttribute> handle) {
        return dataByName(handle).isPresent();
    }

    /**
     * Only content data takes part. A connector may omit a content item's reference on the wire.
     */
    private static Optional<Map<String, List<Object>>> dataByName(List<MetadataAttribute> handle) {
        Map<String, List<Object>> data = new HashMap<>();
        for (MetadataAttribute attribute : handle) {
            List<? extends AttributeContent> content = attribute.getContent();
            List<Object> values = content == null
                    ? List.of()
                    : content.stream().map(AttributeContent::getData).toList();
            if (hasContentWithoutData(values)) {
                return Optional.empty();
            }
            if (data.put(attribute.getName(), values) != null) {
                return Optional.empty();
            }
        }
        return Optional.of(data);
    }

    private static boolean hasContentWithoutData(List<Object> values) {
        return values.stream().anyMatch(Objects::isNull);
    }
}
