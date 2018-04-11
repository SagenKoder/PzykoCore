package org.pzyko.pzykocore.mysql;

import com.zaxxer.hikari.HikariDataSource;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQL implements Closeable {

    public static String SQL_CREATE_USER = "CREATE TABLE IF NOT EXISTS `pzyko_user` (\n" + "  `uuid` CHAR(36) NOT NULL,\n" + "  `username` VARCHAR(16) NOT NULL,\n" + "  `last_seen` BIGINT NOT NULL DEFAULT -1,\n" + "  PRIMARY KEY (`uuid`))\n" + "ENGINE = InnoDB;";
    public static String SQL_CREATE_CLAIM = "CREATE TABLE IF NOT EXISTS `pzyko_claim` (\n" + "  `id` INT NOT NULL,\n" + "  `world` VARCHAR(45) NOT NULL,\n" + "  `min_x` INT NOT NULL,\n" + "  `min_z` INT NOT NULL,\n" + "  `max_x` INT NOT NULL,\n" + "  `max_z` INT NOT NULL,\n" + "  PRIMARY KEY (`id`))\n" + "ENGINE = InnoDB\n" + "DEFAULT CHARACTER SET = utf8;";
    public static String SQL_CREATE_CLAIM_USER_ROLE = "CREATE TABLE IF NOT EXISTS `pzyko_claim_user_role` (\n" + "  `claim_id` INT NOT NULL,\n" + "  `user_uuid` CHAR(36) NOT NULL,\n" + "  `rolepermission` ENUM('ACCESS', 'CONTAINER', 'BUILD', 'MANAGE') NOT NULL,\n" + "  PRIMARY KEY (`claim_id`, `user_uuid`, `rolepermission`),\n" + "  INDEX `pzyko_fk_claim_has_user_user1_idx` (`user_uuid` ASC),\n" + "  INDEX `pzyko_fk_claim_has_user_claim_idx` (`claim_id` ASC),\n" + "  CONSTRAINT `pzyko_fk_claim_has_user_claim`\n" + "    FOREIGN KEY (`claim_id`)\n" + "    REFERENCES `pzyko_claim` (`id`)\n" + "    ON DELETE NO ACTION\n" + "    ON UPDATE NO ACTION,\n" + "  CONSTRAINT `pzyko_fk_claim_has_user_user1`\n" + "    FOREIGN KEY (`user_uuid`)\n" + "    REFERENCES `pzyko_user` (`uuid`)\n" + "    ON DELETE NO ACTION\n" + "    ON UPDATE NO ACTION)\n" + "ENGINE = InnoDB\n" + "DEFAULT CHARACTER SET = utf8;";
    public static String SQL_CREATE_CLAIM_FLAG = "CREATE TABLE IF NOT EXISTS `pzyko_claim_flag` (\n" + "  `claim_id` INT NOT NULL,\n" + "  `flag` VARCHAR(45) NOT NULL,\n" + "  `value` VARCHAR(45) NULL,\n" + "  PRIMARY KEY (`claim_id`, `flag`),\n" + "  INDEX `pzyko_fk_claim_flag_claim1_idx` (`claim_id` ASC),\n" + "  CONSTRAINT `pzyko_fk_claim_flag_claim1`\n" + "    FOREIGN KEY (`claim_id`)\n" + "    REFERENCES `pzyko_claim` (`id`)\n" + "    ON DELETE NO ACTION\n" + "    ON UPDATE NO ACTION)\n" + "ENGINE = InnoDB\n" + "DEFAULT CHARACTER SET = utf8;";

    public static String SQL_REPLACE_INTO_USER = "REPLACE INTO pzyko_user (uuid, username, last_seen) VALUES (?, ?, ?);";
    public static String SQL_INSERT_INTO_CLAIM = "INSERT INTO pzyko_claim (world, min_x, min_z, max_x, max_z) VALUES (?, ?, ?, ?, ?);";

    public static String PREFIX = "pzyko_";

    private final HikariDataSource dataSource;

    public SQL(String url, String user, String password) {
        this.dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);

        dataSource.setPoolName("PzykoCore-Connection-Pool");

        dataSource.setMinimumIdle(6);
        dataSource.setMaximumPoolSize(12);
        dataSource.setLeakDetectionThreshold(48_000);
        dataSource.addDataSourceProperty("useUnicode", "true");
        dataSource.addDataSourceProperty("characterEncoding", "utf-8");
        dataSource.addDataSourceProperty("rewriteBatchedStatements", "true");

        dataSource.addDataSourceProperty("cachePrepStmts", "true");
        dataSource.addDataSourceProperty("prepStmtCacheSize", "250");
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dataSource.addDataSourceProperty("useServerPrepStmts", true);
        dataSource.addDataSourceProperty("verifyServerCertificate", false);
    }

    public void close() throws IOException {
        dataSource.close();
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public List<String> getTables() {
        try (Connection conn = getConnection()) {
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet rs = dbm.getTables(conn.getCatalog().toLowerCase(), null, "%", new String[]{"TABLE"});
            ArrayList<String> list = new ArrayList<>();
            while (rs != null && rs.next()) {
                list.add(rs.getString("TABLE_NAME").toLowerCase());
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

}
