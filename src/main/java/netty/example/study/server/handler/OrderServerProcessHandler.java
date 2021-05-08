package netty.example.study.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import netty.example.study.common.Operation;
import netty.example.study.common.OperationResult;
import netty.example.study.common.RequestMessage;
import netty.example.study.common.ResponseMessage;

/**
 * @description: 对RequestMessage数据进行处理
 * @author: lizhenzhen
 * @date: 2021-05-08 15:25
 **/
@Slf4j
public class OrderServerProcessHandler extends SimpleChannelInboundHandler<RequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage requestMessage) throws Exception {
        Operation operation = requestMessage.getMessageBody();
        OperationResult operationResult = operation.execute();

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessageHeader(requestMessage.getMessageHeader());
        responseMessage.setMessageBody(operationResult);

        if (ctx.channel().isActive() && ctx.channel().isWritable()){
            ctx.writeAndFlush(responseMessage);
        }else {
            log.error("not writable now, message dropped");
        }
    }
}
