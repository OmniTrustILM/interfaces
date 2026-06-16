package com.otilm.api.model.client.dashboard;

import com.otilm.api.exception.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SigningRecordStatisticsPeriodTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void findByCode_resolvesWireCode() {
        Assertions.assertEquals(SigningRecordStatisticsPeriod.LAST_7D, SigningRecordStatisticsPeriod.findByCode("7d"));
        Assertions.assertEquals(SigningRecordStatisticsPeriod.LAST_24H, SigningRecordStatisticsPeriod.findByCode("24h"));
        Assertions.assertEquals(SigningRecordStatisticsPeriod.LAST_30D, SigningRecordStatisticsPeriod.findByCode("30d"));
        Assertions.assertEquals(SigningRecordStatisticsPeriod.LAST_90D, SigningRecordStatisticsPeriod.findByCode("90d"));
    }

    @Test
    void findByCode_rejectsUnknownCode() {
        Assertions.assertThrows(ValidationException.class, () -> SigningRecordStatisticsPeriod.findByCode("12h"));
    }

    @Test
    void serializesToWireCode() throws Exception {
        Assertions.assertEquals("\"30d\"", mapper.writeValueAsString(SigningRecordStatisticsPeriod.LAST_30D));
    }

    @Test
    void deserializesFromWireCode() throws Exception {
        Assertions.assertEquals(SigningRecordStatisticsPeriod.LAST_90D,
                mapper.readValue("\"90d\"", SigningRecordStatisticsPeriod.class));
    }
}
