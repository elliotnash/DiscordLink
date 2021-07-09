package org.elliotnash.discordlink.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import org.elliotnash.discordlink.core.DiscordClient;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class StatusUpdater {

    private final ProxyServer server;
    private final DiscordClient client;
    private Map<String, ServerStatus> lastServerStatus;
    private Map<String, ServerStatus> currentServerStatus;

    public StatusUpdater(ProxyServer server, DiscordClient client){
        this.server = server;
        this.client = client;
        lastServerStatus = server.getAllServers().parallelStream().map(server1 -> {
            ServerPing ping = null;
            try {
                ping = server1.ping().get();
            } catch (InterruptedException | ExecutionException ignored) {}
            if (ping == null){
                return new ServerStatus(server1.getServerInfo().getName(), false, 0, 0);
            } else if (!ping.getPlayers().isPresent()){
                return new ServerStatus(server1.getServerInfo().getName(), true, 0, 0);
            } else {
                return new ServerStatus(server1.getServerInfo().getName(), true,
                        ping.getPlayers().get().getOnline(), ping.getPlayers().get().getMax());
            }
        }).collect(Collectors.toMap(entry -> entry.name, entry -> entry));
        currentServerStatus = lastServerStatus;

        System.out.println(currentServerStatus);

    }

}

class ServerStatus{
    String name;
    boolean online;
    int onlinePlayers;
    int maxPlayers;
    ServerStatus(String name, boolean online, int onlinePlayers, int maxPlayers){
        this.name = name;
        this.online = online;
        this.onlinePlayers = onlinePlayers;
        this.maxPlayers = maxPlayers;
    }
    @Override
    public String toString(){
        return "{name: "+name+", online: "+online+", onlinePlayers: "+onlinePlayers+", maxPlayers: "+maxPlayers+"}";
    }
}
