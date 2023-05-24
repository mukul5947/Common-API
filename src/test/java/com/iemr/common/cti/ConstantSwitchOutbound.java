package com.iemr.common.cti;

public interface ConstantSwitchOutbound extends Constants
{
	static final String urlSuccessRequest1 = "http://" + CTI_SERVER_IP
			+ "/apps/appsHandler.php?transaction_id=CTI_AGENT_MODE&agent_id=2003&ip=" + Constants.REQUESTOR_IP
			+ "&mode=MANUAL&resFormat=3";
	static final String urlSuccessResponse1 = "{ \"response\": { \"transaction_id\": \"SWITCH_MODE\", "
			+ "\"agentid\": \"2003\", \"requestparam\": \"SWITCH TO MANUAL\", \"status\": \"SUCCESS\", "
			+ "\"response_code\": \"1\", \"reason\": \"\" } }";
	static final String successRequest1 = "{agent_id:2003}";
	static final String successResponse1 = "{\"data\":{\"agent_id\":\"2003\",\"agentid\":\"2003\","
			+ "\"status\":\"SUCCESS\",\"response_code\":\"1\",\"reason\":\"\",\"response\":{"
			+ "\"transaction_id\":\"SWITCH_MODE\",\"agentid\":\"2003\",\"requestparam\":\"SWITCH TO MANUAL\","
			+ "\"status\":\"SUCCESS\",\"response_code\":\"1\",\"reason\":\"\"}},\"statusCode\":200,"
			+ "\"errorMessage\":\"Success\",\"status\":\"Success\"}";
	static final String controllerSuccessResponse1 = "{\"data\":{\"agent_id\":\"2003\",\"agentid\":\"2003\","
			+ "\"status\":\"SUCCESS\",\"response_code\":\"1\",\"reason\":\"\",\"response\":{"
			+ "\"transaction_id\":\"SWITCH_MODE\",\"agentid\":\"2003\",\"requestparam\":\"SWITCH TO MANUAL\","
			+ "\"status\":\"SUCCESS\",\"response_code\":\"1\",\"reason\":\"\"}},\"statusCode\":200,"
			+ "\"errorMessage\":\"Success\",\"status\":\"Success\"}";

	static final String urlSuccessRequest2 = "http://" + CTI_SERVER_IP
			+ "/apps/appsHandler.php?transaction_id=CTI_AGENT_MODE&agent_id=2003&ip=&mode=MANUAL&resFormat=3";
	static final String urlSuccessResponse2 = "{ \"response\": { \"transaction_id\": \"SWITCH_MODE\", "
			+ "\"agentid\": \"2003\", \"requestparam\": \"SWITCH TO MANUAL\", \"status\": \"SUCCESS\", "
			+ "\"response_code\": \"1\", \"reason\": \"\" } }";
	static final String successRequest2 = "{agent_id:2003}";
	static final String successResponse2 =  "{\"data\":{\"agent_id\":\"2003\",\"agentid\":\"2003\","
			+ "\"status\":\"SUCCESS\",\"response_code\":\"1\",\"reason\":\"\",\"response\":{"
			+ "\"transaction_id\":\"SWITCH_MODE\",\"agentid\":\"2003\",\"requestparam\":\"SWITCH TO MANUAL\","
			+ "\"status\":\"SUCCESS\",\"response_code\":\"1\",\"reason\":\"\"}},\"statusCode\":200,"
			+ "\"errorMessage\":\"Success\",\"status\":\"Success\"}";
	static final String controllerSuccessResponse2 =  "{\"data\":{\"agent_id\":\"2003\",\"agentid\":\"2003\","
			+ "\"status\":\"SUCCESS\",\"response_code\":\"1\",\"reason\":\"\",\"response\":{"
			+ "\"transaction_id\":\"SWITCH_MODE\",\"agentid\":\"2003\",\"requestparam\":\"SWITCH TO MANUAL\","
			+ "\"status\":\"SUCCESS\",\"response_code\":\"1\",\"reason\":\"\"}},\"statusCode\":200,"
			+ "\"errorMessage\":\"Success\",\"status\":\"Success\"}";

