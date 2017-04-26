package com.datatrees.rawdatacentral.domain.enums;

public enum ErrorCode {

    RESULT_SEND_ERROR(202, "Result send error!"),

    TASK_INIT_ERROR(302, "Task init error!"),

    CONFIG_ERROR(304, "Config error!"),

    TASK_INTERRUPTED_ERROR(306, "Thread Interrupted!"),

    LOGIN_TIMEOUT_ERROR(308, "Login Time Out!"),

    LOGIN_ERROR(402, "Login error!"),

    COOKIE_INVALID(404, "Cookie invalid!"),

    GIVE_UP_RETRY(406, "Give up retry!"),

    MESSAGE_DROP(408, "Message drop!"),

    INIT_QUEUE_FAILED_ERROR_CODE(502, "The LinkQueue initialization failed!"),

    NO_ACTIVE_PROXY(503, "can't get vaild proxy"),

    BLOCKED_ERROR_CODE(504, "Access block, the system will early quit"), // Access block

    TASK_TIMEOUT_ERROR_CODE(506, "The task is timeout"), // Task time out

    QUEUE_FULL_ERROR_CODE(508, "The link queue is full"),

    NO_RESULT_ERROR_CODE(510, "There is no result"), // NO_RESULT_ERROR_CODE

    NOT_EMPTY_ERROR_CODE(512, "Field not empty"),

    RESPONSE_EMPTY_ERROR_CODE(514, "Response not empty"),

    UNKNOWN_REASON(520, "Unknown Reason.");



    private int errorCode;
    private String errorMessage;

    private ErrorCode(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return String.valueOf(this.errorCode);
    }
}
