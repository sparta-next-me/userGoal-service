package org.nextme.userGoal_service.userGoal.infrastructure.embedding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nextme.userGoal_service.userGoal.application.exception.GoalErrorCode;
import org.nextme.userGoal_service.userGoal.application.exception.GoalException;
import org.nextme.userGoal_service.userGoal.domain.entity.UserGoal;
import org.nextme.userGoal_service.userGoal.domain.repository.UserGoalRepository;
import org.nextme.userGoal_service.userGoal.infrastructure.client.BankItemClient;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.EmbeddingGoalRequest;
import org.nextme.userGoal_service.userGoal.infrastructure.rebbitmq.UpdateUserGoalEvent;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.document.Document;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmbeddingServiceImpl implements EmbeddingServiceAdapter {
    private final UserGoalRepository userGoalRepository;
    private final VectorStore vectorStore;
    private final BankItemClient bankItemClient;
    private final JdbcClient jdbcClient;


    /**
     * 특정 사용자 목표를 임베딩하고 VectorStore에 저장
     * @return 임베딩 벡터
     */

    @Override
    public void embeddingGoal(EmbeddingGoalRequest request) {

        // 사용자 목표 임베딩 + 저장
        userGoalEmbed(request);

        // 금융상품 임베딩 + 저장
        bankItemEmbed();

    }

    // 사용자 목표 임베딩 + 저장
    public void userGoalEmbed(EmbeddingGoalRequest request) {

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

        // 사용자의 아이디가 있는지 조회
        int count = jdbcClient.sql("SELECT COUNT(*) FROM vector_store WHERE metadata->>'source' = '사용자목표' AND metadata->> 'userId' = :userId")
                .param("userId",request.userid().toString())
                .query(int.class)
                .single();


        if (count > 0) {
            System.out.println("이미 있음");
            return;
        }

        List<Document> userDocs = List.of(new Document(userText,
                Map.of("source", "사용자목표", "userId", goals.getUserId())));
        List<Document> splitUserDocs = splitter.apply(userDocs);
        vectorStore.accept(splitUserDocs);
    }

    // 금융상품 임베딩+저장
    public void bankItemEmbed() {

        // 4. 금융상품 Embedding
        List<Map<String, Object>> bankItems = bankItemClient.getFinancialProducts();

        TokenTextSplitter splitter = new TokenTextSplitter(1000, 400, 10, 5000, true);

        for(Map<String, Object> item : bankItems) {

            int count = jdbcClient.sql("SELECT COUNT(*) FROM vector_store WHERE metadata->>'source' = '금융상품' AND metadata->> 'bankItemId' = :bankItemId")
                    .param("bankItemId",item.get("bankItemId"))
                    .query(int.class)
                    .single();

            if(count > 0) {

                continue;
            }

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



            Document doc = new Document(bankItem,
                    Map.of("source", "금융상품", "bankItemId", item.get("bankItemId")));
            List<Document> splitBankDocs = splitter.apply(List.of(doc));

            // 임베딩한 값 저장
            vectorStore.accept(splitBankDocs);

        }
    }

    @Override
    // 사용자가 목표 수정했을 시 백터 테이블 수정
    public void updateEmbeddingGoal(UpdateUserGoalEvent updateUserGoalEvent) {

    }


}