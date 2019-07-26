package com.helios.seata.storage.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.helios.seata.storage.persistence.Storage;
import com.helios.seata.storage.persistence.StorageMapper;
import io.seata.spring.annotation.GlobalLock;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service
public class StorageService extends ServiceImpl<StorageMapper, Storage> {


    @Autowired
    private StorageMapper storageMapper;
    @Autowired
    private DataSource dataSource;

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
    @GlobalTransactional
    public void batchUpdateMulityCond() throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            String sql = "update storage_tbl set count = ?" +
                    "    where id = ? and commodity_code = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, 100);
            preparedStatement.setLong(2, 1);
            preparedStatement.setString(3, "2001");
            preparedStatement.addBatch();
            preparedStatement.setInt(1, 200);
            preparedStatement.setLong(2, 2);
            preparedStatement.setString(3, "2002");
            preparedStatement.addBatch();
            preparedStatement.setInt(1, 300);
            preparedStatement.setLong(2, 3);
            preparedStatement.setString(3, "2003");
            preparedStatement.addBatch();
            preparedStatement.executeBatch();
            connection.commit();
            System.out.println(1 / 0);
        } catch (Exception e) {
            throw e;
        } finally {
            connection.close();
            preparedStatement.close();
        }
    }
    @GlobalTransactional
    public void batchDeleteMulityCond() throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            String sql = "delete from storage_tbl where id = ? and commodity_code = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, 1);
            preparedStatement.setString(2, "2001");
            preparedStatement.addBatch();
            preparedStatement.setLong(1, 2);
            preparedStatement.setString(2, "2002");
            preparedStatement.addBatch();
            preparedStatement.setLong(1, 3);
            preparedStatement.setString(2, "2003");
            preparedStatement.addBatch();
            preparedStatement.executeBatch();
            connection.commit();
            System.out.println(1 / 0);
        } catch (Exception e) {
            throw e;
        } finally {
            connection.close();
            preparedStatement.close();
        }
    }
}
