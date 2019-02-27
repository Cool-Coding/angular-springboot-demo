package com.hotel.repository;

import com.hotel.entity.Bed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author guangyong.yang
 * @date 2019-01-25
 * @description
 */
@Repository
public interface BedRepository extends JpaRepository<Bed,Integer> {
}
