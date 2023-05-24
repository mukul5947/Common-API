package com.iemr.common.repository.userbeneficiarydata;

import java.util.Objects;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.iemr.common.data.beneficiary.SexualOrientation;

@Repository
@RestResource(exported = false)
public abstract interface SexualOrientationRepository extends CrudRepository<SexualOrientation, Short> {
	@Query("select sexualOrientationId, sexualOrientation from SexualOrientation where deleted = false")
	public abstract Set<Objects[]> findAciveOrientations();

	public SexualOrientation findBysexualOrientationId(Short sexualOrientationId);
}
