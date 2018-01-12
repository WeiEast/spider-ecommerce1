package com.datatrees.rawdatacentral.web.controller;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.api.economic.taobao.EconomicApiForTaoBaoQR;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/economic/taobao")
public class TaoBaoQRControler {

    private static final Logger logger = LoggerFactory.getLogger(TaoBaoQRControler.class);
    @Resource
    private EconomicApiForTaoBaoQR economicApiForTaoBaoQR;

    @RequestMapping("/refeshQRCode")
    public Object refeshQRCode(CommonPluginParam param) {
        return economicApiForTaoBaoQR.refeshQRCode(param);
    }

    @RequestMapping("/queryQRStatus")
    public Object queryQRStatus(CommonPluginParam param) {
        return economicApiForTaoBaoQR.queryQRStatus(param);
    }
}
