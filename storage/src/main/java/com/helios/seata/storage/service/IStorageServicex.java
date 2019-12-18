package com.helios.seata.storage.service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.helios.seata.storage.persistence.Storage;
import com.helios.seata.storage.persistence.StorageMapper;
import io.seata.spring.annotation.GlobalLock;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public interface IStorageServicex {

}
