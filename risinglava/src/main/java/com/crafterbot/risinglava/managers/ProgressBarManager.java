package com.crafterbot.risinglava.managers;

import org.bukkit.Server;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import com.crafterbot.risinglava.GameState;
import com.crafterbot.risinglava.Plugin;
import com.crafterbot.risinglava.events.RaiseLavaEvent;

public class ProgressBarManager implements Listener {

    private static BossBar bossBar;

    private Server server;
    private GameManager gameManager;

    public ProgressBarManager(Server mServer, GameManager mGameManager, PluginManager pluginManager) {
        server = mServer;
        gameManager = mGameManager;
        bossBar = server.createBossBar("Lava Progress", BarColor.RED, BarStyle.SOLID);

        pluginManager.registerEvents(this, Plugin.Instance);
    }

    @EventHandler
    private void onRaiseLava(RaiseLavaEvent event) {
        if (gameManager.state == GameState.GAME_ON && bossBar.getPlayers().size() == 0) {
            for (Player player : gameManager.participants) bossBar.addPlayer(player);
        }

        double startHeight = (double)gameManager.lavaManager.startHeight;
        double percentage = (double)(event.layer - startHeight) / (double)(LavaManager.MaxHeight - startHeight); // converting to double to fix weird division error
        bossBar.setProgress(percentage);
        bossBar.setTitle(String.format("Lava Progess - %s%%", Math.round(percentage * 100)));
    }

    public static void cleanup() {
        if (bossBar != null) {
            bossBar.removeAll();
        }
    }
}
