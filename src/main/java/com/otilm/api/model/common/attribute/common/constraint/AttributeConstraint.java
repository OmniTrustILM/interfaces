package com.otilm.api.model.common.attribute.common.constraint;

import java.io.Serializable;

public abstract class AttributeConstraint implements Serializable {

    public abstract <T extends Object> T getData();
}
