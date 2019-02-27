package com.hotel.service.impl;

import com.hotel.entity.Customer;
import com.hotel.repository.CustomerRepository;
import com.hotel.service.CustomerService;
import com.hotel.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author guangyong.yang
 * @date 2019-01-21
 * @description
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<Customer> findCustomerByIdCard(String idCard) {
        return customerRepository.findCustomersByIdCard(idCard);
    }

    @Override
    public List<Customer> findCustomers(String name, String phoneNo, String idCard) {
        if (Objects.isNull(name)) {
            name = "";
        }

        if (Objects.isNull(phoneNo)) {
            phoneNo = "";
        }

        if (Objects.isNull(idCard)) {
            idCard = "";
        }
        return customerRepository.findCustomers(name,phoneNo,idCard);
    }

    @Override
    public Customer update(Customer customer) {
        Customer currentInstance = customerRepository.findOne(customer.getId());
        //支持部分更新
        String[] nullPropertyNames = BeanUtils.getNullPropertyNames(customer);
        org.springframework.beans.BeanUtils.copyProperties(customer, currentInstance, nullPropertyNames);
        return customerRepository.save(currentInstance);
    }

    @Override
    public void delete(Integer id) {
        customerRepository.delete(id);
    }


    @Override
    public Customer findOne(Integer id) {
        return customerRepository.findOne(id);
    }
}
