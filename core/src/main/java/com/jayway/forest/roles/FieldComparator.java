package com.jayway.forest.roles;

import com.jayway.forest.reflection.ReflectionUtil;
import com.jayway.forest.reflection.SortParameter;

import java.lang.reflect.Field;
import java.util.*;

/**
 */
public class FieldComparator implements Comparator<Object> {

    private LinkedList<SortParameter> fieldNames;
    private Map<SortParameter, Field> fieldCache;

    public FieldComparator( SortParameter sortField ) {
        fieldCache = new HashMap<SortParameter, Field>();
        fieldNames = new LinkedList<SortParameter>();
        fieldNames.addLast( sortField );
    }

    public FieldComparator( List<SortParameter> sortParameters ) {
        fieldCache = new HashMap<SortParameter, Field>();
        fieldNames = new LinkedList<SortParameter>();
        for (SortParameter sortParameter : sortParameters) {
            fieldNames.addLast( sortParameter );
        }
    }

    @Override
    public int compare(Object first, Object second) {
        return compare( first, second, fieldNames );
    }

    private int compare(Object first, Object second, LinkedList<SortParameter> fieldNames ) {
        SortParameter sort = fieldNames.getFirst();
        try {
            Field f1 = getField(first.getClass(), sort);
            f1.setAccessible( true );
            Field f2 = getField( second.getClass(), sort );
            f2.setAccessible( true );
            Object v1 = f1.get(first);
            Object v2 = f2.get(second);

            if ( !ReflectionUtil.basicTypes.contains(v1.getClass())) {
                v1 = v1.toString();
                v2 = v2.toString();
            }
            int compare;
            if ( sort.isAscending() ) {
                compare = basicCompare(v1, v2);
            } else {
                compare = basicCompare(v2, v1);
            }
            if ( compare == 0 && fieldNames.size() >1) {
                SortParameter firstField = fieldNames.removeFirst();
                compare = compare(first, second, fieldNames);
                fieldNames.addFirst( firstField );
            }
            return compare;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Field getField( Class clazz, SortParameter sort ) throws NoSuchFieldException {
        if ( fieldCache.containsKey( sort) ) {
            return fieldCache.get(sort);
        }
        try {
            Field field = clazz.getDeclaredField(sort.name());
            if ( !fieldCache.containsKey( sort )) {
                fieldCache.put( sort, field );
            }
            return field;
        } catch (NoSuchFieldException e) {
            if ( clazz == Object.class ) throw e;
            return getField( clazz.getSuperclass(), sort );
        }
    }


    private int basicCompare( Object v1, Object v2 ) {
        if ( v1 instanceof Integer ) {
            return ((Integer) v1).compareTo((Integer) v2);
        } else if ( v1 instanceof Double ) {
            return ((Double) v1).compareTo((Double) v2);
        } else if ( v1 instanceof Long ) {
            return ((Long) v1).compareTo((Long) v2);
        } else if ( v1 instanceof String ) {
            return ((String) v1).compareTo((String) v2);
        } else {
            // boolean
            return v1.toString().compareTo( v2.toString() );
        }
    }
}
