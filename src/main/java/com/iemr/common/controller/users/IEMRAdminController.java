package com.iemr.common.controller.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iemr.common.config.encryption.SecurePassword;
import com.iemr.common.data.users.LoginSecurityQuestions;
import com.iemr.common.data.users.M_Role;
import com.iemr.common.data.users.ServiceRoleScreenMapping;
import com.iemr.common.data.users.User;
import com.iemr.common.data.users.UserSecurityQMapping;
import com.iemr.common.data.users.UserServiceRoleMapping;
import com.iemr.common.model.user.ChangePasswordModel;
import com.iemr.common.model.user.ForceLogoutRequestModel;
import com.iemr.common.model.user.LoginRequestModel;
import com.iemr.common.model.user.LoginResponseModel;
import com.iemr.common.service.users.IEMRAdminUserService;
import com.iemr.common.utils.exception.IEMRException;
import com.iemr.common.utils.mapper.InputMapper;
import com.iemr.common.utils.mapper.OutputMapper;
import com.iemr.common.utils.redis.RedisSessionException;
import com.iemr.common.utils.response.OutputResponse;
import com.iemr.common.utils.sessionobject.SessionObject;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

// @CrossOrigin()
@RequestMapping("/user")
@RestController
public class IEMRAdminController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private InputMapper inputMapper = new InputMapper();

	private IEMRAdminUserService iemrAdminUserServiceImpl;

	@Autowired
	public void setIemrAdminUserService(IEMRAdminUserService iemrAdminUserService) {
		this.iemrAdminUserServiceImpl = iemrAdminUserService;
	}

	private SessionObject sessionObject;

	@Autowired
	public void setSessionObject(SessionObject sessionObject) {
		this.sessionObject = sessionObject;
	}

	@Autowired
	SecurePassword securePassword;

	@CrossOrigin()
	@RequestMapping(value = "/userAuthenticateNew", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public String userAuthenticateNew(
			@ApiParam(value = "\"{\\\"userName\\\":\\\"String\\\",\\\"password\\\":\\\"String\\\"}\"") @RequestBody String jsonRequest) {
		OutputResponse response = new OutputResponse();
		try {
			User m_user = inputMapper.gson().fromJson(jsonRequest, User.class);
			response.setResponse("hello.....");
		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/userAuthenticate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public String userAuthenticate(
			@ApiParam(value = "\"{\\\"userName\\\":\\\"String\\\",\\\"password\\\":\\\"String\\\"}\"") @RequestBody LoginRequestModel m_User,
			HttpServletRequest request) {
		OutputResponse response = new OutputResponse();
		logger.info("userAuthenticate request - " + m_User + " " + m_User.getUserName() + " " + m_User.getPassword());
		try {
			List<User> mUser = iemrAdminUserServiceImpl.userAuthenticate(m_User.getUserName(), m_User.getPassword());
			JSONObject resMap = new JSONObject();
			JSONObject serviceRoleMultiMap = new JSONObject();
			JSONObject serviceRoleMap = new JSONObject();
			JSONArray serviceRoleList = new JSONArray();
			JSONObject previlegeObj = new JSONObject();
			//condition added to check for concurrent login
			if(m_User.getUserName() !=null && (m_User.getDoLogout() ==null || m_User.getDoLogout() == false))
			{
				String tokenFromRedis=getConcurrentCheckSessionObjectAgainstUser(m_User.getUserName().trim().toLowerCase());
				if (tokenFromRedis !=null)
				{
					//response.setError(5005, "You are already logged in,please confirm to logout from other device and login again");
					throw new IEMRException("You are already logged in,please confirm to logout from other device and login again");
				}
			}
			else if(m_User.getUserName() !=null && m_User.getDoLogout() !=null && m_User.getDoLogout() == true)
			{
				deleteSessionObject(m_User.getUserName().trim().toLowerCase());
			}
			if (mUser.size() == 1) {
				createUserMapping(mUser.get(0), resMap, serviceRoleMultiMap, serviceRoleMap, serviceRoleList,
						previlegeObj);
			} else {
				resMap.put("isAuthenticated", /* Boolean.valueOf(false) */false);
			}
			JSONObject responseObj = new JSONObject(resMap.toString());
			JSONArray previlageObjs = new JSONArray();
			Iterator<?> services = previlegeObj.keys();
			while (services.hasNext()) {
				String service = (String) services.next();
				previlageObjs.put(previlegeObj.getJSONObject(service));
			}
			responseObj.put("previlegeObj", previlageObjs);
			String remoteAddress = request.getHeader("X-FORWARDED-FOR");
			if (remoteAddress == null || remoteAddress.trim().length() == 0) {
				remoteAddress = request.getRemoteAddr();
			}
			responseObj = iemrAdminUserServiceImpl.generateKeyAndValidateIP(responseObj, remoteAddress,
					request.getRemoteHost());
			response.setResponse(responseObj.toString());
		} catch (Exception e) {
			logger.error("userAuthenticate failed with error " + e.getMessage(), e);
			response.setError(e);
		}
		logger.info("userAuthenticate response " + response.toString());
		return response.toString();
	}
	
	@CrossOrigin()
	@RequestMapping(value = "/logOutUserFromConcurrentSession", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public String logOutUserFromConcurrentSession(
			@ApiParam(value = "\"{\\\"userName\\\":\\\"String\\\"}\"") @RequestBody LoginRequestModel m_User,
			HttpServletRequest request) {
		OutputResponse response = new OutputResponse();
		logger.info("logOutUserFromConcurrentSession request - " + m_User);
		try {
			if(m_User!= null && m_User.getUserName() !=null)
			{
				List<User> mUsers = iemrAdminUserServiceImpl.userExitsCheck(m_User.getUserName());

				if (mUsers == null || mUsers.size() <= 0) {
					throw new IEMRException("User not found, please contact administrator");
				} else if (mUsers.size() > 1)
					throw new IEMRException("More than 1 user found, please contact administrator");
				else if (mUsers.size() == 1) {
				String previousTokenFromRedis = sessionObject
							.getSessionObject((mUsers.get(0).getUserName().toString().trim().toLowerCase()));
				if (previousTokenFromRedis != null) {
					deleteSessionObjectByGettingSessionDetails(previousTokenFromRedis);
					sessionObject.deleteSessionObject(previousTokenFromRedis);
					response.setResponse("User successfully logged out");
				}
				else
					throw new IEMRException("Unable to fetch session from redis");
			 }
			}
			else
			{
				throw new IEMRException("Invalid request object");
			}
		
		} catch (Exception e) {
			logger.error("logOutUserFromConcurrentSession failed with error " + e.getMessage(), e);
			response.setError(e);
		}
		logger.info("logOutUserFromConcurrentSession response " + response.toString());
		return response.toString();
	}

	/**
	 * 
	 * @param SH20094090,19-04-2022
	 * function to return session object against userName
	 */
	private String getConcurrentCheckSessionObjectAgainstUser(String userName)
	{
		String response=null;
		try
		{
			response= sessionObject
			.getSessionObject(userName);
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		}
		return response;
	}
	private void createUserMapping(User mUser, JSONObject resMap, JSONObject serviceRoleMultiMap,
			JSONObject serviceRoleMap, JSONArray serviceRoleList, JSONObject previlegeObj) {
		String fName = mUser.getFirstName();
		String lName = mUser.getLastName();
		String mName = mUser.getMiddleName();
//		mName = mName == null ? "" : mName;
		String uName = (fName == null ? "" : fName) + " " + (mName == null ? "" : mName) + " "
				+ (lName == null ? "" : lName);
		resMap.put("userID", mUser.getUserID());
		resMap.put("isAuthenticated", /* Boolean.valueOf(true) */true);
		resMap.put("userName", mUser.getUserName());
		resMap.put("fullName", uName);
		resMap.put("Status", mUser.getM_status().getStatus());
		resMap.put("agentID", mUser.getAgentID());
		resMap.put("agentPassword", mUser.getAgentPassword());
		resMap.put("m_UserLangMappings", new JSONArray(mUser.getM_UserLangMappings().toString()));
		resMap.put("designationID", mUser.getDesignationID());
		if (mUser.getDesignation() != null) {
			resMap.put("designation", new JSONObject(mUser.getDesignation().toString()));
		}
		// User userData;
		// for (Iterator localIterator1 = mUser.iterator(); localIterator1.hasNext();)
		// {
		// userData = (User) localIterator1.next();
		for (UserServiceRoleMapping m_UserServiceRoleMapping : mUser.getM_UserServiceRoleMapping()) {
			serviceRoleMultiMap.put(
					m_UserServiceRoleMapping.getM_ProviderServiceMapping().getM_ServiceMaster().getServiceName(),
					m_UserServiceRoleMapping.getM_Role().getRoleName());
			String serv = m_UserServiceRoleMapping.getM_ProviderServiceMapping().getM_ServiceMaster().getServiceName();
			if (!previlegeObj.has(serv)) {
				previlegeObj.put(serv, new JSONObject(
						m_UserServiceRoleMapping.getM_ProviderServiceMapping().getM_ServiceMaster().toString()));
				previlegeObj.getJSONObject(serv).put("serviceName", serv);
				previlegeObj.getJSONObject(serv).put("serviceID",
						m_UserServiceRoleMapping.getM_ProviderServiceMapping().getProviderServiceMapID());
				previlegeObj.getJSONObject(serv).put("providerServiceMapID",
						m_UserServiceRoleMapping.getM_ProviderServiceMapping().getProviderServiceMapID());
				previlegeObj.getJSONObject(serv).put("apimanClientKey",
						m_UserServiceRoleMapping.getM_ProviderServiceMapping().getAPIMANClientKey());
				previlegeObj.getJSONObject(serv).put("roles", new JSONArray());
				previlegeObj.getJSONObject(serv).put("stateID",
						m_UserServiceRoleMapping.getM_ProviderServiceMapping().getStateID());
				previlegeObj.getJSONObject(serv).put("agentID", m_UserServiceRoleMapping.getAgentID());
				previlegeObj.getJSONObject(serv).put("agentPassword", m_UserServiceRoleMapping.getAgentPassword());
			}
			JSONArray roles = previlegeObj.getJSONObject(serv).getJSONArray("roles");
			roles.put(new JSONObject(m_UserServiceRoleMapping.getM_Role().toString()));
		}
		// }
		Iterator<String> keySet = serviceRoleMultiMap.keys();
		while (keySet.hasNext()) {
			String s = keySet.next();
			serviceRoleMap.put("Service", s);
			serviceRoleMap.put("Role", serviceRoleMultiMap.get(s));
			serviceRoleList.put(serviceRoleMap);
		}
		resMap.put("Previlege", serviceRoleList);
	}

	@CrossOrigin(/*
					 * allowedHeaders = "Authorization", exposedHeaders = "Authorization", methods =
					 * RequestMethod.POST
					 */)
	@RequestMapping(value = "/superUserAuthenticate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public String superUserAuthenticate(
			@ApiParam(value = "\"{\\\"userName\\\":\\\"String\\\",\\\"doLogout\\\":\\\"Boolean\\\"}\"") @RequestBody LoginRequestModel m_User,
			HttpServletRequest request) {
		OutputResponse response = new OutputResponse();
		logger.info("userAuthenticate request ");
		try {
			
			if (!m_User.getUserName().equalsIgnoreCase("SuperAdmin")) {
				throw new IEMRException("Please log with admin credentials");
			}
			User mUser = iemrAdminUserServiceImpl.superUserAuthenticate(m_User.getUserName(), m_User.getPassword());
			JSONObject resMap = new JSONObject();
			JSONObject previlegeObj = new JSONObject();
			//condition added to check for concurrent login
			if(m_User.getUserName() !=null && (m_User.getDoLogout() ==null || m_User.getDoLogout() == false))
			{
				String tokenFromRedis=getConcurrentCheckSessionObjectAgainstUser(m_User.getUserName().trim().toLowerCase());
				if (tokenFromRedis !=null)
				{
					//response.setError(5005, "You are already logged in,please confirm to logout from other device and login again");
					throw new IEMRException("You are already logged in,please confirm to logout from other device and login again");
				}
			}
			else if(m_User.getUserName() !=null && m_User.getDoLogout() !=null && m_User.getDoLogout() == true)
			{
				deleteSessionObject(m_User.getUserName().trim().toLowerCase());
			}
			if (mUser != null) {
//				String fName = mUser.getFirstName();
//				String lName = mUser.getLastName();
//				String mName = mUser.getMiddleName();
//				mName = mName == null ? "" : mName;
//				String uName = (fName == null ? "" : fName) + " " + (mName == null ? "" : mName) + " "
//						+ (lName == null ? "" : lName);
				resMap.put("userID", mUser.getUserID());
				resMap.put("isAuthenticated", /* Boolean.valueOf(true) */true);
				resMap.put("userName", mUser.getUserName());
			} else {
				resMap.put("isAuthenticated", /* Boolean.valueOf(false) */false);
			}
			JSONObject responseObj = new JSONObject(resMap.toString());
			JSONArray previlageObjs = new JSONArray();
			Iterator<?> services = previlegeObj.keys();
			while (services.hasNext()) {
				String service = (String) services.next();
				previlageObjs.put(previlegeObj.getJSONObject(service));
			}
			responseObj.put("previlegeObj", previlageObjs);

			String remoteAddress = request.getHeader("X-FORWARDED-FOR");
			if (remoteAddress == null || remoteAddress.trim().length() == 0) {
				remoteAddress = request.getRemoteAddr();
			}
			responseObj = iemrAdminUserServiceImpl.generateKeyAndValidateIP(responseObj, remoteAddress,
					request.getRemoteHost());
			response.setResponse(responseObj.toString());
		} catch (Exception e) {
			logger.error("userAuthenticate failed with error " + e.getMessage(), e);
			response.setError(e);
		}
		logger.info("userAuthenticate response " + response.toString());
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/userAuthenticateV1", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public String userAuthenticateV1(
			@ApiParam(value = "\"{\\\"userName\\\":\\\"String\\\",\\\"password\\\":\\\"String\\\"}\"") @RequestBody LoginRequestModel loginRequest,
			HttpServletRequest request) {
		OutputResponse response = new OutputResponse();
		logger.info("userAuthenticate request ");
		try {

			String remoteAddress = request.getHeader("X-FORWARDED-FOR");
			if (remoteAddress == null || remoteAddress.trim().length() == 0) {
				remoteAddress = request.getRemoteAddr();
			}
			LoginResponseModel resp = iemrAdminUserServiceImpl.userAuthenticateV1(loginRequest, remoteAddress,
					request.getRemoteHost());
			// logger.info("Login response is before redis " +
			// OutputMapper.gsonWithoutExposeRestriction().toJson(resp));
			JSONObject responseObj = new JSONObject(OutputMapper.gsonWithoutExposeRestriction().toJson(resp));
			responseObj = iemrAdminUserServiceImpl.generateKeyAndValidateIP(responseObj, remoteAddress,
					request.getRemoteHost());
			response.setResponse(responseObj.toString());
		} catch (Exception e) {
			logger.error("userAuthenticate failed with error " + e.getMessage(), e);
			response.setError(e);
		}
		logger.info("userAuthenticate response " + response.toString());
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/getLoginResponse", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, headers = "Authorization")
	public String getLoginResponse(HttpServletRequest request) {
		OutputResponse response = new OutputResponse();
		try {
			response.setResponse(sessionObject.getSessionObject(request.getHeader("Authorization")));
		} catch (RedisSessionException e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin(/*
					 * allowedHeaders = "Authorization", exposedHeaders = "Authorization", methods =
					 * RequestMethod.POST
					 */)
	@RequestMapping(value = "/forgetPassword", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public String forgetPassword(
			@ApiParam(value = "\"{\\\"userName\\\":\\\"String\\\"}\"") @RequestBody ChangePasswordModel m_User) {
		OutputResponse response = new OutputResponse();
		logger.info("forgetPassword request " + m_User);
		try {
			List<User> mUsers = iemrAdminUserServiceImpl.userExitsCheck(m_User.getUserName());

			if (mUsers == null || mUsers.size() <= 0) {
				throw new IEMRException("user not found, please contact administrator");
			} else if (mUsers.size() > 1)
				throw new IEMRException("more than 1 user found, please contact administrator");
			else if (mUsers.size() == 1) {
				List<Map<String, String>> quesAnsList = new ArrayList<>();
				Map<String, String> quesAnsMap;
				Map<Object, Object> resMap = new HashMap<>();
				List<UserSecurityQMapping> mUserSecQuesMapping = iemrAdminUserServiceImpl
						.userSecurityQuestion(mUsers.get(0).getUserID());
				if (mUserSecQuesMapping != null) {
					for (UserSecurityQMapping element : mUserSecQuesMapping) {
						quesAnsMap = new HashMap<>();
						quesAnsMap.put("questionId", element.getQuestionID());
						quesAnsMap.put("question", element.getM_LoginSecurityQuestions().getQuestion());

						quesAnsList.add(quesAnsMap);
					}
					// unnecessary code, remove in next build, return list of questionaries,
					// quesAnsList
					resMap.put("SecurityQuesAns", quesAnsList);
				}
				response.setResponse(OutputMapper.gsonWithoutExposeRestriction().toJson(resMap));
			}
		} catch (Exception e) {
			logger.error("forgetPassword failed with error " + e.getMessage(), e);
			response.setError(e);
		}
		logger.info("forgetPassword response " + response.toString());
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/setForgetPassword", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public String setPassword(
			@ApiParam(value = "\"{\"userName\":\"String\",\"password\":\"String\",\"transactionId\":\"String\"}\"") @RequestBody ChangePasswordModel m_user) {
		OutputResponse response = new OutputResponse();
		logger.info("setForgetPassword request " + m_user);
		try {
			int noOfRowModified = 0;
			List<User> mUsers = iemrAdminUserServiceImpl.userExitsCheck(m_user.getUserName());
			if (mUsers.size() != 1) {
				throw new IEMRException(
						"Set forgot password failed as the user does not exist or is not active or multiple user found.Please contact with administrator");
			}
			User mUser = mUsers.get(0);
			String setStatus;
			noOfRowModified = iemrAdminUserServiceImpl.setForgetPassword(mUser, m_user.getPassword(),
					m_user.getTransactionId(),m_user.getIsAdmin());
			if (noOfRowModified > 0) {
				setStatus = "Password Changed";
			} else {
				setStatus = "Something Wrong..!!!";
			}
			logger.info("Set forgot password changed " + noOfRowModified + " rows of data.");
			response.setResponse(setStatus);
		} catch (Exception e) {
			logger.error("setForgetPassword failed with error " + e.getMessage(), e);
			if (e.getMessage().equals(
					"Set forgot password failed as the user does not exist or is not active or multiple user found.Please contact with administrator"))
				response.setError(e);
			else
				response.setError(5000, e.getMessage());
		}

		logger.info("setForgetPassword response " + response.toString());
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/changePassword", method = RequestMethod.POST, produces = "application/json")
	public String changePassword(
			@ApiParam(value = "\"{\\\"userName\\\":\\\"String\\\",\\\"password\\\":\\\"String\\\",\\\"transactionId\\\":\\\"String\\\"}\"") @RequestBody ChangePasswordModel changePassword) {
		OutputResponse response = new OutputResponse();
		logger.info("changePassword request " + changePassword);
		try {
			int noOfRowUpdated = 0;
			List<User> mUsers = iemrAdminUserServiceImpl.userExitsCheck(changePassword.getUserName());
			String changeReqResult;
			if (mUsers.size() != 1) {
				throw new IEMRException("Change password failed with error as user is not available");
			}
			try {
				if (!securePassword.validatePassword(changePassword.getPassword(), mUsers.get(0).getPassword())) {
					throw new IEMRException("Change password failed with error as old password is incorrect");
				}
			} catch (Exception e) {
				throw new IEMRException("Change password failed with error as old password is incorrect");
			}
			User mUser = mUsers.get(0);
			noOfRowUpdated = iemrAdminUserServiceImpl.setForgetPassword(mUser, changePassword.getNewPassword(),
					changePassword.getTransactionId(),changePassword.getIsAdmin());
			if (noOfRowUpdated > 0) {
				changeReqResult = "Password SuccessFully Change";
			} else {
				changeReqResult = "Something WentWrong.....Please Contact Administrator..!!!";
			}
			response.setResponse(changeReqResult);
		} catch (Exception e) {
			logger.error("changePassword failed with error " + e.getMessage(), e);
			response.setError(e);
		}
		logger.info("changePassword response " + response.toString());
		return response.toString();
	}
	

	@CrossOrigin()
	@RequestMapping(value = "/saveUserSecurityQuesAns", method = RequestMethod.POST, produces = "application/json")
	public String saveUserSecurityQuesAns(
			@ApiParam(value = "\"[{\\\"userID\\\":\\\"Integer\\\",\\\"questionID\\\":\\\"Integer\\\",\\\"answers\\\":\\\"String\\\","
					+ "\\\"mobileNumber\\\":\\\"String\\\",\\\"createdBy\\\":\\\"String\\\"}]\"") @RequestBody Iterable<UserSecurityQMapping> m_UserSecurityQMapping) {
		OutputResponse response = new OutputResponse();
		logger.info("saveUserSecurityQuesAns request " + m_UserSecurityQMapping);
		try {
			String responseData = iemrAdminUserServiceImpl.saveUserSecurityQuesAns(m_UserSecurityQMapping);
			response.setResponse(responseData);
		} catch (Exception e) {
			logger.error("saveUserSecurityQuesAns failed with error " + e.getMessage(), e);
			response.setError(e);
		}
		logger.info("saveUserSecurityQuesAns response " + response.toString());
		return response.toString();
	}

	/**
	 * 
	 * @return security qtns
	 */
	@CrossOrigin(/*
					 * allowedHeaders = "Authorization", exposedHeaders = "Authorization", methods =
					 * RequestMethod.GET
					 */)
	@RequestMapping(value = "/getsecurityquetions", method = RequestMethod.GET)
	public String getSecurityts() {
		OutputResponse response = new OutputResponse();
		logger.info("getsecurityquetions request ");
		try {
			ArrayList<LoginSecurityQuestions> test = iemrAdminUserServiceImpl.getAllLoginSecurityQuestions();
			response.setResponse(test.toString());
		} catch (Exception e) {
			logger.error("getsecurityquetions failed with error " + e.getMessage(), e);
			response.setError(e);
		}
		logger.info("getsecurityquetions response " + response.toString());
		return response.toString();
	}

	@CrossOrigin(/*
					 * allowedHeaders = "Authorization", exposedHeaders = "Authorization", methods =
					 * RequestMethod.POST
					 */)
	@RequestMapping(value = "/getRolesByProviderID", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON, headers = "Authorization")
	public String getRolesByProviderID(
			@ApiParam(value = "{\"providerServiceMapID\":\"Integer - providerServiceMapID\"}") @RequestBody String request) {
		OutputResponse response = new OutputResponse();
		logger.info("getRolesByProviderID request ");
		try {
			response.setResponse(iemrAdminUserServiceImpl.getRolesByProviderID(request));
		} catch (Exception e) {
			logger.error("getRolesByProviderID failed with error " + e.getMessage(), e);
			response.setError(e);
		}
		logger.info("getRolesByProviderID response " + response.toString());
		return response.toString();
	}

	@CrossOrigin(/*
					 * allowedHeaders = "Authorization", exposedHeaders = "Authorization", methods =
					 * RequestMethod.POST
					 */)
	@RequestMapping(value = "/getRoleScreenMappingByProviderID", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON, headers = "Authorization")
	public String getRoleScreenMappingByProviderID(
			@ApiParam(value = "{\"providerServiceMapID\":\"Integer - providerServiceMapID\"}") @RequestBody String request) {

		OutputResponse response = new OutputResponse();
		logger.info("getRoleScreenMappingByProviderID");
		try {

			ServiceRoleScreenMapping serviceRoleScreenMapping = inputMapper.gson().fromJson(request,
					ServiceRoleScreenMapping.class);

			List<ServiceRoleScreenMapping> mapping = iemrAdminUserServiceImpl
					.getUserServiceRoleMappingForProvider(serviceRoleScreenMapping.getProviderServiceMapID());

			response.setResponse(mapping.toString());
		} catch (Exception e) {
			logger.error("getRoleScreenMappingByProviderID failed with error " + e.getMessage(), e);
			response.setError(e);
		}
		logger.info("getRoleScreenMappingByProviderID response " + response.toString());
		return response.toString();
	}

	@CrossOrigin(/*
					 * allowedHeaders = "Authorization", exposedHeaders = "Authorization", methods =
					 * RequestMethod.POST
					 */)
	@RequestMapping(value = "/getUsersByProviderID", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON, headers = "Authorization")
	public String getUsersByProviderID(
			@ApiParam(value = "{\"providerServiceMapID\":\"Integer - providerServiceMapID\", "
					+ "\"RoleID\":\"Optional: Integer - role ID to be filtered\", "
					+ "\"languageName\":\"Optional: String - languageName\"}") @RequestBody String request) {
		OutputResponse response = new OutputResponse();
		logger.info("getRolesByProviderID request ");
		try {
			response.setResponse(iemrAdminUserServiceImpl.getUsersByProviderID(request));
		} catch (Exception e) {
			logger.error("getRolesByProviderID failed with error " + e.getMessage(), e);
			response.setError(e);
		}
		logger.info("getRolesByProviderID response " + response.toString());
		return response.toString();
	}

	@CrossOrigin(/*
					 * allowedHeaders = "Authorization", exposedHeaders = "Authorization", methods =
					 * RequestMethod.POST
					 */)
	@RequestMapping(value = "/getUserServicePointVanDetails", method = RequestMethod.POST, produces = "application/json", headers = "Authorization")
	public String getUserServicePointVanDetails(
			@ApiParam(value = "\"{\\\"userID\\\":\\\"Integer\\\",\"providerServiceMapID\":\"Integer\"}\"") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();
		try {

			JSONObject obj = new JSONObject(comingRequest);
			logger.info("getUserServicePointVanDetails request " + comingRequest);
			String responseData = iemrAdminUserServiceImpl.getUserServicePointVanDetails(obj.getInt("userID"));
			response.setResponse(responseData);
		} catch (Exception e) {
			// e.printStackTrace();
			response.setError(e);
			logger.error("get User SP and van details failed with " + e.getMessage(), e);

		}
		logger.info("getUserServicePointVanDetails response " + response.toString());
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/getServicepointVillages", method = RequestMethod.POST, produces = "application/json", headers = "Authorization")
	public String getServicepointVillages(
			@ApiParam(value = "\"{\\\"servicePointID\\\":\\\"Integer\\\"}\"") @RequestBody String comingRequest) {
		OutputResponse response = new OutputResponse();
		try {

			JSONObject obj = new JSONObject(comingRequest);
			logger.info("getServicepointVillages request " + comingRequest);
			String responseData = iemrAdminUserServiceImpl.getServicepointVillages(obj.getInt("servicePointID"));
			response.setResponse(responseData);
		} catch (Exception e) {
			// e.printStackTrace();
			response.setError(e);
			logger.error("get villages with servicepoint failed with " + e.getMessage(), e);

		}
		logger.info("getServicepointVillages response " + response.toString());
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/getLocationsByProviderID", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON, headers = "Authorization")
	public String getLocationsByProviderID(
			@ApiParam(value = "{\"providerServiceMapID\":\"Integer - providerServiceMapID\", "
					+ "\"roleID\":\"Integer - roleID\"}") @RequestBody String request) {
		OutputResponse response = new OutputResponse();
		logger.info("getLocationsByProviderID request ");
		try {
			response.setResponse(iemrAdminUserServiceImpl.getLocationsByProviderID(request));
		} catch (Exception e) {
			logger.error("getLocationsByProviderID failed with error " + e.getMessage(), e);
			response.setError(e);
		}
		logger.info("getLocationsByProviderID response " + response.toString());
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/userLogout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, headers = "Authorization")
	public String userLogout(HttpServletRequest request) {
		OutputResponse response = new OutputResponse();
		logger.info("userLogout");
		try {
			deleteSessionObjectByGettingSessionDetails(request.getHeader("Authorization"));
			sessionObject.deleteSessionObject(request.getHeader("Authorization"));
			response.setResponse("Success");
		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	private void deleteSessionObjectByGettingSessionDetails(String key)
	{
		String sessionDetails=null;
		try {
			logger.info("inside delete child:"+key);
			sessionDetails=sessionObject.getSessionObject(key);
			logger.info("isessionDetails:"+sessionDetails);
			JsonObject jsnOBJ = new JsonObject();
			JsonParser jsnParser = new JsonParser();
			JsonElement jsnElmnt = jsnParser.parse(sessionDetails);
			jsnOBJ = jsnElmnt.getAsJsonObject();
			if(jsnOBJ.has("userName") && jsnOBJ.get("userName") !=null)
			{
				logger.info("deleting key:"+jsnOBJ.get("userName").getAsString().trim().toLowerCase());
				sessionObject.deleteSessionObject(jsnOBJ.get("userName").getAsString().trim().toLowerCase());
			}
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		}
	}
	private void deleteSessionObject(String key)
	{
		try
		{
			sessionObject.deleteSessionObject(key);
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		}
	}
	
	@CrossOrigin()
	@RequestMapping(value = "/forceLogout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, headers = "Authorization")
	public String forceLogout(@ApiParam(value = "{\"userName\":\"String user name to force logout\", "
			+ "\"providerServiceMapID\":\"Integer service provider ID\"}") @RequestBody ForceLogoutRequestModel request) {
		OutputResponse response = new OutputResponse();
		try {
			iemrAdminUserServiceImpl.forceLogout(request);
			response.setResponse("Success");
		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/userForceLogout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, headers = "Authorization")
	public String userForceLogout(
			@ApiParam(value = "\"{\\\"userName\\\":\\\"String\\\"}\"") @RequestBody ForceLogoutRequestModel request) {
		OutputResponse response = new OutputResponse();
		try {
			iemrAdminUserServiceImpl.userForceLogout(request);
			response.setResponse("Success");
		} catch (Exception e) {
			response.setError(e);
		}
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/getAgentByRoleID", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON, headers = "Authorization")
	public String getAgentByRoleID(@ApiParam(value = "{\"providerServiceMapID\":\"Integer - providerServiceMapID\", "
			+ "\"RoleID\":\"Optional: Integer - role ID to be filtered\"}") @RequestBody String request) {
		OutputResponse response = new OutputResponse();
		logger.info("getAgentByRoleID request " + request.toString());
		try {
			response.setResponse(iemrAdminUserServiceImpl.getAgentByRoleID(request));
		} catch (Exception e) {
			logger.error("getAgentByRoleID failed with error " + e.getMessage(), e);
			response.setError(e);
		}
		logger.info("getAgentByRoleID response " + response.toString());
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/userAuthenticateByEncryption", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public String userAuthenticateByEncryption(
			@ApiParam(value = "\"{\\\"userName\\\":\\\"String\\\",\\\"password\\\":\\\"String\\\"}\"") @RequestBody String req,
			HttpServletRequest request) {
		OutputResponse response = new OutputResponse();
		logger.info("userAuthenticateByEncryption request ");
		try {
			List<User> mUser = iemrAdminUserServiceImpl.userAuthenticateByEncryption(req);
			JSONObject resMap = new JSONObject();
			JSONObject serviceRoleMultiMap = new JSONObject();
			JSONObject serviceRoleMap = new JSONObject();
			JSONArray serviceRoleList = new JSONArray();
			JSONObject previlegeObj = new JSONObject();

			if (mUser.size() == 1) {
				createUserMapping(mUser.get(0), resMap, serviceRoleMultiMap, serviceRoleMap, serviceRoleList,
						previlegeObj);
			} else {
				resMap.put("isAuthenticated", /* Boolean.valueOf(false) */false);
			}
			JSONObject responseObj = new JSONObject(resMap.toString());
			JSONArray previlageObjs = new JSONArray();
			Iterator<?> services = previlegeObj.keys();
			while (services.hasNext()) {
				String service = (String) services.next();
				previlageObjs.put(previlegeObj.getJSONObject(service));
			}
			responseObj.put("previlegeObj", previlageObjs);
			String remoteAddress = request.getHeader("X-FORWARDED-FOR");
			if (remoteAddress == null || remoteAddress.trim().length() == 0) {
				remoteAddress = request.getRemoteAddr();
			}
			responseObj = iemrAdminUserServiceImpl.generateKeyAndValidateIP(responseObj, remoteAddress,
					request.getRemoteHost());
			response.setResponse(responseObj.toString());
		} catch (Exception e) {
			logger.error("userAuthenticateByEncryption failed with error " + e.getMessage(), e);
			response.setError(e);
		}
		logger.info("userAuthenticateByEncryption response " + response.toString());
		return response.toString();
	}

	@CrossOrigin()
	@RequestMapping(value = "/role/{roleID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON, headers = "Authorization")
	public String getrolewrapuptime(@PathVariable("roleID") Integer roleID) {

		OutputResponse response = new OutputResponse();
		try {
			M_Role test = iemrAdminUserServiceImpl.getrolewrapuptime(roleID);
			if (test == null) {
				throw new Exception("RoleID Not Found");
			}
			response.setResponse(test.toString());
		} catch (Exception e) {
			response.setError(e);
		}
//		logger.info("response is " + response.toStringWithSerialization());
		return response.toString();
//		return test.toString();
	}

	/**
	 * @author DE40034072 Date 23-03-2022
	 * @param request
	 * @return transaction Id for password change
	 */
	@CrossOrigin
	@ApiOperation(value = "Validating security question and answers for password change", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = { "/validateSecurityQuestionAndAnswer" }, method = { RequestMethod.POST })
	public String validateSecurityQuestionAndAnswer(
			@ApiParam(value = "{\"SecurityQuesAns\": [{\"questionId\":\"String\",\"answer\":\"String\"}],\"userName\":\"String\"}") @RequestBody String request) {

		OutputResponse response = new OutputResponse();
		logger.info("validateSecurityQuestionAndAnswer API request" + request);
		try {
			if (request != null) {
				JsonObject requestObj = new JsonObject();
				JsonParser jsnParser = new JsonParser();
				JsonElement jsnElmnt = jsnParser.parse(request);
				requestObj = jsnElmnt.getAsJsonObject();
				String resp = iemrAdminUserServiceImpl.validateQuestionAndAnswersForPasswordChange(requestObj);
				response.setResponse(resp);
			} else
				throw new IEMRException("Invalid Request");
		} catch (Exception e) {
			response.setError(5000, e.getMessage());
			logger.error(e.toString());
		}
		logger.info("validateSecurityQuestionAndAnswer API response" + response.toString());
		return response.toString();
	}

}