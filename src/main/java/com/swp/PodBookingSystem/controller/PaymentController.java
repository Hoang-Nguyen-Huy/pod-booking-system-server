package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.respone.Payment.PaymentResDTO;
import com.swp.PodBookingSystem.dto.respone.Payment.TransactionStatusDTO;
import com.swp.PodBookingSystem.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class    PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/url")
    public ResponseEntity<PaymentResDTO> generatePaymentUrl( @RequestParam("amount") long amount,
                                                 @RequestParam("orderId") String orderId, @RequestParam("returnUrl") String returnUrl,
                                                             HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        PaymentResDTO paymentResDTO = paymentService.generatePaymentUrl(amount, orderId, clientIp, returnUrl);
        return ResponseEntity.status(HttpStatus.OK).body(paymentResDTO);
    }

    @GetMapping("/info")
    public ResponseEntity<TransactionStatusDTO> getTransactionStatus(
            @RequestParam(value = "vnp_Amount") String amount,
            @RequestParam(value = "vnp_BankCode") String bankCode,
            @RequestParam(value = "vnp_OrderInfo") String orderInfo,
            @RequestParam(value = "vnp_ResponseCode") String responseCode) {
        TransactionStatusDTO transactionStatusDTO = paymentService.getTransactionStatus(amount, bankCode, orderInfo, responseCode);
        return ResponseEntity.status(HttpStatus.OK).body(transactionStatusDTO);
    }
}
