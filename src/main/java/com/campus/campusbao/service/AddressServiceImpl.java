package com.campus.campusbao.service;

import com.campus.campusbao.entity.Address;
import com.campus.campusbao.repository.AddressRepository;
import com.campus.campusbao.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public List<Address> listByUserId(Integer userId) {
        return addressRepository.findByUserId(userId);
    }

    @Override
    public void save(Address address) {
        addressRepository.save(address);
    }

    @Override
    public void removeById(Integer id) {
        addressRepository.deleteById(id);
    }

    @Override
    public void setDefault(Integer id, Integer userId) {
        // 先把该用户所有地址设为非默认
        List<Address> list = addressRepository.findByUserId(userId);
        for (Address addr : list) {
            addr.setIsDefault(0);
            addressRepository.save(addr);
        }
        // 再把当前地址设为默认
        Address addr = addressRepository.findById(id).orElse(null);
        if (addr != null) {
            addr.setIsDefault(1);
            addressRepository.save(addr);
        }
    }
}