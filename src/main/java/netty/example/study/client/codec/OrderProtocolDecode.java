package netty.example.study.client.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import netty.example.study.common.ResponseMessage;

import java.util.List;

/**
 * @description: 【二次解码】ByteBuf -> ResponseMessage（转化）
 * @author: lizhenzhen
 * @date: 2021-05-08 14:49
 **/
public class OrderProtocolDecode extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.decode(byteBuf);

        // 将转化后的，传递出去
        out.add(responseMessage);
    }
}
