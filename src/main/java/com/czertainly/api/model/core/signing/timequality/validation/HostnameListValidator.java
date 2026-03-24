package com.czertainly.api.model.core.signing.timequality.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bouncycastle.util.IPAddress;

import java.util.List;
import java.util.regex.Pattern;

public class HostnameListValidator implements ConstraintValidator<ValidHostnameList, List<String>> {

    // RFC 1123: labels are 1-63 alphanumeric/hyphen chars, not starting or ending with hyphen; total max 253 chars
    private static final Pattern HOSTNAME_PATTERN = Pattern.compile(
            "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])\\.)*" +
                    "([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]{0,61}[A-Za-z0-9])$"
    );

    @Override
    public boolean isValid(List<String> hostnames, ConstraintValidatorContext context) {
        if (hostnames == null) {
            return true;
        }
        return hostnames.stream().allMatch(HostnameListValidator::isValidHostname);
    }

    static boolean isValidHostname(String hostname) {
        if (hostname == null || hostname.isEmpty() || hostname.length() > 253) {
            return false;
        }
        // Delegate IP address validation (IPv4 + IPv6) to Bouncy Castle
        if (IPAddress.isValid(hostname)) {
            return true;
        }
        return HOSTNAME_PATTERN.matcher(hostname).matches();
    }
}
