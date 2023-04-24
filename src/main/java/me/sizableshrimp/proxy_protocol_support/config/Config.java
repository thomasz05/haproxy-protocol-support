package me.sizableshrimp.proxy_protocol_support.config;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Class which represents configuration file
 *
 * @author PanSzelescik
 * @see Configuration
 */
public class Config {

    @SerializedName("enable-proxy-protocol")
    public boolean enableProxyProtocol = true;

    @SerializedName("proxy-protocol-whitelisted-ips")
    public List<String> whitelistedIPs = new ArrayList<>();

    @SerializedName("whitelistTCPShieldServers")
    public boolean whitelistTCPShieldServers = false;
}
