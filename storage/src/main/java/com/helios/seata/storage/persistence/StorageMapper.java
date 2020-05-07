package com.helios.seata.storage.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface StorageMapper {

    Storage selectById(@Param("id") Long id);

    @ResultType(value = Storage.class)
    @Select("select id, commodity_code commodityCode, count from storage_tbl where commodity_code =#{commodityCode}")
    Storage findByCommodityCode(@Param("commodityCode") String commodityCode);

    @Delete("delete tb from storage_tbl tb where tb.commodity_code =#{commodityCode}")
    void deltByCommodityCode(@Param("commodityCode") String commodityCode);

    void insert(Storage record);

    void insertBatch(List<Storage> records);

    @Update("update storage_tbl tb set count=1 where tb.commodity_code =#{commodityCode}")
    void updateByCommodityCode(@Param("commodityCode") String commodityCode);

    int updateBatch(@Param("list") List<Long> ids, @Param("commodityCode") String commodityCode);

    int updateById(Storage record);
}