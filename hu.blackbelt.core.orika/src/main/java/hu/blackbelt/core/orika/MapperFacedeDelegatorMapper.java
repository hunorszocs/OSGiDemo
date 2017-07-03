package hu.blackbelt.core.orika;

import fj.F;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class MapperFacedeDelegatorMapper implements Mapper {

    MapperServiceImpl mapperService;

    public MapperFacedeDelegatorMapper(MapperServiceImpl mapperService) {
        this.mapperService = mapperService;
    }

    @Override
    public <S, D> D map(S sourceObject, Class<D> destinationClass) {
        return mapperService.getMapperFactory().getMapperFacade().map(sourceObject, destinationClass);
    }

    @Override
    public <S, D> F<S, D> mapF(Class<S> sourceClass, final Class<D> destinationClass) {
        return new F<S, D>() {
            @Override
            public D f(S s) {
                return mapperService.getMapperFactory().getMapperFacade().map(s, destinationClass);
            }
        };
    }

    @Override
    public <S, D> void map(S sourceObject, D destinationObject) {
        mapperService.getMapperFactory().getMapperFacade().map(sourceObject, destinationObject);
    }

    @Override
    public <S, D> Set<D> mapAsSet(Iterable<S> source, Class<D> destinationClass) {
        return mapperService.getMapperFactory().getMapperFacade().mapAsSet(source, destinationClass);
    }

    @Override
    public <S, D> Set<D> mapAsSet(S[] source, Class<D> destinationClass) {
        return mapperService.getMapperFactory().getMapperFacade().mapAsSet(source, destinationClass);
    }

    @Override
    public <S, D> List<D> mapAsList(Iterable<S> source, Class<D> destinationClass) {
        return mapperService.getMapperFactory().getMapperFacade().mapAsList(source, destinationClass);
    }

    @Override
    public <S, D> List<D> mapAsList(S[] source, Class<D> destinationClass) {
        return mapperService.getMapperFactory().getMapperFacade().mapAsList(source, destinationClass);
    }

    @Override
    public <S, D> D[] mapAsArray(D[] destination, Iterable<S> source, Class<D> destinationClass) {
        return mapperService.getMapperFactory().getMapperFacade().mapAsArray(destination, source, destinationClass);
    }

    @Override
    public <S, D> D[] mapAsArray(D[] destination, S[] source, Class<D> destinationClass) {
        return mapperService.getMapperFactory().getMapperFacade().mapAsArray(destination, source, destinationClass);
    }

    @Override
    public <S, D> void mapAsCollection(Iterable<S> source, Collection<D> destination, Class<D> destinationClass) {
        mapperService.getMapperFactory().getMapperFacade().mapAsCollection(source, destination, destinationClass);
    }

    @Override
    public <S, D> void mapAsCollection(S[] source, Collection<D> destination, Class<D> destinationClass) {
        mapperService.getMapperFactory().getMapperFacade().mapAsCollection(source, destination, destinationClass);
    }

    @Override
    public <S, D> D convert(S source, Class<D> destinationClass, String converterId) {
        return mapperService.getMapperFactory().getMapperFacade().convert(source, destinationClass, converterId);
    }
}
