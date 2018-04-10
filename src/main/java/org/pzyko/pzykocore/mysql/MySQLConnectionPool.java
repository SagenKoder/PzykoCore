package org.pzyko.pzykocore.mysql;

import com.zaxxer.hikari.HikariDataSource;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class MySQLConnectionPool implements Closeable {

    private final HikariDataSource dataSource;

    public MySQLConnectionPool(String url, String user, String password) {
        this.dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);

        dataSource.setMinimumIdle(2);
        dataSource.setPoolName("PzykoCore-Connection-Pool");

        dataSource.addDataSourceProperty("useUnicode", "true");
        dataSource.addDataSourceProperty("characterEncoding", "utf-8");
        dataSource.addDataSourceProperty("rewriteBatchedStatements", "true");

        dataSource.addDataSourceProperty("cachePrepStmts", "true");
        dataSource.addDataSourceProperty("prepStmtCacheSize", "250");
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    }

    public void close() throws IOException {
        dataSource.close();
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
