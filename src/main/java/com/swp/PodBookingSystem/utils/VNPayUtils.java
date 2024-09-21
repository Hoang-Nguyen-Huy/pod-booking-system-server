package com.swp.PodBookingSystem.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VNPayUtils {


    public static String generateSecureHash(Map<String, String> params, String secretKey) throws Exception {
        // Tạo chuỗi để mã hóa
        StringBuilder sb = new StringBuilder();
        // Sắp xếp các tham số theo tên
        params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                        sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                    }
                });
        // Bỏ ký tự '&' ở cuối
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }

        // Tạo mã HMACSHA512
        Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        sha512_HMAC.init(secretKeySpec);

        byte[] hashBytes = sha512_HMAC.doFinal(sb.toString().getBytes(StandardCharsets.UTF_8));

        // Chuyển đổi mảng byte thành chuỗi hexa
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public static String generatePaymentUrl(String vnp_Url, Map<String, String> params) {
        StringBuilder query = new StringBuilder(vnp_Url);
        query.append("?");
        params.forEach((key, value) -> {
            try {
                query.append(key).append("=").append(URLEncoder.encode(value, StandardCharsets.UTF_8.toString())).append("&");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Failed to encode URL parameter", e);
            }
        });
        // Remove the last '&'
        query.setLength(query.length() - 1);
        return query.toString();
    }
}
