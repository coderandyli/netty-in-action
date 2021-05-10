package netty.example.study.server.handler;

import io.netty.buffer.ByteBuf;
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
        // 【内存泄露检测】创建一个buffer, 不释放
        ByteBuf buffer = ctx.alloc().buffer();

        Operation operation = requestMessage.getMessageBody();
        OperationResult operationResult = operation.execute();

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessageHeader(requestMessage.getMessageHeader());
        responseMessage.setMessageBody(operationResult);

        if (ctx.channel().isActive() && ctx.channel().isWritable()){
            // 【不能使用】ctx.write()方法仅仅是将信息加到队列里面，并没有把信息真正的发送出去
            // 【不能使用】ctx.channel().writeAndFlush() // 该方法会在整个pipeline都走一遍
            // 寻找下一个合适的channelHandler
            ctx.writeAndFlush(responseMessage);
        }else {
            log.error("not writable now, message dropped");
        }
    }
}
