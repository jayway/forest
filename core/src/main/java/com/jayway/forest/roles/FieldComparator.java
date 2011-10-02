package com.jayway.forest.roles;

import com.jayway.forest.reflection.ReflectionUtil;
import com.jayway.forest.reflection.SortingParameter;

import java.lang.reflect.Field;
import java.util.Comparator;

/**
 */
public class FieldComparator implements Comparator<Object> {

    private String fieldName;
    private String direction;

    public FieldComparator( String fieldName, String direction ) {
        this.fieldName = fieldName;
        this.direction = direction;
    }

    @Override
    public int compare(Object first, Object second) {
        try {
            Field f1 = getField(first.getClass());
            f1.setAccessible( true );
            Field f2 = getField( second.getClass() );
            f2.setAccessible( true );
            Object v1 = f1.get(first);
            Object v2 = f2.get(second);

            if ( !ReflectionUtil.basicTypes.contains(v1.getClass())) {
                v1 = v1.toString();
                v2 = v2.toString();
            }
            if ( direction.equals(SortingParameter.SORT_ORDER_ASCENDING)) {
                return basicCompare( v1, v2);
            } else {
                return basicCompare(v2, v1);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Field getField( Class clazz ) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField( fieldName );
        } catch (NoSuchFieldException e) {
            if ( clazz == Object.class ) throw e;
            return getField( clazz.getSuperclass() );
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
