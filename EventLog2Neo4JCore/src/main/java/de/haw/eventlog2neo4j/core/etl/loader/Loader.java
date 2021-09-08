package de.haw.eventlog2neo4j.core.etl.loader;

/**
 * Use this interface to create a loader.
 * The task of a loader is to load data into a target.
 * @param <D> - data
 */
public interface Loader<D> {

    void load(D data);
}
