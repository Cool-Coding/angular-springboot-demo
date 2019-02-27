package com.hotel.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author guangyong.yang
 * @date 2019-01-25
 * @description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RoomServiceTest {

    @Autowired
    private RoomService roomService;

    @Test
    public void testDeleteBed(){
        /*Room room = roomService.findOne(1);
        List<Bed> beds = room.getBeds();
        Bed bed = beds.get(1);
        bed.setRoom(null);
        beds.remove(1);
        room.setBeds(beds);
        roomService.save(room);*/

    }
}
