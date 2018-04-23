package org.pzyko.pzykocore;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.pzyko.pzykocore.claim.Claim;
import org.pzyko.pzykocore.claim.particle.ClaimVisualiser;
import org.pzyko.pzykocore.mysql.SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class ClaimManager {

    private static final int DISTANCE_TO_KEEP_LOADED = 150;
    private static final int TIME_KEEP_UNACTIVE_LOADED = 1000 * 60 * 2; // 2 minutter

    private static boolean isCreated = false;

    private ArrayList<Claim> loadedClaims;
    private HashMap<UUID, Claim> claimCreation;
    private ClaimVisualiser claimVisualiser;

    public ClaimManager() {
        if(isCreated)
            throw new IllegalStateException("An instance of ClaimManager has already been created!");
        isCreated = true;

        loadedClaims = new ArrayList<>();
        claimCreation = new HashMap<>();
        claimVisualiser = new ClaimVisualiser();

        new BukkitRunnable() {
            @Override
            public void run() {
                ArrayList<Claim> remove = new ArrayList<>();
                for(Player o : Bukkit.getOnlinePlayers()) {
                    for(Claim c : loadedClaims) {

                        // insert if updated
                        if(c.isChanged())
                            c.insert();

                        if(c.distanceFromClaimSqr(o.getLocation().getBlockX(), o.getLocation().getBlockZ()) < Math.pow(DISTANCE_TO_KEEP_LOADED, 2)) {
                            // touch if players nearby
                            c.touch();
                        } else if(System.currentTimeMillis() - c.getLastTouch() > TIME_KEEP_UNACTIVE_LOADED) {
                            // remove if inactive
                            remove.add(c);
                        }
                    }
                }
                for(Claim c : remove) {
                    loadedClaims.remove(c);
                }

                load();
            }
        }.runTaskTimer(PzykoCore.get(), 10 * 20, 10 * 20);

        load();
    }

    private void load() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            try (Connection conn = PzykoCore.get().getSql().getConnection(); PreparedStatement ps = conn.prepareStatement(SQL.SQL_SELECT_ALL_NEAR)) {
                ps.setString(1, p.getWorld().getName());
                ps.setInt(2, p.getLocation().getBlockX());
                ps.setInt(3, p.getLocation().getBlockZ());
                ResultSet rs = ps.executeQuery();
                while (rs != null && rs.next()) {
                    if (hasLoadedId(rs.getInt(1))) {
                        continue;
                    }

                    Claim c = new Claim(rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getInt(6));
                    c.setSqlID(rs.getInt(1));

                    try (Connection conn2 = PzykoCore.get().getSql().getConnection(); PreparedStatement ps2 = conn.prepareStatement(SQL.SQL_SELECT_USERROLE)) {
                        ps2.setInt(1, c.getSqlID());
                        ResultSet rs2 = ps2.executeQuery();
                        while (rs2 != null && rs2.next()) {
                            switch(rs2.getString(3)) {
                                case "ACCESS" : c.grantACCESS(UUID.fromString(rs2.getString(2))); break;
                                case "CONTAINER" : c.grantCONTAINER(UUID.fromString(rs2.getString(2))); break;
                                case "BUILD" : c.grantBUILD(UUID.fromString(rs2.getString(2))); break;
                                case "MANAGE" : c.grantMANAGE(UUID.fromString(rs2.getString(2))); break;
                            }
                        }
                    }
                    loadedClaims.add(c);
                    c.touch();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean hasLoadedId(int id) {
        for(Claim c : loadedClaims) {
            if(c.getSqlID() == id) return true;
        }
        return false;
    }

    public ArrayList<Claim> getLoadedClaims() {
        return loadedClaims;
    }

    public ArrayList<Claim> getClaimsNear(double x, double y, double distance) {
        ArrayList<Claim> ret = new ArrayList<>();

        double distSqr = distance * distance;
        for(Claim c : loadedClaims) {
            if(c.distanceFromClaimSqr(x, y) < distSqr) {
                ret.add(c);
            }
        }

        return ret;
    }

    public Optional<Object> getTopClaim(World world, double x, double y) {
        return Optional.empty();
    }

    public HashMap<UUID, Claim> getClaimCreation() {
        return claimCreation;
    }
}
