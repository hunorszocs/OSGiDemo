package hu.blackbelt.core.orika;


import fj.F;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface Mapper {

    /**
     * Create and return a new instance of type D mapped with the properties of
     * <code>sourceObject</code>.
     *
     * @param sourceObject     the object to map from
     * @param destinationClass the type of the new object to return
     * @return a new instance of type D mapped with the properties of
     * <code>sourceObject</code>
     */
    <S, D> D map(S sourceObject, Class<D> destinationClass);

    /**
     * Creates a function that returns a new instance of type D mapped from an instance of type S.
     *
     * @param sourceClass      the type to map from
     * @param destinationClass the type of the new object to return
     * @return a function that returns a new instance of type D mapped from an instance of type S.
     */
    <S, D> F<S, D> mapF(Class<S> sourceClass, Class<D> destinationClass);

    /**
     * Maps the properties of <code>sourceObject</code> onto
     * <code>destinationObject</code>.
     *
     * @param sourceObject      the object from which to read the properties
     * @param destinationObject the object onto which the properties should be mapped
     */
    <S, D> void map(S sourceObject, D destinationObject);

    /**
     * Maps the source iterable into a new Set parameterized by
     * <code>destinationClass</code>.
     *
     * @param source           the Iterable from which to map
     * @param destinationClass the type of elements to be contained in the returned Set.
     * @return a new Set containing elements of type
     * <code>destinationClass</code> mapped from the elements of
     * <code>source</code>.
     */
    <S, D> Set<D> mapAsSet(Iterable<S> source, Class<D> destinationClass);

    /**
     * Maps the source Array into a new Set parameterized by
     * <code>destinationClass</code>.
     *
     * @param source           the Array from which to map
     * @param destinationClass the type of elements to be contained in the returned Set.
     * @return a new Set containing elements of type
     * <code>destinationClass</code> mapped from the elements of
     * <code>source</code>.
     */
    <S, D> Set<D> mapAsSet(S[] source, Class<D> destinationClass);

    /**
     * Maps the source Iterable into a new List parameterized by
     * <code>destinationClass</code>.
     *
     * @param source           the Iterable from which to map
     * @param destinationClass the type of elements to be contained in the returned Set.
     * @return a new List containing elements of type
     * <code>destinationClass</code> mapped from the elements of
     * <code>source</code>.
     */
    <S, D> List<D> mapAsList(Iterable<S> source, Class<D> destinationClass);

    /**
     * Maps the source Array into a new List parameterized by
     * <code>destinationClass</code>.
     *
     * @param source           the Array from which to map
     * @param destinationClass the type of elements to be contained in the returned Set.
     * @return a new List containing elements of type
     * <code>destinationClass</code> mapped from the elements of
     * <code>source</code>.
     */
    <S, D> List<D> mapAsList(S[] source, Class<D> destinationClass);


    /**
     * Maps the source interable into a new Array of type<code>D</code>.
     *
     * @param destination
     * @param source           the Array from which to map
     * @param destinationClass the type of elements to be contained in the returned Set.
     * @return a new Array containing elements of type
     * <code>destinationClass</code> mapped from the elements of
     * <code>source</code>.
     */
    <S, D> D[] mapAsArray(D[] destination, Iterable<S> source, Class<D> destinationClass);

    /**
     * Maps the source array into a new Array of type<code>D</code>.
     *
     * @param destination      the destination array which is also returned
     * @param source           the source array
     * @param destinationClass the destination class
     * @return a new Array containing elements of type
     * <code>destinationClass</code> mapped from the elements of
     * <code>source</code>.
     */
    <S, D> D[] mapAsArray(D[] destination, S[] source, Class<D> destinationClass);


    /**
     * Maps the source Iterable into the destination Collection
     *
     * @param source           the source Iterable
     * @param destination      the destination Collection
     * @param destinationClass the destination class
     */
    <S, D> void mapAsCollection(Iterable<S> source, Collection<D> destination, Class<D> destinationClass);

    /**
     * Map an array onto an existing collection
     *
     * @param source           the source array
     * @param destination      the destination collection
     * @param destinationClass the type of elements in the destination
     */
    <S, D> void mapAsCollection(S[] source, Collection<D> destination, Class<D> destinationClass);

    /**
     * Convert the source object into the appropriate destination type
     *
     * @param source           the source object to map
     * @param destinationClass the type of the destination class to produce
     * @param converterId      the specific converter to use; if null, the first compatible
     *                         global converter is used
     * @return an instance of the converted destination type
     */
    <S, D> D convert(S source, Class<D> destinationClass, String converterId);


}