package com.hotel.constant;

import com.mysql.jdbc.StringUtils;

/**
 * @author guangyong.yang
 * @date 2019-01-16
 * @description  房间类型
 */
public enum RoomType {
    /**
     * 小时房
     */
    HOUR("小时房"),

    /**
     * 天房
     */
    DAY("天房"),

    /**
     * 包月房
     */
    MONTH("月房");

    private String name;
    RoomType(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public static RoomType parse(String value) {
        if (StringUtils.isNullOrEmpty(value)) {
            return null;
        }

        switch (value){
            case "小时房":
                return HOUR;
            case "天房":
                return DAY;
            case "月房":
                return MONTH;
                default:
                    throw new IllegalArgumentException("客房类型不存在");
        }
    }
}
