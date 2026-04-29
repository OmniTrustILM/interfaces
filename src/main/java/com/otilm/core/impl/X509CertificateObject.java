package com.otilm.core.impl;

import com.otilm.core.interfaces.CertificateObject;

import java.io.Serializable;
import java.util.Map;

public class X509CertificateObject implements CertificateObject {

    private Long id;
    private String name;
    private String type;
    private Map<Serializable, Serializable> attributes;

    @Override
    public Long getId() {
        return null;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return null;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Map<Serializable, Serializable> getAttributes() {
        return null;
    }

    public void setAttributes(Map<Serializable, Serializable> attributes) {
        this.attributes = attributes;
    }
}
