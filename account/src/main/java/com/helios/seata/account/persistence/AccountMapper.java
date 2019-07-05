package com.helios.seata.account.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {

    @Select("select * from account_tbl where user_id =#{userId}")
    Account findByUserId(@Param("userId") String userId);

}