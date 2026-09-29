package com.otilm.api.interfaces.connector.discovery.v2;

import com.otilm.api.model.connector.discovery.v2.DiscoveryDrainRequestDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryInitiateRequestDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryInitiateResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryResultsResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryRunRequestDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryStatusResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryStopResponseDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DiscoveryOperationControllerCompileTest {

    /** Minimal mock impl — compilation alone proves the interface signatures are coherent. */
    static class Mock implements DiscoveryOperationController {
        @Override
        public DiscoveryInitiateResponseDto initiate(DiscoveryInitiateRequestDto request) {
            return new DiscoveryInitiateResponseDto();
        }

        @Override
        public DiscoveryStatusResponseDto status(DiscoveryRunRequestDto request) {
            return new DiscoveryStatusResponseDto();
        }

        @Override
        public DiscoveryResultsResponseDto results(DiscoveryDrainRequestDto request) {
            return new DiscoveryResultsResponseDto();
        }

        @Override
        public DiscoveryStopResponseDto stop(DiscoveryRunRequestDto request) {
            return new DiscoveryStopResponseDto();
        }

        @Override
        public DiscoveryInitiateResponseDto resume(DiscoveryRunRequestDto request) {
            return new DiscoveryInitiateResponseDto();
        }

        @Override
        public void cancel(DiscoveryRunRequestDto request) {
            throw new UnsupportedOperationException("compile-time contract check only");
        }
    }

    @Test
    void mockImplementsInterface() {
        assertNotNull(new Mock());
    }
}
