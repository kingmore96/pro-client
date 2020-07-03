package cn.net.icomp.protestdemo.jfdj;

import cn.net.icomp.protestdemo.common.utils.ApplicationContextHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@ComponentScan(basePackages = {"cn.net.icomp.protestdemo"})
public class NettyClient {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(NettyClient.class);
        ApplicationContext ctx = app.run(args);
        ApplicationContextHelper.setApplicationContext(ctx);
    }

    public static void connect(String ip, Integer port) {
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(new NioEventLoopGroup())
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 4000)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .remoteAddress(ip, port)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //in 事件
                            pipeline.addLast(new IdleStateHandler(20, 0, 0, TimeUnit.SECONDS));
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 2, 2, -4, 0));
                            pipeline.addLast(new ByteArrayDecoder());
                            pipeline.addLast(new ClientHandler());

                            //out 事件
                            pipeline.addLast(new ByteArrayEncoder());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        System.out.println("连接成功！");
                    } else {
                        System.out.println("连接失败！");
                        channelFuture.channel().close();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
