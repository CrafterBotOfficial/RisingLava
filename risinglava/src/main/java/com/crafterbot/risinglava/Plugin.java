package com.crafterbot.risinglava;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import com.crafterbot.risinglava.managers.GameManager;
import com.crafterbot.risinglava.managers.LavaManager;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

public class Plugin extends JavaPlugin
{
  public static Plugin Instance;
  public static final Logger LOGGER = Logger.getLogger("Rising Lava");

  public void onEnable() {
    Instance = this;
    LOGGER.info("risinglava enabled");

    GameManager gameManager = new GameManager(this, getServer());

    LiteralArgumentBuilder<CommandSourceStack> startCommand = Commands.literal("start");
    startCommand.then(Commands.argument("seed", LongArgumentType.longArg()).executes(ctx -> gameManager.startGame(LongArgumentType.getLong(ctx, "seed"))));

    getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> 
      commands.registrar().register(Commands.literal("risinglava").then(startCommand).build())
    );
  }

  public void onDisable() {
    LOGGER.info("risinglava disabled");
  }
}