	static final String urlFailureRequest1 = "http://" + CTI_SERVER_IP
			+ "/apps/appsHandler.php?transaction_id=CTI_AGENT_MODE&agent_id=2004&ip=" + Constants.REQUESTOR_IP
			+ "&mode=MANUAL&resFormat=3";
	static final String urlFailureResponse1 = "{ \"response\": { \"transaction_id\": \"SWITCH_MODE\", "
			+ "\"agentid\": \"2004\", \"requestparam\": \"\", \"status\": \"FAIL\", \"response_code\": \"0\", "
			+ "\"reason\": \"Agent 2004 is already in MANUAL mode\" } }";
	static final String failureRequest1 = "{agent_id:2004}";
	static final String failureResponse1 = "{\"statusCode\":5000,"
			+ "\"errorMessage\":\"Agent 2004 is already in MANUAL mode\",\"status\":\"FAIL\"}";
	static final String controllerFailureResponse1 = "{\"statusCode\":5000,"
			+ "\"errorMessage\":\"Agent 2004 is already in MANUAL mode\",\"status\":\"FAIL\"}";

	static final String urlFailureRequest2 = "http://" + CTI_SERVER_IP
			+ "/apps/appsHandler.php?transaction_id=CTI_AGENT_MODE&agent_id=2004&ip=&mode=MANUAL&resFormat=3";
	static final String urlFailureResponse2 = "{ \"response\": { \"transaction_id\": \"SWITCH_MODE\", "
			+ "\"agentid\": \"2004\", \"requestparam\": \"\", \"status\": \"FAIL\", \"response_code\": \"0\", "
			+ "\"reason\": \"Agent 2004 is already in MANUAL mode\" } }";
	static final String failureRequest2 = "{agent_id:2004}";
	static final String failureResponse2 = "{\"statusCode\":5000,"
			+ "\"errorMessage\":\"Agent 2004 is already in MANUAL mode\",\"status\":\"FAIL\"}";
	static final String controllerFailureResponse2 = "{\"statusCode\":5000,"
			+ "\"errorMessage\":\"Agent 2004 is already in MANUAL mode\",\"status\":\"FAIL\"}";

	static final String urlFailureRequest3 = "http://" + CTI_SERVER_IP
			+ "/apps/appsHandler.php?transaction_id=CTI_AGENT_MODE&agent_id=&ip=" + Constants.REQUESTOR_IP
			+ "&mode=MANUAL&resFormat=3";
	static final String urlFailureResponse3 = "{ \"response\": { \"transaction_id\": \"SWITCH_MODE\", "
			+ "\"agentid\": \"\", \"requestparam\": \"\", \"status\": \"FAIL\", \"response_code\": \"0\", "
			+ "\"reason\": \"Agent is not allowed to switch to MANUAL mode\" } }";
	static final String failureRequest3 = "{}";
	static final String failureResponse3 = "{\"statusCode\":5000,"
			+ "\"errorMessage\":\"Agent is not allowed to switch to MANUAL mode\",\"status\":\"FAIL\"}";
	static final String controllerFailureResponse3 = "{\"statusCode\":5000,"
			+ "\"errorMessage\":\"Agent is not allowed to switch to MANUAL mode\",\"status\":\"FAIL\"}";

	static final String urlFailureRequest4 = "http://" + CTI_SERVER_IP
			+ "/apps/appsHandler.php?transaction_id=CTI_AGENT_MODE&agent_id=&ip=&mode=MANUAL&resFormat=3";
	static final String urlFailureResponse4 = "{ \"response\": { \"transaction_id\": \"SWITCH_MODE\", "
			+ "\"agentid\": \"\", \"requestparam\": \"\", \"status\": \"FAIL\", \"response_code\": \"0\", "
			+ "\"reason\": \"Agent is not allowed to switch to MANUAL mode\" } }";
	static final String failureRequest4 = "{}";
	static final String failureResponse4 = "{\"statusCode\":5000,"
			+ "\"errorMessage\":\"Agent is not allowed to switch to MANUAL mode\",\"status\":\"FAIL\"}";
	static final String controllerFailureResponse4 = "{\"statusCode\":5000,"
			+ "\"errorMessage\":\"Agent is not allowed to switch to MANUAL mode\",\"status\":\"FAIL\"}";

}
