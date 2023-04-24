package me.sizableshrimp.proxy_protocol_support.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class CommonConfig {
    public static final CommonConfig INSTANCE;
    private static final ForgeConfigSpec spec;

    public final ForgeConfigSpec.BooleanValue enableProxyProtocol;
    public final ForgeConfigSpec.ConfigValue<List<String>> whitelistedIPs;
    public final ForgeConfigSpec.BooleanValue whitelistTCPShieldServers;

    static {
        Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        INSTANCE = specPair.getLeft();
        spec = specPair.getRight();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, spec);
    }

    private CommonConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Proxy Protocol Support configuration.")
                .push("server");

        this.enableProxyProtocol = builder
                .comment("Whether this mod should be enabled at all")
                .define("enableProxyProtocol", true);

        this.whitelistedIPs = builder
                .comment("The list of IPs to whitelist for joining the server.",
                        "Generally, this list should only be your proxy IPs.",
                        "If empty, all IPs are allowed.")
                .define("whitelistedIPs", List.of(), o -> o instanceof List);

        this.whitelistTCPShieldServers = builder
                .comment("Whether TCPShield's IPs should be automatically added to the whitelist")
                .define("whitelistTCPShieldServers", false);
    }
}
