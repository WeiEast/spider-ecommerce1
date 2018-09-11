/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.crawler.plugin.alipay.ts;

/**
 * @author Jerry
 * @since 00:03 27/11/2017
 */
public class TransactionPage {

    private final String  url;
    private final String  actualUrl;
    private final String  type;
    private final String  beginDate;
    private final String  beginTime;
    private final String  endDate;
    private final String  endTime;
    private final int     pageNum;
    private final String  content;
    private       boolean success;
    //是否是最后一页
    private       boolean end;

    public TransactionPage(String url, String actualUrl, String type, String beginDate, String endDate, int pageNum, String content, boolean success, boolean end) {
        this.url = url;
        this.actualUrl = actualUrl;
        this.type = type;
        this.beginDate = beginDate;
        this.success = success;
        this.beginTime = "00:00";
        this.endDate = endDate;
        this.endTime = "24:00";
        this.pageNum = pageNum;
        this.content = content;
        this.end = end;
    }

    public String getUrl() {
        return url;
    }

    public String getActualUrl() {
        return actualUrl;
    }

    public String getType() {
        return type;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getPageNum() {
        return pageNum;
    }

    public String getContent() {
        return content;
    }

    public String getExpectedUrl() {
        return this.url + "\"dateType=createDate&dateRange=customDate&tradeType=" + type + "&beginDate=" + beginDate + "&endDate=" + endDate + "&beginTime=" + beginTime + "&endTime=" + endTime + "&status=all&fundFlow=all&pageNum=" + pageNum;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }
}
