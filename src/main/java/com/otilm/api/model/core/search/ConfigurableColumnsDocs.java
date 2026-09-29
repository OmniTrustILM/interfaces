package com.otilm.api.model.core.search;

/**
 * Shared wording for the configurable-column contract.
 *
 * <p>
 * Every listing that accepts ordering and column selection behaves the same way, and every field catalogue that feeds
 * one publishes the same two capability flags. The sentences are declared once here so the operations cannot drift
 * apart in how they explain a single contract, the same reason
 * {@link AttributeProjectable#ATTRIBUTE_VALUES_DESCRIPTION} is shared across the listing responses.
 */
public final class ConfigurableColumnsDocs {

    /**
     * Ordering, column selection, and the guarantee owed to a caller that sends neither. Belongs on every listing whose
     * request body carries {@code sort} and {@code columns}.
     */
    public static final String SORT_AND_COLUMNS = """
            Ordering and columns address a field by its source together with its identifier, because an identifier is \
            unique only within its source. Both halves, and which fields may be shown or ordered on, come from the \
            searchable-fields operation of this resource.

            `sort` orders the whole result set before it is paged, so paging walks the sorted set rather than sorting \
            one page at a time; only fields the catalogue marks `sortable` may be used. Objects without a value for \
            the sorted field come last in both directions; a field holding several values sorts by its smallest value \
            ascending and its largest descending. `columns` names the fields the caller means to display, and only \
            fields the catalogue marks `displayable` may be named. It does not \
            narrow the response: every listing object comes back whole, and naming a property field asks for nothing \
            extra because the object already carries it. Naming an attribute-sourced field is what has an effect, \
            described below.

            A request that carries neither `sort` nor `columns` is answered exactly as it was before the two fields \
            existed: the endpoint's own default ordering, the full default shape of every object, and no \
            `attributeValues` member. A caller written against the previous contract therefore needs no change.""";

    /**
     * The attribute projection. Belongs on the listings whose objects implement {@link AttributeProjectable}.
     */
    public static final String ATTRIBUTE_PROJECTION = """


            Requesting attribute-sourced columns adds an `attributeValues` member to each returned object, keyed by \
            field source and then by field identifier. A field the object holds no value for is absent rather than \
            empty, and a multi-valued attribute arrives in its stored `item_order`.""";

    /**
     * The two capability flags. Belongs on every searchable-fields operation that feeds a configurable-column listing.
     */
    public static final String CATALOGUE_FLAGS = """
            Besides the conditions a field may be filtered with, each field reports whether it can serve as a \
            configurable column of the listing: `displayable` marks the fields that may be named in `columns`, and \
            `sortable` those the listing may be ordered by. A field that reports neither flag is filter-only, so an \
            absent flag is to be read as `false` rather than as unknown.

            Both flags are answered per field, and that answer is authoritative for attribute-sourced fields as \
            much as for property ones: whether a given attribute may be ordered on depends on the resource and is \
            reported here rather than assumed. A field that may be shown but not ordered on reports `displayable` \
            without `sortable`.""";

    private ConfigurableColumnsDocs() {
    }
}
