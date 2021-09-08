package de.haw.eventlog2neo4j.core.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Tuple<A, B> {

    private A left;
    private B right;

}
