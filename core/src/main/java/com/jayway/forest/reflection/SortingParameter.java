package com.jayway.forest.reflection;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 */
final public class SortingParameter extends Touchable implements Iterable<SortParameter> {

    private List<SortParameter> parameters;

    protected SortingParameter( String sortByUrl ) {
        parameters = new LinkedList<SortParameter>();
        String[] sortBy = sortByUrl.split(",");
        for ( String rawParameter : sortBy ) {
            parameters.add( new SortParameter( rawParameter ));
        }
    }

    @Override
    public Iterator<SortParameter> iterator() {
        touch();
        return parameters.iterator();
    }

    public String firstParameterName() {
        touch();
        if ( parameters.isEmpty() ) return "";
        return parameters.get(0).name();
    }

    public String sortByFirstQuery( boolean ascending, Integer pageSize ) {
        touch();
        if ( parameters.isEmpty() ) return "";
        if ( ascending ) {
            return "?sortBy=" + parameters.get(0).name() + "&pageSize="+pageSize;
        } else {
            return "?sortBy=-" + parameters.get(0).name() + "&pageSize="+pageSize;
        }
    }
}
