package com.hotel.constant;

import com.mysql.jdbc.StringUtils;

/**
 * @author guangyong.yang
 * @date 2019-01-18
 * @description  床型
 */
public enum  BedType {
    /**
     * 三人床
     */
    THREE_PEOPLE("三人床"),

    /**
     * 双人床
     */
    TWO_PEOPLE("双人床"),

    /**
     * 单人床
     */
    ONE_PEOPLE("单人床");


    private String name;
    BedType(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public static BedType parse(String name){
        if (StringUtils.isNullOrEmpty(name)) {
            return null;
        }
        switch (name) {
            case "三人床":
                return THREE_PEOPLE;
            case "双人床":
                return TWO_PEOPLE;
            case "单人床":
                return ONE_PEOPLE;
                default:
                    throw new IllegalArgumentException("床类型不正确");
        }
    }
}
