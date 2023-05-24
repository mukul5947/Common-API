package com.iemr.common.dto.identity;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;
import com.iemr.common.model.beneficiary.BenPhoneMapModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = { "benFamilyMapId", "deleted", "createdBy", "modifiedBy", "createdDate", "lastModDate",
		"relationshipToSelf" })
public class BenFamilyDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private BigInteger benFamilyMapId;
	private BigInteger associatedBenRegId;// parentregid
	private String createdBy;// createdBy
	private Timestamp createdDate;
	private Boolean deleted;
	private Boolean isEmergencyContact;
	private Timestamp lastModDate;
	private String modifiedBy;
	private String relationshipToSelf;// benRelationshipTypeModel name
	private Integer relationshipID;// benRelationshipID

	// public static void createBenFamilyDTO(List<BenFamilyDTO> benFamilyDTOs,
	// List<BenPhoneMap> benPhoneMaps, BigInteger beneficiaryID) {
	// BenPhoneMap benPhoneMap;
	// BenFamilyDTO benFamilyDTO;
	// ListIterator<BenPhoneMap> iterator = benPhoneMaps.listIterator();
	// while(iterator.hasNext()) {
	//
	// benPhoneMap = iterator.next();
	// benFamilyDTO = new BenFamilyDTO();
	// benFamilyDTO.setRelationshipId(BigInteger.valueOf(benPhoneMap.getBenRelationshipID()));
	// benFamilyDTO.setRelationshipToSelf(benPhoneMap.getPhoneTypeName());
	// benFamilyDTO.setAssociatedBenRegId(BigInteger.valueOf(benPhoneMap.getParentBenRegID()));
	//
	// }
	// }

	// new column added for data sync
	// 17-06-2018
	@Expose
	private Integer vanID;
	@Expose
	private Integer parkingPlaceID;
	// END OF new column added for data sync

	public static BenFamilyDTO benFamilyFromBenPhoneMap(BenPhoneMapModel benPhoneMapModel) {
		BenFamilyDTO benFamilyDTO = new BenFamilyDTO();
		benFamilyDTO.associatedBenRegId = BigInteger.valueOf(benPhoneMapModel.getParentBenRegID());
		benFamilyDTO.createdBy = benPhoneMapModel.getCreatedBy();
		if (benPhoneMapModel.getBenRelationshipType() != null) {
			benFamilyDTO.relationshipToSelf = benPhoneMapModel.getBenRelationshipType().getBenRelationshipType();
		}
		benFamilyDTO.relationshipID = benPhoneMapModel.getBenRelationshipID();
		return benFamilyDTO;

	}

	public static Set<BenFamilyDTO> benFamilyFromBenPhoneMapList(List<BenPhoneMapModel> benPhoneMapModelList) {
		// Set<BenFamilyDTO> benFamilyDTO = new ArrayList<BenFamilyDTO>();
		Set<BenFamilyDTO> benFamilyDTOHashMap = new HashSet<BenFamilyDTO>();
		for (BenPhoneMapModel benPhoneMapModel : benPhoneMapModelList) {
			BenFamilyDTO benFamilyDTO = benFamilyFromBenPhoneMap(benPhoneMapModel);
			benFamilyDTOHashMap.add(benFamilyDTO);
		}
		// if (benFamilyDTOHashMap.size() == 0)
		// {
		// return null;
		// }
		return benFamilyDTOHashMap;

	}

}
