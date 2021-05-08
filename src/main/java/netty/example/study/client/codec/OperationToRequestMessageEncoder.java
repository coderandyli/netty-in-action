package netty.example.study.client.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import netty.example.study.common.Operation;
import netty.example.study.common.RequestMessage;
import netty.example.study.util.IdUtil;

import java.util.List;

/**
 * @description: Operation编码成Operation
 * @author: lizhenzhen
 * @date: 2021-05-08 18:01
 **/
public class OperationToRequestMessageEncoder extends MessageToMessageEncoder<Operation> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Operation operation, List<Object> out) throws Exception {
        RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), operation);
        ctx.writeAndFlush(requestMessage);

        out.add(requestMessage);
    }
}
