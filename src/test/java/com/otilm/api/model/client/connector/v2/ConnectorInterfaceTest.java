package com.otilm.api.model.client.connector.v2;

import com.otilm.api.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConnectorInterfaceTest {

    @Test
    void keyLifecycle_isAFunctionalInterfaceFoundByItsCode() {
        // given
        String code = "keyLifecycle";

        // when
        ConnectorInterface found = ConnectorInterface.findByCode(code);

        // then
        assertEquals(ConnectorInterface.KEY_LIFECYCLE, found);
        assertEquals(code, found.getCode());
        assertEquals(ConnectorInterface.InterfaceCategory.FUNCTIONAL, found.getCategory());
    }

    @Test
    void findByCode_refusesAnUnknownCode() {
        // given
        String unknownCode = "keyLifecycl";

        // when
        Executable lookup = () -> ConnectorInterface.findByCode(unknownCode);

        // then
        assertThrows(ValidationException.class, lookup);
    }
}
