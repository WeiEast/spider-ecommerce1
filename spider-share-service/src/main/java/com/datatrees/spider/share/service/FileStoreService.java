package com.datatrees.spider.share.service;

import com.datatrees.spider.share.service.domain.ExtractMessage;

public interface FileStoreService {

    void storeEviFile(String storePath, ExtractMessage extractMessage);
}
