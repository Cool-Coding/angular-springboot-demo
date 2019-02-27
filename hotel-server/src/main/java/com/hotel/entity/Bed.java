package com.hotel.entity;

import com.hotel.constant.BedType;
import lombok.*;

import javax.persistence.*;

/**
 * @author guangyong.yang
 * @date 2019-01-16
 * @description  床
 */
@Data
@Entity
@Table(name = "bed")
public class Bed {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    /**
     * 床名
     */
    @Column(length = 20)
    private String name;


    /**
     * 型号
     */
    @Column(length = 30)
    private String size;


    /**
     * 所属房间
     */
    @ManyToOne(optional = false)
    private Room room;


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BedType type;

    public Bed() {
    }
}
