package com.jayway.forest.reflection;

import com.jayway.forest.di.DependencyInjectionSPI;
import com.jayway.forest.roles.FieldComparator;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.roles.Resource;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.*;
import java.util.*;

public class QueryForListCapability extends QueryCapability {

    private DependencyInjectionSPI dependencyInjectionSPI;
    private Map<Class, Transformer> transformers;

    public QueryForListCapability(DependencyInjectionSPI dependencyInjectionSPI, Map<Class, Transformer> transformers, Resource resource, Method method, String name, String documentation) {
        super(resource, method, name, documentation);
        this.dependencyInjectionSPI = dependencyInjectionSPI;
        this.transformers = transformers;
    }

    @Override
    public PagedSortedListResponse get(HttpServletRequest request) {
        // setup Paging and Sorting parameter
        UrlParameter urlParameter = new UrlParameter(request.getParameterMap());
        PagingSortingParameter pagingSortingParameter = pagingParameter( urlParameter );
        dependencyInjectionSPI.addRequestContext( PagingSortingParameter.class, pagingSortingParameter );

        // actual call to the resource method
        List<?> returnedList = (List<?>) super.get(request);
        boolean touched = pagingSortingParameter.isTouched();

        Class listElementClass = inferListType();
        Transformer transformer = null;
        if ( transformers != null ) {
            transformer = transformers.get( listElementClass );
        }
        if ( listElementClass == Linkable.class || transformer != null || urlParameter.sortBy() != null) {
            // If there is a transformer, we have to transform all elements
            transformList(returnedList, transformer);
            // since this is a linkable we must be able to sort on href and name
            pagingSortingParameter.addSortByField( "href" );
            pagingSortingParameter.addSortByField( "name" );
        }

        PagedSortedListResponse response = new PagedSortedListResponse();
        response.setName(name());
        urlParameter.setPageSize(pagingSortingParameter.getPageSize());
        if ( touched ) {
            // the resource has handled the paging
            // so just copy the values to the pagedSortedListResponse
            response.setPage( pagingSortingParameter.getPage() );
            response.setList( returnedList );
            response.setPageSize( pagingSortingParameter.getPageSize() );
            response.setTotalElements( pagingSortingParameter.getTotalElements() );
            response.setTotalPages( calculateTotalPages(response.getTotalElements(), response.getPageSize()) );
            if ( pagingSortingParameter.getPage() < response.getTotalPages() ) {
                response.setNext( name() + urlParameter.linkTo( pagingSortingParameter.getPage()+1) );
            }
            if ( pagingSortingParameter.getPage() > 1 ) {
                response.setPrevious( name() + urlParameter.linkTo( pagingSortingParameter.getPage()-1) );
            }

            for ( String sortField : pagingSortingParameter.getAddedSortFields() ) {
                response.addOrderByAsc( sortField, name() + urlParameter.linkSortBy(sortField, true));
                response.addOrderByDesc(sortField, name() + urlParameter.linkSortBy(sortField, false));
            }
        } else {
            // assume resource has not used the parameters
            // to split the result list according to the
            // passed in paging parameters
            if ( returnedList != null && !returnedList.isEmpty() ) {
                List<String> sortingParameters = new LinkedList<String>();
                inferSortParameters(sortingParameters, returnedList.get(0).getClass());

                for (String sortField : sortingParameters) {
                    response.addOrderByAsc( sortField, name() + urlParameter.linkSortBy(sortField, true));
                    response.addOrderByDesc(sortField, name() + urlParameter.linkSortBy(sortField, false));
                }
                Collections.sort(returnedList, new FieldComparator(pagingSortingParameter.sortParameters() ));
            }

            Integer page = pagingSortingParameter.getPage();
            Integer pageSize = pagingSortingParameter.getPageSize();

            // build response
            response.setPage(page);
            response.setPageSize(pageSize);
            response.setTotalElements( returnedList == null ? 0 : ((Integer)returnedList.size()).longValue() );
            response.setTotalPages(  calculateTotalPages(response.getTotalElements(), response.getPageSize()) );

            long actualListSize = response.getTotalElements();
            int maxIndex = page * pageSize;
            int minIndex = ( page - 1 )*pageSize;
            if (actualListSize >= minIndex) {
                List<Object> resultList = new ArrayList<Object>();
                for ( int i=minIndex; i<actualListSize && i<maxIndex; i++ ) {
                    resultList.add(returnedList.get(i));
                }
                response.setList( resultList );

                if ( maxIndex < actualListSize ) {
                    response.setNext( name() + urlParameter.linkTo( pagingSortingParameter.getPage()+1) );
                }
                if ( minIndex > 0 ) {
                    response.setPrevious( name() + urlParameter.linkTo( pagingSortingParameter.getPage()-1) );
                }
            }
        }

        return response;
    }

    // TODO handle more complicated lists
    private Class inferListType() {
        Type type = method.getGenericReturnType();
        Class listElementClass = Object.class;
        if ( type instanceof ParameterizedType) {
            Type listElementType = ((ParameterizedType) type).getActualTypeArguments()[0];
            // no nested lists yet
            if ( !(listElementType instanceof ParameterizedType) ) {
                listElementClass = (Class) listElementType;
            }
        }
        return listElementClass;
    }

    private void inferSortParameters(List<String> sortingParameters, Class<?> clazz) {
        if ( clazz != Object.class ) {
            for (Field field : clazz.getDeclaredFields()) {
                if ( Modifier.isStatic( field.getModifiers() )) continue;
                if ( Modifier.isFinal( field.getModifiers())) continue;
                sortingParameters.add( field.getName() );
            }
            inferSortParameters( sortingParameters, clazz.getSuperclass() );
        }
    }

    private int calculateTotalPages( Long totalElements, Integer pageSize ) {
        return 1+(int)Math.ceil(totalElements / pageSize );
    }

    private void transformList(List list, Transformer transformer) {
        if ( list != null && list.size() > 0 && transformer != null ) {
            for ( int i=0; i<list.size(); i++ ) {
                list.add(i, transformer.transform(list.remove(i)));
            }
        }
    }

    private PagingSortingParameter pagingParameter( UrlParameter urlParameter ) {
        // todo set as a property of the application
        Integer pageSize = 10;
        if ( urlParameter.pageSize() != null ) {
            pageSize = urlParameter.pageSize();
        }
        String sortBy = urlParameter.sortBy();
        if ( sortBy == null ) {
            // default sorting
            sortBy = "name";
        }
        return new PagingSortingParameter( urlParameter.page(), pageSize, sortBy );
    }

}
