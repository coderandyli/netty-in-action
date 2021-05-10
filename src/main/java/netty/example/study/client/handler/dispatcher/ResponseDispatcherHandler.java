package netty.example.study.client.handler.dispatcher;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty.example.study.common.ResponseMessage;

/**
 * @description: 响应(response)分发处理器
 * @author: lizhenzhen
 * @date: 2021-05-10 10:29
 *
 * 推荐继承SimpleChannelInboundHandler，他会自动释放内存{@link SimpleChannelInboundHandler#channelRead(ChannelHandlerContext, Object)}
 **/
public class ResponseDispatcherHandler extends SimpleChannelInboundHandler<ResponseMessage> {
    private RequestPendingCenter requestPendingCenter;

    public ResponseDispatcherHandler(RequestPendingCenter requestPendingCenter) {
        this.requestPendingCenter = requestPendingCenter;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseMessage responseMessage) throws Exception {
        // 设置响应结果
        requestPendingCenter.set(responseMessage.getMessageHeader().getStreamId(), responseMessage.getMessageBody());
    }
}
