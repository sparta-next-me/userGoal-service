package org.nextme.userGoal_service.userGoal.infrastructure.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nextme.userGoal_service.userGoal.domain.entity.ChatMessage;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiMessageResponse implements MessageTpl {
    private UUID roomId;
    private String roomType;
    private String content;
    private String sessionId;

    public static AiMessageResponse of(ChatMessage chatMessage,String content) {
        return AiMessageResponse.builder()
                .roomId(chatMessage.getRoomId())
                .roomType(chatMessage.getRoomType())
                .content(content)
                .sessionId(chatMessage.getSessionId())
                .build();
    }
}
