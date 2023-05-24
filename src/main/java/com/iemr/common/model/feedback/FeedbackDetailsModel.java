package com.iemr.common.model.feedback;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import com.iemr.common.model.beneficiary.BeneficiaryModel;
import com.iemr.common.model.institute.InstituteModel;
import com.iemr.common.model.institute.InstituteTypeModel;
import com.iemr.common.model.user.ProviderServiceMappingModel;
import com.iemr.common.model.user.UserModel;
import com.iemr.common.model.userbeneficiary.DesignationModel;
import com.iemr.common.model.userbeneficiary.DistrictModel;
import com.iemr.common.model.userbeneficiary.DistrictBlockModel;
import com.iemr.common.model.userbeneficiary.DistrictBranchModel;
import com.iemr.common.model.userbeneficiary.EmailStatusModel;
import com.iemr.common.model.userbeneficiary.StateModel;

import lombok.Data;

@Data
public class FeedbackDetailsModel
{
	private Long feedbackID;
	private String requestID;
	private List<FeedbackRequestModel> feedbackRequests;
	private List<FeedbackResponseModel> feedbackResponses;
	private List<FeedbackRequestModel> consolidatedRequests;
	private Long institutionID;
	private InstituteModel institute;
	private String instituteName = "";
	private Integer designationID;
	private DesignationModel designationModel;
	private String designationName = "";
	private Integer severityID;
	private FeedbackSeverityModel severity;
	private String severityTypeName = "";
	private Integer feedbackTypeID;
	private FeedbackTypeModel feedbackType;
	private String feedbackTypeName = "";
	private Long beneficiaryRegID;
	private BeneficiaryModel beneficiary;
	private String beneficiaryName;
	private Integer serviceID;
	private ProviderServiceMappingModel mservicemaster;
	private String serviceName = "";
	private Integer userID;
	private UserModel mUser;
	private String userName;
	private String sMSPhoneNo;
	private Timestamp serviceAvailDate;
	private Integer feedbackStatusID;
	private FeedbackStatusModel feedbackStatus;
	private String feedbackStatusName;
	private String feedback;
	private Integer emailStatusID;
	private EmailStatusModel emailStatus;
	private String emailStatusName;
	private Long benCallID;
	private Integer subServiceID;
	private Timestamp startDate = new Timestamp(0);
	private Timestamp endDate = new Timestamp(Calendar.getInstance().getTimeInMillis());
	private Integer stateID;
	private StateModel stateModel;
	private Integer districtID;
	private DistrictModel district;
	private Integer blockID;
	private DistrictBlockModel districtBlock;
	private Integer districtBranchID;
	private DistrictBranchModel districtBranchMapping;
	private Integer instituteTypeID;
	private InstituteTypeModel instituteType;
	private Integer feedbackNatureID;
	private FeedbackNatureDetailModel feedbackNatureDetail;
	private Boolean deleted;
	private String createdBy;
	private Timestamp createdDate;
	private String modifiedBy;
	private Timestamp lastModDate;
	private String feedbackAgainst;
}
