package org.nextme.userGoal_service.userGoal.application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum GoalErrorCode {

    GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "목표를 찾을 수 없습니다."),
    GOAL_NOTING_CHANGE(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "수정할 정보가 없습니다."),
    GOAL_MISSING_PARAMETER(HttpStatus.BAD_REQUEST,"BAD_REQUEST", "필수 요청 파라미터가 없습니다"),
    USER_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "사용자 아이디를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String defaultMessage;

    GoalErrorCode(HttpStatus status, String code, String message) {
        this.httpStatus = status;
        this.code = code;
        this.defaultMessage = message;
    }
}
