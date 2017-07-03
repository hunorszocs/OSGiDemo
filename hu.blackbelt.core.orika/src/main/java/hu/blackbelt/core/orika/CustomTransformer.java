package hu.blackbelt.core.orika;

/**
 * for customizing orika behaviour
 *
 * @author Akos Mocsanyi
 */
public interface CustomTransformer<A, B> {

    public Class<A> getAType();

    public Class<B> getBType();

    public void mapAtoB(A a, B b);

    public void mapBtoA(B b, A a);
}
