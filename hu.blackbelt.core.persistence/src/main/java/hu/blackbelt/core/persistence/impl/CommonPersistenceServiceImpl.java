package hu.blackbelt.core.persistence.impl;

import com.querydsl.sql.Configuration;
import com.querydsl.sql.HSQLDBTemplates;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import hu.blackbelt.core.persistence.api.CommonPersistenceService;
import hu.blackbelt.core.persistence.entity.Car;
import hu.blackbelt.core.persistence.entity.QCar;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.hsqldb.jdbc.JDBCDataSource;

@Component(immediate = true, service = CommonPersistenceService.class)
public class CommonPersistenceServiceImpl implements CommonPersistenceService {

    private DataSource dataSource;

    @Activate
    public void activate() {
        this.dataSource = this.dataSource();
        this.querydslConfiguration();

        try {
            this.test();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.err.println("CommonPersistenceService.activated");
    }

    @Deactivate
    public void deactivate() {
        System.err.println("CommonPersistenceService.deactivate");
    }

    public DataSource dataSource() {
        JDBCDataSource ds = new JDBCDataSource();
        ds.setURL("jdbc:hsqldb:hsql://localhost/testdb");
        ds.setDatabaseName("testdb");
        ds.setUser("RON");
        ds.setPassword("strong-random-password");

        return ds;
    }

    public Configuration querydslConfiguration() {
        SQLTemplates templates = HSQLDBTemplates.builder()
                .printSchema()
                .quote()
                .newLineToSingleSpace()
                .build();
        Configuration configuration = new Configuration(templates);

        return configuration;
    }

    public void test() throws Exception {
        try (Connection con = dataSource.getConnection()) {
            QCar car = new QCar("c");

            SQLQueryFactory queryFactory = new SQLQueryFactory(querydslConfiguration(), dataSource);

            List<Long> cars = queryFactory.select(car.count()).from(car).fetch();

            System.out.println(".......... Juhejj, elertunk idaig:  " + cars.toString());

            List<Double> lastNames = queryFactory.select(car.speed).from(car).fetch();

            System.out.println(".......... Juhejj, elertunk idaig:  " + lastNames.toString());

        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Override
    public <T> List<T> listAll(Class T) {
        List<T> cars = new ArrayList<>();
        Car car = new Car();
//        car.setLicensePlate("AAA-123");
        car.setRim("BSD");
        car.setSpeed(120.0);
        cars.add((T) car);

        return cars;
        //return list("Select * from car c;");
    }

    protected <T> List<T> list(String jpql) {
        //Query query = entityManager.createQuery(jpql);
        return null; //query.getResultList();
    }


}
