package com.campus.campusbao.controller;

import com.campus.campusbao.common.Result;
import com.campus.campusbao.entity.Address;
import com.campus.campusbao.service.AddressService;
import com.campus.campusbao.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    // 获取当前用户的所有地址
    @GetMapping("/list")
    public Result getAddressList(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return Result.error("未登录");
        List<Address> list = addressService.listByUserId(loginUser.getId());
        return Result.success(list);
    }

    // 新增/修改地址
    @PostMapping("/save")
    public Result saveAddress(@RequestBody Address address, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return Result.error("未登录");
        address.setUserId(loginUser.getId());
        addressService.save(address);
        return Result.success("保存成功");
    }

    // 删除地址
    @DeleteMapping("/delete/{id}")
    public Result deleteAddress(@PathVariable Integer id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return Result.error("未登录");
        addressService.removeById(id);
        return Result.success("删除成功");
    }

    // 设置默认地址
    @PostMapping("/setDefault/{id}")
    public Result setDefault(@PathVariable Integer id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return Result.error("未登录");
        addressService.setDefault(id, loginUser.getId());
        return Result.success("设置成功");
    }
}