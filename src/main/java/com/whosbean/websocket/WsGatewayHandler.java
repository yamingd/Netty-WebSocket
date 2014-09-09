package com.whosbean.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;

/**
 * Created by yaming_deng on 14-9-5.
 */
public class WsGatewayHandler extends SimpleChannelInboundHandler<FullHttpRequest>
{

    public WsGatewayHandler() {
        super(false);
    }

    private static final ByteBuf NOT_FOUND =
            Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("NOT FOUND", CharsetUtil.US_ASCII));

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req)
            throws Exception
    {
        String uri = req.getUri();

        System.out.println("channelRead0: " + uri);

        /*AppPlug<?,?> ap = AppRegistry.getApp(uri);

        if (ap == null) {
            logger.info("No AppPlug found for uri (id) '{}'", uri);
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HTTP_1_1, HttpResponseStatus.NOT_FOUND, NOT_FOUND);
            ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            return;
        }

        ctx.channel().attr(appPlugKey).set(ap);*/

        // add websocket handler for the request uri where app lives
        ctx.pipeline().addLast(new WsServerProtocolHandler(uri));

        // now add our application handler
        ctx.pipeline().addLast(new WsMessageHandler());

        // remove, app is attached and websocket handler in place
        ctx.pipeline().remove(this);

        // pass the request on
        ctx.fireChannelRead(req);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("channelInactive: " + ctx.toString());
    }
}
