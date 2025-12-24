package org.nextme.userGoal_service.userGoal.infrastructure.ai;

import lombok.extern.slf4j.Slf4j;
import org.nextme.userGoal_service.userGoal.domain.entity.ChatMessage;
import org.nextme.userGoal_service.userGoal.infrastructure.embedding.EmbeddingServiceAdapter;
import org.nextme.userGoal_service.userGoal.infrastructure.kafka.dto.AiMessageResponse;
import org.nextme.userGoal_service.userGoal.infrastructure.presentation.dto.request.EmbeddingGoalRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AiService implements AiServiceAdapter {

    private EmbeddingServiceAdapter embeddingServiceAdapter;
    private VectorStore vectorStore;
    private ChatClient client;
    private ChatModel chatModel;

    @Value("classpath:/ai/prompt.txt")
    private Resource resource;

    @Value("classpath:/ai/chat_prompt.txt")
    private Resource resource1;

    public AiService(ChatClient.Builder builder,
                     ChatMemory chatMemory,
                     VectorStore vectorStore,
                     EmbeddingServiceAdapter embeddingServiceAdapter,
                     ChatModel chatModel) {

        this.client = builder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                ).build();

        this.vectorStore = vectorStore;
        this.embeddingServiceAdapter = embeddingServiceAdapter;
        this.chatModel = chatModel;
    }

    @Override
    public String answer(EmbeddingGoalRequest request, UUID userId) {

        // 1. 사용자 목표 임베딩 (기존 로직 유지)
        embeddingServiceAdapter.embeddingGoal(request, userId);

        // 2. 사용자목표 + 금융상품 함께 검색
        SearchRequest search = SearchRequest.builder()
                .query(request.question())
                .topK(5)
                .filterExpression(
                        "source == '금융상품' OR " +
                                "(source == '사용자목표' AND userId == '" + userId.toString() + "')"
                )
                .build();

        // 3. 벡터 검색 실행
        List<Document> topKs = vectorStore.similaritySearch(search);

        if (topKs.isEmpty()) {
            log.info("No documents found for userId={}", userId);
            return "요청하신 조건에 맞는 금융상품 정보가 존재하지 않습니다. 금융 상담원을 통해 확인해 주세요.";
        }

        // 4. 금융상품 존재 여부 검증 (환각 차단 핵심)
        boolean hasFinancialProduct = topKs.stream()
                .anyMatch(d -> "금융상품".equals(d.getMetadata().get("source")));

        if (!hasFinancialProduct) {
            return "요청하신 조건에 맞는 금융상품 정보가 부족하여 정확한 추천이 어렵습니다. 금융 상담원을 통해 확인해 주세요.";
        }

        // 5. Context 생성
        String documents = topKs.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n"));

        // 6. LLM 호출
        return client.prompt()
                .user(s -> s.text(resource, StandardCharsets.UTF_8)
                        .param("question", request.question())
                        .param("context", documents)
                        .param("forceKorean", true)
                )
                .call()
                .content();
    }

    @Override
    public AiMessageResponse chatAnswer(ChatMessage chatMessage) {

        SearchRequest search = SearchRequest.builder()
                .query(chatMessage.getContent())
                .topK(3)
                .build();

        List<Document> topKs = vectorStore.similaritySearch(search);

        if (topKs.isEmpty()) return null;

        String documents = topKs.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining());

        String result = client.prompt()
                .user(s -> s.text(resource1, StandardCharsets.UTF_8)
                        .param("question", chatMessage.getContent())
                        .param("context", documents)
                        .param("forceKorean", true)
                )
                .call()
                .content();

        return AiMessageResponse.of(chatMessage, result);
    }
}
