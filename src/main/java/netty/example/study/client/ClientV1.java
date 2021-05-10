package netty.example.study.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import netty.example.study.client.codec.*;
import netty.example.study.common.order.OrderOperation;

import java.util.concurrent.ExecutionException;

/**
 * @description: 客户端入口
 *  - 请求入参抽取，新增一个{@link OperationToRequestMessageEncoder}
 * @author: lizhenzhen
 * @date: 2021-05-08 15:59
 **/
@Slf4j
public class ClientV1 {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Bootstrap b = new Bootstrap();
        b.channel(NioSocketChannel.class)
                .group(new NioEventLoopGroup());

        b.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                //编解码
                pipeline.addLast(new OrderFrameDecode()); // 入站
                pipeline.addLast(new OrderFrameEncode()); // 出站
                pipeline.addLast(new OrderProtocolEncode()); // 出站
                pipeline.addLast(new OrderProtocolDecode()); // 入站

                // 请求参数转换
                pipeline.addLast(new OperationToRequestMessageEncoder()); // 出站
                pipeline.addLast(new LoggingHandler(LogLevel.INFO)); // 日志
            }
        });

        // 绑定端口，启动
        ChannelFuture f = b.connect("127.0.0.1", 8090).sync();

        // 发送一个请求
        OrderOperation operation = new OrderOperation(1001, "tudou");
        f.channel().writeAndFlush(operation);

        f.channel().closeFuture().sync().get();
    }
}
