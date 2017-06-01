package com.datatrees.rawdatacentral.domain.constant;

/**
 * redis 交互指令任务状态
 * Created by zhouxinghai on 2017/5/19.
 */
public class DirectiveRedisCode {

    /**
     * 指令已发出,等待前端处理返回数据
     */
    public static final String WAIT_APP_DATA                = "WAIT_APP_DATA";

    /**
     * 前端处理完成,数据已经保存到Redis,等待后端处理
     */
    public static final String WAIT_SERVER_PROCESS          = "WAIT_SERVER_PROCESS";

    /**
     * 后端数据处理失败
     */
    public static final String SERVER_FAIL                  = "SERVER_FAIL";

    /**
     * 后端数据处理成功
     */
    public static final String SERVER_SUCCESS               = "SERVER_SUCCESS";

    /**
     * 取消
     */
    public static final String CANCEL                       = "CANCEL";

    /**
     * 跳过指令
     */
    public static final String SKIP                         = "SKIP";

    /**
     * 登陆时,发送短信验证码到手机
     */
    public static final String REFRESH_LOGIN_RANDOMPASSWORD = "REFRESH_LOGIN_RANDOMPASSWORD";

    /**
     * 登陆时,刷新图片验证码
     */
    public static final String REFRESH_LOGIN_CODE           = "REFRESH_LOGIN_CODE";

    /**
     * 登陆时,REFRESH_LOGIN_QR_CODE
     */
    public static final String REFRESH_LOGIN_QR_CODE        = "REFRESH_LOGIN_QR_CODE";

    /**
     * 等待
     */
    public static final String WAITTING                     = "WAITTING";

    /**
     * 用户已经扫码二维码
     */
    public static final String SCANNED                      = "SCANNED";

    /**
     * 验证失败
     */
    public static final String FAILED                       = "FAILED";

    /**
     * 验证成功
     */
    public static final String SUCCESS                      = "SUCCESS";

}