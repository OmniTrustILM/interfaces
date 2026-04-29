package com.otilm.api.model.core.certificate;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum CertificateState implements IPlatformEnum {
	REQUESTED("requested", "Requested"),
	REJECTED("rejected", "Rejected"),
	PENDING_APPROVAL("pending_approval", "Pending approval"),
	PENDING_ISSUE("pending_issue", "Pending issue", "Issue action pending (CA approval)"),
	PENDING_REVOKE("pending_revoke", "Pending revoke", "Revoke action pending (CA approval)"),
	FAILED("failed", "Failed", "Issue action failed"),
	ISSUED("issued", "Issued"),
	REVOKED("revoked", "Revoked")
	;

	private static final CertificateState[] VALUES;

	static {
		VALUES = values();
	}

	private final String code;
	private final String label;
	private final String description;

	CertificateState(String code, String label) {
		this(code, label,null);
	}

	CertificateState(String code, String label, String description) {
		this.code = code;
		this.label = label;
		this.description = description;
	}

	@Override
	@JsonValue
	public String getCode() {
		return this.code;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@JsonCreator
	public static CertificateState findByCode(String code) {
		return Arrays.stream(VALUES)
				.filter(k -> k.code.equals(code))
				.findFirst()
				.orElseThrow(() ->
						new ValidationException(ValidationError.create("Unknown certificate state {}", code)));
	}
}
