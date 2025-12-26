package org.nextme.userGoal_service.userGoal.infrastructure.embedding;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nextme.userGoal_service.userGoal.application.dto.UpdateUserGoal;
import org.nextme.userGoal_service.userGoal.application.exception.GoalErrorCode;
import org.nextme.userGoal_service.userGoal.application.exception.GoalException;
import org.nextme.userGoal_service.userGoal.domain.entity.UserGoal;
import org.nextme.userGoal_service.userGoal.domain.repository.UserGoalRepository;
//import org.nextme.userGoal_service.userGoal.infrastructure.client.feignClient;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.EmbeddingGoalRequest;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.document.Document;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmbeddingServiceImpl implements EmbeddingServiceAdapter {
    private final UserGoalRepository userGoalRepository;
//    @Qualifier("customVectorStore")
    private final VectorStore vectorStore;
//    private final feignClient feignClient;
    private final JdbcClient jdbcClient;


    /**
     * 특정 사용자 목표를 임베딩하고 VectorStore에 저장
     * @return 임베딩 벡터
     */

    @Override
    public void embeddingGoal(EmbeddingGoalRequest request, UUID userId) {

        // 사용자 목표 임베딩 + 저장
        userGoalEmbed(request, userId);

        // 금융상품 임베딩 + 저장
//        bankItemEmbed();

        // 거래내역 임베딩 + 저장
        //tranListEmbed(request.userid());

    }


     /**
     * 사용자 목표 정보를 기반으로 임베딩 수행
     * - DB에서 사용자 목표 조회
     * - 자연어 텍스트로 변환
     * - 토큰 단위로 분할 후 벡터 스토어에 저장
     */
    public void userGoalEmbed(EmbeddingGoalRequest request,UUID userId) {

        // 1. DB에서 사용자 목표 조회
        UserGoal goals = userGoalRepository.findByUserId(userId);

        if (goals == null) {
            throw new GoalException(GoalErrorCode.USER_ID_NOT_FOUND);
        }

        String userText =
                "나이 : " + goals.getAge() +
                        " 직업: " + goals.getJob() +
                        " 자본금 : " + goals.getCapital() +
                        " 월수입 : " + goals.getMonthlyIncome() +
                        " 월고정지출 : " + goals.getFixedExpenses()+
                " 질문 : " + request.question();

        //텍스트를 토큰 단위로 분할하기 위한 설정
        //긴 텍스트를 어떤 기준과 크기로 쪼갤지 정의하는 분할 규칙
        //chunk size : 한 Document에 들어갈 최대 토큰 수
        //overlap : 다음 chunk와 겹쳐서 포함할 토큰 수
        // 최소 분할 단위 : 너무 짧은 조각을 방지하기 위한 기준
        //최대 허용 토큰 : 이 이상이면 강제 분할
        // 정규화 여부 : 토큰 기준으로 정확히 분할
        TokenTextSplitter splitter = new TokenTextSplitter(1000, 400, 10, 5000, true);

        // 사용자의 아이디가 있는지 조회
        int count = jdbcClient.sql("SELECT COUNT(*) FROM vector_store WHERE metadata->>'source' = '사용자목표' AND metadata->> 'userId' = :userId")
                .param("userId",userId.toString())
                .query(int.class)
                .single();

        if (count > 0) {
            System.out.println("이미 있음");
            return;
        }

         // Document 객체 생성 (본문 + 메타데이터)
        List<Document> userDocs = List.of(new Document(userText,
                Map.of("source", "사용자목표", "userId", goals.getUserId())));

         // 긴 Context 텍스트를 토큰 기준으로 여러 개의 작은 Document로 분할
        // 임베딩 전 단계
        List<Document> splitUserDocs = splitter.apply(userDocs);

        // 이때 임베딩 처리
        vectorStore.accept(splitUserDocs);

    }


    @Override
    // 사용자가 목표 수정했을 시 백터 테이블 수정
    public void updateEmbeddingGoal(UpdateUserGoal updateUserGoal) {
        // 사용자의 아이디가 있는지 조회
        int count = jdbcClient.sql("SELECT COUNT(*) FROM vector_store WHERE metadata->>'source' = '사용자목표' AND metadata->> 'userId' = :userId")
                .param("userId",updateUserGoal.getUserId().toString())
                .query(int.class)
                .single();

        // 유저가 있다면
        if (count > 0) {
            jdbcClient.sql(
                            "UPDATE vector_store SET content = :content " +
                                    "WHERE metadata->>'source' = '사용자목표' AND metadata->>'userId' = :userId")
                    .param("content", updateUserGoal.getUpdateGoal())
                    .param("userId", updateUserGoal.getUserId().toString())
                    .update();
        }

    }

    // 금융상품 임베딩+저장
