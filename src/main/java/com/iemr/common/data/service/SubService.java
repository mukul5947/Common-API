package com.iemr.common.data.service;

import java.sql.Timestamp;
import java.util.List;

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
import com.iemr.common.data.beneficiary.BenRelationshipType;
import com.iemr.common.data.beneficiary.BeneficiaryRegistrationData;
import com.iemr.common.data.beneficiary.GovtIdentityType;
import com.iemr.common.data.beneficiary.BeneficiaryOccupation;
import com.iemr.common.data.users.ProviderServiceMapping;
import com.iemr.common.utils.mapper.OutputMapper;

import lombok.Data;

@Entity
@Table(name = "m_subservice")
@Data
public class SubService {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Expose
	@Column(name = "SubServiceID")
	private Integer subServiceID;

	@Column(name = "ProviderServiceMapID")
	@Expose
	private Integer providerServiceMapID;
	@JoinColumn(name = "ProviderServiceMapID", insertable = false, updatable = false)
	@OneToOne(fetch = FetchType.LAZY)
	@Expose
	private ProviderServiceMapping service;

	@Expose
	@Column(name = "SubServiceName")
	private String subServiceName;
	@Expose
	@Column(name = "SubServiceDesc")
	private String subServiceDesc;
	@Expose
	@Column(name = "Deleted", insertable = false, updatable = true)
	private Boolean deleted;
	@Expose
	@Column(name = "CreatedBy")
	private String createdBy;
	@Column(name = "CreatedDate")
	private Timestamp createdDate;
	@Column(name = "ModifiedBy")
	private String modifiedBy;
	@Column(name = "LastModDate")
	private Timestamp lastModDate;

	@Transient
	OutputMapper outputMapper = new OutputMapper();

	@Override
	public String toString() {
		return outputMapper.gson().toJson(this);
	}

	public SubService() {
	}

	public SubService(Integer subServiceID, String subServiceName, String subServiceDesc, Boolean deleted) {
		this.subServiceID = subServiceID;
		this.subServiceName = subServiceName;
		this.subServiceDesc = subServiceDesc;
		this.deleted = deleted;
	}

	public Integer getSubServiceID() {
		return subServiceID;
	}

	public Integer getProviderServiceMapID() {
		return providerServiceMapID;
	}

}
