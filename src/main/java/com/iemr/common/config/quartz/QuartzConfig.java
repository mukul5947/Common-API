package com.iemr.common.config.quartz;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import com.iemr.common.utils.config.ConfigProperties;

@Configuration
public class QuartzConfig {

	private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

	// @Autowired
	// private DataSource dataSource;

	// @Autowired(required = true)
	// private ConfigProperties configProperties;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private ApplicationContext applicationContext;

	@PostConstruct
	public void init() {
		log.debug("QuartzConfig initialized.");
	}

	@Bean
	public Properties quartzProperties() {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource("/application.properties"));
		Properties properties = null;
		try {
			propertiesFactoryBean.afterPropertiesSet();
			properties = propertiesFactoryBean.getObject();

		} catch (IOException e) {
			log.warn("Cannot load application.properties.");
		}

		return properties;
	}

	@Bean
	public SchedulerFactoryBean quartzScheduler() {
		SchedulerFactoryBean quartzScheduler = new SchedulerFactoryBean();

		// quartzScheduler.setso;
		quartzScheduler.setTransactionManager(transactionManager);
		quartzScheduler.setOverwriteExistingJobs(true);
		quartzScheduler.setSchedulerName("jelies-quartz-scheduler");

		// custom job factory of spring with DI support for @Autowired!
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(applicationContext);
		quartzScheduler.setJobFactory(jobFactory);

		quartzScheduler.setQuartzProperties(quartzProperties());

		Trigger[] triggers = { processMQTriggerForUnblock().getObject(), processMQTriggerForSMS().getObject(),
				processMQTriggerForEmail().getObject(), processMQTriggerForRegistration().getObject(),
				processMQTriggerForEverwellDataSync().getObject(), processMQTriggerForCtiDataSync().getObject(),
				processMQTriggerForAvniRegistration().getObject(), processMQTriggerForNHMDashboardData().getObject() };

		quartzScheduler.setTriggers(triggers);

		return quartzScheduler;
	}

