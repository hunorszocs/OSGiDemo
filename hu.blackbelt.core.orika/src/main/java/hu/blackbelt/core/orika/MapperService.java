package hu.blackbelt.core.orika;

import java.util.Map;

public interface MapperService {

    void registerMap(Class src, Class dest);

    /**
     * @param aClass   subject class A
     * @param bClass   subject class B
     * @param fieldMap custom field map to use when there are differing field names.
     */
    void registerMap(Class aClass, Class bClass, Map<String, String> fieldMap);

    public <A, B> void registerMapping(CustomTransformer<A, B> transformer);

    Mapper getMapper();

}
