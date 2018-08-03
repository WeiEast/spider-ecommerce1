package com.datatrees.rawdatacentral.submitter.filestore;

import com.datatrees.spider.share.service.domain.SubmitMessage;

public interface FileStoreService {

    public void storeEviFile(SubmitMessage submitMessage);
}
