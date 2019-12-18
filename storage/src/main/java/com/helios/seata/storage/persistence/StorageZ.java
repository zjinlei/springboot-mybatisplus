package com.helios.seata.storage.persistence;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@TableName(value = "storage_tblz")
public class StorageZ {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;

    private Integer count;
}