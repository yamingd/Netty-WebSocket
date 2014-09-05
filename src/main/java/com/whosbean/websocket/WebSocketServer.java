package com.whosbean.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Properties;

/**
 * Created by yaming_deng on 14-8-6.
 */
public class WebSocketServer {

    private static int port = 8080;

    /**
     * 启动推送服务 8080端口
     */
    public static void start(Properties prop) {
        String actSize = prop.getProperty("server.actors", "10");
        String workerSize = prop.getProperty("server.workers", "10");
        port = Integer.parseInt(prop.getProperty("server.port", "8080"));

        EventLoopGroup parentGroup = new NioEventLoopGroup(Integer.parseInt(actSize)); // 用于接收发来的连接请求
        EventLoopGroup childGroup = new NioEventLoopGroup(Integer.parseInt(workerSize)); // 用于处理parentGroup接收并注册给child的连接中的信息
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap(); // 服务器助手类
            // 简历新的accept连接，用于构建serverSocketChannel的工厂类
            serverBootstrap.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(
                                    new HttpRequestDecoder(),
                                    new HttpObjectAggregator(65536),
                                    new HttpResponseEncoder(),
                                    new HttpSessionHandler(),
                                    new WsGatewayHandler()
                            );
                        }
                    });

            serverBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            serverBootstrap.option(ChannelOption.TCP_NODELAY, true);
            serverBootstrap.option(ChannelOption.SO_REUSEADDR, true);

            System.out.println("start server " + port + " ... ");
            ChannelFuture f = serverBootstrap.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            childGroup.shutdownGracefully();
            parentGroup.shutdownGracefully();
        }
    }

    /**
     * 入口
     * @param args
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        Properties prop = context.getBean("appConfig", Properties.class);
        start(prop);
    }
}