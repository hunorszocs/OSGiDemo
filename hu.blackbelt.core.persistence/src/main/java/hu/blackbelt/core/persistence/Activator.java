package hu.blackbelt.core.persistence;

import hu.blackbelt.core.persistence.api.CommonPersistenceService;
import hu.blackbelt.core.persistence.impl.CommonPersistenceServiceImpl;
import org.hsqldb.jdbc.JDBCDataSource;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.sql.Connection;
import java.util.Dictionary;
import java.util.Hashtable;

public class Activator implements BundleActivator {

    private ServiceRegistration serviceRegistration;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        JDBCDataSource ds = new JDBCDataSource();
        ds.setURL("jdbc:hsqldb:hsql://localhost/testdb");
        ds.setDatabaseName("testdb");
        ds.setUser("RON");
        ds.setPassword("strong-random-password");

        try (Connection con = ds.getConnection()) {
            Dictionary props = new Hashtable<>();
            props.put("commonPersistenceService", "testdb");
            serviceRegistration = bundleContext.registerService(CommonPersistenceService.class, new CommonPersistenceServiceImpl(), props);
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
