package com.otilm.api.model.common.signature.parameters;

import com.otilm.api.model.common.signature.SignatureParameterGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Files the dotted path of each field a signature-parameters tree describes under the group that governs it, with
 * groups in enum order and the fields of a group in {@code describe()} order.
 */
public final class SignatureParameterPaths {

    private SignatureParameterPaths() {
    }

    /** Every field reachable through the containers present in {@code root}, whatever its value. */
    public static Map<SignatureParameterGroup, List<String>> byGroup(SignatureParameterContainer root) {
        return collect(root, value -> true);
    }

    /** The fields of {@code root} that carry a value; {@code null} and an empty collection carry none. */
    public static Map<SignatureParameterGroup, List<String>> presentByGroup(SignatureParameterContainer root) {
        return collect(root, SignatureParameterPaths::carriesValue);
    }

    private static Map<SignatureParameterGroup, List<String>> collect(SignatureParameterContainer root,
            Predicate<Object> listed) {
        Map<SignatureParameterGroup, List<String>> byGroup = new EnumMap<>(SignatureParameterGroup.class);
        if (root != null) {
            root.describe(new PathSink("", listed, byGroup));
        }
        return byGroup;
    }

    private static boolean carriesValue(Object value) {
        return value != null && !(value instanceof Collection<?> collection && collection.isEmpty());
    }

    /** @param prefix the enclosing container's path and a dot, empty at the root */
    private record PathSink(String prefix, Predicate<Object> listed,
            Map<SignatureParameterGroup, List<String>> byGroup) implements SignatureParameterSink {

        @Override
        public void leaf(String name, SignatureParameterGroup group, Object value) {
            if (listed.test(value)) {
                byGroup.computeIfAbsent(group, newGroup -> new ArrayList<>()).add(prefix + name);
            }
        }

        @Override
        public void container(String name, SignatureParameterContainer value) {
            if (value != null) {
                value.describe(new PathSink(prefix + name + ".", listed, byGroup));
            }
        }
    }
}
