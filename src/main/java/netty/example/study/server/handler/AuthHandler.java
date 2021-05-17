package netty.example.study.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import netty.example.study.common.Operation;
import netty.example.study.common.RequestMessage;
import netty.example.study.common.auth.AuthOperation;
import netty.example.study.common.auth.AuthOperationResult;

/**
 * @description: 授权Handler
 * @author: lizhenzhen
 * @date: 2021-05-17 13:35
 **/
@Slf4j
@ChannelHandler.Sharable
public class AuthHandler extends SimpleChannelInboundHandler<RequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage msg) {
        try {
            Operation operation = msg.getMessageBody();
            if (operation instanceof AuthOperation) {
                AuthOperation authOperation = (AuthOperation) operation;
                AuthOperationResult authOperationResult = authOperation.execute();
                if (authOperationResult.isPassAuth()) {
                    log.info("pass auth.");
                } else {
                    log.info("fail to auth");
                    ctx.close();
                }
            } else {
                log.error("expect first msg is auth");
                ctx.close();
            }
        } catch (Exception e) {
            log.error("exception happend for: " + e.getMessage(), e);
            ctx.close();
        } finally {
            // 授权handler执行完后就移除
            ctx.pipeline().remove(this);
        }
    }
}
