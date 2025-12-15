//package org.nextme.userGoal_service.userGoal.infrastructure.embedding;
//
//import org.springframework.ai.embedding.EmbeddingModel;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.simple.JdbcClient;
//import org.springframework.ai.vectorstore.VectorStore;
//import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
//
//@Configuration
//public class VectorStoreConfig {
//
//    @Bean
//    public VectorStore customVectorStore(
//            JdbcTemplate jdbcTemplate,
//            EmbeddingModel embeddingModel
//    ) {
//        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
//                .indexType(PgVectorStore.PgIndexType.HNSW)
//                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
//                //.schemaName("usergoalservice")       // 스키마 명시
//                .vectorTableName("vector_store")      // 테이블 명시
//                .initializeSchema(true)              // 자동 schema init 옵션
//                .build();
//    }
//
//}
//
