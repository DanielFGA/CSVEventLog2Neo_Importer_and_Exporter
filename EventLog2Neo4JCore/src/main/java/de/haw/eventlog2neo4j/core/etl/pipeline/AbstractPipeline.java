package de.haw.eventlog2neo4j.core.etl.pipeline;

import de.haw.eventlog2neo4j.core.etl.extractor.Extractor;
import de.haw.eventlog2neo4j.core.etl.loader.Loader;
import de.haw.eventlog2neo4j.core.etl.transformer.Transformer;
import lombok.RequiredArgsConstructor;


/**
 * Use this class to create a Pipeline.
 * @param <S> - the type of the source
 * @param <R> - the type of the raw data
 * @param <T> - the type of the transformed data
 */
@RequiredArgsConstructor
public abstract class AbstractPipeline<S, R, T> {

    private final Extractor<S, R>  extractor;
    private final Transformer<R, T> transformer;
    private final Loader<T> loader;

    public void run(S source) {
        R rawData = extractor.extract(source);
        T transformedData = transformer.transform(rawData);
        loader.load(transformedData);
    }

}
