package com.xinlian.common.request;

import lombok.Data;

import java.util.List;
@Data
public class TakeOfferReq {
    private List<Long> uids;
}
