package org.nextme.userGoal_service.userGoal.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@ToString
@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserGoalId {
    private UUID id;

    protected UserGoalId(UUID id) {
        this.id = id;
    }

    public static UserGoalId of(UUID id) {
        return new UserGoalId(id);
    }

    public static UserGoalId of(String id) {
        return UserGoalId.of(UUID.randomUUID());
    }
}
