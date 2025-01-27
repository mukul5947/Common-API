package com.iemr.common.service.esanjeevani;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.iemr.common.model.esanjeevani.ESanjeevaniPatientAddress;
import com.iemr.common.model.esanjeevani.ESanjeevaniPatientContactDetail;
import com.iemr.common.model.esanjeevani.ESanjeevaniPatientRegistration;
import com.iemr.common.model.esanjeevani.ESanjeevaniProviderAuth;
import com.iemr.common.repo.esanjeevani.ESanjeevaniRepo;
import com.iemr.common.utils.config.ConfigProperties;

@Service
public class ESanjeevaniServiceImpl implements ESanjeevaniService {

	@Autowired
	RestTemplate restTemplate;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	ESanjeevaniRepo eSanjeevaniRepo;

	@Value("${eSanjeevani.url}")
	private String eSanjeevaniUrl;

	@Value("${eSanjeevani.registerPatient}")
	private String eSanjeevaniRegisterPatient;

	@Value("${eSanjeevani.source}")
	private String eSanjeevaniSource;

	@Value("${eSanjeevani.salt}")
	private String eSanjeevaniSalt;

	@Value("${eSanjeevani.userName}")
	private String eSanjeevaniUserName;

	@Value("${eSanjeevani.password}")
	private String eSanjeevaniPassword;

	@Value("${eSanjeevani.routeUrl}")
	private String eSanjeevaniRouteUrl;

	public String getProviderLogin() throws Exception {
		String token = null;

		try {
			String encryptedPassword = DigestUtils.sha512Hex(eSanjeevaniPassword).toLowerCase()
					+ DigestUtils.sha512Hex(eSanjeevaniSalt).toLowerCase();
			String encryptedPaasword = DigestUtils.sha512Hex(encryptedPassword);

			ESanjeevaniProviderAuth reqObj = new ESanjeevaniProviderAuth();
			reqObj.setUserName(eSanjeevaniUserName);
			reqObj.setPassword(eSanjeevaniPassword);
			reqObj.setSalt(eSanjeevaniSalt);
			reqObj.setSource(eSanjeevaniSource);

			logger.info("E-Sangeevani auth api reqObj - " + reqObj);
			String response = restTemplate.postForObject(eSanjeevaniUrl, reqObj, String.class);
			logger.info("E-Sangeevani auth api response - " + response);
			if (response != null && !StringUtils.isEmpty(response)) {
				JSONObject obj = new JSONObject(response);
				String modelResponse = obj.getString("model");
				JSONObject tokenResponse = new JSONObject(modelResponse);
				token = tokenResponse.getString("access_token");
				if (token != null && !StringUtils.isEmpty(token)) {
					return token;
				}
			}
		} catch (Exception e) {
			return ("Error while fetching Authtoken for E-sanjeevani : " + e.getMessage());
		}
		return token;

	}