// --------------------------------------------------------------------------------------------------------------
	@Bean
	public JobDetailFactoryBean processMQJobForUnblock() {
		JobDetailFactoryBean jobDetailFactory;
		jobDetailFactory = new JobDetailFactoryBean();
		jobDetailFactory.setJobClass(ScheduleJobServiceForUnblock.class);
		jobDetailFactory.setGroup("spring-quartz");
		return jobDetailFactory;
	}

	@Bean
	public CronTriggerFactoryBean processMQTriggerForUnblock() {
		Boolean startJob = ConfigProperties.getBoolean("start-unblock-scheduler");
		CronTriggerFactoryBean cronTriggerFactoryBean = null;
		String scheduleConfig = "0 0 0 31 12 ? *";
		if (startJob) {
			scheduleConfig = ConfigProperties.getPropertyByName("cron-scheduler-unblock");
		}
		cronTriggerFactoryBean = new CronTriggerFactoryBean();
		cronTriggerFactoryBean.setJobDetail(processMQJobForUnblock().getObject());

		cronTriggerFactoryBean.setCronExpression(scheduleConfig);
		cronTriggerFactoryBean.setGroup("spring-quartz");
		return cronTriggerFactoryBean;
	}

	// --------------------------------------------------------------------------------------------------------------
	@Bean
	public JobDetailFactoryBean processMQJobForSMS() {
		JobDetailFactoryBean jobDetailFactory;
		jobDetailFactory = new JobDetailFactoryBean();
		jobDetailFactory.setJobClass(ScheduleJobServiceForSMS.class);
		jobDetailFactory.setGroup("spring-quartz");
		return jobDetailFactory;
	}

	@Bean
	public CronTriggerFactoryBean processMQTriggerForSMS() {
		Boolean startJob = ConfigProperties.getBoolean("start-sms-scheduler");
		CronTriggerFactoryBean cronTriggerFactoryBean = null;
		String scheduleConfig = "0 0 0 31 12 ? *";
		if (startJob) {
			scheduleConfig = ConfigProperties.getPropertyByName("cron-scheduler-sms");
		}
		cronTriggerFactoryBean = new CronTriggerFactoryBean();
		cronTriggerFactoryBean.setJobDetail(processMQJobForSMS().getObject());

		cronTriggerFactoryBean.setCronExpression(scheduleConfig);
		cronTriggerFactoryBean.setGroup("spring-quartz");
		return cronTriggerFactoryBean;
	}

	// --------------------------------------------------------------------------------------------------------------
	@Bean
	public JobDetailFactoryBean processMQJobForEmail() {
		JobDetailFactoryBean jobDetailFactory;
		jobDetailFactory = new JobDetailFactoryBean();
		jobDetailFactory.setJobClass(ScheduleJobServiceForEmail.class);
		jobDetailFactory.setGroup("spring-quartz");
		return jobDetailFactory;
	}

	@Bean
	public CronTriggerFactoryBean processMQTriggerForEmail() {
		Boolean startJob = ConfigProperties.getBoolean("start-email-scheduler");
		CronTriggerFactoryBean cronTriggerFactoryBean = null;
		String scheduleConfig = "0 0 0 31 12 ? *";
		if (startJob) {
			scheduleConfig = ConfigProperties.getPropertyByName("cron-scheduler-email");
		}
		cronTriggerFactoryBean = new CronTriggerFactoryBean();
		cronTriggerFactoryBean.setJobDetail(processMQJobForEmail().getObject());

		cronTriggerFactoryBean.setCronExpression(scheduleConfig);
		cronTriggerFactoryBean.setGroup("spring-quartz");
		return cronTriggerFactoryBean;
	}

	// --------------------------------------------------------------------------------------------------------------
	@Bean
	public JobDetailFactoryBean processMQJobForRegistration() {
		JobDetailFactoryBean jobDetailFactory;
		jobDetailFactory = new JobDetailFactoryBean();
		jobDetailFactory.setJobClass(ScheduleForEverwellRegistration.class);
		jobDetailFactory.setGroup("spring-quartz");
		return jobDetailFactory;
	}

	@Bean
	public CronTriggerFactoryBean processMQTriggerForRegistration() {
		Boolean startJob = ConfigProperties.getBoolean("start-registration-scheduler");
		CronTriggerFactoryBean cronTriggerFactoryBean = null;
		String scheduleConfig = "0 0 0 31 12 ? *";
		if (startJob) {
			scheduleConfig = ConfigProperties.getPropertyByName("cron-scheduler-registration");
		}
		cronTriggerFactoryBean = new CronTriggerFactoryBean();
		cronTriggerFactoryBean.setJobDetail(processMQJobForRegistration().getObject());

		cronTriggerFactoryBean.setCronExpression(scheduleConfig);
		cronTriggerFactoryBean.setGroup("spring-quartz");
		return cronTriggerFactoryBean;
	}

	// --------------------------------------------------------------------------------------------------------------
	@Bean
	public JobDetailFactoryBean processMQJobForEverwellDataSync() {
		JobDetailFactoryBean jobDetailFactory;
		jobDetailFactory = new JobDetailFactoryBean();
		jobDetailFactory.setJobClass(ScheduleForEverwellDataSync.class);
		jobDetailFactory.setGroup("spring-quartz");
		return jobDetailFactory;
	}

	@Bean
	public CronTriggerFactoryBean processMQTriggerForEverwellDataSync() {
		Boolean startJob = ConfigProperties.getBoolean("start-everwelldatasync-scheduler");
		CronTriggerFactoryBean cronTriggerFactoryBean = null;
		String scheduleConfig = "0 0 0 31 12 ? *";
		if (startJob) {
			scheduleConfig = ConfigProperties.getPropertyByName("cron-scheduler-everwelldatasync");
		}
		cronTriggerFactoryBean = new CronTriggerFactoryBean();
		cronTriggerFactoryBean.setJobDetail(processMQJobForEverwellDataSync().getObject());

		cronTriggerFactoryBean.setCronExpression(scheduleConfig);
		cronTriggerFactoryBean.setGroup("spring-quartz");
		return cronTriggerFactoryBean;
	}

	// --------------------------------------------------------------------------------------------------------------
	@Bean
	public JobDetailFactoryBean processMQJobForCtiDataSync() {
		JobDetailFactoryBean jobDetailFactory;
		jobDetailFactory = new JobDetailFactoryBean();
		jobDetailFactory.setJobClass(ScheduleForCzentrixCall.class);
		jobDetailFactory.setGroup("spring-quartz");
		return jobDetailFactory;
	}

	@Bean
	public CronTriggerFactoryBean processMQTriggerForCtiDataSync() {
		Boolean startJob = ConfigProperties.getBoolean("start-ctidatasync-scheduler");
		CronTriggerFactoryBean cronTriggerFactoryBean = null;
		String scheduleConfig = "0 0 0 31 12 ? *";
		if (startJob) {
			scheduleConfig = ConfigProperties.getPropertyByName("cron-scheduler-ctidatasync");
		}
		cronTriggerFactoryBean = new CronTriggerFactoryBean();
		cronTriggerFactoryBean.setJobDetail(processMQJobForCtiDataSync().getObject());

		cronTriggerFactoryBean.setCronExpression(scheduleConfig);
		cronTriggerFactoryBean.setGroup("spring-quartz");
		return cronTriggerFactoryBean;
	}

	// --------------------------------------------------------------------------------------------------------------
	@Bean
	public JobDetailFactoryBean processMQJobForAvniRegistration() {
		JobDetailFactoryBean jobDetailFactory;
		jobDetailFactory = new JobDetailFactoryBean();
		jobDetailFactory.setJobClass(ScheduleJobServiceForAvniRegistration.class);
		jobDetailFactory.setGroup("spring-quartz");
		return jobDetailFactory;
	}

	@Bean
	public CronTriggerFactoryBean processMQTriggerForAvniRegistration() {
		Boolean startJob = ConfigProperties.getBoolean("start-avni-scheduler");
		CronTriggerFactoryBean cronTriggerFactoryBean = null;
		String scheduleConfig = "0 0 0 31 12 ? *";
		if (startJob) {
			scheduleConfig = ConfigProperties.getPropertyByName("cron-avni-registration");
		}
		cronTriggerFactoryBean = new CronTriggerFactoryBean();
		cronTriggerFactoryBean.setJobDetail(processMQJobForAvniRegistration().getObject());

		cronTriggerFactoryBean.setCronExpression(scheduleConfig);
		cronTriggerFactoryBean.setGroup("spring-quartz");
		return cronTriggerFactoryBean;
	}

	// --------------------------------------------------------------------------------------------------------------
	@Bean
	public JobDetailFactoryBean processMQJobForNHMDashboardData() {
		JobDetailFactoryBean jobDetailFactory;
		jobDetailFactory = new JobDetailFactoryBean();
		jobDetailFactory.setJobClass(ScheduleJobForNHMDashboardData.class);
		jobDetailFactory.setGroup("spring-quartz");
		return jobDetailFactory;
	}

	@Bean
	public CronTriggerFactoryBean processMQTriggerForNHMDashboardData() {
		Boolean startJob = ConfigProperties.getBoolean("start-nhmdashboard-scheduler");
		CronTriggerFactoryBean cronTriggerFactoryBean = null;
		String scheduleConfig = "0 0 0 31 12 ? *";
		if (startJob) {
			scheduleConfig = ConfigProperties.getPropertyByName("cron-scheduler-nhmdashboard");
		}
		cronTriggerFactoryBean = new CronTriggerFactoryBean();
		cronTriggerFactoryBean.setJobDetail(processMQJobForNHMDashboardData().getObject());

		cronTriggerFactoryBean.setCronExpression(scheduleConfig);
		cronTriggerFactoryBean.setGroup("spring-quartz");

		return cronTriggerFactoryBean;

	}

}