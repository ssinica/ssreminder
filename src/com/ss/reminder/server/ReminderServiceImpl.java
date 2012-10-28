package com.ss.reminder.server;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Objectify;
import com.ss.reminder.server.entity.TaskEntity;
import com.ss.reminder.server.entity.UserEntity;
import com.ss.reminder.server.exceptions.EmailNotValidatedException;
import com.ss.reminder.server.exceptions.InvalidSecretException;
import com.ss.reminder.server.exceptions.MaxTasksCountException;
import com.ss.reminder.server.util.BCrypt;
import com.ss.reminder.server.util.ServerUtil;

public class ReminderServiceImpl implements ReminderService {

	public ReminderServiceImpl() {
	}

	@Override
	public UserEntity loadUser(String email) {
		Objectify obf = ServerUtil.obf();
		try {
			return obf.get(UserEntity.class, email);
		} catch (NotFoundException e) {
			return null;
		}
	}

	@Override
	public List<TaskEntity> listTasks(String email) {
		Objectify obf = ServerUtil.obf();
		Key<UserEntity> userKey = new Key<UserEntity>(UserEntity.class, email);
		List<TaskEntity> tasks = obf.query(TaskEntity.class).filter("userKey =", userKey).list();

		if (CollectionUtils.isEmpty(tasks)) {
			return Collections.emptyList();
		}

		Collections.sort(tasks, new Comparator<TaskEntity>() {
			@Override
			public int compare(TaskEntity o1, TaskEntity o2) {
				if (o1.getStartAt() == o2.getStartAt()) {
					return 0;
				} else if (o1.getStartAt() < o2.getStartAt()) {
					return -1;
				} else {
					return 1;
				}
			}
		});

		return tasks;
	}

	@Override
	public UserEntity addTask(String email, String secret, String taskDesc, long startsAt, int tz) throws EmailNotValidatedException, InvalidSecretException, MaxTasksCountException {

		// load user
		UserEntity user = loadUser(email);
		UserEntity ret = null;

		if (user == null) {

			// user not found. create it.
			user = createUser(email, secret, tz);
			ret = user;

		} else {

			// check if user can perform action
			checkUserCanAddTask(user, secret);

		}

		// create task
		TaskEntity task = new TaskEntity();
		task.setDescription(taskDesc);
		task.setStartAt(startsAt);
		task.setUserKey(new Key<UserEntity>(UserEntity.class, email));
		
		Objectify obf = ServerUtil.obft();
		try {
			obf.put(task);
			obf.getTxn().commit();
		} finally {
			if (obf.getTxn().isActive()) {
				obf.getTxn().rollback();
			}
		}

		// user already exist // update tz, if required
		if (ret == null && user != null) {
			user.setTz(tz);
			user.setTasksCount(user.getTasksCount() + 1);
			Objectify obft = ServerUtil.obft();
			try {
				obft.put(user);
				obft.getTxn().commit();
			} finally {
				if (obft.getTxn().isActive()) {
					obft.getTxn().rollback();
				}
			}
		}

		// schedule account validation email if required
		if (ret != null && !Boolean.TRUE.equals(ret.getValidated())) {
			scheduleVerificationEmail(user);
		} else {
			// scheduleReportEmail(email); // (it is to costly to send email on each new task)
		}

		return ret;
	}

	@Override
	public void scheduleReportEmail(String email) {
		DateTime dtCurrentUtc = new DateTime(System.currentTimeMillis(), DateTimeZone.UTC);
		long currentTime = ServerUtil.round(dtCurrentUtc).getMillis();

		TaskOptions task = TaskOptions.Builder.withUrl("/tasks/sendemail");
		task = task.param("email", email);
		task = task.param("time", Long.toString(currentTime));
		Queue qu = QueueFactory.getQueue("adhockemail");
		qu.add(task);
	}

	private void scheduleVerificationEmail(UserEntity user) {
		TaskOptions t = TaskOptions.Builder.withUrl("/tasks/sendverifyemail");
		t = t.param("email", user.getEmail());
		t = t.param("code", user.getValidateKey());
		Queue qu = QueueFactory.getQueue("adhockemail");
		qu.add(t);
	}

	private void checkUserCanAddTask(UserEntity user, String secret) throws EmailNotValidatedException, InvalidSecretException, MaxTasksCountException {
		if (!Boolean.TRUE.equals(user.getValidated())) {
			throw new EmailNotValidatedException(user.getEmail());
		}
		if (!user.getSecret().equals(secret)) {
			throw new InvalidSecretException();
		}
		if (user.getTasksCount() > 50) {
			throw new MaxTasksCountException();
		}
	}

	private UserEntity createUser(String email, String secret, int tz) {

		String code = BCrypt.hashpw(Long.toHexString(System.currentTimeMillis()), BCrypt.gensalt());
		code = code.replaceAll("&", "-");

		UserEntity user = new UserEntity();
		user.setEmail(email);
		user.setSecret(secret);
		user.setRegisteredAt(System.currentTimeMillis());
		user.setTasksCount(1);
		user.setValidated(false);
		user.setValidateKey(code);
		user.setTz(tz);

		Objectify obf = ServerUtil.obft();
		try {
			obf.put(user);
			obf.getTxn().commit();
			return user;
		} finally {
			if (obf.getTxn().isActive()) {
				obf.getTxn().rollback();
			}
		}
	}

}
