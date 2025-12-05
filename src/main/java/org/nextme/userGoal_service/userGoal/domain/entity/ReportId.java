package org.nextme.userGoal_service.userGoal.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@ToString
@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportId {
    private UUID id;

    protected ReportId(UUID id) {
        this.id = id;
    }

    public static ReportId of(UUID id) {
        return new ReportId(id);
    }

    public static ReportId of(String id) {
        return ReportId.of(UUID.randomUUID());
    }
}
