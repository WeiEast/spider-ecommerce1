package com.datatrees.rawdatacentral.service.lock;

/**
 * @author Jerry
 * @since 19:20 2018/5/3
 */
public class LockingFailureException extends Exception {

    public LockingFailureException(String s) {
        super(s);
    }
}
