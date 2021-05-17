package netty.example.study.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import netty.example.study.common.RequestMessage;
import netty.example.study.common.keepalive.KeepaliveOperation;
import netty.example.study.util.IdUtil;

/**
 * @description: keepalive handler
 * @author: lizhenzhen
 * @date: 2021-05-17 10:53
 **/
@Slf4j
public class KeepaliveHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT) {
            log.info("write idle happen. so need to send keepalive to keep connection not closed by server.");
            KeepaliveOperation keepaliveOperation = new KeepaliveOperation();
            RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), keepaliveOperation);
            ctx.writeAndFlush(requestMessage);
        }

        super.userEventTriggered(ctx, evt);
    }
}
