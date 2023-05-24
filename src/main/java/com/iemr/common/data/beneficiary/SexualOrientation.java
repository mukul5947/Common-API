package com.iemr.common.data.beneficiary;

import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.iemr.common.utils.mapper.OutputMapper;

import lombok.Data;

@Entity
@Table(name = "m_sexualorientation")
@Data
public class SexualOrientation
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SexualOrientationId")
	@Expose
	private Short sexualOrientationId;

	@OneToMany(mappedBy = "sexualOrientation", fetch = FetchType.LAZY)
	@Expose
	@Transient
	private Set<Beneficiary> i_Beneficiaries;

	@Column(name = "SexualOrientation")
	@Expose
	private String sexualOrientation;
	@Column(name = "SexualOrientationDesc")
	@Expose
	private String sexualOrientationDesc;

	@Column(name = "Deleted", insertable = false, updatable = true)
	@Expose
	private Boolean deleted;
	@Column(name = "CreatedBy")
	@Expose
	private String createdBy;
	@Column(name = "CreatedDate", insertable = false, updatable = false)
	private Timestamp createdDate;
	@Column(name = "ModifiedBy")
	private String modifiedBy;
	@Column(name = "LastModDate", insertable = false, updatable = false)
	private Timestamp lastModDate;

	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	public SexualOrientation()
	{

	}

	public SexualOrientation(Short sexualOrientationId, String sexualOrientation)
	{
		this.sexualOrientationId = sexualOrientationId;
		this.sexualOrientation = sexualOrientation;
	}

	@Override
	public String toString()
	{
		return outputMapper.gson().toJson(this);
	}
}
