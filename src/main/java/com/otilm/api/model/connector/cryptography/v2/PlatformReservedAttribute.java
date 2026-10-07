package com.otilm.api.model.connector.cryptography.v2;

/**
 * Marks a platform-defined attribute with an identity shared by attribute schemas and requests.
 *
 * <p>
 * A platform attribute has a predefined UUID and name that identify its meaning wherever it is published or supplied.
 * Both identifiers remain stable and must be unique among platform attributes: reusing either identifier for another
 * attribute makes attribute matching ambiguous. Providers use these identifiers when publishing a schema, and callers
 * use the same identifiers when supplying a value. Supported values and schema properties are defined by the individual
 * attribute definition and are outside this identity contract.
 * </p>
 *
 * <p>
 * Implementations declare public static final {@code UUID ATTRIBUTE_UUID} and {@code String NAME} constants. This
 * marker allows definitions to be discovered and their identity constants checked without maintaining a manual registry
 * or creating instances. It does not prescribe supported values or schema and request builder methods.
 * </p>
 */
public interface PlatformReservedAttribute {
}
