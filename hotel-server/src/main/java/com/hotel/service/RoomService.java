package com.hotel.service;

import com.hotel.dto.RoomSearchCondition;
import com.hotel.entity.Room;

import java.util.List;

/**
 * @author guangyong.yang
 * @date 2019-01-20
 * @description
 */
public interface RoomService {
    /**
     * 搜索符合条件的房间集合
     * @param condition
     * @return
     */
    List<Room> search(RoomSearchCondition condition);


    /**
     * 根据客户ID搜索客房信息
     * @param id
     * @return
     */
    Room  findOne(Integer id);

    /**
     * 保存客房信息
     * @param room
     * @return
     */
    Room  save(Room room);

    /**
     * 根据房间号查询房间信息
     * @param roomNo    房间号
     * @return          房间信息
     */
    List<Room> findRoomByRoomNo(String roomNo);

    /**
     * 更新客房信息
     * @param room  客房信息
     * @return      客房信息
     */
    Room updateRoom(Room room, boolean isUpdatingBed);
}
