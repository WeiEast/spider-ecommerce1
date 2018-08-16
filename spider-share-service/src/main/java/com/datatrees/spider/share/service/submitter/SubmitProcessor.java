package com.datatrees.spider.share.service.submitter;

import javax.annotation.Nonnull;

import com.datatrees.spider.share.service.domain.SubmitMessage;

public interface SubmitProcessor {

    boolean process(@Nonnull SubmitMessage message);
}
