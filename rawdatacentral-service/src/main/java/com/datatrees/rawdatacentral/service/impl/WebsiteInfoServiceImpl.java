package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.List;

import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.dao.WebsiteInfoDAO;
import com.datatrees.rawdatacentral.domain.model.WebsiteInfoCriteria;
import com.datatrees.rawdatacentral.domain.model.WebsiteInfoWithBLOBs;
import com.datatrees.rawdatacentral.service.WebsiteInfoService;
import com.datatrees.spider.share.domain.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by zhangyanjia on 2018/3/20.
 */
@Service
public class WebsiteInfoServiceImpl implements WebsiteInfoService {

    private static final Logger         logger = LoggerFactory.getLogger(WebsiteInfoServiceImpl.class);

    @Resource
    private              WebsiteInfoDAO websiteInfoDAO;

    @Override
    public WebsiteInfoWithBLOBs getByWebsiteNameFromInfo(String websiteName) {
        CheckUtils.checkNotBlank(websiteName, ErrorCode.EMPTY_WEBSITE_NAME);
        WebsiteInfoCriteria example = new WebsiteInfoCriteria();
        String env = TaskUtils.getSassEnv();
        example.createCriteria().andWebsiteNameEqualTo(websiteName).andEnvEqualTo(env);
        List<WebsiteInfoWithBLOBs> list = websiteInfoDAO.selectByExampleWithBLOBs(example);
        return list.isEmpty() ? null : list.get(0);
    }
}
