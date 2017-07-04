package hu.blackbelt.core.persistence;

import hu.blackbelt.core.persistence.impl.HsqldbDataSourceFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

public class Activator implements BundleActivator {

    private ServiceRegistration serviceRegistration;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        HsqldbDataSourceFactory dsf = new HsqldbDataSourceFactory();

        Properties dataSourceProperties = new Properties();
        dataSourceProperties.setProperty("url", "jdbc:hsqldb:hsql://localhost/testdb");
        dataSourceProperties.setProperty("user", "RON");
        dataSourceProperties.setProperty("password", "strong-random-password");
        dataSourceProperties.setProperty("databaseName", "testdb");

        DataSource ds = dsf.createDataSource(dataSourceProperties);

        try (Connection con = ds.getConnection()) {
            Dictionary props = new Hashtable<>();
            props.put("dataSourceReady", "testdb");
            serviceRegistration = bundleContext.registerService(DataSource.class, ds, props);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        serviceRegistration.unregister();
    }
}
