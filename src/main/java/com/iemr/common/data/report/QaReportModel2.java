package com.iemr.common.data.report;

import java.sql.Timestamp;

import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;

public class QaReportModel2 {
	private Long SNo;
	private Timestamp dateOfCall;
	//private String callerPhoneNumber;
	private String callID;
	private String agentID;
	private String agentName;
	private String skillSet;
	private String symptom;
	private String diseaseSummaryProvided;
	//private String closureRemark;
	private String callType;
	//private String callSubType;
	private String callDuration;
	private String Remarks;
	private String recordingFilePath;

	public QaReportModel2 getModel(Long SNo, Timestamp dateOfCall,String callId,String agentID, String firstName,
			String receivedRoleName, String diseaseSummary, String selecteDiagnosis, 
			String callTypeName, Integer callDuration, String remarks,String recordingFilePath) {
		this.SNo=SNo;
		this.dateOfCall=dateOfCall;
		this.callID=callId;
		this.agentID=agentID;
		this.agentName=firstName;
		this.skillSet=receivedRoleName;
		this.symptom=diseaseSummary;
		this.diseaseSummaryProvided=selecteDiagnosis;
		//this.closureRemark=remarks;
		this.callType=callTypeName;
		//this.callSubType=callSubTypeName;
		if(callDuration!=null)
		this.callDuration=Integer.toString(callDuration)+"secs";
		this.Remarks=remarks;
		this.recordingFilePath=recordingFilePath;
		return this;
	}

	public String getRemarks() {
		return Remarks;
	}

	public void setRemarks(String remarks) {
		Remarks = remarks;
	}

	public Long getSNo() {
		return SNo;
	}

	public void setSNo(Long sNo) {
		SNo = sNo;
	}

	public Timestamp getDateOfCall() {
		return dateOfCall;
	}

	public void setDateOfCall(Timestamp dateOfCall) {
		this.dateOfCall = dateOfCall;
	}

	public String getCallID() {
		return callID;
	}

	public void setCallID(String callID) {
		this.callID = callID;
	}

	public String getAgentID() {
		return agentID;
	}

	public void setAgentID(String agentID) {
		this.agentID = agentID;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getSkillSet() {
		return skillSet;
	}

	public void setSkillSet(String skillSet) {
		this.skillSet = skillSet;
	}

	public String getSymptom() {
		return symptom;
	}

	public void setSymptom(String symptom) {
		this.symptom = symptom;
	}

	public String getDiseaseSummaryProvided() {
		return diseaseSummaryProvided;
	}

	public void setDiseaseSummaryProvided(String diseaseSummaryProvided) {
		this.diseaseSummaryProvided = diseaseSummaryProvided;
	}

	public String getCallType() {
		return callType;
	}

	public void setCallType(String callType) {
		this.callType = callType;
	}

	public String getCallDuration() {
		return callDuration;
	}

	public void setCallDuration(String callDuration) {
		this.callDuration = callDuration;
	}

	public String getRecordingFilePath() {
		return recordingFilePath;
	}

	public void setRecordingFilePath(String recordingFilePath) {
		this.recordingFilePath = recordingFilePath;
	}

	@Override
	public String toString() {

		return new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).serializeNulls()
				.setDateFormat("dd-MM-yyyy HH:mm:ss").create().toJson(this);

	}


}
