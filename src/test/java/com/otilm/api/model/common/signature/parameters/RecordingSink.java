package com.otilm.api.model.common.signature.parameters;

import com.otilm.api.model.common.signature.SignatureParameterGroup;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Records what a {@code describe} call reported, so a sweep can compare it against the declared fields. */
final class RecordingSink implements SignatureParameterSink {

    /**
     * @param group the declared group, or {@code null} for a container
     * @param container whether the entry arrived as a nested parameters object
     */
    record Entry(String name, SignatureParameterGroup group, Object value, boolean container) {
    }

    private final List<Entry> entries = new ArrayList<>();

    @Override
    public void leaf(String name, SignatureParameterGroup group, Object value) {
        entries.add(new Entry(name, group, value, false));
    }

    @Override
    public void container(String name, SignatureParameterContainer value) {
        entries.add(new Entry(name, null, value, true));
    }

    List<Entry> entries() {
        return entries;
    }

    Set<String> fieldNames() {
        Set<String> names = new LinkedHashSet<>();
        entries.forEach(entry -> names.add(entry.name()));
        return names;
    }

    static RecordingSink of(SignatureParameterContainer node) {
        RecordingSink sink = new RecordingSink();
        node.describe(sink);
        return sink;
    }
}
