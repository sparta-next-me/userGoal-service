package org.nextme.userGoal_service.userGoal.infrastructure.gemini;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nextme.userGoal_service.userGoal.infrastructure.embedding.EmbeddingServiceAdapter;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.EmbeddingGoalRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GeminiService implements GeminiServiceAdapter {

    private  EmbeddingServiceAdapter embeddingServiceAdapter;
    private  VectorStore vectorStore;
    private  ChatClient client;

    @Value("classpath:/ai/prompt.txt")
    private Resource resource;


    public GeminiService(ChatClient.Builder builder, ChatMemory chatMemory, VectorStore vectorStore, EmbeddingServiceAdapter embeddingServiceAdapter) {
        this.client = builder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                ).build();
        this.vectorStore = vectorStore;
        this.embeddingServiceAdapter = embeddingServiceAdapter;
    }

    @Override
    public String answer(EmbeddingGoalRequest request) {

        // 사용자 목표 내용을 임베딩 시켜 디비 저장
        embeddingServiceAdapter.embeddingGoal(request);

        // 질문과 유사한 내용을 담고 있는 문서 3개 추출
        //SearchRequest : similaritySearch를 호출할 때 전달하는 검색 조건 객체
        SearchRequest search = SearchRequest.builder()
                //query → 벡터 유사도 계산용 (임베딩 필요),
                // query 용도: 1. 백터 존재 여부 확인용 / 2. 질문에 대한 값을 넣음 (예시 : "서울 아파트 투자 전략 알려줘")
                .query(request.question()).topK(3).build();

        List<Document> topKs = vectorStore.similaritySearch(search);


        if (topKs.isEmpty()) return null;

        String documents = topKs.stream().map(Document::getFormattedContent)
                .collect(Collectors.joining());


        return client.prompt()
                .user(s -> s.text(resource, StandardCharsets.UTF_8)
                        .param("question", request.question())
                        .param("context", documents)
                        .param("forceKorean", true)
                ).call().content();

    }
}
