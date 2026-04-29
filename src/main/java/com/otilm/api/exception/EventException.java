package com.otilm.api.exception;

import com.otilm.api.model.core.other.ResourceEvent;
import lombok.Getter;

@Getter
public class EventException extends Exception {

    private final ResourceEvent event;

    public EventException(String message) {
        super(message);
        this.event = null;
    }

    public EventException(ResourceEvent event, String message) {
        super(message);
        this.event = event;
    }

}
