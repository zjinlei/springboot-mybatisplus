package com.helios.seata.storage.service;

import com.helios.seata.storage.persistence.Storage;
import com.helios.seata.storage.persistence.StorageMapper;
import io.seata.spring.annotation.GlobalLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StorageService {


    @Autowired
    private StorageMapper storageMapper;

    @Transactional
    public void deduct(String commodityCode, int count) {
        Storage storage = storageMapper.findByCommodityCode(commodityCode);
        storage.setCount(storage.getCount() - count);
        storageMapper.updateById(storage);
    }

    @GlobalLock
    public Storage get(Long id) {
        Storage storage = storageMapper.selectById(id);
        storage.setCount(storage.getCount() -10);
        storageMapper.updateById(storage);
        return storageMapper.selectById(id);
    }
}
