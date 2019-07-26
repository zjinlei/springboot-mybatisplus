package com.helios.seata.storage.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StorageMapper extends BaseMapper<Storage> {

    @ResultType(value = Storage.class)
    @Select("select id, commodity_code commodityCode, count from storage_tbl where commodity_code =#{commodityCode}")
    Storage findByCommodityCode(@Param("commodityCode") String commodityCode);

    void insertBatch(List<Storage> records);

    int updateBatch(@Param("list") List<Long> ids,@Param("commodityCode") String commodityCode);
}