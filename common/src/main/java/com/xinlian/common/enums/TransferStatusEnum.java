package com.xinlian.common.enums;

import lombok.Getter;

@Getter
public enum TransferStatusEnum {


    TRANSFER_ALL(1,"全部开启"),
    TRANSFER_INSTATION(2,"只支持同节点"),
    TRANSFER_EXTERNAL(3,"只支持不同节点"),
    TRANSFER_NOT(4,"全部不开启"),
    ;

    private int code;
    private String desc;

    TransferStatusEnum(int code ,String desc){
        this.code = code;
        this.desc = desc;
    }
}
