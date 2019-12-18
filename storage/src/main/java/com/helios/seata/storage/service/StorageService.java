package com.helios.seata.storage.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.helios.seata.storage.persistence.Storage;
import com.helios.seata.storage.persistence.StorageMapper;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalLock;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

@Service
public class StorageService extends ServiceImpl<StorageMapper, Storage> {


    @Autowired
    private StorageMapper storageMapper;
    @Autowired
    private DataSource dataSource;

    @GlobalTransactional
    public void deduct(String commodityCode, int count) {
        System.out.println("storage XID " + RootContext.getXID());
        Storage storage = storageMapper.findByCommodityCode(commodityCode);
        storage.setCount(storage.getCount() - count);
        storageMapper.updateBatch(Arrays.asList(storage.getId()), storage.getCommodityCode());
        //this.updateById(storage);
    }

    @GlobalLock
    public Storage get(Long id) {
        Storage storage = storageMapper.selectById(id);
        storage.setCount(storage.getCount() - 10);
        storageMapper.updateById(storage);
        return storageMapper.selectById(id);
    }

    @GlobalTransactional(timeoutMills = 60000000)
    public void batchInsert() {
        Storage storage1 = new Storage();
        storage1.setId(null);
        storage1.setCommodityCode("9001");
        storage1.setCount(11);
        Storage storage2 = new Storage();
        storage2.setId(null);
        storage2.setCommodityCode("9002");
        storage2.setCount(22);
        Storage storage3 = new Storage();
        storage3.setId(null);
        storage3.setCommodityCode("9003");
        storage3.setCount(33);
        storageMapper.insertBatch(Arrays.asList(storage1,storage2,storage3));
        System.out.println(1 / 0);
    }

    @GlobalTransactional
    public void batchInsertOracle() throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(true);
            String sql = "insert into storage_tbl (id,commodity_code,count) values(?,?,?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, 11);
            preparedStatement.setString(2, "10001");
            preparedStatement.setInt(3, 10);
            preparedStatement.addBatch();
            preparedStatement.setLong(1, 12);
            preparedStatement.setString(2, "20002");
            preparedStatement.setInt(3, 20);
            preparedStatement.addBatch();
            preparedStatement.setLong(1, 13);
            preparedStatement.setString(2, "30003");
            preparedStatement.setInt(3, 30);
            preparedStatement.addBatch();
            try{
                preparedStatement.executeBatch();
            }catch (Exception e){
                System.out.println("-----------123----------------------");
            }
           // connection.commit();
            Storage storage1 = new Storage();
            storage1.setId(null);
            storage1.setCommodityCode("9001");
            storage1.setCount(11);
            Storage storage2 = new Storage();
            storage2.setId(null);
            storage2.setCommodityCode("9002");
            storage2.setCount(22);
            Storage storage3 = new Storage();
            storage3.setId(null);
            storage3.setCommodityCode("9003");
            storage3.setCount(33);
            storageMapper.insertBatch(Arrays.asList(storage1,storage2,storage3));
            System.out.println(1 / 0);
        } catch (Exception e) {
            throw e;
        } finally {
            connection.close();
            preparedStatement.close();
        }
    }
    @GlobalTransactional
    public void batchUpdateMulityCond() throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            String sql = "update storage_tbl set count = ?" +
                "    where id = ? and commodity_code = 2001";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, 10);
            preparedStatement.setLong(2, 1);
           /* preparedStatement.setString(3, "2001");
            preparedStatement.addBatch();
            preparedStatement.setInt(1, 20);
            preparedStatement.setLong(2, 2);
            preparedStatement.setString(3, "2002");
            preparedStatement.addBatch();
            preparedStatement.setInt(1, 30);
            preparedStatement.setLong(2, 3);
            preparedStatement.setString(3, "2003");
            preparedStatement.addBatch();*/
            preparedStatement.execute();
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
            String sql = "delete from storage_tbl where  count = ? and commodity_code = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, 11);
            preparedStatement.setString(2, "2001");
            preparedStatement.addBatch();
            preparedStatement.setInt(1, 22);
            preparedStatement.setString(2, "2002");
            preparedStatement.addBatch();
            preparedStatement.setInt(1, 33);
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
