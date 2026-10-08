package com.otilm.api.config.jackson3;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.otilm.api.model.client.signing.profile.scheme.ManagedSigningDto;
import com.otilm.api.model.client.signing.profile.scheme.ManagedSigningRequestDto;
import com.otilm.api.model.client.signing.profile.scheme.OneTimeKeyManagedSigningDto;
import com.otilm.api.model.client.signing.profile.scheme.OneTimeKeyManagedSigningRequestDto;
import com.otilm.api.model.client.signing.profile.scheme.StaticKeyManagedSigningDto;
import com.otilm.api.model.client.signing.profile.scheme.StaticKeyManagedSigningRequestDto;
import java.util.Map;
import java.util.Set;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.Annotated;
import tools.jackson.databind.introspect.NopAnnotationIntrospector;

/**
 * Reads the Jackson 2 {@code @JsonSerialize} and {@code @JsonDeserialize} on a DTO and hands Jackson 3 the twin of the
 * handler it names. Jackson 3 resolves these annotations through a class hierarchy exactly as Jackson 2 does, so a
 * subclass that resets an inherited handler keeps doing so.
 */
final class Jackson2AnnotationBridge extends NopAnnotationIntrospector {

    private static final long serialVersionUID = 1L;

    private static final Map<Class<?>, Object> SERIALIZERS = Map
            .of(com.otilm.api.config.serializer.BaseAttributeSerializer.class, new BaseAttributeSerializer(),
                    com.otilm.api.config.serializer.ResponseAttributeSerializer.class,
                    new ResponseAttributeSerializer());

    private static final Map<Class<?>, Object> DESERIALIZERS = Map
            .of(com.otilm.api.config.serializer.BaseAttributeDeserializer.class, new BaseAttributeDeserializer(),
                    com.otilm.api.config.serializer.AttributeContentDeserializer.class,
                    new AttributeContentDeserializer(),
                    com.otilm.api.model.common.attribute.common.content.ZonedDateTimeDeserializer.class,
                    new ZonedDateTimeDeserializer(),
                    com.otilm.api.model.common.attribute.v1.content.ZonedDateTimeDeserializer.class,
                    new OffsetZonedDateTimeDeserializer(), com.otilm.api.model.core.secret.PassphraseDeserializer.class,
                    new PassphraseDeserializer(), com.otilm.api.model.core.secret.UploadedFileDeserializer.class,
                    new UploadedFileDeserializer(), ManagedSigningDto.Deserializer.class,
                    new ManagedSigningDeserializer<>(ManagedSigningDto.class, StaticKeyManagedSigningDto.class,
                            OneTimeKeyManagedSigningDto.class),
                    ManagedSigningRequestDto.Deserializer.class,
                    new ManagedSigningDeserializer<>(ManagedSigningRequestDto.class,
                            StaticKeyManagedSigningRequestDto.class, OneTimeKeyManagedSigningRequestDto.class));

    /** Jackson 2 handlers that only spell out the default, which Jackson 3 applies on its own. */
    private static final Set<Class<?>> DEFAULTS = Set
            .of(com.fasterxml.jackson.databind.JsonSerializer.None.class,
                    com.fasterxml.jackson.databind.JsonDeserializer.None.class, LocalDateSerializer.class,
                    LocalDateDeserializer.class, LocalTimeSerializer.class, LocalTimeDeserializer.class,
                    ZonedDateTimeSerializer.class);

    static boolean translates(Class<?> jackson2Handler) {
        return DEFAULTS.contains(jackson2Handler) || SERIALIZERS.containsKey(jackson2Handler)
                || DESERIALIZERS.containsKey(jackson2Handler);
    }

    @Override
    public Object findSerializer(MapperConfig<?> config, Annotated annotated) {
        JsonSerialize annotation = annotated.getAnnotation(JsonSerialize.class);
        return annotation == null ? null : SERIALIZERS.get(annotation.using());
    }

    @Override
    public Object findDeserializer(MapperConfig<?> config, Annotated annotated) {
        JsonDeserialize annotation = annotated.getAnnotation(JsonDeserialize.class);
        return annotation == null ? null : DESERIALIZERS.get(annotation.using());
    }
}
