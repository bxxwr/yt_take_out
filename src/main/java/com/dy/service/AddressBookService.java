package com.dy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dy.common.R;
import com.dy.entity.AddressBook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


public interface AddressBookService extends IService<AddressBook> {
    /**
     * 查询指定用户全部地址
     * @param addressBook
     * @return
     */
    R<List<AddressBook>> findAddressByUserId(AddressBook addressBook);

    /**
     * 新增收获地址
     * @param addressBook
     */
    R<AddressBook> saveAddress(AddressBook addressBook);

    /**
     * 修改收获地址
     * @param addressBook
     */
    void UpdateAddressByUserId(AddressBook addressBook);

    /**
     * 设置默认地址
     * @param addressBook
     */
    void setDefaultAddress(AddressBook addressBook);
}
