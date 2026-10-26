package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import java.util.List;

public final class KeyHandles {

    private KeyHandles() {
    }

    /** Whether the handle names each metadata attribute once and every content item carries data. */
    public static boolean isUnambiguous(List<MetadataAttribute> handle) {
        return !hasRepeatedName(handle) && handle.stream().noneMatch(KeyHandles::hasContentWithoutData);
    }

    private static boolean hasRepeatedName(List<MetadataAttribute> handle) {
        return handle.stream().map(MetadataAttribute::getName).distinct().count() < handle.size();
    }

    private static boolean hasContentWithoutData(MetadataAttribute attribute) {
        List<? extends AttributeContent> content = attribute.getContent();
        return content != null && content.stream().anyMatch(item -> item == null || item.getData() == null);
    }
}
