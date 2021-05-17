package netty.example.study.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import io.netty.handler.ipfilter.RuleBasedIpFilter;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;
import lombok.extern.slf4j.Slf4j;
import netty.example.study.server.codec.OrderFrameDecode;
import netty.example.study.server.codec.OrderFrameEncode;
import netty.example.study.server.codec.OrderProtocolDecode;
import netty.example.study.server.codec.OrderProtocolEncode;
import netty.example.study.server.handler.AuthHandler;
import netty.example.study.server.handler.MetricsHandler;
import netty.example.study.server.handler.OrderServerProcessHandler;
import netty.example.study.server.handler.ServerIdleCheckHandler;

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
        UnorderedThreadPoolEventExecutor businessGroup = new UnorderedThreadPoolEventExecutor(10, new DefaultThreadFactory("business")); // 业务处理线程池
        // NioEventLoopGroup businessGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("business")); // 不建议使用的NioEventLoopGroup做业务线程池，因为默认情况下handler始终会在池中的某一个线程中执行(handle绑定group(线程池)中唯一的executor)

        b.group(bossGroup, workGroup);

        //metrics
        MetricsHandler metricsHandler = new MetricsHandler();

        // trafficShaping 流量整形 （读写流控分别为100M）
        GlobalTrafficShapingHandler globalTrafficShapingHandler = new GlobalTrafficShapingHandler(new NioEventLoopGroup(), 100 * 1024 * 1024, 100 * 1024 * 1024);

        //ipfilter
//        IpSubnetFilterRule ipSubnetFilterRule = new IpSubnetFilterRule("127.0.0.1", 8, IpFilterRuleType.REJECT);
        IpSubnetFilterRule ipSubnetFilterRule = new IpSubnetFilterRule("127.1.1.1", 16, IpFilterRuleType.REJECT);
        RuleBasedIpFilter ruleBasedIpFilter = new RuleBasedIpFilter(ipSubnetFilterRule);

        //auth
        AuthHandler authHandler = new AuthHandler();

        // channel顺序，入站：自上而下；出站：自下而上，对于服务端而言，先执行入站操作，而对于客户端而言，先执行出站操作。
        b.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                pipeline.addLast("loggingHandler", new LoggingHandler(LogLevel.DEBUG));

                pipeline.addLast("ipFilter", ruleBasedIpFilter);

                pipeline.addLast("tsHandler", globalTrafficShapingHandler); // 流量整形

                pipeline.addLast("metricHandler", metricsHandler); // 可共享的

                pipeline.addLast("idleHandler", new ServerIdleCheckHandler()); // 空闲检测

                pipeline.addLast("frameDecoder", new OrderFrameDecode()); // 入站
                pipeline.addLast("frameEncode", new OrderFrameEncode()); // 出站
                pipeline.addLast("protocolEncode", new OrderProtocolEncode()); // 出站
                pipeline.addLast("protocolDecode", new OrderProtocolDecode()); // 入站

                // flush增强，增加了吞吐量，但有一定延迟
                pipeline.addLast("flushEnhance", new FlushConsolidationHandler(10, true)); // 5次之后，才进行flush，打开异步增强

                pipeline.addLast("auth", authHandler);

                pipeline.addLast(businessGroup, new OrderServerProcessHandler()); // 入站
            }
        });

        // 绑定端口，启动
        ChannelFuture f = b.bind(8090).sync();
        f.channel().closeFuture().sync().get();
    }
}
