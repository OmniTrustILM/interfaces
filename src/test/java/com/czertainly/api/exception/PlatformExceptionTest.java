package com.czertainly.api.exception;

import com.czertainly.api.interfaces.core.cmp.error.CmpBaseException;
import com.czertainly.api.interfaces.core.cmp.error.CmpConfigurationException;
import com.czertainly.api.interfaces.core.cmp.error.CmpCrmfValidationException;
import com.czertainly.api.interfaces.core.cmp.error.CmpProcessingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlatformExceptionTest {

    private static void assertImplements(Class<?> cls) {
        assertTrue(PlatformException.class.isAssignableFrom(cls),
                cls.getSimpleName() + " must implement PlatformException");
    }

    @Test void allDomainExceptions_implementPlatformException() {
        assertAll(
            () -> assertImplements(AcmeProblemDocumentException.class),
            () -> assertImplements(AlreadyExistException.class),
            () -> assertImplements(AttributeException.class),
            () -> assertImplements(CbomRepositoryException.class),
            () -> assertImplements(CertificateException.class),
            () -> assertImplements(CertificateOperationException.class),
            () -> assertImplements(CertificateRequestException.class),
            () -> assertImplements(ConnectorException.class),
            () -> assertImplements(ConnectionServiceException.class),
            () -> assertImplements(ConnectorClientException.class),
            () -> assertImplements(ConnectorCommunicationException.class),
            () -> assertImplements(ConnectorEntityNotFoundException.class),
            () -> assertImplements(ConnectorProblemException.class),
            () -> assertImplements(ConnectorServerException.class),
            () -> assertImplements(DiscoveryException.class),
            () -> assertImplements(EventException.class),
            () -> assertImplements(LocationException.class),
            () -> assertImplements(MessageHandlingException.class),
            () -> assertImplements(NotDeletableException.class),
            () -> assertImplements(NotFoundException.class),
            () -> assertImplements(NotSupportedException.class),
            () -> assertImplements(RuleException.class),
            () -> assertImplements(ScepException.class),
            () -> assertImplements(SchedulerException.class),
            () -> assertImplements(SecretOperationException.class),
            () -> assertImplements(ValidationException.class),
            () -> assertImplements(CmpBaseException.class),
            () -> assertImplements(CmpConfigurationException.class),
            () -> assertImplements(CmpCrmfValidationException.class),
            () -> assertImplements(CmpProcessingException.class)
        );
    }
}
