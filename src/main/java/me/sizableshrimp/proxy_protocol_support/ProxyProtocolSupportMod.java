package me.sizableshrimp.proxy_protocol_support;

import me.sizableshrimp.proxy_protocol_support.config.CIDRMatcher;
import me.sizableshrimp.proxy_protocol_support.config.CommonConfig;
import me.sizableshrimp.proxy_protocol_support.config.TCPShieldIntegration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mod(ProxyProtocolSupportMod.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ProxyProtocolSupportMod {
    public static final String MODID = "proxy_protocol_support";
    public static final Logger LOGGER = LogManager.getLogger();

    private static boolean enabled = false;
    private static final List<CIDRMatcher> whitelistedIPs = new ArrayList<>();
    public static final List<CIDRMatcher> whitelistedIPsView = Collections.unmodifiableList(whitelistedIPs);

    public ProxyProtocolSupportMod() {
        CommonConfig.register();
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static List<CIDRMatcher> getWhitelistedIPs() {
        return whitelistedIPsView;
    }

    @SubscribeEvent
    public static void onModConfigLoadingReloading(ModConfig.ModConfigEvent event) throws IOException {
        if (FMLEnvironment.dist != Dist.DEDICATED_SERVER || event.getConfig().getType() != ModConfig.Type.COMMON)
            return;

        if (!CommonConfig.INSTANCE.enableProxyProtocol.get()) {
            LOGGER.info("Proxy Protocol disabled!");
            return;
        }

        enabled = true;
        LOGGER.info("Proxy Protocol enabled!");

        for (String ip : CommonConfig.INSTANCE.whitelistedIPs.get()) {
            whitelistedIPs.add(new CIDRMatcher(ip));
        }

        if (CommonConfig.INSTANCE.whitelistTCPShieldServers.get()) {
            LOGGER.info("TCPShield integration enabled!");

            whitelistedIPs.addAll(TCPShieldIntegration.getWhitelistedIPs());
        }

        LOGGER.debug("Using {} whitelisted IPs: {}", whitelistedIPs.size(), whitelistedIPs);
    }
}
