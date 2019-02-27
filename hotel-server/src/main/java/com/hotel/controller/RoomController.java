package com.hotel.controller;

import com.hotel.annotation.AddRoomGroup;
import com.hotel.annotation.LoginGroup;
import com.hotel.constant.RetCode;
import com.hotel.constant.RoomStatus;
import com.hotel.constant.RoomType;
import com.hotel.dto.*;
import com.hotel.entity.*;
import com.hotel.service.CustomerService;
import com.hotel.service.RoomService;
import com.hotel.util.Converters;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author guangyong.yang
 * @date 2019-01-20
 * 客房restful接口
 */
@RestController
@Slf4j
@CrossOrigin
@RequestMapping("api")
@Api(description = "客房RestFul接口")
public class RoomController {

    private RoomService roomService;
    private CustomerService customerService;


    @Autowired
    public RoomController(RoomService roomService, CustomerService customerService) {
        this.roomService = roomService;
        this.customerService = customerService;
    }

    /**
     * 搜索客房
     * @param condition     搜索条件
     * @return              客房列表
     */
    @GetMapping("/rooms")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("搜索客房")
    @ApiImplicitParams({@ApiImplicitParam(name="roomNo",dataType = "string",value="客房号",paramType = "form"),
            @ApiImplicitParam(name="userName",dataType = "String",value="客人姓名",paramType = "form"),
            @ApiImplicitParam(name="idCard",dataType = "String",value="客人身份证号",paramType = "form"),
            @ApiImplicitParam(name="phoneNo",dataType = "String",value="客人手机号",paramType = "form")})
    public List<RoomDto> searchRooms(RoomSearchCondition condition) {
        //查询客房信息
        List<Room> rooms = roomService.search(condition);


        //将room信息转化为roomDto
        List<RoomDto> roomDtos = new ArrayList<>();
        for(Room room: rooms) {
            roomDtos.add(Converters.room2RoomDto(room));
        }

        return roomDtos;
    }


    @PostMapping("/rooms")
    @ApiOperation("新增客房")
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<ResultDto> create(@RequestBody @Validated(AddRoomGroup.class) RoomDto roomDto) {
        String roomNo = roomDto.getRoomNo();
        List<Room> roomByRoomNo = roomService.findRoomByRoomNo(roomNo);
        if (roomByRoomNo != null && roomByRoomNo.size() > 0) {
            return new ResponseEntity<>(ResultDto.builder()
                    .success(false)
                    .code(RetCode.RETCODE_40012)
                    .message("已经存在"+roomNo+"客房")
                    .build(),HttpStatus.BAD_REQUEST);
        }

        //客房初始状态为"空房"状态
        roomDto.setStatus(RoomStatus.EMPTY.getCode());
        Room room = roomService.save(Converters.roomDto2Room(roomDto));
        return new ResponseEntity<>(ResultDto.builder()
                .success(true)
                .code(RetCode.RETCODE_20005)
                .message(RetCode.RETCODE_20005_MSG)
                .data(Converters.room2RoomDto(room))
                .build(),HttpStatus.OK);
    }

