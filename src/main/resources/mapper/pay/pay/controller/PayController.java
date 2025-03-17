package com.udigo.hotel.pay.controller;

import com.udigo.hotel.member.security.CustomUserDetails;
import com.udigo.hotel.pay.model.service.PayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RestController
@RequestMapping("/payment")
public class PayController {

    @Autowired
    private PayService payService;
    private static final Logger logger = LoggerFactory.getLogger(PayController.class);
    @PostMapping("/cart")
    public ResponseEntity<List<Map<String, Object>>> getCartItemsByMember() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            List<Map<String, Object>> cartItems = payService.getCartItemsByMember(userDetails.getMemberCode());
            return ResponseEntity.ok(cartItems);  // 응답을 ResponseEntity로 감싸서 반환
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // 인증되지 않은 경우
    }


    // 프론트에서 /pay/payment-perview API를 호출하면 결제 예정 숙소 정보를 Json으로 반환
    @PostMapping
    public ResponseEntity<Map<String, Object>> processPayment(@RequestBody Map<String, Object> requestData) {
        // TODO: cartController 참고
        String memberCode = (String) requestData.get("member_code");
        String payMethod = (String) requestData.get("pay_method");
        // TODO: acmId 리스트 추가

        // System.out.println(payMethod);

        List<Map<String, Object>> accommodations = (List<Map<String, Object>>) requestData.get("accommodations");

        // ✅ 결제 ID 생성
        String payId = UUID.randomUUID().toString();

        // ✅ 결제 정보 DB 저장
        boolean isSaved = savePaymentToDatabase(memberCode, payId, payMethod, accommodations);

        // ✅ 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
        response.put("success", isSaved);
        response.put("pay_id", payId);

        return ResponseEntity.ok(response);
    }

    private boolean savePaymentToDatabase(String memberCode, String payId, String payMethod, List<Map<String, Object>> accommodations) {
        try {
            System.out.println("🔹 결제 저장: " + payId + " / 회원: " + memberCode);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 결제 요청 API
    @PostMapping("/process")
    public ResponseEntity<String> processPayment(@RequestParam String memberCode) {
        try {
            payService.processPayment(memberCode);
            return ResponseEntity.ok("결제가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("결제 중 오류 발생: " + e.getMessage());
        }
    }


}