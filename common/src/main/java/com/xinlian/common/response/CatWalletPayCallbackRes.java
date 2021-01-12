package com.xinlian.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatWalletPayCallbackRes implements Serializable {
    private static final long serialVersionUID = 1L;

    private String data;

    private String sign;

}
