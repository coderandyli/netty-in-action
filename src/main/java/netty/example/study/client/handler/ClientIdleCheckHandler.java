package netty.example.study.client.handler;

import io.netty.handler.timeout.IdleStateHandler;

/**
 * @description: 客户端空闲检测
 * @author: lizhenzhen
 * @date: 2021-05-17 10:51
 **/
public class ClientIdleCheckHandler extends IdleStateHandler {
    public ClientIdleCheckHandler() {
        super(0, 5, 0);
    }
}
