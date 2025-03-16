$(document).ready(function () {
    // 결제 버튼 클릭 이벤트 등록
    $(document).on("click", ".payButton", function () {
        console.log("결제 버튼 클릭됨!");
        requestPayment();
    });

});

// PortOne(구 아임포트) 결제 요청 함수
function requestPayment() {
    IMP.init("imp61481528"); // PortOne 가맹점 코드 입력

    let userName = $("input[placeholder='회원 이름을 입력해주세요.']").val();
    let phoneNumber = $("input[placeholder='전화번호를 입력해주세요.']").val();
    let amount = 230000; // 실제 결제 금액 (예제 값)

    IMP.request_pay({
        pg: "html5_inicis", // 이니시스 PG 사용
        pay_method: "card", // 카드 결제
        merchant_uid: "ORDER_" + new Date().getTime(), // 주문 번호 (고유값)
        name: "숙소 예약 결제",
        // amount: amount, // 결제 금액
        amount: 100, // 결제 금액
        buyer_email: "user@example.com", // 예제 이메일 (실제 적용 시 변경 필요)
        buyer_name: userName,
        buyer_tel: phoneNumber
    }, function (rsp) {
        if (rsp.success) {
            console.log("결제 성공:", rsp);
            verifyPayment(rsp);
        } else {
            console.error("결제 실패:", rsp.error_msg);
            alert("결제 실패: " + rsp.error_msg);
        }
    });
}

// 🔹 백엔드로 결제 검증 요청
function verifyPayment(rsp) {
    const date = new Date(rsp.paid_at * 1000); // 유닉스 타임스탬프 값을 밀리초 단위로 변환

    fetch("/payment/api/verify", {
        method: "POST", // GET: 보안 X / URL blogPost&page=3 / GET 방식은 속도가 빨라, POST: 암호화돼서 넘어가 / 보안 good~! / 조금 더 느림
        headers: { "Content-Type": "application/json" }, // Content-Type: ajax 요청 보낼 때 이 데이터가 무슨 값인지 백엔드(JAVA) 알려주는 용도 - 파일 형태가 json이라고 알려주는 거
        body: JSON.stringify({
            member_code: "--",
            acm_id: "",
            pay_method: "card",
            pay_status: "결제완료",
            pay_type: rsp.pay_method,
            pay_date: date,
            pay_price: rsp.paid_amount,
            discount: 0,
            transaction_id: rsp.merchantUid,
            pay_provider: rsp.pg_provider,
            imp_uid: rsp.impUid,
            // merchantUid: rsp.merchantUid 
        }) // 넘겨질 데이터
    }).then(response => response.json())
        .then(data => {
            if (data.success) {
                alert("결제 성공!");
                window.location.href = "/order-confirmation"; // 결제 완료 페이지 이동
            } else {
                alert("결제 검증 실패!");
            }
        }).catch(error => {
        console.error("결제 검증 중 오류:", error);
        alert("결제 검증 중 오류 발생!");
    });
}


// 장바구니 ---------------------------------------------

// 장바구니 임시 구현
document.addEventListener("DOMContentLoaded", function () {
    loadCartItems();
});

