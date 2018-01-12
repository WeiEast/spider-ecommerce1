package com.datatrees.rawdatacentral.api.internal;

import java.util.concurrent.ThreadPoolExecutor;

public interface ThreadPoolService {

    ThreadPoolExecutor getMailLoginExecutors();

    ThreadPoolExecutor getOperatorInitExecutors();

}
