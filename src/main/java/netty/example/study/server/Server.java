package netty.example.study.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import netty.example.study.server.codec.OrderFrameDecode;
import netty.example.study.server.codec.OrderFrameEncode;
import netty.example.study.server.codec.OrderProtocolDecode;
import netty.example.study.server.codec.OrderProtocolEncode;
import netty.example.study.server.handler.OrderServerProcessHandler;

import java.util.concurrent.ExecutionException;

/**
 * @description: 服务端入口
 * @author: lizhenzhen
 * @date: 2021-05-08 15:59
 **/
@Slf4j
public class Server {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ServerBootstrap b = new ServerBootstrap();
        b.channel(NioServerSocketChannel.class)
//                .option(NioChannelOption.SO_BACKLOG, 1024)
//                .childOption(NioChannelOption.TCP_NODELAY, true)
                .handler(new LoggingHandler(LogLevel.INFO)) // 为NioServerSocketChannel添加 ChannelHandler
                .group(new NioEventLoopGroup());

        b.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new OrderFrameDecode()); // 入站
                pipeline.addLast(new OrderFrameEncode()); // 出站
                pipeline.addLast(new OrderProtocolEncode()); // 出站
                pipeline.addLast(new OrderProtocolDecode()); // 入站

                pipeline.addLast(new LoggingHandler(LogLevel.INFO));

                pipeline.addLast(new OrderServerProcessHandler()); // 入站

            }
        });

        // 绑定端口，启动
        ChannelFuture f = b.bind(8090).sync();
        f.channel().closeFuture().sync().get();
    }
}
