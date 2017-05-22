package com.datatrees.rawdatacentral.domain.constant;

/**
 * redis 交互指令任务状态
 * Created by zhouxinghai on 2017/5/19.
 */
public class DirectiveRedisCode {

    /**
     * 指令已发出,等待前端处理返回数据
     */
    public static final String WAIT_APP_DATA          = "WAIT_APP_DATA";

    /**
     * 前端处理完成,数据已经保存到Redis,等待后端处理
     */
    public static final String WAIT_SERVER_CHECK_DATA = "WAIT_SERVER_CHECK_DATA";

    /**
     * 后端数据处理失败
     */
    public static final String SERVER_FAIL            = "SERVER_FAIL";

    /**
     * 后端数据处理成功
     */
    public static final String SERVER_SUCCESS         = "SERVER_SUCCESS";

    /**
     * 取消
     */
    public static final String CANCEL                 = "CANCEL";

    /**
     * 跳过指令
     */
    public static final String SKIP                   = "SKIP";

}