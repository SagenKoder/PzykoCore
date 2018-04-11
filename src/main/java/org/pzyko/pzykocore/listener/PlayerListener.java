package org.pzyko.pzykocore.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.pzyko.pzykocore.PzykoCore;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        PzykoCore.get().getPlayerManager().updatePlayer(e.getPlayer());
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        PzykoCore.get().getPlayerManager().updatePlayer(e.getPlayer());
    }
}
