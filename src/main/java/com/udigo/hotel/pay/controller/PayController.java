package com.udigo.hotel.pay.controller;

import com.udigo.hotel.pay.model.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RestController
@RequestMapping("/payment")
public class PayController {

    @Autowired
    private PayService payService;

    @PostMapping("/cart")
    public ResponseEntity<Map<String, Object>> getCartItemsByMember() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

                payService.getCartItemsByMember(userDetails.getMemberId());
                return ResponseEntity.ok("카트 내역 불러오기가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("카트 내역 불러오기 중 오류 발생: " + e.getMessage());
        }
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




