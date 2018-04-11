package org.pzyko.pzykocore.claim;

import org.pzyko.pzykocore.PzykoCore;
import org.pzyko.pzykocore.mysql.SQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Claim {

    private int sqlID = -1;

    private List<UUID> ACCESS;
    private List<UUID> CONTAINER;
    private List<UUID> BUILD;
    private List<UUID> MANAGE;

    private String world;
    private int xMin;
    private int xMax;
    private int zMin;
    private int zMax;

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
    }

    public void setSqlID(int id) {
        this.sqlID = id;
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
    }

    public void grantCONTAINER(UUID uuid) {
        if(!CONTAINER.contains(uuid))
            CONTAINER.add(uuid);
    }

    public void grantBUILD(UUID uuid) {
        if(!BUILD.contains(uuid))
            BUILD.add(uuid);
    }

    public void grantMANAGE(UUID uuid) {
        if(!MANAGE.contains(uuid))
            MANAGE.add(uuid);
    }

    public void insert() {
        if(sqlID >= 0) {
            try (Connection conn = PzykoCore.get().getSql().getConnection();
                 PreparedStatement ps = conn.prepareStatement(SQL.SQL_INSERT_INTO_CLAIM.replace("pzyko_", SQL.PREFIX), Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, world);
                ps.setInt(2, xMin);
                ps.setInt(3, zMin);
                ps.setInt(4, xMax);
                ps.setInt(5, zMax);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if(rs.next()) {
                    this.sqlID = rs.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
