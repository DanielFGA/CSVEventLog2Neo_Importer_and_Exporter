package de.haw.eventlog2neo4j.core.etl.extractor.impl;

import de.haw.eventlog2neo4j.core.etl.extractor.Extractor;

/**
 * Idle extractor which does nothing but return the input data as output data.
 * @param <D>
 */
public class IdleExtractor<D> implements Extractor<D, D> {
    @Override
    public D extract(D source) {
        return source;
    }
}
