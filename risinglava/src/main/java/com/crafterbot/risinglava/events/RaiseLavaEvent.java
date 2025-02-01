package com.crafterbot.risinglava.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RaiseLavaEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public int layer;

    public RaiseLavaEvent(int mLayer) {
        layer = mLayer;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
