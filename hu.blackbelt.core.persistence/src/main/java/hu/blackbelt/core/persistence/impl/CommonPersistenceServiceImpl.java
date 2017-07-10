package hu.blackbelt.core.persistence.impl;

import com.querydsl.sql.*;
import hu.blackbelt.core.persistence.api.CommonPersistenceService;
import hu.blackbelt.core.persistence.entity.Car;
import hu.blackbelt.core.persistence.entity.QCar;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.math.BigInteger;
import java.sql.Connection;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

@Component(immediate = true, service = CommonPersistenceService.class)
@Designate(ocd = CommonPersistenceServiceImpl.Config.class)
@Slf4j
public class CommonPersistenceServiceImpl implements CommonPersistenceService {

    private BundleContext bundleContext;
    private String dataSourceName;
    private String databaseType;

private SQLQueryFactory queryFactory;
    private DataSource dataSource;

    @ObjectClassDefinition(name = "Common Persistence Configuration")
    public @interface Config {
        @AttributeDefinition(name = "Datasource Name")
        String datasource_name();

        @AttributeDefinition(name = "Database Type")
        String database_type();
    }

    @Activate
    @Modified
    public void activate(Config config, BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.dataSourceName = config.datasource_name();
        this.databaseType = config.database_type();

        try {
            final Collection<ServiceReference<DataSource>> srs = bundleContext.getServiceReferences(DataSource.class, "(dataSourceName=" + dataSourceName + ")");
            for (final ServiceReference<DataSource> sr : srs) {
                dataSource = bundleContext.getService(sr);
            }

            queryFactory = new SQLQueryFactory(querydslConfiguration(), dataSource);

        } catch (InvalidSyntaxException ex) {
            System.err.println("Invalid OSGi filter definition, no data is filtered");
        }

        System.err.println("CommonPersistenceService.activated");
    }

    @Deactivate
    public void deactivate() {
        System.err.println("CommonPersistenceService.deactivate");
    }

    public Configuration querydslConfiguration() {
        Configuration configuration = new Configuration(QuerydslDatabaseTemplate.TEMPLATES.get(databaseType));

        return configuration;
    }

    @Override
    public SQLQueryFactory getQueryFactory() {
        return queryFactory;
    }

}
