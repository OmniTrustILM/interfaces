package com.otilm.api.model.core.logging.records;

import java.util.UUID;

public record ResourceObjectIdentity(
        String name,
        UUID uuid
) {
    public ResourceObjectIdentity {
        if (name == null && uuid == null) {
            throw new IllegalArgumentException("Both name and UUID cannot be null at the same time");
        }
    }
}
