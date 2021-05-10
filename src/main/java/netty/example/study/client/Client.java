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
import netty.example.study.client.codec.OrderFrameDecode;
import netty.example.study.client.codec.OrderFrameEncode;
import netty.example.study.client.codec.OrderProtocolDecode;
import netty.example.study.client.codec.OrderProtocolEncode;
import netty.example.study.common.RequestMessage;
import netty.example.study.common.order.OrderOperation;
import netty.example.study.util.IdUtil;

import java.util.concurrent.ExecutionException;

/**
 * @description: 客户端入口
 * @author: lizhenzhen
 * @date: 2021-05-08 15:59
 **/
@Slf4j
public class Client {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Bootstrap b = new Bootstrap();
        b.channel(NioSocketChannel.class)
                .group(new NioEventLoopGroup());

        // 客户端，先执行出站（倒序）
        b.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new OrderFrameDecode()); // 入站
                pipeline.addLast(new OrderFrameEncode()); // 出站
                pipeline.addLast(new OrderProtocolEncode()); // 出站
                pipeline.addLast(new OrderProtocolDecode()); // 入站

                pipeline.addLast(new LoggingHandler(LogLevel.INFO));
            }
        });

        // 绑定端口，启动
        ChannelFuture f = b.connect("127.0.0.1", 8090).sync();

        // 发送一个请求
        RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), new OrderOperation(1001, "tudou"));

        // 发送一万次，内存泄露检测.
        for (int i = 0; i < 100000; i++) {
            f.channel().writeAndFlush(requestMessage);
        }
        f.channel().closeFuture().sync().get();
    }
}
