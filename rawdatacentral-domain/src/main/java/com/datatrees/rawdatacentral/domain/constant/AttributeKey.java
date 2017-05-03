package com.datatrees.rawdatacentral.domain.constant;

import java.io.Serializable;

/**
 * 扩展属性attributes
 * Created by zhouxinghai on 2017/4/25.
 */
public class AttributeKey implements Serializable {

    public static final String CAPTCHA     = "captcha";    // 验证码

    public static final String STATUS      = "status";     // 状态

    public static final String REMARK      = "remark";     // 备注

    public static final String TASK_ID     = "taskId";     // 任务ID

    public static final String QR          = "qr";         // 二维码图片

    public static final String ACCOUNT_NO  = "account_No";

    public static final String ACCOUNT_KEY = "account_key";

    public static final String END_URL     = "endurl";     // 前段登陆成功跳转的url,里面有sig等可用信息

}
