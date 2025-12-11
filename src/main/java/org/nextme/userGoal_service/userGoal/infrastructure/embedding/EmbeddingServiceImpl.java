package org.nextme.userGoal_service.userGoal.infrastructure.embedding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.nextme.userGoal_service.userGoal.application.exception.GoalErrorCode;
import org.nextme.userGoal_service.userGoal.application.exception.GoalException;
import org.nextme.userGoal_service.userGoal.domain.entity.UserGoal;
import org.nextme.userGoal_service.userGoal.domain.repository.UserGoalRepository;
import org.nextme.userGoal_service.userGoal.infrastructure.client.BankItemClient;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.EmbeddingGoalRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.rebbitmq.UpdateUserGoalEvent;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmbeddingServiceImpl implements EmbeddingServiceAdapter {
    private final UserGoalRepository userGoalRepository;
    private final VectorStore vectorStore;
    private final BankItemClient bankItemClient;



    /**
     * 특정 사용자 목표를 임베딩하고 VectorStore에 저장
     * @return 임베딩 벡터
     */

    @Override
    public void embeddingGoal(EmbeddingGoalRequest request) {

        // 1. DB에서 사용자 목표 조회
        UserGoal goals = userGoalRepository.findByUserId(request.userid());

        if (goals == null) {
            throw new GoalException(GoalErrorCode.USER_ID_NOT_FOUND);
        }

        String userText =
                "나이 : " + goals.getAge() +
                        " 직업: " + goals.getJob() +
                        " 자본금 : " + goals.getCapital() +
                        " 월수입 : " + goals.getMonthlyIncome() +
                        " 월고정지출 : " + goals.getFixedExpenses();

        TokenTextSplitter splitter = new TokenTextSplitter(1000, 400, 10, 5000, true);


        // 백터디비에 사용자의 목표 있는지 조회
        // similaritySearch : 유사한 벡터를 검색하는 메서드
        List<Document> existingUser = vectorStore.similaritySearch(
                //SearchRequest : similaritySearch를 호출할 때 전달하는 검색 조건 객체
                SearchRequest.builder()
                        //query → 벡터 유사도 계산용 (임베딩 필요),
                        // query 용도: 1. 백터 존재 여부 확인용 / 2. 질문에 대한 값을 넣음 (예시 : "서울 아파트 투자 전략 알려줘")
                        .query(userText)
                        // filterExpression → metadata 기준 필터링
                        .filterExpression("userId == '" + goals.getUserId().toString() + "'")
                        .topK(1)
                        .build()
        );

        // 이미 데이터가 있다면 임베딩 하지 않고 검색하여 ai로 보냄
        if (existingUser.isEmpty()) {
            List<Document> userDocs = List.of(new Document(userText,
                    Map.of("source", "사용자목표", "userId", goals.getUserId())));
            List<Document> splitUserDocs = splitter.apply(userDocs);
            vectorStore.accept(splitUserDocs);
        } else {
            System.out.println("사용자 목표 이미 존재: " + goals.getUserId());
        }



        List<Document> userGoal =List.of(new Document(userText.toString(), Map.of("source","사용자목표","userId",goals.getUserId())));


        // 4. 금융상품 Embedding
        List<Map<String, Object>> bankItems = bankItemClient.getFinancialProducts();

        for(Map<String, Object> item : bankItems) {
            String bankItem ="";
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


            // 백터디비에 금융상품 있는지 조회
            // similaritySearch : 유사한 벡터를 검색하는 메서드
            List<Document> existingGoal = vectorStore.similaritySearch(
                    //SearchRequest : similaritySearch를 호출할 때 전달하는 검색 조건 객체
                    SearchRequest.builder()
                            //query → 벡터 유사도 계산용 (임베딩 필요),
                            // query 용도: 1. 백터 존재 여부 확인용 / 2. 질문에 대한 값을 넣음 (예시 : "서울 아파트 투자 전략 알려줘")
                            .query(bankItem)
                            // filterExpression → metadata 기준 필터링
                            .filterExpression("bankId == " + item.get("bankItemId") + "'")
                            .topK(1)
                            .build()
            );
            if (existingGoal.isEmpty()) {
                Document doc = new Document(bankItem,
                        Map.of("source", "금융상품", "bankItemId", item.get("bankItemId")));
                List<Document> splitBankDocs = splitter.apply(List.of(doc));
                vectorStore.accept(splitBankDocs);
            } else {
                System.out.println("금융상품 이미 존재: " + item.get("bankItemId"));
            }
        }

    }

    @Override
    // 사용자가 목표 수정했을 시 백터 테이블 수정
    public void updateEmbeddingGoal(UpdateUserGoalEvent updateUserGoalEvent) {

    }


}
