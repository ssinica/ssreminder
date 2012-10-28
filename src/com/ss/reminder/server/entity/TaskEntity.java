package com.ss.reminder.server.entity;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;

@Cached
public class TaskEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	private Key<UserEntity> userKey;

	private Long startAt;

	@Unindexed
	private String description;

	public TaskEntity() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Key<UserEntity> getUserKey() {
		return userKey;
	}

	public void setUserKey(Key<UserEntity> userKey) {
		this.userKey = userKey;
	}

	public Long getStartAt() {
		return startAt;
	}

	public void setStartAt(Long startAt) {
		this.startAt = startAt;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
