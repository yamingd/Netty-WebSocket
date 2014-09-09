package com.whosbean.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import java.util.List;

/**
 * Created by yaming_deng on 14-9-9.
 */
public class WsServerProtocolHandler extends WebSocketServerProtocolHandler {


    public WsServerProtocolHandler(String websocketPath) {
        super(websocketPath);
    }

    public WsServerProtocolHandler(String websocketPath, String subprotocols) {
        super(websocketPath, subprotocols);
    }

    public WsServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions) {
        super(websocketPath, subprotocols, allowExtensions);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
        System.out.println(frame);
        if (frame instanceof CloseWebSocketFrame) {
            System.out.println("Close WS.");
            super.decode(ctx, frame, out);
            return;
        }
        super.decode(ctx, frame, out);
    }

}
