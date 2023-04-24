package me.sizableshrimp.proxy_protocol_support;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import me.sizableshrimp.proxy_protocol_support.mixin.ChannelInitializerInvoker;

/**
 * Initializes HAProxyMessageDecoder and ProxyProtocolHandler
 *
 * @author PanSzelescik
 * @see io.netty.handler.codec.haproxy.HAProxyMessageDecoder
 * @see ProxyProtocolHandler
 */
public class ProxyProtocolChannelInitializer extends ChannelInitializer<Channel> {

    private final ChannelInitializerInvoker channelInitializer;

    public ProxyProtocolChannelInitializer(ChannelInitializerInvoker invoker) {
        this.channelInitializer = invoker;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        this.channelInitializer.invokeInitChannel(channel);

        if (!ProxyProtocolSupport.enableProxyProtocol) {
            return;
        }

        channel.pipeline()
                .addAfter("timeout", "haproxy-decoder", new HAProxyMessageDecoder())
                .addAfter("haproxy-decoder", "haproxy-handler", new ProxyProtocolHandler());
    }
}
