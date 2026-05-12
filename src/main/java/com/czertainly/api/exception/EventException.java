package com.czertainly.api.exception;

import com.czertainly.api.model.core.other.ResourceEvent;
import lombok.Getter;

@Getter
public class EventException extends Exception implements PlatformException {

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
