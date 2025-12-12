//package org.nextme.userGoal_service.userGoal.infrastructure.gemini;
//
//// ===== 메소드 별 정리 ======
///*
//connectionDetails()
//        - Vertex AI 프로젝트와 위치 정보를 담은 Bean
//        - 나중에 임베딩 모델이 Vertex AI API와 연결될 때 사용
//embeddingOptions()
//        - 어떤 모델을 사용할지, 벡터 차원은 몇인지 정하는 Bean
//        - 임베딩 결과 벡터의 길이와 모델에 따라 유사도 계산 정확도에 영향
//embeddingModel()
//        - 실제 텍스트를 벡터로 바꾸는 객체
//        - connectionDetails + embeddingOptions가 있어야 생성 가능
//        - RAG나 유사도 검색에 사용되는 핵심 객체
//*/
//
//// Lombok을 이용해 final 필드를 자동으로 초기화하는 생성자를 만들어 줌
//import lombok.RequiredArgsConstructor;
//
//// Vertex AI 임베딩 관련 클래스
//import org.springframework.ai.vertexai.embedding.VertexAiEmbeddingConnectionDetails;
//import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingModel;
//import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingOptions;
//
//// Spring Boot 설정 관련
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//// 문자열 관련 유틸
//import org.springframework.util.StringUtils;
//
//// Spring Configuration 클래스임을 나타냄
//@Configuration
//
//// final 필드에 대한 생성자 자동 생성
//@RequiredArgsConstructor
//
//// GeminiProperties 설정값을 자동으로 주입받도록 함
//@EnableConfigurationProperties(GeminiProperties.class)
//public class GeminiConfig {
//
//    // 설정값 주입 (프로젝트 ID, 모델명 등)
//    private final GeminiProperties properties;
//
//    // Vertex AI 연결 정보를 Bean으로 등록
//    @Bean
//    public VertexAiEmbeddingConnectionDetails connectionDetails() {
//        return VertexAiEmbeddingConnectionDetails.builder()
//                .projectId(properties.projectId()) // GeminiProperties에서 프로젝트 ID 가져오기
//                .location("us-west4") // Vertex AI 리전 설정
//                .build();
//    }
//
//    // 텍스트 임베딩 옵션 Bean 등록
//    @Bean
//    public VertexAiTextEmbeddingOptions embeddingOptions() {
//        String model = properties.embeddingModel(); // 설정에서 임베딩 모델 이름 가져오기
//        return VertexAiTextEmbeddingOptions
//                .builder()
//                // 모델명이 비어있지 않으면 설정값 사용, 비어있으면 기본 모델명 사용
//                .model(StringUtils.hasText(model) ? model : VertexAiTextEmbeddingOptions.DEFAULT_MODEL_NAME)
//                // 모델별 임베딩 차원 수 지정
//                // 보통 모델명이 있으면 최신 모델이라 3072차원
//                // 모델명이 없으면 예전 기본 모델이라 768차원
//                // 차원 수는 벡터 길이, 나중에 유사도 계산할 때 중요
//                .dimensions(StringUtils.hasText(model) ? 3072 : 768)
//                .build();
//    }
//
//    // Vertex AI 임베딩 모델 Bean 등록
//    @Bean
//    public VertexAiTextEmbeddingModel embeddingModel(
//            // connectionDetails()에서 만든 VertexAiEmbeddingConnectionDetails Bean 자동 주입
//            VertexAiEmbeddingConnectionDetails details,
//
//            // embeddingOptions()에서 만든 VertexAiTextEmbeddingOptions Bean 자동 주입
//            VertexAiTextEmbeddingOptions options) {
//
//        // 주입받은 연결 정보와 옵션을 가지고 Vertex AI 임베딩 모델 객체 생성
//        // 이 객체를 통해 실제 텍스트를 벡터로 변환 가능
//        // 예: "안녕하세요" -> [0.12, -0.34, ..., 0.56] 같은 숫자 배열
//        return new VertexAiTextEmbeddingModel(details, options); // 모델 생성
//    }
//}
