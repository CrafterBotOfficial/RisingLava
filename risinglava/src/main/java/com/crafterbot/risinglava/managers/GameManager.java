package com.crafterbot.risinglava.managers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import com.crafterbot.risinglava.GameState;
import com.crafterbot.risinglava.Plugin;
import com.mojang.brigadier.Command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

public class GameManager implements Listener {
    private static final NamespacedKey worldKey = new NamespacedKey(Plugin.Instance, "risinglava");

    private Plugin plugin;
    private Server server;
    private PluginManager pluginManager;

    public GameState state = GameState.READY;

    public Collection<? extends Player> participants;
    private LavaManager lavaManager;

    public GameManager(Plugin mPlugin, Server mServer) {
        plugin = mPlugin;
        server = mServer;
        pluginManager = server.getPluginManager();

        pluginManager.registerEvents(this, plugin);
    }

    public int startGame(Long seed) {
        if (state != GameState.READY) {
            return 1;
        }
        state = GameState.GAME_ON;

        participants = server.getOnlinePlayers();

        WorldCreator creator = new WorldCreator(worldKey);
        creator.seed(seed);
        creator.type(WorldType.FLAT);
        World lavaWorld = creator.createWorld();

        for (Player participant : participants) {
            participant.teleport(lavaWorld.getSpawnLocation());
            // todo: show message to player
        }

        lavaManager = new LavaManager(this, server, lavaWorld);

        return Command.SINGLE_SUCCESS;
    }
}
