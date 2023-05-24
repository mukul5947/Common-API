package com.iemr.common.repository.users;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.iemr.common.data.users.UserServiceRoleMapping;

@Repository
@RestResource(exported = false)
public interface UserRoleMappingRepository extends CrudRepository<UserServiceRoleMapping, Long>
{
	@Query("select map.USRMappingID, map.UserID, map.RoleID, map.m_Role, map.providerServiceMapID, "
			+ "map.agentID, map.inbound, map.outbound, map.agentPassword, map.workingLocationID, map.providerServiceAddressMapping from "
			+ "UserServiceRoleMapping map left join map.m_ProviderServiceMapping psm left join psm.serviceProvider sp "
			+ "left join map.m_Role left join map.providerServiceAddressMapping "
			+ "where map.Deleted = false and UserID = :UserID and sp.statusID in (1,2) "
			+ "and sp.deleted = false and psm.statusID in (1,2) "
			+ "and sp.validTill > current_timestamp() and sp.validFrom < current_timestamp() "
			+ "order by map.USRMappingID ")
	Set<Objects[]> getUserRoleMappingForUser(@Param("UserID") Long UserID);

	@Query("select map.RoleID, map.m_Role from UserServiceRoleMapping map join map.m_Role "
			+ "where map.Deleted = false and map.providerServiceMapID = :providerServiceMapID group by map.RoleID "
			+ "order by map.RoleID ")
	Set<Objects[]> getRolesByProviderServiceMapID(@Param("providerServiceMapID") Integer providerServiceMapID);

	@Query("select distinct map.workingLocationID, map.providerServiceAddressMapping "
			+ "from UserServiceRoleMapping map join map.providerServiceAddressMapping "
			+ "where map.Deleted = false and map.providerServiceMapID = :providerServiceMapID "
			+ "group by map.workingLocationID order by map.workingLocationID")
	Set<Objects[]> getLocationsByProviderID(@Param("providerServiceMapID") Integer providerServiceMapID);

	@Query("select distinct map.workingLocationID, map.providerServiceAddressMapping "
			+ "from UserServiceRoleMapping map join map.providerServiceAddressMapping "
			+ "where map.Deleted = false and map.providerServiceMapID = :providerServiceMapID and "
			+ "map.RoleID = :RoleID group by map.workingLocationID order by map.workingLocationID")
	Set<Objects[]> getLocationsByProviderID(@Param("providerServiceMapID") Integer providerServiceMapID,
			@Param("RoleID") Integer RoleID);

	@Query("select distinct UserID from UserServiceRoleMapping where Deleted = false and "
			+ "providerServiceMapID = :providerServiceMapID")
	List<Long> getUsersByProviderServiceMapID(@Param("providerServiceMapID") Integer providerServiceMapID);

	@Query("select distinct usr.UserID from UserServiceRoleMapping usr join usr.m_Role role "
			+ "where usr.Deleted = false and usr.providerServiceMapID = :providerServiceMapID and role.RoleID = :roleID ")
	List<Long> getUsersByProviderServiceMapID(@Param("providerServiceMapID") Integer providerServiceMapID,
			@Param("roleID") Integer roleID);

	@Query("select distinct usr.UserID from UserServiceRoleMapping usr join usr.m_user user "
			+ "join user.m_UserLangMappings langs join langs.m_language lang "
			+ "where usr.Deleted = false and usr.providerServiceMapID = :providerServiceMapID "
			+ "and langs.canSpeak = true and lang.languageName = :languageName order by usr.UserID")
	List<Long> getUsersByProviderServiceMapLang(@Param("providerServiceMapID") Integer providerServiceMapID,
			@Param("languageName") String languageName);

	@Query("select distinct usr.UserID from UserServiceRoleMapping usr join usr.m_Role role join usr.m_user user "
			+ "join user.m_UserLangMappings langs join langs.m_language lang "
			+ "where usr.Deleted = false and usr.providerServiceMapID = :providerServiceMapID and role.RoleID = :roleID "
			+ "and langs.canSpeak = true and lang.languageName = :languageName order by usr.UserID")
	List<Long> getUsersByProviderServiceMapRoleLang(@Param("providerServiceMapID") Integer providerServiceMapID,
			@Param("roleID") Integer roleID, @Param("languageName") String languageName);

	@Query("select count(usr) from UserServiceRoleMapping usr "
			+ "where usr.UserID = :userID and usr.providerServiceMapID = :providerServiceMapID")
	long getCountByUserIDAndProviderServiceMapID(@Param("userID") Long userID,
			@Param("providerServiceMapID") Integer providerServiceMapID);

	@Query("select usr from UserServiceRoleMapping usr "
			+ "where usr.UserID = :userID and usr.providerServiceMapID = :providerServiceMapID")
	List<UserServiceRoleMapping> getMappingsByUserIDAndProviderServiceMapID(@Param("userID") Long userID,
			@Param("providerServiceMapID") Integer providerServiceMapID);

	@Query("select distinct user.agentID from UserServiceRoleMapping user where Deleted = false and "
			+ "user.providerServiceMapID = :providerServiceMapID and user.agentID !=null order by user.agentID")
	List<String> getAgentByProviderServiceMapID(@Param("providerServiceMapID") Integer providerServiceMapID);

	@Query("select distinct user.agentID from UserServiceRoleMapping user join user.m_Role role "
			+ "where user.Deleted = false and user.providerServiceMapID = :providerServiceMapID and role.RoleID = :roleID and user.agentID !=null order by user.agentID")
	List<String> getAgentByRoleID(@Param("providerServiceMapID") Integer providerServiceMapID,
			@Param("roleID") Integer roleID);
}
