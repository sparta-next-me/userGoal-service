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
import java.util.ArrayList;
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
        log.info("userId : {}", userId);

        //사용자 목표 임베딩 (기존 로직 유지)
        embeddingServiceAdapter.embeddingGoal(request, userId);

        //사용자 목표 조회
        SearchRequest userGoalSearch = SearchRequest.builder()
                .query(request.question())
                .topK(1)
                .filterExpression("source == '사용자목표' AND userId == '" + userId.toString() + "'")
                .build();

        log.info("userGoalSearcher : " + userGoalSearch );

        List<Document> userGoalDocs = vectorStore.similaritySearch(userGoalSearch);
        log.info("userGoalDocs : " + userGoalDocs);


        // 금융상품 조회
        SearchRequest productSearch = SearchRequest.builder()
                .query(request.question())
                .topK(5)
                .filterExpression("source == '금융상품'")
                .build();

        log.info("productSearcher : " + productSearch );

        List<Document> productDocs = vectorStore.similaritySearch(productSearch);
        log.info("productDocs : " + productDocs);


        // context 합치기
        List<Document> contextDocs = new ArrayList<>();
        log.info("contextDocs : " + contextDocs);

        contextDocs.addAll(userGoalDocs);
        contextDocs.addAll(productDocs);
        log.info("contextDocs 결과 : " + contextDocs);

        String context = contextDocs.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n"));
        log.info("context : " + context);

        // LLM 호출 (프롬프트에서 context 기반으로만 추천하도록 명시)
        String result = client.prompt()
                .user(s -> s.text(resource, StandardCharsets.UTF_8)
                        .param("question", request.question())
                        .param("context", context)
                        .param("forceKorean", true)
                )
                .call()
                .content();
        log.info("result : " + result);
        return result;
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