	@Override
	public String registerPatient(Long benRegId) throws Exception {
		try {
			// String accessToken = getProviderLogin();
			ESanjeevaniPatientRegistration reqObj = new ESanjeevaniPatientRegistration();
			ArrayList<ESanjeevaniPatientAddress> addressObj = new ArrayList<>();
			ArrayList<ESanjeevaniPatientContactDetail> contactObj = new ArrayList<>();

			try {
				List<Object[]> benMappingResponse = eSanjeevaniRepo.getBeneficiaryMappingIds(benRegId);
				logger.info("Beneficiary mapping details - " + benMappingResponse);
				List<Object[]> benAbhaDetailsResponse = eSanjeevaniRepo.getBeneficiaryHealthIdDeatils(benRegId);
				logger.info("Beneficiary HealthId details - " + benAbhaDetailsResponse);
				
				if (!benAbhaDetailsResponse.isEmpty()) {
					Object[] benAbhaDetails = benAbhaDetailsResponse.get(0);
					reqObj.setAbhaAddress(benAbhaDetails[0].toString());
					reqObj.setAbhaNumber(benAbhaDetails[1].toString());
				}
				if (!benMappingResponse.isEmpty()) {
					Object[] benMappingIds = benMappingResponse.get(0);

					BigInteger benDetailsId = (BigInteger) benMappingIds[0];
					BigInteger benAddressId = (BigInteger) benMappingIds[1];
					BigInteger benContactsId = (BigInteger) benMappingIds[2];

					setBeneficiaryDetails(benDetailsId, reqObj);
					setBeneficiaryAddressDetails(benAddressId, addressObj);
					setBeneficiaryContactDetails(benContactsId, contactObj);
					
					if (!ObjectUtils.isEmpty(addressObj))
						reqObj.setLstPatientAddress(addressObj);
					if (!ObjectUtils.isEmpty(contactObj))
						reqObj.setLstPatientContactDetail(contactObj);

				} else {
					return("No beneficiary Details found for benRegId : " + benRegId);
				}
			} catch (Exception e) {
				return ("Issue while fetching mapping details : " + e.getMessage());
			}

			String accessToken = getProviderLogin();
			if (accessToken != null && !StringUtils.isEmpty(accessToken)) {
				MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
				String registryReqObj = new Gson().toJson(reqObj);
//				JSONObject registryReqObj = new JSONObject(reqObj);
				headers.add("Authorization", "Bearer " + accessToken );
				headers.add("Content-Type", "application/json");
				HttpEntity<Object> requestOBJ = new HttpEntity<Object>(registryReqObj, headers);
				logger.info("E-Sangeevani patient registery api request - " + requestOBJ);
				ResponseEntity<String> response = restTemplate.exchange(eSanjeevaniRegisterPatient, HttpMethod.POST,
						requestOBJ, String.class);
				logger.info("E-Sangeevani patient registery api response - " + response);
				if (response != null && response.getBody() != null) {
					String res = response.getBody();
					JSONObject obj = new JSONObject(res);
					logger.info("E-Sangeevani patient  api response object - " + response);
					if (obj.getBoolean("success")) {
						if(obj.get("message") != null && ((String) obj.get("message")).equalsIgnoreCase("success") )
						return eSanjeevaniRouteUrl;
						else {
							return ("Unable to register patient in e-sanjeevani " + obj.get("message"));
						}
					} else {
						return ("Unable to register patient for routing to E-Sanjeevani due to : "
								+ response.getBody());
					}
				}
			} else {
				return ("Empty response from E-sanjeevani Authorization API");
			}
		} catch (Exception e) {
			throw new Exception("Error while E-Sanjeevani registering patient : " + e.getMessage());
		}
		return null;

	}

	private int calculateAge(String dob) {

		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate date = LocalDate.parse(dob, formatter);
		Period period = Period.between(date, currentDate);
		return period.getYears();

	}

	private void setBeneficiaryDetails(BigInteger benDetailsId, ESanjeevaniPatientRegistration reqObj) {
		List<Object[]> beneficaryDetails = eSanjeevaniRepo.getBeneficiaryDeatils(benDetailsId);
		if (beneficaryDetails != null) {
			Object[] beneficaryObj = beneficaryDetails.get(0);
			logger.info("Beneficiary details - " + beneficaryObj.toString());
			// JSONObject beneficaryObj = new JSONObject(beneficaryDetails);
			if (beneficaryObj[0] != null)
			reqObj.setFirstName(beneficaryObj[0].toString());
			if (beneficaryObj[1] != null)
				reqObj.setLastName(beneficaryObj[1].toString());
			if (beneficaryObj[2] != null)
				reqObj.setMiddleName(beneficaryObj[2].toString());
			if (beneficaryObj[3] != null) {
				reqObj.setGenderDisplay(beneficaryObj[3].toString());
				if (beneficaryObj[3].toString().equalsIgnoreCase("male"))
					reqObj.setGenderCode(1);
				else if (beneficaryObj[3].toString().equalsIgnoreCase("female"))
					reqObj.setGenderCode(2);
				else
					reqObj.setGenderCode(3);
			}
			if (beneficaryObj[4] != null) {
				String s = beneficaryObj[4].toString();
				String dob = s.split(" ")[0];
				reqObj.setAge(calculateAge(dob));
				reqObj.setBirthdate(dob);
			}
			reqObj.setBlock(false);
			reqObj.setSource(eSanjeevaniSource);
		}

	}

