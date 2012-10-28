package com.ss.reminder.server.entity;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;

@Cached
public class UserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String email;

	@Unindexed
	private Long registeredAt;

	@Unindexed
	private String secret;

	private Boolean validated;

	@Unindexed
	private String validateKey;

	@Unindexed
	private int tasksCount;

	private int tz = 0;

	@Unindexed
	private long lastNotifyTime = 0L;

	public UserEntity() {

	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getRegisteredAt() {
		return registeredAt;
	}

	public void setRegisteredAt(Long registeredAt) {
		this.registeredAt = registeredAt;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public Boolean getValidated() {
		return validated;
	}

	public void setValidated(Boolean validated) {
		this.validated = validated;
	}

	public String getValidateKey() {
		return validateKey;
	}

	public void setValidateKey(String validateKey) {
		this.validateKey = validateKey;
	}

	public int getTasksCount() {
		return tasksCount;
	}

	public void setTasksCount(int tasksCount) {
		this.tasksCount = tasksCount;
	}

	public int getTz() {
		return tz;
	}

	public void setTz(int tz) {
		this.tz = tz;
	}

	public long getLastNotifyTime() {
		return lastNotifyTime;
	}

	public void setLastNotifyTime(long lastNotifyTime) {
		this.lastNotifyTime = lastNotifyTime;
	}

}
