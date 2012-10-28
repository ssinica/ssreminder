package com.ss.reminder.server;

import java.util.List;

import com.ss.reminder.server.entity.TaskEntity;
import com.ss.reminder.server.entity.UserEntity;
import com.ss.reminder.server.exceptions.EmailNotValidatedException;
import com.ss.reminder.server.exceptions.InvalidSecretException;
import com.ss.reminder.server.exceptions.MaxTasksCountException;

public interface ReminderService {

	UserEntity addTask(String email, String secret, String taskDesc, long startsAt, int clientTz)
			throws EmailNotValidatedException, InvalidSecretException, MaxTasksCountException;

	List<TaskEntity> listTasks(String email);

	UserEntity loadUser(String email);

	void scheduleReportEmail(String email);

}
