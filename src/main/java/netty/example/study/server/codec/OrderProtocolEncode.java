package netty.example.study.server.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import netty.example.study.common.ResponseMessage;

import java.util.List;

/**
 * @description: 【二次编码】ResponseMessage -> ByteBuf（转化）
 * @author: lizhenzhen
 * @date: 2021-05-08 14:49
 **/
public class OrderProtocolEncode extends MessageToMessageEncoder<ResponseMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ResponseMessage responseMessage, List<Object> out) throws Exception {
        // 创建bytebuf，不能使用ByteBufAllocator.DEFAULT.buffer(), 因为堆外内存与堆内切换是就不会生效
        ByteBuf buffer = ctx.alloc().buffer();

        responseMessage.encode(buffer);

        // 将转化后的，传递出去
        out.add(buffer);
    }
}
