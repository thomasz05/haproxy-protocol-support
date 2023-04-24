package me.sizableshrimp.proxy_protocol_support.mixin;

import net.minecraft.network.NetworkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.net.SocketAddress;

/**
 * Adds Accessor for address
 *
 * @author PanSzelescik
 * @see NetworkManager#address
 */
@Mixin(NetworkManager.class)
public interface ProxyProtocolAddressSetter {
    @Accessor
    void setAddress(SocketAddress address);
}
