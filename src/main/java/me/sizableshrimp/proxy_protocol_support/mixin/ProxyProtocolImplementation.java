package me.sizableshrimp.proxy_protocol_support.mixin;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import me.sizableshrimp.proxy_protocol_support.ProxyProtocolChannelInitializer;
import net.minecraft.server.network.ServerConnectionListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Replaces anonymous ChannelInitializer with ProxyProtocolChannelInitializer
 *
 * @author PanSzelescik
 * @see ProxyProtocolChannelInitializer
 */
@Mixin(ServerConnectionListener.class)
public class ProxyProtocolImplementation {

    // TODO: Mixin into anonymous class?
    @Redirect(method = "startTcpServerListener", at = @At(value = "INVOKE", target = "Lio/netty/bootstrap/ServerBootstrap;childHandler(Lio/netty/channel/ChannelHandler;)Lio/netty/bootstrap/ServerBootstrap;", remap = false))
    private ServerBootstrap addProxyProtocolSupport(ServerBootstrap bootstrap, ChannelHandler childHandler) {
        return bootstrap.childHandler(new ProxyProtocolChannelInitializer(((ChannelInitializerInvoker) childHandler)));
    }
}
