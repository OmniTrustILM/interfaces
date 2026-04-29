package com.otilm.api.model.common.attribute.common.content.data;

import com.otilm.api.exception.ValidationException;

import java.io.Serializable;

public interface AttributeContentData extends Serializable {

    void validate() throws ValidationException;
}
