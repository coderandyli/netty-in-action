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
        // 【内存泄露检测】创建一个buffer, 不释放
        // ByteBuf buffer = ctx.alloc().buffer();

        Operation operation = requestMessage.getMessageBody();
        OperationResult operationResult = operation.execute();

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessageHeader(requestMessage.getMessageHeader());
        responseMessage.setMessageBody(operationResult);

        if (ctx.channel().isActive() && ctx.channel().isWritable()){

            /**
             * write 与 flush方法
             *
             *
             */

            // 【不能使用】ctx.write()方法仅仅是将信息加到队列里面，并没有把信息真正的发送出去
            // 【不能使用】ctx.channel().writeAndFlush() // 该方法会在整个pipeline都走一遍
            // 寻找下一个合适的channelHandler

            /**
             * 【延迟与吞吐量的抉择】ctx.writeAndFlush 属于"加急式"的(类似与快递一样，进了仓库立刻发送) 每次写数据之后都会flush，吞吐量不是很高。
             *  因此可以考虑牺牲一些延迟，增大吞吐量
             *
             * 方案一：可以参考官方示例：【echo】，channelRead方法中wirte，channelReadComplete方法中flush，
             * 但是这种方案不适合在异步业务线程中，因为channelRead 中的业务处理结果的 write 很可能发生在 channelReadComplete 之后
             *
             * 方案二：使用 FlushConsolidationHandler
             */
            ctx.writeAndFlush(responseMessage);
        }else {
            log.error("not writable now, message dropped");
        }
    }
}
