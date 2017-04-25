package com.datatrees.rawdata.evidence.dto;

import java.io.Serializable;

public class ObjectResult implements Serializable{
	
private static final long serialVersionUID = 3890382241592488877L;
    
    private int id;
    private int userId;
    private int taskId;
    private int websiteId;
    private String uniqueSign;
    private String uniqueMd5;
    private int status;
    private String remark;
    private String storagePath;
    private String resultType;
    private String url;
    private String pageExtractId;
    private int operatorId;
    private long duration;
    private String websiteType;
    private String websiteName;
    private String createdAt;
    private String sender;
    private String subject;
    private String receivedAt;
    private int flagId;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public int getWebsiteId() {
		return websiteId;
	}
	public void setWebsiteId(int websiteId) {
		this.websiteId = websiteId;
	}
	public String getUniqueSign() {
		return uniqueSign;
	}
	public void setUniqueSign(String uniqueSign) {
		this.uniqueSign = uniqueSign;
	}
	public String getUniqueMd5() {
		return uniqueMd5;
	}
	public void setUniqueMd5(String uniqueMd5) {
		this.uniqueMd5 = uniqueMd5;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getStoragePath() {
		return storagePath;
	}
	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPageExtractId() {
		return pageExtractId;
	}
	public void setPageExtractId(String pageExtractId) {
		this.pageExtractId = pageExtractId;
	}
	public int getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public String getWebsiteType() {
		return websiteType;
	}
	public void setWebsiteType(String websiteType) {
		this.websiteType = websiteType;
	}
	public String getWebsiteName() {
		return websiteName;
	}
	public void setWebsiteName(String websiteName) {
		this.websiteName = websiteName;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getReceivedAt() {
		return receivedAt;
	}
	public void setReceivedAt(String receivedAt) {
		this.receivedAt = receivedAt;
	}
	public int getFlagId() {
		return flagId;
	}
	public void setFlagId(int flagId) {
		this.flagId = flagId;
	}

}
