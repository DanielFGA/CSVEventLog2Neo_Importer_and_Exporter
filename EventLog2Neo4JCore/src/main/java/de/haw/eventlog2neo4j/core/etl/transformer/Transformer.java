package de.haw.eventlog2neo4j.core.etl.transformer;

/**
 * Use this interface to create a transformer.
 * The task of a transformer is to transform input data into output data.
 * @param <I> - input data
 * @param <O> - output data
 */
public interface Transformer<I, O> {

    O transform(I data);

}
