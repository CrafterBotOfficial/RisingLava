package com.crafterbot.risinglava.managers;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import com.crafterbot.risinglava.GameState;
import com.crafterbot.risinglava.Plugin;

import net.kyori.adventure.text.Component;

public class LavaManager implements Listener {
    private GameManager gameManager;
    private Server server;
    private BukkitScheduler scheduler;

    private BukkitTask raiseLavaTask;

    public World gameWorld;
    public int lavaHeight = -65;

    public LavaManager(GameManager mGameManager, Server mServer, World mGameWorld, int startHeight) {
        gameManager = mGameManager;
        server = mServer;
        gameWorld = mGameWorld;
        scheduler = Bukkit.getScheduler();

        lavaHeight = startHeight;
        raiseLavaTask = scheduler.runTaskTimer(Plugin.Instance, () -> raiseLava(), 0L, 20 * 5);
    }

    private void raiseLava() {
        if (gameManager.state != GameState.GAME_ON) { 
            raiseLavaTask.cancel();
            raiseLavaTask = null;
            return; 
        }
        lavaHeight++;
        server.sendMessage(Component.text(String.format("Raising lava. Layer: %s", lavaHeight)));

        for (Chunk chunk : gameWorld.getLoadedChunks()) {
            scheduler.runTaskLater(Plugin.Instance, () -> {
                int x = chunk.getX() * 16;
                int z = chunk.getZ() * 16;
                fillZone(x, lavaHeight - 1, z,
                         x + 15, lavaHeight, z + 15);
            }, (int)(Math.random() * 100));
        }
    }

    @EventHandler
    private void onLoadChunk(ChunkLoadEvent event) {
        if (event.getWorld() != gameWorld) return;
        // todo: fill lava
    }

    // todo: do not send packets for every block changed, instead for every chunk changed. tmp code
    private void fillZone(int x1, int y1, int z1, 
                          int x2, int y2, int z2) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    Block block = gameWorld.getBlockAt(x, y, z);
                    if (!block.getType().isAir()) continue;
                    block.setType(Material.LAVA, false);
                }
            }
        }
    }
}
