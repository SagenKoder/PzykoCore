package org.pzyko.pzykocore.claim;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.pzyko.pzykocore.PzykoCore;

public class ClaimListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreateClaim(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player p = e.getPlayer();

        if(p.getInventory().getItemInMainHand() == null || p.getInventory().getItemInMainHand().getType() == Material.AIR || p.getInventory().getItemInMainHand().getItemMeta() == null)
            return;
        if(!p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase("§6§lClaiming tool!"))
            return;

        if(PzykoCore.get().getClaimManager().getClaimCreation().containsKey(p.getUniqueId())) {
            Claim c = PzykoCore.get().getClaimManager().getClaimCreation().get(p.getUniqueId());

            int minX = c.getxMin();
            int minY = c.getzMin();
            int maxX = c.getxMax();
            int maxZ = c.getzMax();

            c.expandToBlock(e.getClickedBlock().getX(), e.getClickedBlock().getZ());

            // check if overlapping
            boolean remove = false;
            for (Claim other : PzykoCore.get().getClaimManager().getLoadedClaims()) {
                if (other.overlapsWith(c.getxMin(), c.getzMin(), c.getxMax(), c.getzMax())) {
                    p.sendMessage("§cCannot expand claim here because it would overlap with another claim!");
                    remove = true;
                    break;
                }
            }
            if (remove) {
                c.set(minX, minY, maxX, maxZ);
                c.touch();
            }
        } else {
            Claim c = new Claim(p.getWorld().getName(), e.getClickedBlock().getX(), e.getClickedBlock().getZ());
            c.grantACCESS(p.getUniqueId());
            c.grantCONTAINER(p.getUniqueId());
            c.grantBUILD(p.getUniqueId());
            c.grantMANAGE(p.getUniqueId());

            // check if overlapping
            for(Claim other : PzykoCore.get().getClaimManager().getLoadedClaims()) {
                if(other.overlapsWith(c.getxMin(), c.getzMin(), c.getxMax(), c.getzMax())) {
                    p.sendMessage("§cCannot create claim here because it would overlap with another claim!");
                    return;
                }
            }

            PzykoCore.get().getClaimManager().getClaimCreation().put(p.getUniqueId(), c);
            c.touch();
            c.markChanged();
        }

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDropTool(PlayerDropItemEvent e) {
        if(!e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase("§6§lClaiming tool!"))
            return;

        if(PzykoCore.get().getClaimManager().getClaimCreation().containsKey(e.getPlayer().getUniqueId())) {
            e.getPlayer().sendMessage("§6§lYou finished the creation of your new claim!");
            Claim c = PzykoCore.get().getClaimManager().getClaimCreation().get(e.getPlayer().getUniqueId());
            PzykoCore.get().getClaimManager().getClaimCreation().remove(e.getPlayer().getUniqueId());
            PzykoCore.get().getClaimManager().getLoadedClaims().add(c);
            e.getItemDrop().setItemStack(new ItemStack(Material.AIR));
        } else {
            e.getPlayer().sendMessage("§c§lYou cancelled the creation of your new claim!");
            e.getItemDrop().setItemStack(new ItemStack(Material.AIR));
        }
    }

}
