package hu.blackbelt.core.orika;

import com.google.common.base.Preconditions;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.TypeFactory;

/**
 * @author Akos Mocsanyi
 */
public final class CustomTransformerAdapter<A, B> extends CustomMapper<A, B> {
    private final CustomTransformer<A, B> transformer;


    public CustomTransformerAdapter(CustomTransformer<A, B> transformer) {
        Preconditions.checkNotNull(transformer, "transformer must not be null");

        this.transformer = transformer;

        this.aType = TypeFactory.valueOf(transformer.getAType());
        this.bType = TypeFactory.valueOf(transformer.getBType());
    }

    @Override
    public void mapAtoB(A a, B b, MappingContext context) {
        transformer.mapAtoB(a, b);
    }

    @Override
    public void mapBtoA(B b, A a, MappingContext context) {
        transformer.mapBtoA(b, a);
    }
}
