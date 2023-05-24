package com.iemr.common.data.directory;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.iemr.common.data.users.ProviderServiceMapping;
import com.iemr.common.utils.mapper.OutputMapper;

import lombok.Data;

@Entity
@Table(name = "m_institutedirectory")
@Data
public class Directory
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "InstituteDirectoryID")
	@Expose
	private Integer instituteDirectoryID;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "directory")
	@JsonIgnore
	@Expose
	@Transient
	private List<InstituteDirectoryMapping> instituteDirectoryMappings;
	// m_institutesubdirectory
	@Column(name = "InstituteDirectoryName")
	@Expose
	private String instituteDirectoryName;
	@Column(name = "InstituteDirectoryDesc")
	@Expose
	private String instituteDirectoryDesc;
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
	@Column(name = "ProviderServiceMapID")
	@Expose
	private Integer providerServiceMapID;
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ProviderServiceMapID", updatable = false, insertable = false)
	@JsonIgnore
	@Expose
	private ProviderServiceMapping providerServiceMapping;

	@Transient
	private OutputMapper outputMapper = new OutputMapper();

	protected Directory()
	{
	}

	public Directory(Integer institutionID, String directoryName)
	{
		this.instituteDirectoryID = institutionID;
		this.instituteDirectoryName = directoryName;
	}

	public Integer getProviderServiceMapID()
	{
		return providerServiceMapID;
	}

	@Override
	public String toString()
	{
		return outputMapper.gson().toJson(this);
	}
}