    @PutMapping("/rooms/login")
    @ApiOperation("登记")
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<ResultDto> login(@RequestBody @Validated(LoginGroup.class) RoomDto roomDto) {

        List<CustomerDto> customerDtos = roomDto.getCustomers();
        if (customerDtos == null) {
            return new ResponseEntity<>(ResultDto.builder()
                    .success(false)
                    .code(RetCode.RETCODE_40011)
                    .message("客人信息不能为空")
                    .build(),HttpStatus.BAD_REQUEST);
        }

        //处理客人信息
        List<Customer> customers = new ArrayList<>();
        for (CustomerDto customerDto : customerDtos) {
            List<Customer> customerByIdCard = customerService.findCustomerByIdCard(customerDto.getIdCard());
            if (customerByIdCard.size() > 1) {
                return new ResponseEntity<>(ResultDto.builder().success(false)
                        .code(RetCode.RETCODE_40001)
                        .message(customerDto.getIdCard() + "身份证存在多个客人信息").build(), HttpStatus.BAD_REQUEST);
            }

            Customer customer;
            if (customerByIdCard.size() == 1) {
                customer = customerByIdCard.get(0);
                List<Room> rooms = customer.getRooms();
                if (rooms != null) {
                    for (Room room : rooms) {
                        if (room.getRoomNo().equals(roomDto.getRoomNo())) {
                            return new ResponseEntity<>(ResultDto.builder().success(false)
                                    .code(RetCode.RETCODE_40005)
                                    .message(customer.getIdCard() + "身份证号的客人已登记过" + room.getRoomNo() + "客房:").build(),
                                    HttpStatus.BAD_REQUEST);
                        }
                    }
                }
            } else {
                customer = new Customer();
                customer.setIdCard(customerDto.getIdCard());
            }

            customer.setComment(customerDto.getComment());
            customer.setName(customerDto.getName());
            customer.setPhoneNo(customerDto.getPhoneNo());
            customers.add(customer);
        }

        List<Room> rooms = roomService.findRoomByRoomNo(roomDto.getRoomNo());
        if (rooms == null || rooms.size() == 0) {
            return new ResponseEntity<>(ResultDto.builder().success(false)
                    .code(RetCode.RETCODE_40002)
                    .message(roomDto.getRoomNo() +"号客房不存在").build(),HttpStatus.BAD_REQUEST);
        }

        if (rooms.size() > 1) {
            return new ResponseEntity<>(ResultDto.builder().success(false)
                    .code(RetCode.RETCODE_40003)
                    .message(roomDto.getRoomNo() +"号客房存在多个房间").build(),HttpStatus.BAD_REQUEST);
        }

        Room room = rooms.get(0);
        if (room.getStatus() == RoomStatus.UNUSED) {
            return new ResponseEntity<>(ResultDto.builder().success(false)
                    .code(RetCode.RETCODE_40004)
                    .message(roomDto.getRoomNo() +"号客房为不可订状态").build(),HttpStatus.BAD_REQUEST);
        }

        List<Bed> beds = room.getBeds();
        if (beds == null || beds.size() == 0) {
            return new ResponseEntity<>(ResultDto.builder().success(false)
                    .code(RetCode.RETCODE_40016)
                    .message(roomDto.getRoomNo() +"号客房没有维护床铺信息，不可登记").build(),HttpStatus.BAD_REQUEST);
        }

        float currentMoney = Converters.getCurrentMoney(room);
        if ( currentMoney == 0) {
            return new ResponseEntity<>(ResultDto.builder().success(false)
                    .code(RetCode.RETCODE_40017)
                    .message(roomDto.getRoomNo() +"号客房没有维护当前房价或房价为0，不可登记").build(),HttpStatus.BAD_REQUEST);
        }

        room.setCheckInDate(roomDto.getCheckInDate());
        room.setCheckOutDate(roomDto.getCheckOutDate());
        room.setStatus(RoomStatus.CHECKINGIN);
        room.getCustomers().addAll(customers);
        roomService.save(room);

        return new ResponseEntity<>(ResultDto.builder()
                .success(true)
                .code(RetCode.RETCODE_20001)
                .message(RetCode.RETCODE_20001_MSG)
                .build(),HttpStatus.OK);
    }


    @PutMapping("/rooms/logout")
    @ApiOperation("退房")
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<ResultDto> logout(@NotNull @RequestBody @ApiParam("客房ID") Integer roomId) {
        log.trace("退房,id:{}",roomId);

        Room room = roomService.findOne(roomId);
        if (room == null) {
            return new ResponseEntity<>(ResultDto.builder().success(false)
                    .code(RetCode.RETCODE_40018)
                    .message("没有查询到ID为"+roomId+"的客房信息").build(),HttpStatus.BAD_REQUEST);
        }

        //计算入住费用
        int interval = calculateInterval(room.getType(),room.getCheckInDate(),room.getCheckOutDate());
        float price = Converters.getCurrentMoney(room);
        String sum = Converters.getStringPrice(interval*price);

        Income income = new Income();
        income.setIncoming(Float.valueOf(sum));
        income.setRoom(room);
        Date date = new Date();
        income.setLogoutDate(date);

        List<Income> incomes = room.getIncomes();
        incomes.add(income);

        // 清空客户的入住、退房日期、客人以及修改状态
        room.setCustomers(null);
        room.setCheckInDate(null);
        room.setCheckOutDate(null);
        room.setStatus(RoomStatus.EMPTY);

        roomService.save(room);

        //构造返回数据
         return new ResponseEntity<>(ResultDto.builder().success(true)
                .code(RetCode.RETCODE_20002)
                .message(RetCode.RETCODE_20002_MSG)
                .data(LogoutDto.builder().logoutDate(date)
                        .interval(String.valueOf(interval))
                        .type(room.getType().getName())
                        .price(String.valueOf(price))
                        .sum(sum)
                        .roomNo(room.getRoomNo())
                        .build()).build(),HttpStatus.OK);
    }



