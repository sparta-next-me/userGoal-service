package org.nextme.userGoal_service.test;

import lombok.RequiredArgsConstructor;
import org.nextme.userGoal_service.userGoal.infrastructure.client.BankItemClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TestService {
    private final BankItemClient bankItemClient;
    private final VectorStore vectorStore;
    public void test() {
        // 금융상품 받아오기
        List<Map<String, Object>> bankItems = bankItemClient.getFinancialProducts();


        String bankItem ="";

        List<Document> bankItemDocuments = new ArrayList<>();

        for(Map<String, Object> item : bankItems) {
            bankItem += " 금융상품 상품명 : " + item.get("fin_prdt_nm");
            bankItem += " 가입제한 : " + item.get("join_deny");
            bankItem += " 가입대상 : " + item.get("join_member");
            bankItem += " 우대조건 : " + item.get("spcl_cnd");
            bankItem += " 저축기간(개월) : " + item.get("save_trm");
            bankItem += " 기본금리 : " + item.get("intr_rate");
            bankItem += " 공시시작일 : " + item.get("dcls_strt_day");
            bankItem += " 공시종료일 : " + item.get("dcls_end_day");
            bankItem += " 최고한도 : " + item.get("max_limit");
            bankItem += " 금융상품타입 : " + item.get("item_type");
            bankItem += " 기타유의사항 : " + item.get("etc_note");


            Document doc = new Document(
                    bankItem,
                    Map.of(
                            "source", "금융상품 정보",
                            "bankItemId", item.get("bankItemId")
                    )
            );
            // 백터디비에 금융상품 있는지 조회
            // similaritySearch : 유사한 벡터를 검색하는 메서드
            List<Document> existingGoal = vectorStore.similaritySearch(
                    //SearchRequest : similaritySearch를 호출할 때 전달하는 검색 조건 객체
                    SearchRequest.builder()
                            //query → 벡터 유사도 계산용 (임베딩 필요),
                            // query 용도: 1. 백터 존재 여부 확인용 / 2. 질문에 대한 값을 넣음 (예시 : "서울 아파트 투자 전략 알려줘")
                            .query(bankItem)
                            // filterExpression → metadata 기준 필터링
                            .filterExpression("bankId == '" + item.get("bankItemId") + "'")
                            .topK(1)
                            .build()
            );
            // 금융상품이 이미 존재한다면
            if(!existingGoal.isEmpty()) {
                System.out.println("이미 존재");
                System.out.println(existingGoal.toString() + " 금융상품");
            }
            else{

                bankItemDocuments.add(doc);
            }
        }

        System.out.println(bankItem + " 상품");
    }
}
