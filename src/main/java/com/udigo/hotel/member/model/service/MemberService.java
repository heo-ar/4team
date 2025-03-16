package com.udigo.hotel.member.model.service;

import com.udigo.hotel.member.model.dao.MemberMapper;
import com.udigo.hotel.member.model.dto.MemberDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberMapper memberMapper, PasswordEncoder passwordEncoder) {
        this.memberMapper = memberMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /** 회원가입 처리 **/
    public void signup(MemberDTO memberDTO) {
        if (memberDTO.getPassword() == null || memberDTO.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호를 입력해야 합니다.");
        }

        System.out.println("🔍 회원가입 데이터 (DB 저장 전): " + memberDTO.toString());

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(memberDTO.getPassword());
        memberDTO.setPassword(encodedPassword);
        System.out.println("🔐 비밀번호 암호화 완료");

        // 회원 정보 저장
        try {
            memberMapper.insertMember(memberDTO);
            System.out.println("✅ 회원 정보 DB 저장 완료: " + memberDTO.getMemberId());
        } catch (Exception e) {
            System.err.println("❌ 회원 정보 저장 실패: " + e.getMessage());
            throw new RuntimeException("회원가입 실패: " + e.getMessage());
        }
    }
    public MemberDTO findByEmail(String email) {
        return memberMapper.findByEmail(email);
    }



    public MemberDTO findByMemberId(String memberId) {
        return memberMapper.findByMemberId(memberId);
    }

    @Transactional
    public void updateMember(MemberDTO memberDTO) {
        memberMapper.updateMember(memberDTO);

    }

    public MemberDTO findMemberById(String name) {
        return null;
    }

    // pay
    @Transactional
    public void processPayment(String memberCode) {
        // 장바구니에서 숙소 정보 조회
        List<Map<String, Object>> cartItems = memberMapper.getCartItemsWithDetails(memberCode);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("장바구니가 비어 있습니다.");
        }

        // 각 숙소 정보를 결제 테이블(tbl_pay)에 저장
        for (Map<String, Object> item : cartItems) {
            memberMapper.insertPaymentRecord(item);
        }
    }
}
