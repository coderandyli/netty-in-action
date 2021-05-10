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
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import netty.example.study.server.codec.OrderFrameDecode;
import netty.example.study.server.codec.OrderFrameEncode;
import netty.example.study.server.codec.OrderProtocolDecode;
import netty.example.study.server.codec.OrderProtocolEncode;
import netty.example.study.server.handler.MetricsHandler;
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
//                .option(NioChannelOption.SO_BACKLOG, 1024) // 最大连接等待数量
//                .childOption(NioChannelOption.TCP_NODELAY, true)
                .handler(new LoggingHandler(LogLevel.INFO)); // 为NioServerSocketChannel添加 ChannelHandler

        // 线程
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("boss"));
        NioEventLoopGroup workGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("worker"));

        b.group(bossGroup, workGroup);

        //metrics
        MetricsHandler metricsHandler = new MetricsHandler();

        // channel顺序，入站：自上而下；出站：自下而上，对于服务端而言，先执行入站操作，而对于客户端而言，先执行出站操作。
        b.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                pipeline.addLast("metricHandler", metricsHandler); // 可共享的

                pipeline.addLast("frameDecoder", new OrderFrameDecode()); // 入站
                pipeline.addLast("frameEncode", new OrderFrameEncode()); // 出站
                pipeline.addLast("protocolEncode", new OrderProtocolEncode()); // 出站
                pipeline.addLast("protocolDecode", new OrderProtocolDecode()); // 入站

                pipeline.addLast("loggingHandler", new LoggingHandler(LogLevel.INFO));

                pipeline.addLast("orderServerProcessHandler", new OrderServerProcessHandler()); // 入站
            }
        });

        // 绑定端口，启动
        ChannelFuture f = b.bind(8090).sync();
        f.channel().closeFuture().sync().get();
    }
}
