package com.iemr.common.covidVaccination;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Entity
@Table(name = "t_covidvaccinationstatus")
@Data
public class CovidVaccinationStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CovidVSID", insertable = false, updatable = false)
	@Expose
	private Long covidVSID;

	@Column(name = "BeneficiaryRegID")
	@Expose
	private Long beneficiaryRegID;
	@Expose
	@Column(name = "CovidVaccineTypeID")
	private Integer covidVaccineTypeID;

	@Expose
	@Column(name = "DoseTypeID")
	private Integer doseTypeID;

	@Expose
	@Column(name = "VaccineStatus")
	private String vaccineStatus;
	
	@Expose
	@Column(name = "Dose1_Date")
	private Timestamp dose1_Date;
	@Expose
	@Column(name = "Dose2_Date")
	private Timestamp dose2_Date;
	@Expose
	@Column(name = "Booster_Date")
	private Timestamp booster_Date;

	@Expose
	@Column(name = "ProviderServiceMapID")
	private Integer providerServiceMapID;

	@Expose
	@Column(name = "Deleted", insertable = false, updatable = true)
	private Boolean deleted;

	@Expose
	@Column(name = "Processed", insertable = false)
	private String processed;

	@Expose
	@Column(name = "CreatedBy")
	private String createdBy;

	@Expose
	@Column(name = "CreatedDate", insertable = false, updatable = false)
	private Timestamp createdDate;

	@Expose
	@Column(name = "ModifiedBy")
	private String modifiedBy;

	@Expose
	@Column(name = "LastModDate", insertable = false, updatable = false)
	private Timestamp lastModDate;

	@Expose
	@Column(name = "VanSerialNo")
	private Integer vanSerialNo;

	@Expose
	@Column(name = "VanID")
	private Integer vanID;

	@Expose
	@Column(name = "VehicalNo")
	private Integer vehicalNo;
	@Expose
	@Column(name = "ParkingPlaceID")
	private Integer parkingPlaceID;
	@Expose
	@Column(name = "SyncedBy")
	private String syncedBy;

	@Expose
	@Column(name = "SyncedDate")
	private Timestamp syncedDate;

}
