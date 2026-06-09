package com.otilm.api.model.core.logging;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public interface Loggable {

    Serializable toLogData();

    List<String> toLogResourceObjectsNames();

    List<UUID> toLogResourceObjectsUuids();
}

