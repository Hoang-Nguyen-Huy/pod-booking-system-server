package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.configuration.VNPayConfig;
import com.swp.PodBookingSystem.dto.respone.Payment.PaymentResDTO;
import com.swp.PodBookingSystem.dto.respone.Payment.TransactionStatusDTO;
import com.swp.PodBookingSystem.exception.AppException;
import com.swp.PodBookingSystem.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PaymentService {

    public PaymentResDTO generatePaymentUrl(long amount, String orderId, String clientIP) {
        try {
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
            String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount) + "00");
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_BankCode", "NCB");
            vnp_Params.put("vnp_IpAddr", clientIP);
            vnp_Params.put("vnp_OrderType", "100000");
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + orderId);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());

            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if (fieldValue != null && fieldValue.length() > 0) {
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString())).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
            String queryUrl = query.toString() + "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;

            PaymentResDTO paymentResDTO = new PaymentResDTO();
            paymentResDTO.setStatus("OK");
            paymentResDTO.setMessage("successfully");
            paymentResDTO.setUrl(paymentUrl);
            return paymentResDTO;

        } catch (UnsupportedEncodingException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public TransactionStatusDTO getTransactionStatus(String amount, String bankCode, String orderInfo, String responseCode) {
        TransactionStatusDTO transactionStatusDTO = new TransactionStatusDTO();
        if ("00".equals(responseCode)) {
            transactionStatusDTO.setMessage("Payment success");
            transactionStatusDTO.setStatus("OK");
            transactionStatusDTO.setData(amount + " " + bankCode + " " + orderInfo);
        } else {
            transactionStatusDTO.setMessage("Payment failed");
            transactionStatusDTO.setStatus("FAILED");
            transactionStatusDTO.setData("");
        }
        return transactionStatusDTO;
    }
}
