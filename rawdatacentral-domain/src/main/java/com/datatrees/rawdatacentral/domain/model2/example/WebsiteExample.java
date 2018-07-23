package com.datatrees.rawdatacentral.domain.model2.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WebsiteExample implements Serializable {

    private static final long           serialVersionUID = 1L;

    protected            String         orderByClause;

    protected            boolean        distinct;

    protected            List<Criteria> oredCriteria;

    /** 当前页 */
    protected            int            pageNum;

    /** 每页数据条数 */
    protected            int            pageSize;

    public WebsiteExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    public void initPageInfo(Integer pageNum, Integer pageSize) {
        if (null == pageNum || pageNum <= 0) {
            this.pageNum = 1;
        } else {
            this.pageNum = pageNum.intValue();
        }
        if (null == pageSize || pageSize <= 0) {
            this.pageSize = 10;
        } else {
            this.pageSize = pageSize.intValue();
        }
    }

    public int getPageNum() {
        return pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getLimitStart() {
        if (pageNum > 0 && pageSize > 0) {
            return (pageNum - 1) * pageSize;
        }
        return 0;
    }

    protected abstract static class GeneratedCriteria {

        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andWebsiteIdIsNull() {
            addCriterion("Id is null");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdIsNotNull() {
            addCriterion("Id is not null");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdEqualTo(Integer value) {
            addCriterion("Id =", value, "websiteId");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdNotEqualTo(Integer value) {
            addCriterion("Id <>", value, "websiteId");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdGreaterThan(Integer value) {
            addCriterion("Id >", value, "websiteId");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("Id >=", value, "websiteId");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdLessThan(Integer value) {
            addCriterion("Id <", value, "websiteId");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdLessThanOrEqualTo(Integer value) {
            addCriterion("Id <=", value, "websiteId");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdIn(List<Integer> values) {
            addCriterion("Id in", values, "websiteId");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdNotIn(List<Integer> values) {
            addCriterion("Id not in", values, "websiteId");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdBetween(Integer value1, Integer value2) {
            addCriterion("Id between", value1, value2, "websiteId");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdNotBetween(Integer value1, Integer value2) {
            addCriterion("Id not between", value1, value2, "websiteId");
            return (Criteria) this;
        }

        public Criteria andWebsitetypeIsNull() {
            addCriterion("WebsiteType is null");
            return (Criteria) this;
        }

        public Criteria andWebsitetypeIsNotNull() {
            addCriterion("WebsiteType is not null");
            return (Criteria) this;
        }

        public Criteria andWebsitetypeEqualTo(String value) {
            addCriterion("WebsiteType =", value, "websitetype");
            return (Criteria) this;
        }

        public Criteria andWebsitetypeNotEqualTo(String value) {
            addCriterion("WebsiteType <>", value, "websitetype");
            return (Criteria) this;
        }

        public Criteria andWebsitetypeGreaterThan(String value) {
            addCriterion("WebsiteType >", value, "websitetype");
            return (Criteria) this;
        }

        public Criteria andWebsitetypeGreaterThanOrEqualTo(String value) {
            addCriterion("WebsiteType >=", value, "websitetype");
            return (Criteria) this;
        }

        public Criteria andWebsitetypeLessThan(String value) {
            addCriterion("WebsiteType <", value, "websitetype");
            return (Criteria) this;
        }

        public Criteria andWebsitetypeLessThanOrEqualTo(String value) {
            addCriterion("WebsiteType <=", value, "websitetype");
            return (Criteria) this;
        }

        public Criteria andWebsitetypeIn(List<String> values) {
            addCriterion("WebsiteType in", values, "websitetype");
            return (Criteria) this;
        }

        public Criteria andWebsitetypeNotIn(List<String> values) {
            addCriterion("WebsiteType not in", values, "websitetype");
            return (Criteria) this;
        }

        public Criteria andWebsitetypeBetween(String value1, String value2) {
            addCriterion("WebsiteType between", value1, value2, "websitetype");
            return (Criteria) this;
        }

        public Criteria andWebsitetypeNotBetween(String value1, String value2) {
            addCriterion("WebsiteType not between", value1, value2, "websitetype");
            return (Criteria) this;
        }

        public Criteria andWebsitenameIsNull() {
            addCriterion("WebsiteName is null");
            return (Criteria) this;
        }

        public Criteria andWebsitenameIsNotNull() {
            addCriterion("WebsiteName is not null");
            return (Criteria) this;
        }

        public Criteria andWebsitenameEqualTo(String value) {
            addCriterion("WebsiteName =", value, "websitename");
            return (Criteria) this;
        }

        public Criteria andWebsitenameNotEqualTo(String value) {
            addCriterion("WebsiteName <>", value, "websitename");
            return (Criteria) this;
        }

        public Criteria andWebsitenameGreaterThan(String value) {
            addCriterion("WebsiteName >", value, "websitename");
            return (Criteria) this;
        }

        public Criteria andWebsitenameGreaterThanOrEqualTo(String value) {
            addCriterion("WebsiteName >=", value, "websitename");
            return (Criteria) this;
        }

        public Criteria andWebsitenameLessThan(String value) {
            addCriterion("WebsiteName <", value, "websitename");
            return (Criteria) this;
        }

        public Criteria andWebsitenameLessThanOrEqualTo(String value) {
            addCriterion("WebsiteName <=", value, "websitename");
            return (Criteria) this;
        }

        public Criteria andWebsitenameLike(String value) {
            addCriterion("WebsiteName like", value, "websitename");
            return (Criteria) this;
        }

        public Criteria andWebsitenameNotLike(String value) {
            addCriterion("WebsiteName not like", value, "websitename");
            return (Criteria) this;
        }

        public Criteria andWebsitenameIn(List<String> values) {
            addCriterion("WebsiteName in", values, "websitename");
            return (Criteria) this;
        }

        public Criteria andWebsitenameNotIn(List<String> values) {
            addCriterion("WebsiteName not in", values, "websitename");
            return (Criteria) this;
        }

        public Criteria andWebsitenameBetween(String value1, String value2) {
            addCriterion("WebsiteName between", value1, value2, "websitename");
            return (Criteria) this;
        }

        public Criteria andWebsitenameNotBetween(String value1, String value2) {
            addCriterion("WebsiteName not between", value1, value2, "websitename");
            return (Criteria) this;
        }

        public Criteria andWebsitedomainIsNull() {
            addCriterion("WebsiteDomain is null");
            return (Criteria) this;
        }

        public Criteria andWebsitedomainIsNotNull() {
            addCriterion("WebsiteDomain is not null");
            return (Criteria) this;
        }

        public Criteria andWebsitedomainEqualTo(String value) {
            addCriterion("WebsiteDomain =", value, "websitedomain");
            return (Criteria) this;
        }

        public Criteria andWebsitedomainNotEqualTo(String value) {
            addCriterion("WebsiteDomain <>", value, "websitedomain");
            return (Criteria) this;
        }

        public Criteria andWebsitedomainGreaterThan(String value) {
            addCriterion("WebsiteDomain >", value, "websitedomain");
            return (Criteria) this;
        }

        public Criteria andWebsitedomainGreaterThanOrEqualTo(String value) {
            addCriterion("WebsiteDomain >=", value, "websitedomain");
            return (Criteria) this;
        }

        public Criteria andWebsitedomainLessThan(String value) {
            addCriterion("WebsiteDomain <", value, "websitedomain");
            return (Criteria) this;
        }

        public Criteria andWebsitedomainLessThanOrEqualTo(String value) {
            addCriterion("WebsiteDomain <=", value, "websitedomain");
            return (Criteria) this;
        }

        public Criteria andWebsitedomainLike(String value) {
            addCriterion("WebsiteDomain like", value, "websitedomain");
            return (Criteria) this;
        }

        public Criteria andWebsitedomainNotLike(String value) {
            addCriterion("WebsiteDomain not like", value, "websitedomain");
            return (Criteria) this;
        }

        public Criteria andWebsitedomainIn(List<String> values) {
            addCriterion("WebsiteDomain in", values, "websitedomain");
            return (Criteria) this;
        }

        public Criteria andWebsitedomainNotIn(List<String> values) {
            addCriterion("WebsiteDomain not in", values, "websitedomain");
            return (Criteria) this;
        }

        public Criteria andWebsitedomainBetween(String value1, String value2) {
            addCriterion("WebsiteDomain between", value1, value2, "websitedomain");
            return (Criteria) this;
        }

        public Criteria andWebsitedomainNotBetween(String value1, String value2) {
            addCriterion("WebsiteDomain not between", value1, value2, "websitedomain");
            return (Criteria) this;
        }

        public Criteria andIsenabledIsNull() {
            addCriterion("isenabled is null");
            return (Criteria) this;
        }

        public Criteria andIsenabledIsNotNull() {
            addCriterion("isenabled is not null");
            return (Criteria) this;
        }

        public Criteria andIsenabledEqualTo(Boolean value) {
            addCriterion("isenabled =", value, "isenabled");
            return (Criteria) this;
        }

        public Criteria andIsenabledNotEqualTo(Boolean value) {
            addCriterion("isenabled <>", value, "isenabled");
            return (Criteria) this;
        }

        public Criteria andIsenabledGreaterThan(Boolean value) {
            addCriterion("isenabled >", value, "isenabled");
            return (Criteria) this;
        }

        public Criteria andIsenabledGreaterThanOrEqualTo(Boolean value) {
            addCriterion("isenabled >=", value, "isenabled");
            return (Criteria) this;
        }

        public Criteria andIsenabledLessThan(Boolean value) {
            addCriterion("isenabled <", value, "isenabled");
            return (Criteria) this;
        }

        public Criteria andIsenabledLessThanOrEqualTo(Boolean value) {
            addCriterion("isenabled <=", value, "isenabled");
            return (Criteria) this;
        }

        public Criteria andIsenabledIn(List<Boolean> values) {
            addCriterion("isenabled in", values, "isenabled");
            return (Criteria) this;
        }

        public Criteria andIsenabledNotIn(List<Boolean> values) {
            addCriterion("isenabled not in", values, "isenabled");
            return (Criteria) this;
        }

        public Criteria andIsenabledBetween(Boolean value1, Boolean value2) {
            addCriterion("isenabled between", value1, value2, "isenabled");
            return (Criteria) this;
        }

        public Criteria andIsenabledNotBetween(Boolean value1, Boolean value2) {
            addCriterion("isenabled not between", value1, value2, "isenabled");
            return (Criteria) this;
        }

        public Criteria andLogintipIsNull() {
            addCriterion("LoginTip is null");
            return (Criteria) this;
        }

        public Criteria andLogintipIsNotNull() {
            addCriterion("LoginTip is not null");
            return (Criteria) this;
        }

        public Criteria andLogintipEqualTo(String value) {
            addCriterion("LoginTip =", value, "logintip");
            return (Criteria) this;
        }

        public Criteria andLogintipNotEqualTo(String value) {
            addCriterion("LoginTip <>", value, "logintip");
            return (Criteria) this;
        }

        public Criteria andLogintipGreaterThan(String value) {
            addCriterion("LoginTip >", value, "logintip");
            return (Criteria) this;
        }

        public Criteria andLogintipGreaterThanOrEqualTo(String value) {
            addCriterion("LoginTip >=", value, "logintip");
            return (Criteria) this;
        }

        public Criteria andLogintipLessThan(String value) {
            addCriterion("LoginTip <", value, "logintip");
            return (Criteria) this;
        }

        public Criteria andLogintipLessThanOrEqualTo(String value) {
            addCriterion("LoginTip <=", value, "logintip");
            return (Criteria) this;
        }

        public Criteria andLogintipLike(String value) {
            addCriterion("LoginTip like", value, "logintip");
            return (Criteria) this;
        }

        public Criteria andLogintipNotLike(String value) {
            addCriterion("LoginTip not like", value, "logintip");
            return (Criteria) this;
        }

        public Criteria andLogintipIn(List<String> values) {
            addCriterion("LoginTip in", values, "logintip");
            return (Criteria) this;
        }

        public Criteria andLogintipNotIn(List<String> values) {
            addCriterion("LoginTip not in", values, "logintip");
            return (Criteria) this;
        }

        public Criteria andLogintipBetween(String value1, String value2) {
            addCriterion("LoginTip between", value1, value2, "logintip");
            return (Criteria) this;
        }

        public Criteria andLogintipNotBetween(String value1, String value2) {
            addCriterion("LoginTip not between", value1, value2, "logintip");
            return (Criteria) this;
        }

        public Criteria andVerifytipIsNull() {
            addCriterion("VerifyTip is null");
            return (Criteria) this;
        }

        public Criteria andVerifytipIsNotNull() {
            addCriterion("VerifyTip is not null");
            return (Criteria) this;
        }

        public Criteria andVerifytipEqualTo(String value) {
            addCriterion("VerifyTip =", value, "verifytip");
            return (Criteria) this;
        }

        public Criteria andVerifytipNotEqualTo(String value) {
            addCriterion("VerifyTip <>", value, "verifytip");
            return (Criteria) this;
        }

        public Criteria andVerifytipGreaterThan(String value) {
            addCriterion("VerifyTip >", value, "verifytip");
            return (Criteria) this;
        }

        public Criteria andVerifytipGreaterThanOrEqualTo(String value) {
            addCriterion("VerifyTip >=", value, "verifytip");
            return (Criteria) this;
        }

        public Criteria andVerifytipLessThan(String value) {
            addCriterion("VerifyTip <", value, "verifytip");
            return (Criteria) this;
        }

        public Criteria andVerifytipLessThanOrEqualTo(String value) {
            addCriterion("VerifyTip <=", value, "verifytip");
            return (Criteria) this;
        }

        public Criteria andVerifytipLike(String value) {
            addCriterion("VerifyTip like", value, "verifytip");
            return (Criteria) this;
        }

        public Criteria andVerifytipNotLike(String value) {
            addCriterion("VerifyTip not like", value, "verifytip");
            return (Criteria) this;
        }

        public Criteria andVerifytipIn(List<String> values) {
            addCriterion("VerifyTip in", values, "verifytip");
            return (Criteria) this;
        }

        public Criteria andVerifytipNotIn(List<String> values) {
            addCriterion("VerifyTip not in", values, "verifytip");
            return (Criteria) this;
        }

        public Criteria andVerifytipBetween(String value1, String value2) {
            addCriterion("VerifyTip between", value1, value2, "verifytip");
            return (Criteria) this;
        }

        public Criteria andVerifytipNotBetween(String value1, String value2) {
            addCriterion("VerifyTip not between", value1, value2, "verifytip");
            return (Criteria) this;
        }

        public Criteria andInittimeoutIsNull() {
            addCriterion("InitTimeout is null");
            return (Criteria) this;
        }

        public Criteria andInittimeoutIsNotNull() {
            addCriterion("InitTimeout is not null");
            return (Criteria) this;
        }

        public Criteria andInittimeoutEqualTo(Integer value) {
            addCriterion("InitTimeout =", value, "inittimeout");
            return (Criteria) this;
        }

        public Criteria andInittimeoutNotEqualTo(Integer value) {
            addCriterion("InitTimeout <>", value, "inittimeout");
            return (Criteria) this;
        }

        public Criteria andInittimeoutGreaterThan(Integer value) {
            addCriterion("InitTimeout >", value, "inittimeout");
            return (Criteria) this;
        }

        public Criteria andInittimeoutGreaterThanOrEqualTo(Integer value) {
            addCriterion("InitTimeout >=", value, "inittimeout");
            return (Criteria) this;
        }

        public Criteria andInittimeoutLessThan(Integer value) {
            addCriterion("InitTimeout <", value, "inittimeout");
            return (Criteria) this;
        }

        public Criteria andInittimeoutLessThanOrEqualTo(Integer value) {
            addCriterion("InitTimeout <=", value, "inittimeout");
            return (Criteria) this;
        }

        public Criteria andInittimeoutIn(List<Integer> values) {
            addCriterion("InitTimeout in", values, "inittimeout");
            return (Criteria) this;
        }

        public Criteria andInittimeoutNotIn(List<Integer> values) {
            addCriterion("InitTimeout not in", values, "inittimeout");
            return (Criteria) this;
        }

        public Criteria andInittimeoutBetween(Integer value1, Integer value2) {
            addCriterion("InitTimeout between", value1, value2, "inittimeout");
            return (Criteria) this;
        }

        public Criteria andInittimeoutNotBetween(Integer value1, Integer value2) {
            addCriterion("InitTimeout not between", value1, value2, "inittimeout");
            return (Criteria) this;
        }

        public Criteria andCodewaittimeIsNull() {
            addCriterion("CodeWaitTime is null");
            return (Criteria) this;
        }

        public Criteria andCodewaittimeIsNotNull() {
            addCriterion("CodeWaitTime is not null");
            return (Criteria) this;
        }

        public Criteria andCodewaittimeEqualTo(Integer value) {
            addCriterion("CodeWaitTime =", value, "codewaittime");
            return (Criteria) this;
        }

        public Criteria andCodewaittimeNotEqualTo(Integer value) {
            addCriterion("CodeWaitTime <>", value, "codewaittime");
            return (Criteria) this;
        }

        public Criteria andCodewaittimeGreaterThan(Integer value) {
            addCriterion("CodeWaitTime >", value, "codewaittime");
            return (Criteria) this;
        }

        public Criteria andCodewaittimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("CodeWaitTime >=", value, "codewaittime");
            return (Criteria) this;
        }

        public Criteria andCodewaittimeLessThan(Integer value) {
            addCriterion("CodeWaitTime <", value, "codewaittime");
            return (Criteria) this;
        }

        public Criteria andCodewaittimeLessThanOrEqualTo(Integer value) {
            addCriterion("CodeWaitTime <=", value, "codewaittime");
            return (Criteria) this;
        }

        public Criteria andCodewaittimeIn(List<Integer> values) {
            addCriterion("CodeWaitTime in", values, "codewaittime");
            return (Criteria) this;
        }

        public Criteria andCodewaittimeNotIn(List<Integer> values) {
            addCriterion("CodeWaitTime not in", values, "codewaittime");
            return (Criteria) this;
        }

        public Criteria andCodewaittimeBetween(Integer value1, Integer value2) {
            addCriterion("CodeWaitTime between", value1, value2, "codewaittime");
            return (Criteria) this;
        }

        public Criteria andCodewaittimeNotBetween(Integer value1, Integer value2) {
            addCriterion("CodeWaitTime not between", value1, value2, "codewaittime");
            return (Criteria) this;
        }

        public Criteria andLogintimeoutIsNull() {
            addCriterion("LoginTimeout is null");
            return (Criteria) this;
        }

        public Criteria andLogintimeoutIsNotNull() {
            addCriterion("LoginTimeout is not null");
            return (Criteria) this;
        }

        public Criteria andLogintimeoutEqualTo(Integer value) {
            addCriterion("LoginTimeout =", value, "logintimeout");
            return (Criteria) this;
        }

        public Criteria andLogintimeoutNotEqualTo(Integer value) {
            addCriterion("LoginTimeout <>", value, "logintimeout");
            return (Criteria) this;
        }

        public Criteria andLogintimeoutGreaterThan(Integer value) {
            addCriterion("LoginTimeout >", value, "logintimeout");
            return (Criteria) this;
        }

        public Criteria andLogintimeoutGreaterThanOrEqualTo(Integer value) {
            addCriterion("LoginTimeout >=", value, "logintimeout");
            return (Criteria) this;
        }

        public Criteria andLogintimeoutLessThan(Integer value) {
            addCriterion("LoginTimeout <", value, "logintimeout");
            return (Criteria) this;
        }

        public Criteria andLogintimeoutLessThanOrEqualTo(Integer value) {
            addCriterion("LoginTimeout <=", value, "logintimeout");
            return (Criteria) this;
        }

        public Criteria andLogintimeoutIn(List<Integer> values) {
            addCriterion("LoginTimeout in", values, "logintimeout");
            return (Criteria) this;
        }

        public Criteria andLogintimeoutNotIn(List<Integer> values) {
            addCriterion("LoginTimeout not in", values, "logintimeout");
            return (Criteria) this;
        }

        public Criteria andLogintimeoutBetween(Integer value1, Integer value2) {
            addCriterion("LoginTimeout between", value1, value2, "logintimeout");
            return (Criteria) this;
        }

        public Criteria andLogintimeoutNotBetween(Integer value1, Integer value2) {
            addCriterion("LoginTimeout not between", value1, value2, "logintimeout");
            return (Criteria) this;
        }

        public Criteria andResettypeIsNull() {
            addCriterion("ResetType is null");
            return (Criteria) this;
        }

        public Criteria andResettypeIsNotNull() {
            addCriterion("ResetType is not null");
            return (Criteria) this;
        }

        public Criteria andResettypeEqualTo(String value) {
            addCriterion("ResetType =", value, "resettype");
            return (Criteria) this;
        }

        public Criteria andResettypeNotEqualTo(String value) {
            addCriterion("ResetType <>", value, "resettype");
            return (Criteria) this;
        }

        public Criteria andResettypeGreaterThan(String value) {
            addCriterion("ResetType >", value, "resettype");
            return (Criteria) this;
        }

        public Criteria andResettypeGreaterThanOrEqualTo(String value) {
            addCriterion("ResetType >=", value, "resettype");
            return (Criteria) this;
        }

        public Criteria andResettypeLessThan(String value) {
            addCriterion("ResetType <", value, "resettype");
            return (Criteria) this;
        }

        public Criteria andResettypeLessThanOrEqualTo(String value) {
            addCriterion("ResetType <=", value, "resettype");
            return (Criteria) this;
        }

        public Criteria andResettypeLike(String value) {
            addCriterion("ResetType like", value, "resettype");
            return (Criteria) this;
        }

        public Criteria andResettypeNotLike(String value) {
            addCriterion("ResetType not like", value, "resettype");
            return (Criteria) this;
        }

        public Criteria andResettypeIn(List<String> values) {
            addCriterion("ResetType in", values, "resettype");
            return (Criteria) this;
        }

        public Criteria andResettypeNotIn(List<String> values) {
            addCriterion("ResetType not in", values, "resettype");
            return (Criteria) this;
        }

        public Criteria andResettypeBetween(String value1, String value2) {
            addCriterion("ResetType between", value1, value2, "resettype");
            return (Criteria) this;
        }

        public Criteria andResettypeNotBetween(String value1, String value2) {
            addCriterion("ResetType not between", value1, value2, "resettype");
            return (Criteria) this;
        }

        public Criteria andSmstemplateIsNull() {
            addCriterion("SmsTemplate is null");
            return (Criteria) this;
        }

        public Criteria andSmstemplateIsNotNull() {
            addCriterion("SmsTemplate is not null");
            return (Criteria) this;
        }

        public Criteria andSmstemplateEqualTo(String value) {
            addCriterion("SmsTemplate =", value, "smstemplate");
            return (Criteria) this;
        }

        public Criteria andSmstemplateNotEqualTo(String value) {
            addCriterion("SmsTemplate <>", value, "smstemplate");
            return (Criteria) this;
        }

        public Criteria andSmstemplateGreaterThan(String value) {
            addCriterion("SmsTemplate >", value, "smstemplate");
            return (Criteria) this;
        }

        public Criteria andSmstemplateGreaterThanOrEqualTo(String value) {
            addCriterion("SmsTemplate >=", value, "smstemplate");
            return (Criteria) this;
        }

        public Criteria andSmstemplateLessThan(String value) {
            addCriterion("SmsTemplate <", value, "smstemplate");
            return (Criteria) this;
        }

        public Criteria andSmstemplateLessThanOrEqualTo(String value) {
            addCriterion("SmsTemplate <=", value, "smstemplate");
            return (Criteria) this;
        }

        public Criteria andSmstemplateLike(String value) {
            addCriterion("SmsTemplate like", value, "smstemplate");
            return (Criteria) this;
        }

        public Criteria andSmstemplateNotLike(String value) {
            addCriterion("SmsTemplate not like", value, "smstemplate");
            return (Criteria) this;
        }

        public Criteria andSmstemplateIn(List<String> values) {
            addCriterion("SmsTemplate in", values, "smstemplate");
            return (Criteria) this;
        }

        public Criteria andSmstemplateNotIn(List<String> values) {
            addCriterion("SmsTemplate not in", values, "smstemplate");
            return (Criteria) this;
        }

        public Criteria andSmstemplateBetween(String value1, String value2) {
            addCriterion("SmsTemplate between", value1, value2, "smstemplate");
            return (Criteria) this;
        }

        public Criteria andSmstemplateNotBetween(String value1, String value2) {
            addCriterion("SmsTemplate not between", value1, value2, "smstemplate");
            return (Criteria) this;
        }

        public Criteria andSmsreceiverIsNull() {
            addCriterion("SmsReceiver is null");
            return (Criteria) this;
        }

        public Criteria andSmsreceiverIsNotNull() {
            addCriterion("SmsReceiver is not null");
            return (Criteria) this;
        }

        public Criteria andSmsreceiverEqualTo(String value) {
            addCriterion("SmsReceiver =", value, "smsreceiver");
            return (Criteria) this;
        }

        public Criteria andSmsreceiverNotEqualTo(String value) {
            addCriterion("SmsReceiver <>", value, "smsreceiver");
            return (Criteria) this;
        }

        public Criteria andSmsreceiverGreaterThan(String value) {
            addCriterion("SmsReceiver >", value, "smsreceiver");
            return (Criteria) this;
        }

        public Criteria andSmsreceiverGreaterThanOrEqualTo(String value) {
            addCriterion("SmsReceiver >=", value, "smsreceiver");
            return (Criteria) this;
        }

        public Criteria andSmsreceiverLessThan(String value) {
            addCriterion("SmsReceiver <", value, "smsreceiver");
            return (Criteria) this;
        }

        public Criteria andSmsreceiverLessThanOrEqualTo(String value) {
            addCriterion("SmsReceiver <=", value, "smsreceiver");
            return (Criteria) this;
        }

        public Criteria andSmsreceiverLike(String value) {
            addCriterion("SmsReceiver like", value, "smsreceiver");
            return (Criteria) this;
        }

        public Criteria andSmsreceiverNotLike(String value) {
            addCriterion("SmsReceiver not like", value, "smsreceiver");
            return (Criteria) this;
        }

        public Criteria andSmsreceiverIn(List<String> values) {
            addCriterion("SmsReceiver in", values, "smsreceiver");
            return (Criteria) this;
        }

        public Criteria andSmsreceiverNotIn(List<String> values) {
            addCriterion("SmsReceiver not in", values, "smsreceiver");
            return (Criteria) this;
        }

        public Criteria andSmsreceiverBetween(String value1, String value2) {
            addCriterion("SmsReceiver between", value1, value2, "smsreceiver");
            return (Criteria) this;
        }

        public Criteria andSmsreceiverNotBetween(String value1, String value2) {
            addCriterion("SmsReceiver not between", value1, value2, "smsreceiver");
            return (Criteria) this;
        }

        public Criteria andReseturlIsNull() {
            addCriterion("ResetURL is null");
            return (Criteria) this;
        }

        public Criteria andReseturlIsNotNull() {
            addCriterion("ResetURL is not null");
            return (Criteria) this;
        }

        public Criteria andReseturlEqualTo(String value) {
            addCriterion("ResetURL =", value, "reseturl");
            return (Criteria) this;
        }

        public Criteria andReseturlNotEqualTo(String value) {
            addCriterion("ResetURL <>", value, "reseturl");
            return (Criteria) this;
        }

        public Criteria andReseturlGreaterThan(String value) {
            addCriterion("ResetURL >", value, "reseturl");
            return (Criteria) this;
        }

        public Criteria andReseturlGreaterThanOrEqualTo(String value) {
            addCriterion("ResetURL >=", value, "reseturl");
            return (Criteria) this;
        }

        public Criteria andReseturlLessThan(String value) {
            addCriterion("ResetURL <", value, "reseturl");
            return (Criteria) this;
        }

        public Criteria andReseturlLessThanOrEqualTo(String value) {
            addCriterion("ResetURL <=", value, "reseturl");
            return (Criteria) this;
        }

        public Criteria andReseturlLike(String value) {
            addCriterion("ResetURL like", value, "reseturl");
            return (Criteria) this;
        }

        public Criteria andReseturlNotLike(String value) {
            addCriterion("ResetURL not like", value, "reseturl");
            return (Criteria) this;
        }

        public Criteria andReseturlIn(List<String> values) {
            addCriterion("ResetURL in", values, "reseturl");
            return (Criteria) this;
        }

        public Criteria andReseturlNotIn(List<String> values) {
            addCriterion("ResetURL not in", values, "reseturl");
            return (Criteria) this;
        }

        public Criteria andReseturlBetween(String value1, String value2) {
            addCriterion("ResetURL between", value1, value2, "reseturl");
            return (Criteria) this;
        }

        public Criteria andReseturlNotBetween(String value1, String value2) {
            addCriterion("ResetURL not between", value1, value2, "reseturl");
            return (Criteria) this;
        }

        public Criteria andResettipIsNull() {
            addCriterion("ResetTip is null");
            return (Criteria) this;
        }

        public Criteria andResettipIsNotNull() {
            addCriterion("ResetTip is not null");
            return (Criteria) this;
        }

        public Criteria andResettipEqualTo(String value) {
            addCriterion("ResetTip =", value, "resettip");
            return (Criteria) this;
        }

        public Criteria andResettipNotEqualTo(String value) {
            addCriterion("ResetTip <>", value, "resettip");
            return (Criteria) this;
        }

        public Criteria andResettipGreaterThan(String value) {
            addCriterion("ResetTip >", value, "resettip");
            return (Criteria) this;
        }

        public Criteria andResettipGreaterThanOrEqualTo(String value) {
            addCriterion("ResetTip >=", value, "resettip");
            return (Criteria) this;
        }

        public Criteria andResettipLessThan(String value) {
            addCriterion("ResetTip <", value, "resettip");
            return (Criteria) this;
        }

        public Criteria andResettipLessThanOrEqualTo(String value) {
            addCriterion("ResetTip <=", value, "resettip");
            return (Criteria) this;
        }

        public Criteria andResettipLike(String value) {
            addCriterion("ResetTip like", value, "resettip");
            return (Criteria) this;
        }

        public Criteria andResettipNotLike(String value) {
            addCriterion("ResetTip not like", value, "resettip");
            return (Criteria) this;
        }

        public Criteria andResettipIn(List<String> values) {
            addCriterion("ResetTip in", values, "resettip");
            return (Criteria) this;
        }

        public Criteria andResettipNotIn(List<String> values) {
            addCriterion("ResetTip not in", values, "resettip");
            return (Criteria) this;
        }

        public Criteria andResettipBetween(String value1, String value2) {
            addCriterion("ResetTip between", value1, value2, "resettip");
            return (Criteria) this;
        }

        public Criteria andResettipNotBetween(String value1, String value2) {
            addCriterion("ResetTip not between", value1, value2, "resettip");
            return (Criteria) this;
        }

        public Criteria andCreatedatIsNull() {
            addCriterion("CreatedAt is null");
            return (Criteria) this;
        }

        public Criteria andCreatedatIsNotNull() {
            addCriterion("CreatedAt is not null");
            return (Criteria) this;
        }

        public Criteria andCreatedatEqualTo(Date value) {
            addCriterion("CreatedAt =", value, "createdat");
            return (Criteria) this;
        }

        public Criteria andCreatedatNotEqualTo(Date value) {
            addCriterion("CreatedAt <>", value, "createdat");
            return (Criteria) this;
        }

        public Criteria andCreatedatGreaterThan(Date value) {
            addCriterion("CreatedAt >", value, "createdat");
            return (Criteria) this;
        }

        public Criteria andCreatedatGreaterThanOrEqualTo(Date value) {
            addCriterion("CreatedAt >=", value, "createdat");
            return (Criteria) this;
        }

        public Criteria andCreatedatLessThan(Date value) {
            addCriterion("CreatedAt <", value, "createdat");
            return (Criteria) this;
        }

        public Criteria andCreatedatLessThanOrEqualTo(Date value) {
            addCriterion("CreatedAt <=", value, "createdat");
            return (Criteria) this;
        }

        public Criteria andCreatedatIn(List<Date> values) {
            addCriterion("CreatedAt in", values, "createdat");
            return (Criteria) this;
        }

        public Criteria andCreatedatNotIn(List<Date> values) {
            addCriterion("CreatedAt not in", values, "createdat");
            return (Criteria) this;
        }

        public Criteria andCreatedatBetween(Date value1, Date value2) {
            addCriterion("CreatedAt between", value1, value2, "createdat");
            return (Criteria) this;
        }

        public Criteria andCreatedatNotBetween(Date value1, Date value2) {
            addCriterion("CreatedAt not between", value1, value2, "createdat");
            return (Criteria) this;
        }

        public Criteria andUpdatedatIsNull() {
            addCriterion("UpdatedAt is null");
            return (Criteria) this;
        }

        public Criteria andUpdatedatIsNotNull() {
            addCriterion("UpdatedAt is not null");
            return (Criteria) this;
        }

        public Criteria andUpdatedatEqualTo(Date value) {
            addCriterion("UpdatedAt =", value, "updatedat");
            return (Criteria) this;
        }

        public Criteria andUpdatedatNotEqualTo(Date value) {
            addCriterion("UpdatedAt <>", value, "updatedat");
            return (Criteria) this;
        }

        public Criteria andUpdatedatGreaterThan(Date value) {
            addCriterion("UpdatedAt >", value, "updatedat");
            return (Criteria) this;
        }

        public Criteria andUpdatedatGreaterThanOrEqualTo(Date value) {
            addCriterion("UpdatedAt >=", value, "updatedat");
            return (Criteria) this;
        }

        public Criteria andUpdatedatLessThan(Date value) {
            addCriterion("UpdatedAt <", value, "updatedat");
            return (Criteria) this;
        }

        public Criteria andUpdatedatLessThanOrEqualTo(Date value) {
            addCriterion("UpdatedAt <=", value, "updatedat");
            return (Criteria) this;
        }

        public Criteria andUpdatedatIn(List<Date> values) {
            addCriterion("UpdatedAt in", values, "updatedat");
            return (Criteria) this;
        }

        public Criteria andUpdatedatNotIn(List<Date> values) {
            addCriterion("UpdatedAt not in", values, "updatedat");
            return (Criteria) this;
        }

        public Criteria andUpdatedatBetween(Date value1, Date value2) {
            addCriterion("UpdatedAt between", value1, value2, "updatedat");
            return (Criteria) this;
        }

        public Criteria andUpdatedatNotBetween(Date value1, Date value2) {
            addCriterion("UpdatedAt not between", value1, value2, "updatedat");
            return (Criteria) this;
        }

        public Criteria andSimulateIsNull() {
            addCriterion("Simulate is null");
            return (Criteria) this;
        }

        public Criteria andSimulateIsNotNull() {
            addCriterion("Simulate is not null");
            return (Criteria) this;
        }

        public Criteria andSimulateEqualTo(Boolean value) {
            addCriterion("Simulate =", value, "simulate");
            return (Criteria) this;
        }

        public Criteria andSimulateNotEqualTo(Boolean value) {
            addCriterion("Simulate <>", value, "simulate");
            return (Criteria) this;
        }

        public Criteria andSimulateGreaterThan(Boolean value) {
            addCriterion("Simulate >", value, "simulate");
            return (Criteria) this;
        }

        public Criteria andSimulateGreaterThanOrEqualTo(Boolean value) {
            addCriterion("Simulate >=", value, "simulate");
            return (Criteria) this;
        }

        public Criteria andSimulateLessThan(Boolean value) {
            addCriterion("Simulate <", value, "simulate");
            return (Criteria) this;
        }

        public Criteria andSimulateLessThanOrEqualTo(Boolean value) {
            addCriterion("Simulate <=", value, "simulate");
            return (Criteria) this;
        }

        public Criteria andSimulateIn(List<Boolean> values) {
            addCriterion("Simulate in", values, "simulate");
            return (Criteria) this;
        }

        public Criteria andSimulateNotIn(List<Boolean> values) {
            addCriterion("Simulate not in", values, "simulate");
            return (Criteria) this;
        }

        public Criteria andSimulateBetween(Boolean value1, Boolean value2) {
            addCriterion("Simulate between", value1, value2, "simulate");
            return (Criteria) this;
        }

        public Criteria andSimulateNotBetween(Boolean value1, Boolean value2) {
            addCriterion("Simulate not between", value1, value2, "simulate");
            return (Criteria) this;
        }

        public Criteria andTemplateidIsNull() {
            addCriterion("TemplateId is null");
            return (Criteria) this;
        }

        public Criteria andTemplateidIsNotNull() {
            addCriterion("TemplateId is not null");
            return (Criteria) this;
        }

        public Criteria andTemplateidEqualTo(Integer value) {
            addCriterion("TemplateId =", value, "templateid");
            return (Criteria) this;
        }

        public Criteria andTemplateidNotEqualTo(Integer value) {
            addCriterion("TemplateId <>", value, "templateid");
            return (Criteria) this;
        }

        public Criteria andTemplateidGreaterThan(Integer value) {
            addCriterion("TemplateId >", value, "templateid");
            return (Criteria) this;
        }

        public Criteria andTemplateidGreaterThanOrEqualTo(Integer value) {
            addCriterion("TemplateId >=", value, "templateid");
            return (Criteria) this;
        }

        public Criteria andTemplateidLessThan(Integer value) {
            addCriterion("TemplateId <", value, "templateid");
            return (Criteria) this;
        }

        public Criteria andTemplateidLessThanOrEqualTo(Integer value) {
            addCriterion("TemplateId <=", value, "templateid");
            return (Criteria) this;
        }

        public Criteria andTemplateidIn(List<Integer> values) {
            addCriterion("TemplateId in", values, "templateid");
            return (Criteria) this;
        }

        public Criteria andTemplateidNotIn(List<Integer> values) {
            addCriterion("TemplateId not in", values, "templateid");
            return (Criteria) this;
        }

        public Criteria andTemplateidBetween(Integer value1, Integer value2) {
            addCriterion("TemplateId between", value1, value2, "templateid");
            return (Criteria) this;
        }

        public Criteria andTemplateidNotBetween(Integer value1, Integer value2) {
            addCriterion("TemplateId not between", value1, value2, "templateid");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {

        private String  condition;

        private Object  value;

        private Object  secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String  typeHandler;

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }
    }
}