package com.otilm.api.model.client.auth;

import com.otilm.api.model.common.Named;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class EditAuthProfileDto implements Named {
	
	@Schema(
            description = "Name of the User"
    )
    private String name;
	
	@Schema(
            description = "Surname of the User"
    )
    private String surname;
	
	@Schema(
            description = "Email Id of the User"
    )
    private String email;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("surname", surname)
                .append("email", email)
                .toString();
    }
}
