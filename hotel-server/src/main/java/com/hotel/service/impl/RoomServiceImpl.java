package com.hotel.service.impl;

import com.hotel.dto.RoomSearchCondition;
import com.hotel.entity.Bed;
import com.hotel.entity.Customer;
import com.hotel.entity.Room;
import com.hotel.repository.BedRepository;
import com.hotel.repository.CustomerRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.service.RoomService;
import com.hotel.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


/**
 * @author guangyong.yang
 * @date 2019-01-20
 */
@Service
public class RoomServiceImpl implements RoomService {

    private RoomRepository roomRepository;
    private CustomerRepository customerRepository;
    private BedRepository bedRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository,CustomerRepository customerRepository,BedRepository bedRepository) {
        this.roomRepository = roomRepository;
        this.customerRepository = customerRepository;
        this.bedRepository = bedRepository;
    }

    @Override
    public List<Room> search(RoomSearchCondition condition) {
        List<Room> rooms = null;
        boolean allConditionNull = true;

        if (Objects.nonNull(condition)) {
            //如果搜索条件中房间号有值，则根据房间号搜索
            if (Objects.nonNull(condition.getRoomNo())) {
                allConditionNull = false;
                rooms = roomRepository.searchRoomsByRoomNo(condition.getRoomNo());
            }

            //如果搜索条件中客户信息至少有一个有值
            if (Objects.nonNull(condition.getName()) ||
                    Objects.nonNull(condition.getIdCard()) ||
                    Objects.nonNull(condition.getPhoneNo())) {
                allConditionNull = false;
                //如果客户号查询条件有值，，则根据房间找到房间里的客人信息，然后再与搜索条件匹配
                //否则，查询出客户信息，再查询出客房信息
                if (Objects.nonNull(condition.getRoomNo())) {
                    //如果没有查询到客房信息，说明客房号不正确，则没必要继续查询
                    if (rooms == null) {
                        return null;
                    }

                    Iterator<Room> iterator = rooms.iterator();
                    while (iterator.hasNext()) {
                        Room room = iterator.next();
                        List<Customer> customers = room.getCustomers();

                        //客房中的客人有一个满足查询条件，则不排除此客房
                        boolean exist = false;
                        for (Customer customer : customers) {
                            if (containField("name", condition, customer)
                                    && containField("phoneNo", condition, customer)
                                    && (containField("idCard", condition, customer))) {

                                exist = true;
                            }
                        }

                        if (!exist) {
                            iterator.remove();
                        }

                    }
                } else {
                    if (Objects.isNull(condition.getName())) {
                        condition.setName("");
                    }

                    if (Objects.isNull(condition.getPhoneNo())) {
                        condition.setPhoneNo("");
                    }

                    if (Objects.isNull(condition.getIdCard())) {
                        condition.setIdCard("");
                    }

                    List<Customer> customers = customerRepository.findCustomers(condition.getName(), condition.getPhoneNo(), condition.getIdCard());
                    rooms = new ArrayList<>();
                    for (Customer customer : customers) {
                        rooms.addAll(customer.getRooms());
                    }
                }
            }
        }

        //如果没有查询条件或所有查询条件为空，则查询所有客房
        if (Objects.isNull(condition) || allConditionNull) {
            rooms = roomRepository.findAll();
        }

        return rooms;
    }

    @Override
    public Room findOne(Integer id) {
        return roomRepository.findOne(id);
    }


    @Override
    public Room save(Room room) {
        return roomRepository.save(room);
    }


    @Override
    public List<Room> findRoomByRoomNo(String roomNo) {
        return roomRepository.findRoomByRoomNo(roomNo);

    }

    @Override
    public Room updateRoom(Room room, boolean isUpdatingBed) {
        Room currentInstance = roomRepository.findOne(room.getId());
        //删除床铺
        if (isUpdatingBed) {
            List<Bed> beds = currentInstance.getBeds();
            if (beds != null && beds.size() > 0) {
                List<Bed> newBeds = room.getBeds();
                for (Bed bed : beds) {
                    boolean exist = false;
                    if (newBeds != null) {
                        for (Bed newBed : newBeds) {
                            if (newBed.getId().equals(bed.getId())) {
                                exist = true;
                                break;
                            }
                        }
                    }

                    if (!exist) {
                        bedRepository.delete(bed.getId());
                    }
                }
            }

            //如果新客房中无床铺，而客房原来有床铺，则将原来客房床铺置null，因为下面复制时会跳过null属性
            if (room.getBeds() == null && beds != null && beds.size() > 0) {
                currentInstance.setBeds(null);
            }
        }

        //支持部分更新
        String[] nullPropertyNames = BeanUtils.getNullPropertyNames(room);
        org.springframework.beans.BeanUtils.copyProperties(room, currentInstance, nullPropertyNames);
        return roomRepository.save(currentInstance);
    }


    private boolean containField(String filedName, RoomSearchCondition condition, Customer customer){
        String conditionValue = BeanUtils.getFieldValue(condition, filedName);
        String entityValue = BeanUtils.getFieldValue(customer, filedName);
        return Objects.isNull(conditionValue) ||  entityValue.contains(conditionValue);
    }
}
