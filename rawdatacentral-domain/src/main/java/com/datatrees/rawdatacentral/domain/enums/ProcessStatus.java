package com.datatrees.rawdatacentral.domain.enums;

import java.io.Serializable;

public class ProcessStatus implements Serializable {

    public static final String PROCESSING = "PROCESSING";
    public static final String REQUIRE_SECOND_PASSWORD = "REQUIRE_SECOND_PASSWORD";
    public static final String SUCCESS    = "SUCCESS";
    public static final String FAIL       = "FAIL";

}
