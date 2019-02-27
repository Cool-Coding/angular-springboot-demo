package com.hotel.entity;

import com.hotel.constant.RoomStatus;
import com.hotel.constant.RoomType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author guangyong.yang
 * @date 2019-01-15
 * 房间
 */
@Getter
@Setter
@Entity
@Table(name = "room")
public class Room {
    /**
     * id
     */
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    /**
     * 房间号
     */
    @Column(length = 20,unique = true)
    private String roomNo;


    /**
     * 房间类型
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomType type;


    /**
     * 入住日期
     */
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date checkInDate;


    /**
     * 退房日期
     */
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date checkOutDate;


    /**
     * 客房状态
     */
    @Column
    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    /**
     * 床
     */

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "room")
    private List<Bed> beds;


    /**
     * 顾客
     */
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "room_customer",inverseJoinColumns = @JoinColumn(name = "customer_id"),joinColumns = @JoinColumn(name = "room_id"))
    private List<Customer> customers;


    /**
     * 房价
     */
    @OneToMany(mappedBy = "room",cascade = CascadeType.ALL)
    private List<Charge> charges;

    /**
     * 收入记录
     */
    @OneToMany(mappedBy = "room",cascade = CascadeType.ALL)
    private List<Income> incomes;

}
