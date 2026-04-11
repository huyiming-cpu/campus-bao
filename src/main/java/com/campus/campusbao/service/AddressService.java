package com.campus.campusbao.service;

import com.campus.campusbao.entity.Address;
import java.util.List;

public interface AddressService {
    List<Address> listByUserId(Integer userId);
    void save(Address address);
    void removeById(Integer id);
    void setDefault(Integer id, Integer userId);
}