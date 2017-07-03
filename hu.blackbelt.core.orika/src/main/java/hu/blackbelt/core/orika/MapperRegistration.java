package hu.blackbelt.core.orika;

import java.io.Serializable;
import java.util.Map;

/**
 * This holds the registrated classes, field maps and custom transformers
 */
public class MapperRegistration implements Serializable {
    private Class src;
    private Class dest;
    private Map<String, String> fieldMap;
    private CustomTransformer customTransformer;

    public MapperRegistration(Class src, Class dest) {
        this.src = src;
        this.dest = dest;
    }

    public MapperRegistration(Class src, Class dest, Map<String, String> fieldMap) {
        this.src = src;
        this.dest = dest;
        this.fieldMap = fieldMap;
    }

    public MapperRegistration(Class src, Class dest, CustomTransformer customTransformer) {
        this.src = src;
        this.dest = dest;
        this.customTransformer = customTransformer;
    }

    public Class getSrc() {
        return src;
    }

    public void setSrc(Class src) {
        this.src = src;
    }

    public Class getDest() {
        return dest;
    }

    public void setDest(Class dest) {
        this.dest = dest;
    }

    public Map<String, String> getFieldMap() {
        return fieldMap;
    }

    public void setFieldMap(Map<String, String> fieldMap) {
        this.fieldMap = fieldMap;
    }

    public CustomTransformer getCustomTransformer() {
        return customTransformer;
    }

    public void setCustomTransformer(CustomTransformer customTransformer) {
        this.customTransformer = customTransformer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MapperRegistration)) return false;

        MapperRegistration that = (MapperRegistration) o;

        if (!dest.equals(that.dest)) return false;
        if (!src.equals(that.src)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = src.hashCode();
        result = 31 * result + dest.hashCode();
        return result;
    }
}
