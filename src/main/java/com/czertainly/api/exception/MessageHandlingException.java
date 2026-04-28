package com.czertainly.api.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class MessageHandlingException extends Exception {

    private String queueName;
    private Object messageData;

    public MessageHandlingException(String message) {
        super(message);
    }

    public MessageHandlingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageHandlingException(String queueName, Object messageData, String message) {
        this(message);
        this.queueName = queueName;
        this.messageData = messageData;
    }

    public MessageHandlingException(String queueName, Object messageData, String message, Throwable cause) {
        this(message, cause);
        this.queueName = queueName;
        this.messageData = messageData;
    }

}
