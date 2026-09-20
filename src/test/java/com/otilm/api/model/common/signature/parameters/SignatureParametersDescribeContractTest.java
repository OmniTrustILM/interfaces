package com.otilm.api.model.common.signature.parameters;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.common.signature.SignatureParameterGroup;
import jakarta.validation.Valid;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards {@link SignatureParameterContainer#describe}, which is the single authority on parameter-group membership.
 *
 * <p>
 * The sweep is reflective on purpose. {@code describe} is written by hand precisely so that nothing at runtime has to
 * reflect over these objects; what keeps the handwritten enumeration honest is this test failing the build.
 * </p>
 */
class SignatureParametersDescribeContractTest {

    @Test
    void theUnionRegistersAtLeastOneSubtype() {
        assertFalse(registeredSubtypes().isEmpty());
    }

    @Test
    void everyFieldIsReportedByDescribeUnderAGroupThatAppliesToItsFamily() {
        List<String> problems = new ArrayList<>();
        for (Class<? extends SignatureParametersDto> subtype : registeredSubtypes()) {
            SignatureParametersDto root = ParameterTreeFixture.populated(subtype);
            sweep(root, root.getFamily(), subtype.getSimpleName(), problems,
                    EnumSet.noneOf(SignatureParameterGroup.class));
        }
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    void everyGroupThatAppliesToAFamilyIsPopulatedByThatFamilysParameters() {
        List<String> problems = new ArrayList<>();
        for (Class<? extends SignatureParametersDto> subtype : registeredSubtypes()) {
            SignatureParametersDto root = ParameterTreeFixture.populated(subtype);
            Set<SignatureParameterGroup> used = EnumSet.noneOf(SignatureParameterGroup.class);
            sweep(root, root.getFamily(), subtype.getSimpleName(), problems, used);
            for (SignatureParameterGroup group : SignatureParameterGroup.values()) {
                if (group.appliesTo(root.getFamily()) && !used.contains(group)) {
                    problems
                            .add(group + " applies to " + root.getFamily() + " but no field of "
                                    + subtype.getSimpleName() + " reports it");
                }
            }
        }
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    void theDiscriminatorIsNotReportedAsAParameter() {
        for (Class<? extends SignatureParametersDto> subtype : registeredSubtypes()) {
            assertFalse(
                    RecordingSink
                            .of(ParameterTreeFixture.populated(subtype))
                            .fieldNames()
                            .contains(ParameterTreeFixture.DISCRIMINATOR),
                    subtype.getSimpleName() + " reports the family discriminator as a parameter");
        }
    }

    @Test
    void theSweepCatchesAFieldDescribeForgot() {
        List<String> problems = sweepFixture(new ForgetfulFixture());

        assertEquals(1, problems.size(), String.join("\n", problems));
        assertTrue(problems.get(0).contains("forgotten"), problems.get(0));
    }

    @Test
    void theSweepCatchesAContainerReportedAsALeaf() {
        List<String> problems = sweepFixture(new ContainerAsLeafFixture());

        assertEquals(1, problems.size(), String.join("\n", problems));
        assertTrue(problems.get(0).contains("is a nested parameters object"), problems.get(0));
    }

    @Test
    void theSweepCatchesAContainerThatForgotItsValidCascade() {
        List<String> problems = sweepFixture(new UncascadedContainerFixture());

        assertEquals(1, problems.size(), String.join("\n", problems));
        assertTrue(problems.get(0).contains("@Valid"), problems.get(0));
    }

    @Test
    void theSweepCatchesAContainerHiddenInACollection() {
        List<String> problems = sweepFixture(new ContainerCollectionFixture());

        assertEquals(1, problems.size(), String.join("\n", problems));
        assertTrue(problems.get(0).contains("in a collection"), problems.get(0));
    }

    @Test
    void theSweepCatchesAGroupThatDoesNotApplyToTheFamily() {
        List<String> problems = new ArrayList<>();
        sweep(new WrongFamilyGroupFixture(), SignatureFamily.XADES, "fixture", problems,
                EnumSet.noneOf(SignatureParameterGroup.class));

        assertEquals(1, problems.size(), String.join("\n", problems));
        assertTrue(problems.get(0).contains("does not apply to XADES"), problems.get(0));
    }

    private static List<String> sweepFixture(SignatureParameterContainer fixture) {
        List<String> problems = new ArrayList<>();
        sweep(fixture, SignatureFamily.PADES, "fixture", problems, EnumSet.noneOf(SignatureParameterGroup.class));
        return problems;
    }

    /** Compares what {@code node} reports against the fields it declares, in both directions. */
    private static void sweep(SignatureParameterContainer node, SignatureFamily family, String path,
            List<String> problems, Set<SignatureParameterGroup> used) {
        RecordingSink sink = RecordingSink.of(node);
        Set<String> reported = sink.fieldNames();
        List<Field> fields = ParameterTreeFixture.instanceFields(node.getClass());

        for (Field field : fields) {
            String location = path + "." + field.getName();
            if (ParameterTreeFixture.DISCRIMINATOR.equals(field.getName())) {
                continue;
            }
            if (!reported.remove(field.getName())) {
                problems.add(location + " is a parameter field and must be reported by describe()");
                continue;
            }
            if (isContainer(field) && field.getAnnotation(Valid.class) == null) {
                problems.add(location + " is a container and must carry @Valid, or its constraints go unchecked");
            }
            if (holdsContainers(field)) {
                problems
                        .add(location + " holds parameter containers in a collection; describe() reports one container"
                                + " per field, so give each its own field or teach this sweep to walk the elements");
            }
        }
        reported.forEach(orphan -> problems.add(path + "." + orphan + " is reported by describe() but is no field"));

        for (RecordingSink.Entry entry : sink.entries()) {
            classify(entry, node, family, path, problems, used, fields);
        }
    }

    private static void classify(RecordingSink.Entry entry, SignatureParameterContainer node, SignatureFamily family,
            String path, List<String> problems, Set<SignatureParameterGroup> used, List<Field> fields) {
        String location = path + "." + entry.name();
        boolean declaredAsContainer = fields
                .stream()
                .anyMatch(field -> field.getName().equals(entry.name()) && isContainer(field));
        if (declaredAsContainer != entry.container()) {
            problems
                    .add(location + (declaredAsContainer
                            ? " is a nested parameters object and must be reported with container()"
                            : " is a value and must be reported with leaf()"));
            return;
        }
        if (entry.container()) {
            if (entry.value() instanceof SignatureParameterContainer child) {
                sweep(child, family, location, problems, used);
            }
            return;
        }
        if (entry.group() == null) {
            problems.add(location + " is a parameter field and must report a group");
            return;
        }
        if (!entry.group().appliesTo(family)) {
            problems.add(location + " reports " + entry.group() + ", which does not apply to " + family);
            return;
        }
        if (!reportsItsOwnValue(entry, node, fields)) {
            problems.add(location + " must report the value of its own field");
            return;
        }
        used.add(entry.group());
    }

    private static boolean reportsItsOwnValue(RecordingSink.Entry entry, SignatureParameterContainer node,
            List<Field> fields) {
        return fields
                .stream()
                .filter(field -> field.getName().equals(entry.name()))
                .allMatch(field -> Objects.deepEquals(entry.value(), ParameterTreeFixture.read(field, node)));
    }

    private static boolean isContainer(Field field) {
        return SignatureParameterContainer.class.isAssignableFrom(field.getType());
    }

    private static boolean holdsContainers(Field field) {
        Class<?> element = elementType(field);
        return element != null && SignatureParameterContainer.class.isAssignableFrom(element);
    }

    /** @return the element type of an array or of a single-parameter collection, {@code null} for any other field */
    private static Class<?> elementType(Field field) {
        if (field.getType().isArray()) {
            return field.getType().getComponentType();
        }
        if (Collection.class.isAssignableFrom(field.getType())
                && field.getGenericType() instanceof ParameterizedType parameterized
                && parameterized.getActualTypeArguments().length == 1
                && parameterized.getActualTypeArguments()[0] instanceof Class<?> element) {
            return element;
        }
        return null;
    }

    private static List<Class<? extends SignatureParametersDto>> registeredSubtypes() {
        JsonSubTypes subTypes = SignatureParametersDto.class.getAnnotation(JsonSubTypes.class);
        assertNotNull(subTypes, "SignatureParametersDto must register its subtypes with @JsonSubTypes");
        return Arrays
                .stream(subTypes.value())
                .map(JsonSubTypes.Type::value)
                .<Class<? extends SignatureParametersDto>>map(type -> type.asSubclass(SignatureParametersDto.class))
                .toList();
    }

    static final class ForgetfulFixture implements SignatureParameterContainer {
        String remembered;
        String forgotten;

        @Override
        public void describe(SignatureParameterSink sink) {
            sink.leaf("remembered", SignatureParameterGroup.SIGNATURE_CONTEXT, remembered);
        }
    }

    static final class NestedFixture implements SignatureParameterContainer {
        String nestedParameter;

        @Override
        public void describe(SignatureParameterSink sink) {
            sink.leaf("nestedParameter", SignatureParameterGroup.SIGNATURE_CONTEXT, nestedParameter);
        }
    }

    static final class ContainerAsLeafFixture implements SignatureParameterContainer {

        @Valid
        NestedFixture nested = new NestedFixture();

        @Override
        public void describe(SignatureParameterSink sink) {
            sink.leaf("nested", SignatureParameterGroup.SIGNATURE_CONTEXT, nested);
        }
    }

    static final class ContainerCollectionFixture implements SignatureParameterContainer {

        @Valid
        List<NestedFixture> nested = List.of(new NestedFixture());

        @Override
        public void describe(SignatureParameterSink sink) {
            sink.leaf("nested", SignatureParameterGroup.SIGNATURE_CONTEXT, nested);
        }
    }

    static final class UncascadedContainerFixture implements SignatureParameterContainer {
        NestedFixture nested = new NestedFixture();

        @Override
        public void describe(SignatureParameterSink sink) {
            sink.container("nested", nested);
        }
    }

    static final class WrongFamilyGroupFixture implements SignatureParameterContainer {
        String padesOnly;

        @Override
        public void describe(SignatureParameterSink sink) {
            sink.leaf("padesOnly", SignatureParameterGroup.SIGNATURE_CONTEXT, padesOnly);
        }
    }
}
