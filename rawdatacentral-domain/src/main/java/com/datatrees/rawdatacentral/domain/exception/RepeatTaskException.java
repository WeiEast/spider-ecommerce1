package com.datatrees.rawdatacentral.domain.exception;

import java.io.Serializable;

/**
 * 重复提交
 * Created by zhouxinghai on 2017/5/13.
 */
public class RepeatTaskException extends RuntimeException implements Serializable {

    public RepeatTaskException() {
        super();
    }

    public RepeatTaskException(String errorMsg) {
        super(errorMsg);
    }
}
