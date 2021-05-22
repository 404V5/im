package com.zsxg.im.dispathcer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

/**
 * 分发系统的启动类
 */
public class DispatcherHandler extends ChannelInboundHandlerAdapter {
    /**
     * 一个接入系统跟分发系统建立了连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        String channelId = channel.remoteAddress().getHostName() + ":" +
                channel.remoteAddress().getPort();
        GatewayInstanceManager gatewayInstanceManager = GatewayInstanceManager.getInstance();
        gatewayInstanceManager.addGatewayInstance(channelId, channel);
        System.out.println("已经跟TCP接入系统建立连接，TCP接入系统地址为：" + channel.remoteAddress());
    }

    /**
     * 一个接入系统跟分发系统的连接断开了
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        String channelId = channel.remoteAddress().getHostName() + ":" +
                channel.remoteAddress().getPort();
        GatewayInstanceManager gatewayInstanceManager = GatewayInstanceManager.getInstance();
        gatewayInstanceManager.removeGatewayInstance(channelId);
        System.out.println("跟TCP接入系统的连接断开，地址为：" + channel);
    }

    /**
     * 接收到一个接入系统发送过来的请求
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    }

    /**
     * 处理完毕一个请求
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     * 发生异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
