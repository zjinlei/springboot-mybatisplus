package com.helios.seata.storage.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StorageMapper extends BaseMapper<Storage> {

    @ResultType(value = Storage.class)
    @Select("select id, commodity_code commodityCode, count from storage_tbl where commodity_code =#{commodityCode}")
    Storage findByCommodityCode(@Param("commodityCode") String commodityCode);

    void insertBatch(List<Storage> records);
}