    @PutMapping("/rooms")
    @ApiOperation("更新客房信息")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<ResultDto> updateRoom(@RequestBody  RoomDto roomDto){
        if ( roomDto == null || StringUtils.isEmpty(roomDto.getId())){
            return new ResponseEntity<>(ResultDto.builder()
                    .success(false)
                    .code(RetCode.RETCODE_40008)
                    .message("客房信息不能为空")
                    .build(),HttpStatus.BAD_REQUEST);
        }

        log.trace("更新客房,id:{}",roomDto.getId());

        // 客房状态判断
        Room room = roomService.findOne(roomDto.getId());
        if (room != null) {
            if (room.getStatus() == RoomStatus.CHECKINGIN
                    && !StringUtils.isEmpty(roomDto.getStatus())
                    && RoomStatus.parse(roomDto.getStatus()) != RoomStatus.CHECKINGIN) {
                return new ResponseEntity<>(ResultDto.builder()
                        .success(false)
                        .code(RetCode.RETCODE_40004)
                        .message("客房" + room.getRoomNo() + "当前有客,不能更改为其它状态")
                        .build(), HttpStatus.BAD_REQUEST);
            }

            if (room.getStatus() == RoomStatus.CHECKINGIN
                    && !StringUtils.isEmpty(roomDto.getType())
                    && RoomType.parse(roomDto.getType()) != room.getType()) {
                return new ResponseEntity<>(ResultDto.builder()
                        .success(false)
                        .code(RetCode.RETCODE_40004)
                        .message("客房" + room.getRoomNo() + "当前有客,不能更改房型")
                        .build(), HttpStatus.BAD_REQUEST);
            }

            if (room.getStatus() == RoomStatus.EMPTY
                    && !StringUtils.isEmpty(roomDto.getStatus())
                    && RoomStatus.parse(roomDto.getStatus()) != RoomStatus.EMPTY
                    && RoomStatus.parse(roomDto.getStatus()) != RoomStatus.UNUSED) {
                return new ResponseEntity<>(ResultDto.builder()
                        .success(false)
                        .code(RetCode.RETCODE_40004)
                        .message("客房" + room.getRoomNo() + "目前是空房,只能更改为不可订状态")
                        .build(), HttpStatus.BAD_REQUEST);
            }

            if (room.getStatus() == RoomStatus.UNUSED
                    && !StringUtils.isEmpty(roomDto.getStatus())
                    && RoomStatus.parse(roomDto.getStatus()) != RoomStatus.UNUSED
                    && RoomStatus.parse(roomDto.getStatus()) != RoomStatus.EMPTY) {
                return new ResponseEntity<>(ResultDto.builder()
                        .success(false)
                        .code(RetCode.RETCODE_40004)
                        .message("客房" + room.getRoomNo() + "目前不可订,只能更改为空房状态")
                        .build(), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(ResultDto.builder()
                    .success(false)
                    .code(RetCode.RETCODE_40013)
                    .message("客房ID:"+roomDto.getId()+"错误")
                    .build(),HttpStatus.BAD_REQUEST);
        }


        RoomDto roomDto1 = Converters.room2RoomDto(roomService.updateRoom(Converters.roomDto2Room(roomDto)));
        return new ResponseEntity<>(ResultDto.builder()
                .success(true)
                .code(RetCode.RETCODE_20004)
                .message(RetCode.RETCODE_20004_MSG)
                .data(roomDto1).build(),HttpStatus.OK);
    }


    /**
     * 根据客房ID号获取客房信息
     * @param id     客房ID
     * @return       客房信息
     */
    @GetMapping("/rooms/{id}")
    @ApiOperation("根据客房ID号获取客房信息")
    public ResponseEntity<ResultDto> getRoom(@ApiParam(value = "客房ID",required = true) @PathVariable("id") Integer id){
        log.trace("根据客房id获取客房信息;id:{}",id);
        Room room = roomService.findOne(id);
        if ( room == null ) {
            return new ResponseEntity<>(ResultDto.builder().success(false).code(RetCode.RETCODE_40009).message("没有查询到客房信息").build(),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ResultDto.builder().success(true).code(RetCode.RETCODE_20003).message(RetCode.RETCODE_20003).data(Converters.room2RoomDto(room)).build(),HttpStatus.OK);
    }


    private int calculateInterval(RoomType type,Date in,Date out) {
        if (in == null || out == null) {
            throw new IllegalArgumentException("没有入住或退房日期");
        }

        int result;
        switch (type) {
            case HOUR:
                result = (int)Math.ceil((out.getTime() - in.getTime()) / 3600.0f / 1000.0f);
                break;
            case DAY:
                result = (int)Math.ceil((out.getTime() - in.getTime()) / 24.0f /3600.0f / 1000.0f);
                break;
            case MONTH:
                result = (int)Math.ceil((out.getTime() - in.getTime()) / 30.0f / 24.0f / 3600.0f / 1000.0f);
                break;
            default:
                throw new IllegalArgumentException("房间类型不正确");
        }

        return result;
    }
}
