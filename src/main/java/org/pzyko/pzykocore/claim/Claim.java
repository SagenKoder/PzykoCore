package org.pzyko.pzykocore.claim;

import org.pzyko.pzykocore.PzykoCore;
import org.pzyko.pzykocore.mysql.SQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Claim {

    private int sqlID = -1;

    private boolean isChanged = false;

    private List<UUID> ACCESS;
    private List<UUID> CONTAINER;
    private List<UUID> BUILD;
    private List<UUID> MANAGE;

    private String world;
    private int xMin;
    private int xMax;
    private int zMin;
    private int zMax;

    private long lastTouched;

    public Claim(String world, int x, int z) {
        this(world, x, z, x, z);
    }

    public Claim(String world, int x1, int z1, int x2, int z2) {
        this.world = world;

        this.xMin = Math.min(x1, x2);
        this.xMax = Math.max(x1, x2);
        this.zMin = Math.min(z1, z2);
        this.zMax = Math.max(z1, z2);

        this.ACCESS = new ArrayList<>();
        this.CONTAINER = new ArrayList<>();
        this.BUILD = new ArrayList<>();
        this.MANAGE = new ArrayList<>();

        touch();
    }

    public void set(int x1, int z1, int x2, int z2) {
        this.xMin = Math.min(x1, x2);
        this.xMax = Math.max(x1, x2);
        this.zMin = Math.min(z1, z2);
        this.zMax = Math.max(z1, z2);

        markChanged();
    }

    public void touch() {
        lastTouched = System.currentTimeMillis();
    }

    public Long getLastTouch() {
        return lastTouched;
    }

    public void markChanged() {
        isChanged = true;
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setSqlID(int id) {
        this.sqlID = id;
    }

    public int getSqlID() {
        return sqlID;
    }

    public void expandToBlock(int x, int z) {
        if (this.xMin > x) {
            this.xMin = x;
        } else if (this.xMax < x) {
            this.xMax = x;
        }

        if (this.zMin > z) {
            this.zMin = z;
        } else if (this.zMax < z) {
            this.zMax = z;
        }

        markChanged();
    }

    public double distanceFromClaimSqr(double x, double z) {
        double cx = Math.max(Math.min(x, xMax), xMin);
        double cz = Math.max(Math.min(z, zMax), zMin);

        return Math.pow(x - cx, 2) + Math.pow(z - cz, 2);
    }

    public double distanceFromClaim(double x, double z) {
        return Math.sqrt(distanceFromClaimSqr(x, z));
    }

    public void grantACCESS(UUID uuid) {
        if(!ACCESS.contains(uuid))
            ACCESS.add(uuid);

        markChanged();
    }

    public void grantCONTAINER(UUID uuid) {
        if(!CONTAINER.contains(uuid))
            CONTAINER.add(uuid);

        markChanged();
    }

    public void grantBUILD(UUID uuid) {
        if(!BUILD.contains(uuid))
            BUILD.add(uuid);

        markChanged();
    }

    public void grantMANAGE(UUID uuid) {
        if(!MANAGE.contains(uuid))
            MANAGE.add(uuid);

        markChanged();
    }

    public boolean overlapsWith(int otherXMin, int otherZMin, int otherXMax, int otherZMax) {
        if(this.xMin > otherXMax || otherXMin > this.xMax)
            return false;

        if(this.zMin > otherZMax || otherZMin > this.zMax)
            return false;

        return true;
    }

    public void insert() {
        if(sqlID < 0) {
            try (Connection conn = PzykoCore.get().getSql().getConnection();
                 PreparedStatement ps = conn.prepareStatement(SQL.SQL_INSERT_INTO_CLAIM, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, world);
                ps.setInt(2, xMin);
                ps.setInt(3, zMin);
                ps.setInt(4, xMax);
                ps.setInt(5, zMax);
                System.out.println(ps.toString());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if(rs.next()) {
                    this.sqlID = rs.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try (Connection conn = PzykoCore.get().getSql().getConnection();
                 PreparedStatement ps = conn.prepareStatement(SQL.SQL_UPDATE_CLAIM)) {
                ps.setString(1, world);
                ps.setInt(2, xMin);
                ps.setInt(3, zMin);
                ps.setInt(4, xMax);
                ps.setInt(5, zMax);
                ps.setInt(6, sqlID);
                System.out.println(ps.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        try (Connection conn = PzykoCore.get().getSql().getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL.SQL_INSERT_CLAIMROLE)){
            conn.setAutoCommit(false);
            for(UUID uuid : ACCESS) {
                ps.setInt(1, sqlID);
                ps.setString(2, uuid.toString());
                ps.setString(3, "ACCESS");
                ps.execute();
            }
            for(UUID uuid : CONTAINER) {
                ps.setInt(1, sqlID);
                ps.setString(2, uuid.toString());
                ps.setString(3, "CONTAINER");
                ps.execute();
            }
            for(UUID uuid : BUILD) {
                ps.setInt(1, sqlID);
                ps.setString(2, uuid.toString());
                ps.setString(3, "BUILD");
                ps.execute();
            }
            for(UUID uuid : MANAGE) {
                ps.setInt(1, sqlID);
                ps.setString(2, uuid.toString());
                ps.setString(3, "MANAGE");
                ps.execute();
            }
            //ps.executeBatch();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        isChanged = false;
    }

    public String getWorld() {
        return world;
    }

    public int getxMin() {
        return xMin;
    }

    public int getxMax() {
        return xMax;
    }

    public int getzMin() {
        return zMin;
    }

    public int getzMax() {
        return zMax;
    }
}
