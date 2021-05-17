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
import netty.example.study.client.handler.ClientIdleCheckHandler;
import netty.example.study.client.handler.KeepaliveHandler;
import netty.example.study.common.RequestMessage;
import netty.example.study.common.auth.AuthOperation;
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

        KeepaliveHandler keepaliveHandler = new KeepaliveHandler();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.INFO);

        // 客户端，先执行出站（倒序）
        b.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                pipeline.addLast(new ClientIdleCheckHandler()); // idle check

                pipeline.addLast(new OrderFrameDecode()); // 入站
                pipeline.addLast(new OrderFrameEncode()); // 出站
                pipeline.addLast(new OrderProtocolEncode()); // 出站
                pipeline.addLast(new OrderProtocolDecode()); // 入站

                pipeline.addLast(loggingHandler);

                pipeline.addLast(keepaliveHandler); // keepalive handler
            }
        });

        // 绑定端口，启动
        ChannelFuture f = b.connect("127.0.0.1", 8090).sync();

        // 【自定义授权】
        String userName = "admin"; // 可以授权成功
        String userName1 = "admin1"; // 不会授权成功
        AuthOperation authOperation = new AuthOperation(userName1, "password");
        f.channel().writeAndFlush(new RequestMessage(IdUtil.nextId(), authOperation));

        // 发送一个请求
        RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), new OrderOperation(1001, "tudou"));

        // 【内存泄露检查】发送一万次.
//        for (int i = 0; i < 100000; i++) {
//            f.channel().writeAndFlush(requestMessage);
//        }

        for (int i = 0; i < 20; i++) {
            f.channel().writeAndFlush(requestMessage);
        }

        f.channel().closeFuture().sync().get();
    }
}
