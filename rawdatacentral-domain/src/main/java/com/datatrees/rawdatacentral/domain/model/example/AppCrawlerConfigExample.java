package com.datatrees.rawdatacentral.domain.model.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: yand
 * Date: 2018/3/30
 */
public class AppCrawlerConfigExample implements Serializable {

    private static final long serialVersionUID = 1L;
    protected String         orderByClause;
    protected boolean        distinct;
    protected List<Criteria> oredCriteria;
    /** 当前页 */
    protected int            pageNum;
    /** 每页数据条数 */
    protected int            pageSize;

    public AppCrawlerConfigExample() {
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

        public Criteria andIdIsNull() {
            addCriterion("Id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("Id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("Id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("Id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("Id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("Id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("Id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("Id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("Id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("Id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("Id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("Id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andWebsiteTypeIsNull() {
            addCriterion("WebsiteType is null");
            return (Criteria) this;
        }

        public Criteria andWebsiteTypeIsNotNull() {
            addCriterion("WebsiteType is not null");
            return (Criteria) this;
        }

        public Criteria andWebsiteTypeEqualTo(String value) {
            addCriterion("WebsiteType =", value, "websiteType");
            return (Criteria) this;
        }

        public Criteria andWebsiteTypeNotEqualTo(String value) {
            addCriterion("WebsiteType <>", value, "websiteType");
            return (Criteria) this;
        }

        public Criteria andWebsiteTypeGreaterThan(String value) {
            addCriterion("WebsiteType >", value, "websiteType");
            return (Criteria) this;
        }

        public Criteria andWebsiteTypeGreaterThanOrEqualTo(String value) {
            addCriterion("WebsiteType >=", value, "websiteType");
            return (Criteria) this;
        }

        public Criteria andWebsiteTypeLessThan(String value) {
            addCriterion("WebsiteType <", value, "websiteType");
            return (Criteria) this;
        }

        public Criteria andWebsiteTypeLessThanOrEqualTo(String value) {
            addCriterion("WebsiteType <=", value, "websiteType");
            return (Criteria) this;
        }

        public Criteria andWebsiteTypeLike(String value) {
            addCriterion("WebsiteType like", value, "websiteType");
            return (Criteria) this;
        }

        public Criteria andWebsiteTypeNotLike(String value) {
            addCriterion("WebsiteType not like", value, "websiteType");
            return (Criteria) this;
        }

        public Criteria andWebsiteTypeIn(List<String> values) {
            addCriterion("WebsiteType in", values, "websiteType");
            return (Criteria) this;
        }

        public Criteria andWebsiteTypeNotIn(List<String> values) {
            addCriterion("WebsiteType not in", values, "websiteType");
            return (Criteria) this;
        }

        public Criteria andWebsiteTypeBetween(String value1, String value2) {
            addCriterion("WebsiteType between", value1, value2, "websiteType");
            return (Criteria) this;
        }

        public Criteria andWebsiteTypeNotBetween(String value1, String value2) {
            addCriterion("WebsiteType not between", value1, value2, "websiteType");
            return (Criteria) this;
        }

        public Criteria andAppIdIsNull() {
            addCriterion("AppId is null");
            return (Criteria) this;
        }

        public Criteria andAppIdIsNotNull() {
            addCriterion("AppId is not null");
            return (Criteria) this;
        }

        public Criteria andAppIdEqualTo(String value) {
            addCriterion("AppId =", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotEqualTo(String value) {
            addCriterion("AppId <>", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdGreaterThan(String value) {
            addCriterion("AppId >", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdGreaterThanOrEqualTo(String value) {
            addCriterion("AppId >=", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdLessThan(String value) {
            addCriterion("AppId <", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdLessThanOrEqualTo(String value) {
            addCriterion("AppId <=", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdLike(String value) {
            addCriterion("AppId like", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotLike(String value) {
            addCriterion("AppId not like", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdIn(List<String> values) {
            addCriterion("AppId in", values, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotIn(List<String> values) {
            addCriterion("AppId not in", values, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdBetween(String value1, String value2) {
            addCriterion("AppId between", value1, value2, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotBetween(String value1, String value2) {
            addCriterion("AppId not between", value1, value2, "appId");
            return (Criteria) this;
        }

        public Criteria andProjectIsNull() {
            addCriterion("Project is null");
            return (Criteria) this;
        }

        public Criteria andProjectIsNotNull() {
            addCriterion("Project is not null");
            return (Criteria) this;
        }

        public Criteria andProjectEqualTo(String value) {
            addCriterion("Project =", value, "project");
            return (Criteria) this;
        }

        public Criteria andProjectNotEqualTo(String value) {
            addCriterion("Project <>", value, "project");
            return (Criteria) this;
        }

        public Criteria andProjectGreaterThan(String value) {
            addCriterion("Project >", value, "project");
            return (Criteria) this;
        }

        public Criteria andProjectGreaterThanOrEqualTo(String value) {
            addCriterion("Project >=", value, "project");
            return (Criteria) this;
        }

        public Criteria andProjectLessThan(String value) {
            addCriterion("Project <", value, "project");
            return (Criteria) this;
        }

        public Criteria andProjectLessThanOrEqualTo(String value) {
            addCriterion("Project <=", value, "project");
            return (Criteria) this;
        }

        public Criteria andProjectLike(String value) {
            addCriterion("Project like", value, "project");
            return (Criteria) this;
        }

        public Criteria andProjectNotLike(String value) {
            addCriterion("Project not like", value, "project");
            return (Criteria) this;
        }

        public Criteria andProjectIn(List<String> values) {
            addCriterion("Project in", values, "project");
            return (Criteria) this;
        }

        public Criteria andProjectNotIn(List<String> values) {
            addCriterion("Project not in", values, "project");
            return (Criteria) this;
        }

        public Criteria andProjectBetween(String value1, String value2) {
            addCriterion("Project between", value1, value2, "project");
            return (Criteria) this;
        }

        public Criteria andProjectNotBetween(String value1, String value2) {
            addCriterion("Project not between", value1, value2, "project");
            return (Criteria) this;
        }

        public Criteria andIsCrawlerIsNull() {
            addCriterion("IsCrawler is null");
            return (Criteria) this;
        }

        public Criteria andIsCrawlerIsNotNull() {
            addCriterion("IsCrawler is not null");
            return (Criteria) this;
        }

        public Criteria andIsCrawlerEqualTo(Boolean value) {
            addCriterion("IsCrawler =", value, "isCrawler");
            return (Criteria) this;
        }

        public Criteria andIsCrawlerNotEqualTo(Boolean value) {
            addCriterion("IsCrawler <>", value, "isCrawler");
            return (Criteria) this;
        }

        public Criteria andIsCrawlerGreaterThan(Boolean value) {
            addCriterion("IsCrawler >", value, "isCrawler");
            return (Criteria) this;
        }

        public Criteria andIsCrawlerGreaterThanOrEqualTo(Boolean value) {
            addCriterion("IsCrawler >=", value, "isCrawler");
            return (Criteria) this;
        }

        public Criteria andIsCrawlerLessThan(Boolean value) {
            addCriterion("IsCrawler <", value, "isCrawler");
            return (Criteria) this;
        }

        public Criteria andIsCrawlerLessThanOrEqualTo(Boolean value) {
            addCriterion("IsCrawler <=", value, "isCrawler");
            return (Criteria) this;
        }

        public Criteria andIsCrawlerIn(List<Boolean> values) {
            addCriterion("IsCrawler in", values, "isCrawler");
            return (Criteria) this;
        }

        public Criteria andIsCrawlerNotIn(List<Boolean> values) {
            addCriterion("IsCrawler not in", values, "isCrawler");
            return (Criteria) this;
        }

        public Criteria andIsCrawlerBetween(Boolean value1, Boolean value2) {
            addCriterion("IsCrawler between", value1, value2, "isCrawler");
            return (Criteria) this;
        }

        public Criteria andIsCrawlerNotBetween(Boolean value1, Boolean value2) {
            addCriterion("IsCrawler not between", value1, value2, "isCrawler");
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
