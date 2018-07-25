package com.datatrees.spider.share.domain.directive;

/**
 * 交互指令
 * Created by zhouxinghai on 2017/4/25.
 */
public enum DirectiveEnum {

    REQUIRE_SMS("require_sms", "需要短信验证码"),
    REQUIRE_PICTURE("require_picture", "需要图片验证码"),
    REQUIRE_QR("require_qr", "需要二维码"),
    REQUIRE_SECOND_PASSWORD("require_second_password", "需要二次密码"),
    GRAB_URL("grab_url", "给APP端分配抓取URL任务"),
    TASK_SUCCESS("task_success", "成功"),
    TASK_FAIL("task_fail", "失败"),;

    /**
     * 指令
     */
    private String code;

    /**
     * 描述
     */
    private String name;

    DirectiveEnum(String directive, String remark) {
        this.code = directive;
        this.name = remark;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "DirectiveEnum{" + "code='" + code + '\'' + ", name='" + name + '\'' + '}';
    }
}
