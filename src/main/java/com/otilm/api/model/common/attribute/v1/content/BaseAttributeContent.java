package com.otilm.api.model.common.attribute.v1.content;

public class BaseAttributeContent<T> extends AttributeContent {

    private T value;

    public BaseAttributeContent() { }

    public BaseAttributeContent(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
