package org.pzyko.pzykocore.claim.particle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.pzyko.pzykocore.PzykoCore;
import org.pzyko.pzykocore.claim.Claim;

import java.util.Map;
import java.util.UUID;

public class ClaimVisualiser {

    public ClaimVisualiser() {
        new BukkitRunnable() {
            @Override
            public void run() {
                update();
            }
        }.runTaskTimer(PzykoCore.get(), 5, 5);
    }

    private void update() {
        for(Map.Entry<UUID, Claim> e : PzykoCore.get().getClaimManager().getClaimCreation().entrySet()) {

            Player p = Bukkit.getPlayer(e.getKey());
            if(p == null) continue;

            Claim c = e.getValue();

            render(c, p, ParticleEffect.VILLAGER_HAPPY);
        }

        for(Claim c : PzykoCore.get().getClaimManager().getLoadedClaims()) {
            for(Player o : Bukkit.getOnlinePlayers()) {
                render(c, o, ParticleEffect.REDSTONE);
            }
        }
    }

    public void render(Claim c, Player p, ParticleEffect e) {

        int playerX = p.getLocation().getBlockX();
        int playerY = p.getLocation().getBlockY();
        int playerZ = p.getLocation().getBlockZ();

        if(c.distanceFromClaimSqr(playerX, playerZ) > Math.pow(10, 2)) return;

        int xMin = c.getxMin();
        int xMax = c.getxMax() + 1;

        int yMin = playerY - 1;
        int yMax = playerY + 5;

        int zMin = c.getzMin();
        int zMax = c.getzMax() + 1;

        // render the xMin wall
        if(Math.abs(xMin - playerX) <= 8) { // only render this wall if nearby
            for (int y = yMin; y <= yMax; y++) {
                for(int z = Math.max(zMin, playerZ - 5); z <= Math.min(zMax, playerZ + 5); z++) {
                    // renderParticle at xMin, y, z
                    e.display(0.0f, 0.0f, 0.0f, 0.0f, 1, new Location(p.getWorld(), xMin, y, z), p);
                }
            }
        }

        // render the zMin wall
        if(Math.abs(zMin - playerZ) <= 8) { // only render this wall if nearby
            for (int y = yMin; y <= yMax; y++) {
                for(int x = Math.max(xMin, playerX - 6); x <= Math.min(xMax, playerX + 6); x++) {
                    // renderParticle at x, y, zMin
                    e.display(0.0f, 0.0f, 0.0f, 0.0f, 1, new Location(p.getWorld(), x, y, zMin), p);
                }
            }
        }

        // render the xMax wall
        if(Math.abs(xMax - playerX) <= 8) { // only render this wall if nearby
            for (int y = yMin; y <= yMax; y++) {
                for(int z = Math.max(zMin, playerZ - 6); z <= Math.min(zMax, playerZ + 6); z++) {
                    // renderParticle at xMin, y, z
                    e.display(0.0f, 0.0f, 0.0f, 0.0f, 1, new Location(p.getWorld(), xMax, y, z), p);
                }
            }
        }

        // render the zMax wall
        if(Math.abs(zMax - playerZ) <= 8) { // only render this wall if nearby
            for (int y = yMin; y <= yMax; y++) {
                for(int x = Math.max(xMin, playerX - 6); x <= Math.min(xMax, playerX + 6); x++) {
                    // renderParticle at x, y, zMin
                    e.display(0.0f, 0.0f, 0.0f, 0.0f, 1, new Location(p.getWorld(), x, y, zMax), p);
                }
            }
        }
    }

}
