package com.datatrees.rawdatacentral.core.common;

import com.datatrees.common.conf.PropertiesConfiguration;

public interface SubmitConstant {

    String REDIS_KEY_SEPARATOR                    = "_";
    String MAIL_BILL_REDIS_KEY                    = "bankBills";
    String OPERATOR_PERSONALINFORMATION_REDIS_KEY = "personalInformation";
    String OPERATOR_BILLDETAIL_REDIS_KEY          = "billDetails";
    String OPERATOR_SHORTMESSAGEDETAIL_REDIS_KEY  = "shortMessageDetails";
    String OPERATOR_CALLDETAIL_REDIS_KEY          = "callDetails";
    String ECOMMERCE_BASEINFO_REDIS_KEY           = "baseInfo";
    String ECOMMERCE_RECORDS_REDIS_KEY            = "records";
    String ECOMMERCE_ADDRESSES_REDIS_KEY          = "addresses";
    String ECOMMERCE_BANKCARDS_REDIS_KEY          = "bankCards";
    String ECOMMERCE_FEESACCOUNTS_REDIS_KEY       = "feesAccounts";
    int    SUBMITTER_UPLOAD_CORE_THREAD_NUM       = PropertiesConfiguration.getInstance().getInt("submitter.upload.corePoolSize", 5);
    int    SUBMITTER_UPLOAD_MAX_THREAD_NUM        = PropertiesConfiguration.getInstance().getInt("submitter.upload.maximumPoolSize", 60);
    int    SUBMITTER_UPLOAD_MAX_TASK_NUM          = PropertiesConfiguration.getInstance().getInt("submitter.upload.maximumTaskNum", 10);
    String ALIYUN_OSS_ENDPOINT                    = PropertiesConfiguration.getInstance().get("submitter.aliyun.ossEndpoint");
    String ALIYUN_OSS_ACCESSID                    = PropertiesConfiguration.getInstance().get("submitter.aliyun.ossAccessId");
    String ALIYUN_OSS_ACCESSSECRET                = PropertiesConfiguration.getInstance().get("submitter.aliyun.ossAccessSecret");
    String ALIYUN_OSS_DEFAULTBUCKET               = PropertiesConfiguration.getInstance().get("submitter.aliyun.ossDefaultBucket");
    String ALIYUN_OSS_OBJECT_PATH_ROOT            = PropertiesConfiguration.getInstance().get("oss.object.path.root");
    String SUBMITTER_NEEDUPLOAD_KEY               = PropertiesConfiguration.getInstance().get("submitter.needUpload.keys", "mailHeader,pageContent,attachment");

}
