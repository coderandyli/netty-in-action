package netty.example.study.server.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import netty.example.study.common.RequestMessage;

import java.util.List;

/**
 * @description: 【二次解码】ByteBuf -> RequestMessage（转化）
 * @author: lizhenzhen
 * @date: 2021-05-08 14:49
 **/
public class OrderProtocolDecode extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.decode(byteBuf);

        // 将转化后的，传递出去
        out.add(requestMessage);
    }
}
