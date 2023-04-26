package me.sizableshrimp.proxy_protocol_support;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.haproxy.HAProxyCommand;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import me.sizableshrimp.proxy_protocol_support.config.CIDRMatcher;
import me.sizableshrimp.proxy_protocol_support.mixin.ProxyProtocolAddressSetter;
import net.minecraft.network.NetworkManager;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

/**
 * Reads HAProxyMessage to set valid Player IP
 *
 * @author PanSzelescik
 * @see io.netty.handler.codec.haproxy.HAProxyMessage
 */
public class ProxyProtocolHandler extends ChannelInboundHandlerAdapter {
    public static void setupConnection(Channel channel) {
        channel.pipeline()
                .addAfter("timeout", "haproxy-decoder", new HAProxyMessageDecoder())
                .addAfter("haproxy-decoder", "haproxy-handler", new ProxyProtocolHandler());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof HAProxyMessage)) {
            super.channelRead(ctx, msg);
            return;
        }

        HAProxyMessage message = (HAProxyMessage) msg;

        if (message.command() != HAProxyCommand.PROXY)
            return;

        final String realAddress = message.sourceAddress();
        final int realPort = message.sourcePort();

        final InetSocketAddress socketAddr = new InetSocketAddress(realAddress, realPort);

        NetworkManager connection = ((NetworkManager) ctx.channel().pipeline().get("packet_handler"));
        SocketAddress proxyAddress = connection.getRemoteAddress();

        // ProxyProtocolSupportMod.LOGGER.debug("Detected connection. Real IP: {}, Proxy IP: {}", socketAddr, proxyAddress);

        List<CIDRMatcher> whitelistedIPs = ProxyProtocolSupportMod.getWhitelistedIPs();
        if (!whitelistedIPs.isEmpty()) {
            if (proxyAddress instanceof InetSocketAddress) {
                InetSocketAddress proxySocketAddress = (InetSocketAddress) proxyAddress;
                boolean isWhitelistedIP = false;

                for (CIDRMatcher matcher : whitelistedIPs) {
                    if (matcher.matches(proxySocketAddress.getAddress())) {
                        isWhitelistedIP = true;
                        break;
                    }
                }

                if (!isWhitelistedIP) {
                    if (ctx.channel().isOpen()) {
                        ctx.disconnect();
                        ProxyProtocolSupportMod.LOGGER.debug("Blocked IP {} from joining as it was not in the IP whitelist", proxySocketAddress);
                    }
                    return;
                }
            } else {
                ProxyProtocolSupportMod.LOGGER.warn("Detected a proxy SocketAddress instance other than InetSocketAddress! This is a bug.");
                // ProxyProtocolSupport.LOGGER.warn("**********************************************************************");
                // ProxyProtocolSupport.LOGGER.warn("* Detected other SocketAddress than InetSocketAddress!               *");
                // ProxyProtocolSupport.LOGGER.warn("* Please report it with logs to mod author to provide compatibility! *");
                // ProxyProtocolSupport.LOGGER.warn("* https://github.com/PanSzelescik/proxy-protocol-support/issues      *");
                // ProxyProtocolSupport.LOGGER.warn("**********************************************************************");
                // ProxyProtocolSupport.LOGGER.warn("Class \"{}\" address \"{}\"", proxyAddress.getClass(), proxyAddress);
            }
        }

        ((ProxyProtocolAddressSetter) connection).setAddress(socketAddr);
    }
}
