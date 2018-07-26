package com.datatrees.rawdatacentral.core.dubbo;

import com.datatrees.spider.share.service.RpcOssService;
import com.datatrees.rawdatacentral.core.common.SubmitConstant;
import com.datatrees.rawdatacentral.core.oss.OssServiceProvider;
import com.datatrees.rawdatacentral.core.oss.OssUtils;
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
