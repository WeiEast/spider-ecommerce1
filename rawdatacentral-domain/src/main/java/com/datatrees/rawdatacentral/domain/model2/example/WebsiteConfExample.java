package com.datatrees.rawdatacentral.domain.model2.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WebsiteConfExample implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    /** 当前页 */
    protected int pageNum;

    /** 每页数据条数 */
    protected int pageSize;

    public WebsiteConfExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
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

        public Criteria andWebsiteConfIdIsNull() {
            addCriterion("Id is null");
            return (Criteria) this;
        }

        public Criteria andWebsiteConfIdIsNotNull() {
            addCriterion("Id is not null");
            return (Criteria) this;
        }

        public Criteria andWebsiteConfIdEqualTo(Integer value) {
            addCriterion("Id =", value, "websiteConfId");
            return (Criteria) this;
        }

        public Criteria andWebsiteConfIdNotEqualTo(Integer value) {
            addCriterion("Id <>", value, "websiteConfId");
            return (Criteria) this;
        }

        public Criteria andWebsiteConfIdGreaterThan(Integer value) {
            addCriterion("Id >", value, "websiteConfId");
            return (Criteria) this;
        }

        public Criteria andWebsiteConfIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("Id >=", value, "websiteConfId");
            return (Criteria) this;
        }

        public Criteria andWebsiteConfIdLessThan(Integer value) {
            addCriterion("Id <", value, "websiteConfId");
            return (Criteria) this;
        }

        public Criteria andWebsiteConfIdLessThanOrEqualTo(Integer value) {
            addCriterion("Id <=", value, "websiteConfId");
            return (Criteria) this;
        }

        public Criteria andWebsiteConfIdIn(List<Integer> values) {
            addCriterion("Id in", values, "websiteConfId");
            return (Criteria) this;
        }

        public Criteria andWebsiteConfIdNotIn(List<Integer> values) {
            addCriterion("Id not in", values, "websiteConfId");
            return (Criteria) this;
        }

        public Criteria andWebsiteConfIdBetween(Integer value1, Integer value2) {
            addCriterion("Id between", value1, value2, "websiteConfId");
            return (Criteria) this;
        }

        public Criteria andWebsiteConfIdNotBetween(Integer value1, Integer value2) {
            addCriterion("Id not between", value1, value2, "websiteConfId");
            return (Criteria) this;
        }

        public Criteria andWebsiteidIsNull() {
            addCriterion("WebsiteId is null");
            return (Criteria) this;
        }

        public Criteria andWebsiteidIsNotNull() {
            addCriterion("WebsiteId is not null");
            return (Criteria) this;
        }

        public Criteria andWebsiteidEqualTo(Integer value) {
            addCriterion("WebsiteId =", value, "websiteid");
            return (Criteria) this;
        }

        public Criteria andWebsiteidNotEqualTo(Integer value) {
            addCriterion("WebsiteId <>", value, "websiteid");
            return (Criteria) this;
        }

        public Criteria andWebsiteidGreaterThan(Integer value) {
            addCriterion("WebsiteId >", value, "websiteid");
            return (Criteria) this;
        }

        public Criteria andWebsiteidGreaterThanOrEqualTo(Integer value) {
            addCriterion("WebsiteId >=", value, "websiteid");
            return (Criteria) this;
        }

        public Criteria andWebsiteidLessThan(Integer value) {
            addCriterion("WebsiteId <", value, "websiteid");
            return (Criteria) this;
        }

        public Criteria andWebsiteidLessThanOrEqualTo(Integer value) {
            addCriterion("WebsiteId <=", value, "websiteid");
            return (Criteria) this;
        }

        public Criteria andWebsiteidIn(List<Integer> values) {
            addCriterion("WebsiteId in", values, "websiteid");
            return (Criteria) this;
        }

        public Criteria andWebsiteidNotIn(List<Integer> values) {
            addCriterion("WebsiteId not in", values, "websiteid");
            return (Criteria) this;
        }

        public Criteria andWebsiteidBetween(Integer value1, Integer value2) {
            addCriterion("WebsiteId between", value1, value2, "websiteid");
            return (Criteria) this;
        }

        public Criteria andWebsiteidNotBetween(Integer value1, Integer value2) {
            addCriterion("WebsiteId not between", value1, value2, "websiteid");
            return (Criteria) this;
        }

        public Criteria andExtractorconfigIsNull() {
            addCriterion("ExtractorConfig is null");
            return (Criteria) this;
        }

        public Criteria andExtractorconfigIsNotNull() {
            addCriterion("ExtractorConfig is not null");
            return (Criteria) this;
        }

        public Criteria andExtractorconfigEqualTo(String value) {
            addCriterion("ExtractorConfig =", value, "extractorconfig");
            return (Criteria) this;
        }

        public Criteria andExtractorconfigNotEqualTo(String value) {
            addCriterion("ExtractorConfig <>", value, "extractorconfig");
            return (Criteria) this;
        }

        public Criteria andExtractorconfigGreaterThan(String value) {
            addCriterion("ExtractorConfig >", value, "extractorconfig");
            return (Criteria) this;
        }

        public Criteria andExtractorconfigGreaterThanOrEqualTo(String value) {
            addCriterion("ExtractorConfig >=", value, "extractorconfig");
            return (Criteria) this;
        }

        public Criteria andExtractorconfigLessThan(String value) {
            addCriterion("ExtractorConfig <", value, "extractorconfig");
            return (Criteria) this;
        }

        public Criteria andExtractorconfigLessThanOrEqualTo(String value) {
            addCriterion("ExtractorConfig <=", value, "extractorconfig");
            return (Criteria) this;
        }

        public Criteria andExtractorconfigLike(String value) {
            addCriterion("ExtractorConfig like", value, "extractorconfig");
            return (Criteria) this;
        }

        public Criteria andExtractorconfigNotLike(String value) {
            addCriterion("ExtractorConfig not like", value, "extractorconfig");
            return (Criteria) this;
        }

        public Criteria andExtractorconfigIn(List<String> values) {
            addCriterion("ExtractorConfig in", values, "extractorconfig");
            return (Criteria) this;
        }

        public Criteria andExtractorconfigNotIn(List<String> values) {
            addCriterion("ExtractorConfig not in", values, "extractorconfig");
            return (Criteria) this;
        }

        public Criteria andExtractorconfigBetween(String value1, String value2) {
            addCriterion("ExtractorConfig between", value1, value2, "extractorconfig");
            return (Criteria) this;
        }

        public Criteria andExtractorconfigNotBetween(String value1, String value2) {
            addCriterion("ExtractorConfig not between", value1, value2, "extractorconfig");
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
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

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
    }
}