	private void setBeneficiaryAddressDetails(BigInteger benAddressId,
			ArrayList<ESanjeevaniPatientAddress> addressObjList) {
		ESanjeevaniPatientAddress addressObj = new ESanjeevaniPatientAddress();
		if (benAddressId != null) {
			List<Object[]> beneficiaryAddDetails = eSanjeevaniRepo.getBeneficiaryAddressDetails(benAddressId);
			if (null != beneficiaryAddDetails) {
				Integer countryId = 0;
				Integer stateId = 0;
				Integer districtId = 0;
				Integer blockId = 0;
				Object[] addressDetails = beneficiaryAddDetails.get(0);
				logger.info("address details fetch for beneficiary - " + addressDetails);
//				JSONObject addressDetails = new JSONObject(beneficiaryAddDetails);
				if (addressDetails[0] != null)
					countryId = (int) addressDetails[0];
				else 
					countryId = 91;
				if (addressDetails[1] != null)
					addressObj.setCountryDisplay(addressDetails[1].toString());
				else 
					addressObj.setCountryDisplay("India");
				if (addressDetails[2] != null)
					stateId = (int) addressDetails[2];
				if (addressDetails[3] != null)
					addressObj.setStateDisplay(addressDetails[3].toString());
				if (addressDetails[4] != null)
					districtId = (int) addressDetails[4];
				if (addressDetails[5] != null)
					addressObj.setDistrictDisplay(addressDetails[5].toString());
				if (addressDetails[6] != null)
					blockId = (int) addressDetails[6];
				if (addressDetails[7] != null)
					addressObj.setBlockDisplay(addressDetails[7].toString());
				if (addressDetails[8] != null)
					addressObj.setAddressLine1(addressDetails[8].toString());
				if (addressDetails[9] != null)
					addressObj.setPostalCode(addressDetails[9].toString());
				addressObj.setAddressType("Physical");
				addressObj.setPostalCode("123456");
				addressObj.setCityCode(233);
				addressObj.setCityDisplay("kareemnagar");

				String govCountryCode = eSanjeevaniRepo.getGovCountyId(countryId);
				Integer govtStateCode = eSanjeevaniRepo.getGovStateId(stateId);
				Integer govtDistrictCode = eSanjeevaniRepo.getGovDistrictId(districtId);
				Integer govBlockCode = eSanjeevaniRepo.getGovSubDistrictId(blockId);

				if (null != govCountryCode)
					addressObj.setCountryCode(govCountryCode);
				if (null != govtStateCode)
					addressObj.setStateCode(govtStateCode);
				if (null != govtDistrictCode)
					addressObj.setDistrictCode(govtDistrictCode);
				if (null != govBlockCode)
					addressObj.setBlockCode(govBlockCode);

				addressObjList.add(addressObj);
			}
		}
	}

	private void setBeneficiaryContactDetails(BigInteger benContactId,
			ArrayList<ESanjeevaniPatientContactDetail> contactObjList) {
		ESanjeevaniPatientContactDetail contactObj = new ESanjeevaniPatientContactDetail();
		if (benContactId != null) {
			String beneficiaryContactDetails = eSanjeevaniRepo.getBeneficiaryContactDetails(benContactId);
			logger.info("contact details fetch for beneficiary - " + beneficiaryContactDetails);
			if (null != beneficiaryContactDetails) {
//				JSONObject contactDetails = new JSONObject(beneficiaryContactDetails);
//				Object[] contactDetails = beneficiaryContactDetails.get(0);
				contactObj.setContactPointStatus(true);
				contactObj.setContactPointType("phone");
				contactObj.setContactPointValue(beneficiaryContactDetails);

				contactObjList.add(contactObj);
			}
		}

	}

}
