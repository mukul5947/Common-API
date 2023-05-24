package com.iemr.common.data.beneficiary;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.iemr.common.data.callhandling.OutboundCallRequest;
import com.iemr.common.utils.mapper.OutputMapper;

import lombok.Data;

@Data
public class BenOutboundCallAllocation {

	/**
	 * allocate no to each agent
	 */
	@SerializedName("allocateNo")
	@Expose
	private Integer allocateNo;
	
	/**
	 * list of selected users
	 */
	@SerializedName("userID")
	@Expose
	List<Integer> userID;
	
	/**
	 * All Outbound calls
	 */
	@SerializedName("outboundCallRequests")
	@Expose
	OutboundCallRequest[] outboundCallRequests;

	/**
	 * @return the allocateNo
	 */
	public Integer getAllocateNo() {
		return allocateNo;
	}

	/**
	 * @param allocateNo the allocateNo to set
	 */
	public void setAllocateNo(Integer allocateNo) {
		this.allocateNo = allocateNo;
	}

	/**
	 * @return the userID
	 */
	public List<Integer> getUserID() {
		return userID;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserID(List<Integer> userID) {
		this.userID = userID;
	}

	/**
	 * @return the outboundCallRequests
	 */
	public OutboundCallRequest[] getOutboundCallRequests() {
		return outboundCallRequests;
	}

	/**
	 * @param outboundCallRequests the outboundCallRequests to set
	 */
	public void setOutboundCallRequests(OutboundCallRequest[] outboundCallRequests) {
		this.outboundCallRequests = outboundCallRequests;
	}
		
}
