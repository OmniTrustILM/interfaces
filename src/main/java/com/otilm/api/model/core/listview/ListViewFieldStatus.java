package com.otilm.api.model.core.listview;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

/**
 * How a stored column or filter of a view resolves against the resource's field catalogue when the view is read.
 */
@Schema(enumAsRef = true)
public enum ListViewFieldStatus implements IPlatformEnum {

    AVAILABLE("available", "Available",
            "The field is in the catalogue and, for an attribute, is still backed by a definition the column or filter was bound to"),
    UNAVAILABLE("unavailable", "Unavailable",
            "The catalogue has no field under this identifier for the caller, for example because the attribute was deleted"),
    REPLACED("replaced", "Replaced",
            "The catalogue has a field under this identifier, but it is backed only by attribute definitions other than the ones the column or filter was bound to, so it is not the field the view was saved with");

    private static final ListViewFieldStatus[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    ListViewFieldStatus(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @Override
    @JsonValue
    public String getCode() {
        return this.code;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @JsonCreator
    public static ListViewFieldStatus fromCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Unsupported list view field status %s.", code)));
    }
}
