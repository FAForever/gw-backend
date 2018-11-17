package com.faforever.gw.model.repository;

import com.faforever.gw.model.Reinforcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReinforcementsRepository extends JpaRepository<Reinforcement, UUID> {
	// @Query(value = "select r.id, sum(rt.quantity) from gw_reinforcement r inner join gw_reinforcements_transaction rt on r.id = rt.fk_reinforcement where character = :character group by r.id", nativeQuery = true)
//	@Query("select new map(r, sum(rt.quantity)) from Reinforcement r inner join ReinforcementsTransaction rt where rt.character = :character group by r.id")
//	public Map<Reinforcement, Integer> findCurrentReinforcements(@Param("character") GwCharacter character);
	//TODO
}
