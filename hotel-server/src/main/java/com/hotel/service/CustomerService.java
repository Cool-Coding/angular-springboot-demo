package com.hotel.service;

import com.hotel.entity.Customer;

import java.util.List;

/**
 * @author guangyong.yang
 * @date 2019-01-21
 * @description
 */
public interface CustomerService {
    /**
     * 根据身份证号查询客人信息
     * @param idCard    身份证号
     * @return          客人
     */
    List<Customer> findCustomerByIdCard(String idCard);


    /**
     * 搜索客人信息
     * @param name       姓名
     * @param phoneNo    手机号
     * @param idCard     身份证
     * @return           客人信息
     */
    List<Customer> findCustomers(String name,String phoneNo,String idCard);


    /**
     * 更新客人信息
     * @param customer   客人新信息
     * @return           更新后客人信息
     */
    Customer update(Customer customer);

    /**
     * 删除客人信息
     * @param id    客人ID
     */
    void     delete(Integer id);

    /**
     * 查找客人信息
     * @param id    客人ID
     * @return      客人信息
     */
    Customer findOne(Integer id);
}
