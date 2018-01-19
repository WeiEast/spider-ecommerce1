/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2016
 */

package com.datatrees.crawler.plugin.qrcode;

import java.util.Map;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2016年8月31日 上午11:54:33
 */
public interface QRCodeVerification {

    public QRCodeResult confirmQRCodeStatus(Map<String, Object> params);

    public QRCodeResult verifyQRCodeStatus(Map<String, Object> params);

    public String refreshQRCode(Map<String, Object> params);

    public enum QRCodeStatus {
        WAITING,
        SCANNED,
        CONFIRMED,
        EXPIRE,
        FAILED,
        SUCCESS;
    }

    public class QRCodeResult {

        public QRCodeStatus        status;
        public String              remark;
        public Map<String, Object> result;

        /**
         * @param status
         * @param remark
         * @param result
         */
        public QRCodeResult(QRCodeStatus status, String remark, Map<String, Object> result) {
            super();
            this.status = status;
            this.remark = remark;
            this.result = result;
        }

        /**
         * @param status
         * @param remark
         */
        public QRCodeResult(QRCodeStatus status, String remark) {
            super();
            this.status = status;
            this.remark = remark;
        }

        /**
         *
         */
        public QRCodeResult(QRCodeStatus status) {
            this.status = status;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("QRCodeResult [status=").append(status).append(", remark=").append(remark).append(", result=").append(result).append("]");
            return builder.toString();
        }

    }

}
