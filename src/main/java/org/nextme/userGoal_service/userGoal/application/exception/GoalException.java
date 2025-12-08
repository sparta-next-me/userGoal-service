package org.nextme.userGoal_service.userGoal.application.exception;


import org.nextme.userGoal_service.global.infrastructure.exception.ApplicationException;

public class GoalException extends ApplicationException {
    public GoalException(GoalErrorCode errorCode) {
        super(errorCode.getHttpStatus(), errorCode.getCode(), errorCode.getDefaultMessage());
    }

    public GoalException(GoalErrorCode errorCode, String message) {
        super(errorCode.getHttpStatus(), errorCode.getCode(), message);
    }
}
