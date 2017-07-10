package hu.blackbelt.core.persistence.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QCar is a Querydsl query type for Car
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QCar extends com.querydsl.sql.RelationalPathBase<Car> {

    private static final long serialVersionUID = 1634648706;

    public static final QCar car = new QCar("CAR");

    public final NumberPath<java.math.BigInteger> id = createNumber("id", java.math.BigInteger.class);

    public final StringPath licenseplate = createString("licenseplate");

    public final StringPath rim = createString("rim");

    public final NumberPath<java.math.BigInteger> speed = createNumber("speed", java.math.BigInteger.class);

    public QCar(String variable) {
        super(Car.class, forVariable(variable), "SANDBOX", "CAR");
        addMetadata();
    }

    public QCar(String variable, String schema, String table) {
        super(Car.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QCar(Path<? extends Car> path) {
        super(path.getType(), path.getMetadata(), "SANDBOX", "CAR");
        addMetadata();
    }

    public QCar(PathMetadata metadata) {
        super(Car.class, metadata, "SANDBOX", "CAR");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.DECIMAL).withSize(0).notNull());
        addMetadata(licenseplate, ColumnMetadata.named("LICENSEPLATE").withIndex(3).ofType(Types.VARCHAR).withSize(20).notNull());
        addMetadata(rim, ColumnMetadata.named("RIM").withIndex(4).ofType(Types.VARCHAR).withSize(30));
        addMetadata(speed, ColumnMetadata.named("SPEED").withIndex(2).ofType(Types.DECIMAL).withSize(20));
    }

}

