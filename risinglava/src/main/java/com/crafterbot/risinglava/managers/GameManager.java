package com.crafterbot.risinglava.managers;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.FileUtil;
import org.codehaus.plexus.util.FileUtils;

import com.crafterbot.risinglava.GameState;
import com.crafterbot.risinglava.Plugin;
import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent;
import com.mojang.brigadier.Command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

public class GameManager implements Listener {
    private static final NamespacedKey worldKey = new NamespacedKey(Plugin.Instance, "risinglava");

    private Plugin plugin;
    private Server server;
    private PluginManager pluginManager;

    public GameState state = GameState.READY;

    public ArrayList<Player> participants;
    public LavaManager lavaManager;

    public GameManager(Plugin mPlugin, Server mServer) {
        plugin = mPlugin;
        server = mServer;
        pluginManager = server.getPluginManager();

        new ProgressBarManager(mServer, this, pluginManager);
        pluginManager.registerEvents(this, plugin);
    }

    public int startGame(Long seed, int startHeight) {
        if (state != GameState.READY) return 0;
        if (server.getWorld(worldKey) != null)  cleanup(this);

        state = GameState.GAME_ON;

        participants = new ArrayList<Player>(server.getOnlinePlayers());

        WorldCreator creator = new WorldCreator(worldKey);
        creator.seed(seed);
        creator.type(WorldType.NORMAL);
        World lavaWorld = creator.createWorld();

        for (Player participant : participants) {
            participant.teleport(lavaWorld.getSpawnLocation());
            // todo: show message to player
        }

        lavaManager = new LavaManager(this, server, lavaWorld, startHeight);

        return Command.SINGLE_SUCCESS;
    }

    private void checkGameState() {
        if (state == GameState.GAME_ON) {

            if (participants.size() == 0) {
                
                server.sendMessage(Component.text("Game over"));
                cleanup(this);
                ProgressBarManager.cleanup();
                state = GameState.READY;
                
            } else {
                // continue game
            }

        } else if (state == GameState.READY) {
            // todo
        }
    }

    public static void cleanup(GameManager gameManager) {
        if (gameManager.lavaManager != null && gameManager.lavaManager.gameWorld != null) {
            File worldPath = new File(String.format("%s/%s", Bukkit.getServer().getWorldContainer().getAbsolutePath(), gameManager.lavaManager.gameWorld.getName()));
            if (Bukkit.getServer().unloadWorld(gameManager.lavaManager.gameWorld, false)) {
                gameManager.lavaManager = null;
            
                try {
                    FileUtils.deleteDirectory(worldPath);
                } catch (IOException exception) {
                    Plugin.LOGGER.warning(exception.getMessage());
                }
            }
        }
    }

    @EventHandler 
    private void onPlayerSetSpawn(PlayerSetSpawnEvent event) {
        if (lavaManager != null && lavaManager.gameWorld != null && event.getLocation().getWorld() == lavaManager.gameWorld) {
            event.getPlayer().sendMessage(Component.text("You cannot set your spawn in the rising lava world."));
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerLeft(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (state != GameState.GAME_ON || !participants.contains(player)) return;
        server.sendMessage(Component.text(String.format("%s has been eliminated", player.displayName())));
        participants.remove(player);
        checkGameState();
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        if (!participants.contains(player)) return;
        
        server.sendMessage(Component.text(String.format("%s has been eliminated", player.displayName())));
        participants.remove(player);
        checkGameState();
    } 
}
