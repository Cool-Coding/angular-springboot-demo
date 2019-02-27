package com.hotel.util;

import com.hotel.constant.BedType;
import com.hotel.constant.RoomStatus;
import com.hotel.constant.RoomType;
import com.hotel.dto.*;
import com.hotel.entity.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author guangyong.yang
 * @date 2019-01-27
 * dto与entity之间转换
 */
public class Converters {

    public static CustomerDto customer2Dto(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(customer.getId());
        customerDto.setName(customer.getName());
        customerDto.setIdCard(customer.getIdCard());
        customerDto.setPhoneNo(customer.getPhoneNo());
        customerDto.setComment(customer.getComment());

        List<Room> rooms = customer.getRooms();
        if (rooms != null) {
            List<String> roomNos = new ArrayList<>();
            for (Room room2 : rooms) {
                roomNos.add(room2.getRoomNo());
            }
            customerDto.setRoomNos(roomNos);
        }
        return customerDto;
    }

    public static Customer dto2Customer(CustomerDto customerDto) {
        if (customerDto == null ){
            return null;
        }

        Customer customer = new Customer();
        customer.setId(customerDto.getId());
        customer.setName(customerDto.getName());
        customer.setIdCard(customerDto.getIdCard());
        customer.setPhoneNo(customerDto.getPhoneNo());
        customer.setComment(customerDto.getComment());
        return  customer;
    }


    public static Room roomDto2Room(RoomDto roomDto){
        Room room = new Room();
        room.setId(roomDto.getId());
        room.setRoomNo(roomDto.getRoomNo());
        room.setType(RoomType.parse(roomDto.getType()));
        room.setStatus(RoomStatus.parse(roomDto.getStatus()));
        room.setCheckInDate(roomDto.getCheckInDate());
        room.setCheckOutDate(roomDto.getCheckOutDate());

        //房中现住客人
        List<CustomerDto> customerDtos = roomDto.getCustomers();
        if (customerDtos != null && customerDtos.size() > 0) {
            List<Customer> customers = new ArrayList<>();
            for (CustomerDto customerDto : customerDtos) {
                customers.add(Converters.dto2Customer(customerDto));
            }
            room.setCustomers(customers);
        }

        //房中床铺信息(床铺与床关系由床铺维护，所以需要指定床铺所在的床)
        List<BedDto> bedDtos = roomDto.getBeds();
        if (bedDtos !=null && bedDtos.size() > 0) {
            List<Bed> beds = new ArrayList<>();
            for (BedDto bedDto : bedDtos) {
                Bed bed = new Bed();
                bed.setId(bedDto.getId());
                bed.setName(bedDto.getName());
                bed.setSize(bedDto.getSize());
                bed.setType(BedType.parse(bedDto.getType()));
                bed.setRoom(room);
                beds.add(bed);
            }
            room.setBeds(beds);
        }

        List<ChargeDto> chargeDtos = roomDto.getCharges();
        if (chargeDtos != null && chargeDtos.size() > 0){
            List<Charge> charges = new ArrayList<>();
            for (ChargeDto chargeDto : chargeDtos){
                Charge charge = new Charge();
                charge.setId(chargeDto.getId());
                charge.setCount(chargeDto.getCount());
                charge.setMoney(chargeDto.getMoney());
                charge.setTimeUnit(RoomType.parse(chargeDto.getTimeUnit()));
                charge.setStartDate(chargeDto.getStartDate());
                charge.setEndDate(chargeDto.getEndDate());
                charge.setRoom(room);
                charges.add(charge);
            }
            room.setCharges(charges);
        }
        return room;
    }


    public static  RoomDto room2RoomDto(Room room){
        RoomDto roomDto = new RoomDto();
        roomDto.setId(room.getId());
        roomDto.setRoomNo(room.getRoomNo());
        roomDto.setType(room.getType().getName());
        roomDto.setStatus(room.getStatus().getCode());
        roomDto.setCheckInDate(room.getCheckInDate());
        roomDto.setCheckOutDate(room.getCheckOutDate());

        //现今房价
        roomDto.setMoney(getStringPrice(getCurrentMoney(room)));

        //房中现住客人
        List<Customer> customers = room.getCustomers();
        List<CustomerDto> customerDtos = new ArrayList<>();
        if (customers != null ) {
            for (Customer customer : customers) {
                customerDtos.add(Converters.customer2Dto(customer));
            }
        }
        roomDto.setCustomers(customerDtos);


        //房中床铺信息
        List<Bed> beds = room.getBeds();
        List<BedDto> bedDtos = new ArrayList<>();
        if (beds != null ){
            for (Bed bed : beds) {
                BedDto bedDto = new BedDto();
                bedDto.setId(bed.getId());
                bedDto.setName(bed.getName());
                bedDto.setSize(bed.getSize());
                bedDto.setType(bed.getType().getName());
                bedDtos.add(bedDto);
            }
        }
        roomDto.setBeds(bedDtos);

        // 客房定价历史
        List<Charge> charges = room.getCharges();
        List<ChargeDto> chargeDtos = new ArrayList<>();
        if (charges != null) {
            for (Charge charge : charges) {
                ChargeDto chargeDto = new ChargeDto();
                chargeDto.setId(charge.getId());
                chargeDto.setCount(charge.getCount());
                chargeDto.setMoney(charge.getMoney());
                chargeDto.setTimeUnit(charge.getTimeUnit().getName());
                chargeDto.setStartDate(charge.getStartDate());
                chargeDto.setEndDate(charge.getEndDate());
                chargeDtos.add(chargeDto);
            }
        }
        roomDto.setCharges(chargeDtos);

        //客房收入历史记录
        List<Income> incomes = room.getIncomes();
        List<IncomeDto> incomeDtos = new ArrayList<>();
        if (incomes != null ) {
            for (Income income : incomes) {
                IncomeDto incomeDto = new IncomeDto();
                incomeDto.setId(income.getId());
                incomeDto.setRommNo(income.getRoom().getRoomNo());
                incomeDto.setIncoming(getStringPrice(income.getIncoming()));
                incomeDto.setLogoutDate(income.getLogoutDate());
                incomeDtos.add(incomeDto);
            }
        }
        roomDto.setIncomes(incomeDtos);
        return roomDto;
    }

    public static  String getStringPrice(float price) {
        if (price == 0) {
            return "0.00";
        }

        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(price);
    }

    public static float getCurrentMoney(Room room){
        List<Charge> charges = room.getCharges();
        if (charges == null) {
            return 0;
        }

        Date date = new Date();

        float price = 0;
        for(Charge charge : charges){
            if (charge.getTimeUnit() == room.getType() && charge.getStartDate().getTime() <= date.getTime() && charge.getEndDate().getTime() >= date.getTime()) {
                price = charge.getMoney() / (charge.getCount() == 0 ? 1 : charge.getCount());
                break;
            }
        }

        return price;
    }
}
