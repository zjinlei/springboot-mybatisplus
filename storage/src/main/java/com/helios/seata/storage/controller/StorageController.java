package com.helios.seata.storage.controller;

import com.helios.seata.storage.persistence.Storage;
import com.helios.seata.storage.service.StorageService;
import io.seata.core.context.RootContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RequestMapping("/api/storage")
@RestController
public class StorageController {

    @Autowired
    StorageService storageService;

    @GetMapping(value = "/deduct")
    public void deduct(@RequestParam String commodityCode, @RequestParam Integer count) throws SQLException {
        System.out.println("storage XID " + RootContext.getXID());
        storageService.deduct(commodityCode, count);
    }

    @GetMapping(value = "/get/{id}")
    public Storage getById(@PathVariable("id") Long id) {
        return storageService.get(id);
    }

    @GetMapping(value = "/batch/insert")
    public void batchInsert() {
        storageService.batchInsert();
    }

    @GetMapping(value = "/batch/insert/oracle")
    public void batchInsertOracle() throws SQLException {
        storageService.batchInsertOracle();
    }

    @GetMapping(value = "/batch/update")
    public void batchUpdateMulityCond() {
        try {
            storageService.batchUpdateMulityCond();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/batch/delete")
    public void batchDeleteMulityCond() {
        try {
            storageService.batchDeleteMulityCond();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
