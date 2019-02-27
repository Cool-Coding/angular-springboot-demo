package com.hotel.service.impl;

import com.hotel.repository.BedRepository;
import com.hotel.service.BedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author guangyong.yang
 * @date 2019-01-25
 * @description
 */
@Service
public class BedServiceImpl implements BedService {
    @Autowired
    private BedRepository bedRepository;

    @Override
    public void delete(Integer id) {
        bedRepository.delete(id);
    }
}
