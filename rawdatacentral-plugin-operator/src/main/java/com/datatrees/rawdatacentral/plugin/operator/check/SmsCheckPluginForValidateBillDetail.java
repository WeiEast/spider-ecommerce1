package com.datatrees.rawdatacentral.plugin.operator.check;

import com.datatrees.rawdatacentral.domain.constant.FormType;

/**
 * 详单校验-->图片和短信表单
 * 步骤:图片验证码-->短信验证码-->提交校验
 * Created by zhouxinghai on 2017/7/31
 */
public abstract class SmsCheckPluginForValidateBillDetail extends AbstractSmsCheckPlugin {

    @Override
    public String getFormType() {
        return FormType.VALIDATE_BILL_DETAIL;
    }
}
