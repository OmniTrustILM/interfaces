package com.czertainly.api.model.client.signing.timequality.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bouncycastle.util.IPAddress;

import java.util.List;
import java.util.regex.Pattern;

public class HostnameListValidator implements ConstraintValidator<ValidHostnameList, List<String>> {

    // RFC 1123: label starts/ends with alphanumeric characters, hyphens only in the middle.
    private static final Pattern LABEL_PATTERN = Pattern.compile("[a-zA-Z0-9](?:-*+[a-zA-Z0-9])*+");

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
        String[] labels = hostname.split("\\.", -1);
        for (String label : labels) {
            if (label.length() > 63 || !LABEL_PATTERN.matcher(label).matches()) {
                return false;
            }
        }
        return true;
    }
}
