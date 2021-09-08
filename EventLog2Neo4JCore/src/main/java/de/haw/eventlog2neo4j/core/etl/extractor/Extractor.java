package de.haw.eventlog2neo4j.core.etl.extractor;

/**
 * Use this interface to create an extractor.
 * The task of an extractor is to extract raw data from a source.
 * @param <S> - source
 * @param <O> - output
 */
public interface Extractor<S, O> {

    O extract(S source);

}
