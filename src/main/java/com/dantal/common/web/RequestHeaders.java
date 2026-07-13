package com.dantal.common.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public final class RequestHeaders {

    public static final String DEVICE_ID = "X-Device-Id";

    private RequestHeaders() {
    }

    public static String deviceId(HttpServletRequest request, String bodyDeviceId) {
        if (StringUtils.hasText(bodyDeviceId)) {
            return bodyDeviceId.trim();
        }
        String header = request.getHeader(DEVICE_ID);
        return StringUtils.hasText(header) ? header.trim() : null;
    }
}
