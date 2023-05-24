package com.iemr.common.config.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iemr.common.service.ctiCall.CzentrixDataSync;


@Service
@Transactional
public class ScheduleForCzentrixCall implements Job {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private CzentrixDataSync czentrixDataSync;

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException
	{
		logger.info("Started job for sync " + arg0.getClass().getName());
		czentrixDataSync.ctiDataSync();
		logger.info("Completed job for sync " + arg0.getClass().getName());
	}
}
