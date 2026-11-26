package com.otilm.api.model.core.acme;

import com.otilm.api.model.client.acme.AcmeProfileEditRequestDto;
import com.otilm.api.model.client.acme.AcmeProfileRequestDto;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AcmeRenewalInfoStringFormTest {

    static Stream<Object[]> dtos() {
        Directory directory = new Directory();
        directory.setRenewalInfo("https://acme.example/renewal-info");
        Order order = new Order();
        order.setReplaces("cert-id-1");
        CertificateIssuanceRequest issuance = new CertificateIssuanceRequest();
        issuance.setReplaces("cert-id-2");
        AcmeProfileRequestDto request = new AcmeProfileRequestDto();
        request.setRenewalInfoRetryAfter(4321);
        AcmeProfileEditRequestDto edit = new AcmeProfileEditRequestDto();
        edit.setRenewalInfoRetryAfter(5432);
        edit.setRenewalInfoEnabled(true);
        AcmeProfileDto dto = new AcmeProfileDto();
        dto.setRenewalInfoRetryAfter(6543);
        return Stream
                .of(new Object[]{directory, "renewalInfo=https://acme.example/renewal-info"},
                        new Object[]{order, "replaces=cert-id-1"}, new Object[]{issuance, "replaces=cert-id-2"},
                        new Object[]{request, "renewalInfoEnabled=false"},
                        new Object[]{request, "renewalInfoRetryAfter=4321"},
                        new Object[]{edit, "renewalInfoRetryAfter=5432"}, new Object[]{edit, "renewalInfoEnabled=true"},
                        new Object[]{dto, "renewalInfoEnabled=false"}, new Object[]{dto, "renewalInfoRetryAfter=6543"});
    }

    @ParameterizedTest
    @MethodSource("dtos")
    void stringFormIncludesTheNewMember(Object dto, String expected) {
        assertTrue(dto.toString().contains(expected), dto.toString());
    }
}
