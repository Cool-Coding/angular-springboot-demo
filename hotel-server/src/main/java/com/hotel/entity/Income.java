package com.hotel.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author guangyong.yang
 * @date 2019-01-21
 * @description
 */
@Data
@Entity
@Table(name = "income")
public class Income implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Room    room;

    @Column
    private Float   incoming;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date    logoutDate;
}
