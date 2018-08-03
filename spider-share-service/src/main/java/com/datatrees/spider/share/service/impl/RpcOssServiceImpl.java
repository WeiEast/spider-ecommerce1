package com.datatrees.spider.share.service.impl;

import com.datatrees.spider.share.service.RpcOssService;
import com.datatrees.spider.share.service.constants.SubmitConstant;
import com.datatrees.spider.share.service.oss.OssServiceProvider;
import com.datatrees.spider.share.service.oss.OssUtils;
import org.springframework.stereotype.Service;

/**
 * Created by zhangyanjia on 2017/12/15.
 */
@Service
public class RpcOssServiceImpl implements RpcOssService {

    @Override
    public void upload(String path, byte[] pageContent) {
        OssServiceProvider.getDefaultService().putObject(SubmitConstant.ALIYUN_OSS_DEFAULTBUCKET, OssUtils.getObjectKey(path), pageContent);
    }
}
