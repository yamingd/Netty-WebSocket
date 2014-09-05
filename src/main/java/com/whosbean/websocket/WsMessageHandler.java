package com.whosbean.websocket;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class WsMessageHandler extends SimpleChannelInboundHandler<WebSocketFrame>
{

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception
    {
        ctx.close();
    }

    /**
     * We implement this to catch the websocket handshake completing
     * successfully. At that point we'll setup this client connection.
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception
    {
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            configureClient(ctx);
        }
    }

    /**
     * Should end up being called after websocket handshake completes. Will
     * configure this client for communication with the application.
     */
    protected void configureClient(ChannelHandlerContext ctx) {
        System.out.println("Checking auth");
    }

    /**
     * When a message is sent into the app by the connected user this is
     * invoked.
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception
    {
        if (frame instanceof TextWebSocketFrame){
            TextWebSocketFrame text = (TextWebSocketFrame)frame;
            System.out.println(text.text());
            ctx.channel().writeAndFlush(new TextWebSocketFrame(text.text()));
        }else{
            System.out.println(frame.toString());
        }

    }
}