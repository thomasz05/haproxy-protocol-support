package me.sizableshrimp.proxy_protocol_support.mixin;

import io.netty.channel.Channel;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import me.sizableshrimp.proxy_protocol_support.ProxyProtocolHandler;
import me.sizableshrimp.proxy_protocol_support.ProxyProtocolSupportMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Initializes HAProxyMessageDecoder and ProxyProtocolHandler
 *
 * @see HAProxyMessageDecoder
 * @see ProxyProtocolHandler
 */
@Mixin(targets = "net.minecraft.server.network.ServerConnectionListener$1")
public class ServerConnectionListenerAnon1Mixin {
    @Inject(method = "initChannel", at = @At("TAIL"))
    private void proxy_protocol_support$onInitChannel_Tail(Channel channel, CallbackInfo ci) {
        if (!ProxyProtocolSupportMod.isEnabled())
            return;

        ProxyProtocolHandler.setupConnection(channel);
    }
}