//    public void bankItemEmbed() {
//
//        // 4. 금융상품 Embedding
//        List<Map<String, Object>> bankItems = feignClient.getFinancialProducts();
//
//        TokenTextSplitter splitter = new TokenTextSplitter(1000, 400, 10, 5000, true);
//
//        for(Map<String, Object> item : bankItems) {
//
//            int count = jdbcClient.sql("SELECT COUNT(*) FROM vector_store WHERE metadata->>'source' = '금융상품' AND metadata->> 'bankItemId' = :bankItemId")
//                    .param("bankItemId",item.get("bankItemId"))
//                    .query(int.class)
//                    .single();
//
//            if(count > 0) {
//
//                continue;
//            }
//
//            String bankItem ="";
//            bankItem += " 금융상품 상품명 : " + item.get("fin_prdt_nm");
//            bankItem += " 가입제한 : " + item.get("join_deny");
//            bankItem += " 가입대상 : " + item.get("join_member");
//            bankItem += " 우대조건 : " + item.get("spcl_cnd");
//            bankItem += " 저축기간(개월) : " + item.get("save_trm");
//            bankItem += " 기본금리 : " + item.get("intr_rate");
//            bankItem += " 공시시작일 : " + item.get("dcls_strt_day");
//            bankItem += " 공시종료일 : " + item.get("dcls_end_day");
//            bankItem += " 최고한도 : " + item.get("max_limit");
//            bankItem += " 금융상품타입 : " + item.get("item_type");
//            bankItem += " 기타유의사항 : " + item.get("etc_note");
//
//
//
//            Document doc = new Document(bankItem,
//                    Map.of("source", "금융상품", "bankItemId", item.get("bankItemId")));
//            List<Document> splitBankDocs = splitter.apply(List.of(doc));
//
//            // 임베딩한 값 저장
//            vectorStore.accept(splitBankDocs);
//
//        }
//    }

    // 사용자 거래내역 임베딩
//    public void tranListEmbed(UUID userId) {
//
//        // 거래내역 정보 가져오기
//        List<Map<String, Object>> tranList = feignClient.getTranList(userId);
//
//
//        TokenTextSplitter splitter = new TokenTextSplitter(1000, 400, 10, 5000, true);
//
//
//        // 사용자의 아이디가 있는지 조회
//        int count = jdbcClient.sql("SELECT COUNT(*) FROM vector_store WHERE metadata->>'source' = '사용자목표' AND metadata->> 'userId' = :userId")
//                .param("userId",tranList.get(0).get("userId"))
//                .query(int.class)
//                .single();
//
//
//        // 유저아이디가 있다면
//        if (count > 0) {
//            for(Map<String, Object> item : tranList) {
//                // 거래내역이 이미 있는지 확인
//                int tran_count = jdbcClient.sql("SELECT COUNT(*) FROM vector_store WHERE metadata->>'source' = '거래내역' AND metadata->> 'tranId' = :tranId")
//                        .param("tranId",item.get("tranId"))
//                        .query(int.class)
//                        .single();
//
//                if (tran_count > 0) {
//                    continue;
//                }
//
//                String tran = "";
//                tran += " 거래일자: " + item.get("resAccountTrDate");
//                tran += " 거래시각 : " + item.get("resAccountTrTime");
//                tran += " 출금금액 : " + item.get("resAccountOut");
//                tran += " 입금금액 : " + item.get("resAccountIn");
//                tran +=  " 거래명 : " + item.get("counterpartyName");
//
//                Document doc = new Document(tran,
//                        Map.of("source", "거래내역", "tranId", item.get("tranId")));
//                List<Document> splitBankDocs = splitter.apply(List.of(doc));
//
//                // 임베딩한 값 저장
//                vectorStore.accept(splitBankDocs);
//
//            }
//
//
//
//
//        }
//
//    }



}
