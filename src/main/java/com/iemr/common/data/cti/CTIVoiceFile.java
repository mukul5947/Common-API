package com.iemr.common.data.cti;

import com.google.gson.annotations.Expose;
import com.iemr.common.utils.mapper.OutputMapper;

import lombok.Data;

@Data
public class CTIVoiceFile
{

	OutputMapper mapper = new OutputMapper();
	@Expose
	CTIResponse response;

	// @Expose
	// String response;
	@Expose
	String agent_id;
	@Expose
	String agent_ip;
	@Expose
	String session_id;
	@Expose
	String transaction_id;
	@Expose
	String agentid;
	@Expose
	String status;
	@Expose
	String reason;
	@Expose
	String response_code;

	@Expose
	String result;
	@Expose
	String path;
	@Expose
	String filename;
	@Expose
	String disconnect_by;

	@Override
	public String toString()
	{
		return OutputMapper.gson().toJson(this);
	}

	// public String getAgent_id()
	// {
	// return agent_id;
	// }
	//
	// public String getAgent_ip()
	// {
	// return agent_ip;
	// }
	//
	// public String getSession_id()
	// {
	// return session_id;
	// }
	//
	// public CTIResponse getResponse()
	// {
	// return response;
	// }

	public void setResponse(CTIResponse ctiResponse)
	{
		response = ctiResponse;
		transaction_id = ctiResponse.getTransaction_id();
		agentid = ctiResponse.getAgentid();
		status = ctiResponse.getStatus();
		reason = ctiResponse.getReason();
		response_code = ctiResponse.getResponse_code();
		result = ctiResponse.getResult();
		path = ctiResponse.getPath();
		filename = ctiResponse.getFilename();
		disconnect_by = ctiResponse.getDisconnect_by();
	}

	// public OutputMapper getMapper()
	// {
	// return mapper;
	// }
	//
	// public String getTransaction_id()
	// {
	// return transaction_id;
	// }
	//
	// public String getAgentid()
	// {
	// return agentid;
	// }
	//
	// public String getStatus()
	// {
	// return status;
	// }
	//
	// public String getReason()
	// {
	// return reason;
	// }
	//
	// public String getResponse_code()
	// {
	// return response_code;
	// }
	//
	// public String getResult()
	// {
	// return result;
	// }
	//
	// public String getPath()
	// {
	// return path;
	// }
	//
	// public String getFilename()
	// {
	// return filename;
	// }
	//
	// public String getDisconnect_by()
	// {
	// return disconnect_by;
	// }
}
