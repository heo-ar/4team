package com.udigo.hotel.pay.model.service;

import com.udigo.hotel.pay.model.dao.PayMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
//@RequiredArgsConstructor
public class PayService {

    @Autowired
    private PayMapper payMapper;

    // 카트 내역 불러오는 메서드
    public List<Map<String, Object>> getCartItemsByMember(int memberCode) {
        return payMapper.getCartItemsByMember(memberCode);
    }

    public List<Map<String, Object>> getCheckedAccommodations(String memberCode) {
        // 장바구니에 담긴 숙소 ID 조회
        List<Map<String, Object>> cartItems = payMapper.getCartItemsByMember(Integer.parseInt(memberCode));

        // 각 숙소의 가격을 조회하여 추가
        for (Map<String, Object> item : cartItems) {
            int acmId = (int) item.get("acm_id"); // 숙소 ID 가져오기
            Map<String, Object> priceInfo = payMapper.getAccommodationPrice(acmId); // 숙소 가격 조회

            if (priceInfo != null && priceInfo.containsKey("price")) {
                item.put("price", priceInfo.get("price")); // 가격 추가
            } else {
                item.put("price", 0); // 가격이 없는 경우 기본값 설정
            }
        }
        return cartItems;
    }

    public void processPayment(String memberCode) {

    }
}