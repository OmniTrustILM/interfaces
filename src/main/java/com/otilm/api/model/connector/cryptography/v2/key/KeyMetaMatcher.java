package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The equality {@code keyMetaFilter} promises: the same metadata names in any order, each with the same data in the
 * same order. A handle with a repeated name or a content item without data is ambiguous and matches nothing.
 */
public final class KeyMetaMatcher {

    private KeyMetaMatcher() {
    }

    public static boolean matches(List<MetadataAttribute> handle, List<MetadataAttribute> other) {
        Optional<Map<String, List<Object>>> handleData = dataByName(handle);
        return handleData.isPresent() && handleData.equals(dataByName(other));
    }

    public static boolean isUnambiguous(List<MetadataAttribute> handle) {
        return dataByName(handle).isPresent();
    }

    /**
     * Compares content data alone, since a connector may omit a content item's reference on the wire.
     */
    private static Optional<Map<String, List<Object>>> dataByName(List<MetadataAttribute> handle) {
        if (hasRepeatedName(handle)) {
            return Optional.empty();
        }
        Map<String, List<Object>> data = handle
                .stream()
                .collect(Collectors.toMap(MetadataAttribute::getName, KeyMetaMatcher::contentData));
        return data.values().stream().anyMatch(KeyMetaMatcher::hasContentWithoutData)
                ? Optional.empty()
                : Optional.of(data);
    }

    private static boolean hasRepeatedName(List<MetadataAttribute> handle) {
        return handle.stream().map(MetadataAttribute::getName).distinct().count() < handle.size();
    }

    private static List<Object> contentData(MetadataAttribute attribute) {
        List<? extends AttributeContent> content = attribute.getContent();
        return content == null ? List.of() : content.stream().map(AttributeContent::getData).toList();
    }

    private static boolean hasContentWithoutData(List<Object> values) {
        return values.stream().anyMatch(Objects::isNull);
    }
}
