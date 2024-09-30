package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.Payment.PaymentReqDTO;
import com.swp.PodBookingSystem.dto.respone.Payment.PaymentResDTO;
import com.swp.PodBookingSystem.dto.respone.Payment.TransactionStatusDTO;
import com.swp.PodBookingSystem.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/url")
    public ResponseEntity<?> generatePaymentUrl(@Valid @RequestBody PaymentReqDTO paymentRequestDTO, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        PaymentResDTO paymentResDTO = paymentService.generatePaymentUrl(paymentRequestDTO.getAmount(), paymentRequestDTO.getOrderId(), clientIp);
        return ResponseEntity.status(HttpStatus.OK).body(paymentResDTO);
    }

    @GetMapping("/info")
    public ResponseEntity<?> getTransactionStatus(
            @RequestParam(value = "vnp_Amount") String amount,
            @RequestParam(value = "vnp_BankCode") String bankCode,
            @RequestParam(value = "vnp_OrderInfo") String orderInfo,
            @RequestParam(value = "vnp_ResponseCode") String responseCode) {

        TransactionStatusDTO transactionStatusDTO = paymentService.getTransactionStatus(amount, bankCode, orderInfo, responseCode);
        return ResponseEntity.status(HttpStatus.OK).body(transactionStatusDTO);
    }
}
