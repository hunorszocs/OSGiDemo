package hu.blackbelt.core.persistence.impl;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.hsqldb.jdbc.JDBCCommonDataSource;
import org.hsqldb.jdbc.JDBCDataSource;
import org.hsqldb.jdbc.JDBCDriver;
import org.hsqldb.jdbc.pool.JDBCPooledDataSource;
import org.hsqldb.jdbc.pool.JDBCXADataSource;
import org.osgi.service.jdbc.DataSourceFactory;

public class HsqldbDataSourceFactory implements DataSourceFactory {

    @Override
    public DataSource createDataSource(Properties props) throws SQLException {
        JDBCDataSource ds = new JDBCDataSource();
        setProperties(ds, props);
        return ds;
    }

    private void setProperties(JDBCCommonDataSource ds, Properties properties) throws SQLException {
        Properties props = (Properties) properties.clone();
        String databaseName = (String) props.remove(DataSourceFactory.JDBC_DATABASE_NAME);
        String url = (String) props.remove(DataSourceFactory.JDBC_URL);
        if (databaseName != null) {
            ds.setDatabaseName(databaseName);
        }
        else if (url != null) {
            ds.setUrl(url);
        }
        else {
            throw new SQLException("missing required property "
                    + DataSourceFactory.JDBC_DATABASE_NAME);

        }

        if (props.containsKey(DataSourceFactory.JDBC_PASSWORD)) {
            String password = (String) props.remove(DataSourceFactory.JDBC_PASSWORD);
            ds.setPassword(password);
        }

        String user = (String) props.remove(DataSourceFactory.JDBC_USER);
        ds.setUser(user);

        if (!props.isEmpty()) {
            throw new SQLException("cannot set properties " + props.keySet());
        }
    }

    @Override
    public ConnectionPoolDataSource createConnectionPoolDataSource(Properties props) throws SQLException {
        JDBCPooledDataSource ds = new JDBCPooledDataSource();
        setProperties(ds, props);
        return ds;
    }

    @Override
    public XADataSource createXADataSource(Properties props) throws SQLException {
        JDBCXADataSource ds = new JDBCXADataSource();
        setProperties(ds, props);
        return ds;
    }

    @Override
    public Driver createDriver(Properties props) throws SQLException {
        JDBCDriver driver = new JDBCDriver();
        return driver;
    }
}