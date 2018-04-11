package org.pzyko.pzykocore;

import org.bukkit.entity.Player;
import org.pzyko.pzykocore.mysql.SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayerManager {

    public void updatePlayer(Player p) {
        try (Connection conn = PzykoCore.get().getSql().getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL.SQL_REPLACE_INTO_USER)) {
            ps.setString(1, p.getUniqueId().toString());
            ps.setString(2, p.getName());
            ps.setLong(3, System.currentTimeMillis());
            ps.execute();
        } catch (SQLException ioe) {
            ioe.printStackTrace();
        }

        System.out.println("replaced....");
    }
}
