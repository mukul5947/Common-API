package com.iemr.common.data.report;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.iemr.common.data.callhandling.CallType;

import lombok.Data;

@Entity
@Table(name = "db_reporting.fact_bencall", schema = "db_reporting")
@Data
public class CallQualityReport implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Expose
	@Column(name = "Fact_BenCallID", insertable = false, updatable = false)
	private Long factBenCallID;
	
	@Expose
	@Column(name = "BenCallID")
	private Long benCallID;
	
	@Expose
	@Column(name = "BeneficiaryRegID")
	private Long beneficiaryRegID;
	
	@Expose
	@Column(name = "CallTime")
	private Timestamp callTime;
	
	@Expose
	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "CallTypeID")
	@Expose
	private Integer callTypeID;
	
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CallTypeID", insertable = false, updatable = false)
	private CallType callTypeObj;

	@Expose
	@Column(name = "CallClosureType")
	private String callClosureType;
	
	@Expose
	@Column(name = "CallTypeName")
	private String callType;
	
	@Expose
	@Column(name = "CallSubTypeName")
	private String callSubType;
	
	@Expose
	@Column(name = "ProviderServiceMapID")
	private Integer providerServiceMapID;
	
	@Expose
	@Column(name = "PhoneNo")
	private String phoneNo;

	@Expose
	@Column(name = "IsOutbound")
	private Boolean isOutbound;
	
	@Expose
	@Column(name = "ReceivedRoleName")
	private String receivedRoleName;
	
	@Transient
	private Timestamp startDate;
	
	@Transient
	private Timestamp endDate;
	
	@Transient
	private String searchCriteria;
	
	@Expose
	private Timestamp createdDate;
	
	@Expose
	@Column(name = "CallReceivedUserID")
	private Long userID;
	
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CallReceivedUserID", referencedColumnName = "UserID", insertable = false, updatable = false)
	private UserReport userReportObj;
	
	@Transient
	private Long roleID;
	
	@Transient
	private Integer workingLocationID;
	
	@Expose
	@Column(name = "fact_CreatedDate")
	private Long factCreatedDate;
	
	@Transient
	private Long locationID;
	
	@Transient
	private Long districtID;
	
	@Transient
	private String district;
	
	@Transient
	private Long subdistrictID;
	
	@Transient
	private String fileName;
	
	@Transient
	private Long villageID;
	@Transient
	private Timestamp startDateTime;
	@Transient
	private Timestamp endDateTime;
	
	@Expose
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "beneficiaryRegID", insertable = false, updatable = false,referencedColumnName = "beneficiaryRegID")
	private BenDetails benReport;

}
