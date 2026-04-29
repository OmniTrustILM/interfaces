package com.otilm.api.model.client.approval;

import lombok.Data;
import org.springdoc.core.annotations.ParameterObject;

@Data
@ParameterObject
public class ApprovalUserDto {

    private boolean history;

}
