package com.hotel.repository;

import com.hotel.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author guangyong.yang
 * @date 2019-01-20
 * @description
 */
@Repository
public interface RoomRepository extends JpaRepository<Room,Integer> {
    /**
     * 根据房间号模糊查找
     * @return  房间列表
     */
    @Query("select h from Room h where h.roomNo like %:roomNo%")
    List<Room> searchRoomsByRoomNo(@Param("roomNo") String roomNo);

    /**
     * 根据房间号查询房间信息
     * @param roomNo    房间号
     * @return          房间信息
     */
    List<Room> findRoomByRoomNo(String roomNo);

}
