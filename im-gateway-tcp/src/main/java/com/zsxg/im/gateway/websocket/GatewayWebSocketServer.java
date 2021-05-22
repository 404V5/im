package com.zsxg.im.gateway.websocket;

import com.zsxg.im.gateway.tcp.push.PushManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class GatewayWebSocketServer {
    public static final int PORT = 8080;

    public static void main(String[] args) {
        PushManager pushManager = new PushManager();
        pushManager.start();

        EventLoopGroup connectionThreadGroup = new NioEventLoopGroup();
        EventLoopGroup ioThreadGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap server = new ServerBootstrap();

            server.group(connectionThreadGroup, ioThreadGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("http-codec",new HttpServerCodec());//设置解码器
                            socketChannel.pipeline().addLast("aggregator",new HttpObjectAggregator(65536));//聚合器，使用websocket会用到
                            socketChannel.pipeline().addLast("http-chunked",new ChunkedWriteHandler());//用于大数据的分区传输
                            socketChannel.pipeline().addLast("handler",new GatewayWebSocketHandler());//自定义的业务handler
                        }

                    });

            ChannelFuture channelFuture = server.bind(PORT).sync();

            channelFuture.channel().closeFuture().sync();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            connectionThreadGroup.shutdownGracefully();
            ioThreadGroup.shutdownGracefully();
        }
    }
}
