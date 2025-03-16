package com.udigo.hotel.member.model.dao;

import com.udigo.hotel.member.model.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Mapper
public interface MemberMapper {

    /** ✅ 회원 정보 저장 */
    void insertMember(MemberDTO memberDTO);

    /** ✅ member_id를 기반으로 회원 정보 조회 */
    MemberDTO findByMemberId(String memberId);

    // cart
    MemberDTO findByEmail(String email);
    void updateMember(MemberDTO memberDTO);

    // pay
    List<Map<String, Object>> getCartItemsWithDetails(String memberCode);
    void insertPaymentRecord(Map<String, Object> item);
}
