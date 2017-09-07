package com.datatrees.rawdatacentral.submitter.filestore;

import com.datatrees.rawdatacentral.core.model.SubmitMessage;

public interface FileStoreService {

    public void storeEviFile(SubmitMessage submitMessage);
}
