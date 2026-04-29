package com.otilm.core.interfaces;

import java.io.Serializable;
import java.util.Map;

public interface CommonObject {

    Serializable getId();
    String getName();
    String getType();
    Map<? extends Serializable, ? extends Serializable> getAttributes();
}
