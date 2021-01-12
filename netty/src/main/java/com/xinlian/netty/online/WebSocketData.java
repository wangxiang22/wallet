package com.xinlian.netty.online;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class WebSocketData {
    private long ts;
    private String ch;
    private Object data;

}
