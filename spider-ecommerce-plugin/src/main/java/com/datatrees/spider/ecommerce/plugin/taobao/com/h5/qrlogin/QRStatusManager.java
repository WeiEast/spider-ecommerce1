/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.datatrees.spider.ecommerce.plugin.taobao.com.h5.qrlogin;

import com.datatrees.spider.share.common.utils.RedisUtils;
import com.datatrees.spider.share.domain.QRStatus;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Jerry
 * @date 2019-02-15 12:29
 */
public final class QRStatusManager {
    private static final String QR_STATUS_PREFIX = "TAOBAO_QR_LOGIN_STATUS_";

    private QRStatusManager() {}

    /**
     * 设置二维码状态
     *
     * @param taskId 任务ID
     * @param qrCodeStatus 二维码状态 {@link QRStatus}
     */
    public static void setStatus(Long taskId, String qrCodeStatus) {
        RedisUtils.set(QR_STATUS_PREFIX + taskId, qrCodeStatus, 60 * 2);
    }

    /**
     * 获取二维码状态，默认状态：{@link QRStatus#WAITING}
     *
     * @param taskId 任务ID
     */
    public static String getStatus(Long taskId) {
        String status = RedisUtils.get(QR_STATUS_PREFIX + taskId);
        if (StringUtils.isEmpty(status)) {
            status = QRStatus.WAITING;
        }
        return status;
    }

    /**
     * 清理二维码状态
     *
     * @param taskId 任务ID
     */
    public static void clear(Long taskId) {
        RedisUtils.del(QR_STATUS_PREFIX + taskId);
    }

    /**
     * 记录上次的状态
     * 
     * @param taskId 任务ID
     * @param lgToken 淘宝二维码登录必需参数
     * @param qrStatus 二维码状态 {@link QRStatus}
     * @return 状态记录数
     */
    public static long recordLastStatus(Long taskId, String lgToken, String qrStatus) {
        return RedisUtils.incr(QR_STATUS_PREFIX + taskId + "_" + lgToken + "_" + qrStatus);
    }
}
