package com.jayway.forest.reflection;

import com.jayway.forest.di.DependencyInjectionSPI;
import com.jayway.forest.roles.FieldComparator;
import com.jayway.forest.roles.Resource;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        Map<String, String[]> queryParams = request.getParameterMap();

        PagingParameter paging = pagingParameter( queryParams );
        SortingParameter sorting = sortingParameter( queryParams );
        dependencyInjectionSPI.addRequestContext( PagingParameter.class, paging );
        dependencyInjectionSPI.addRequestContext( SortingParameter.class, sorting );

        Type type = method.getGenericReturnType();
        Class listElementClass = Object.class;
        Transformer transformer = null;
        if ( type instanceof ParameterizedType ) {
            Type listElementType = ((ParameterizedType) type).getActualTypeArguments()[0];
            // no nested lists yet
            if ( !(listElementType instanceof ParameterizedType) ) {
                listElementClass = (Class) listElementType;
            }
            transformer = transformers.get( listElementClass );
        }
        List<?> list = (List<?>) super.get(request);

        List<Object> resultList = new ArrayList<Object>();
        PagedSortedListResponse response = new PagedSortedListResponse();
        response.setName( name());
        if (!sorting.isTouched()) {
            // the resource has not handled sorting so do it here
            Collections.sort( list, new FieldComparator( sorting.getSortBy(), sorting.getSortOrder() ));
            response.addOrderByAsc(sorting.getSortBy(), name() + "?sortBy=" + sorting.getSortBy() + "&sortOrder=" + SortingParameter.SORT_ORDER_ASCENDING);
            response.addOrderByDesc(sorting.getSortBy(), name() + "?sortBy=" + sorting.getSortBy() + "&sortOrder=" + SortingParameter.SORT_ORDER_DESCENDING);
        }

        if ( paging.isTouched() ) {
            // the resource has handled the paging
            // so just copy the values to the pagedSortedListResponse

            // TODO
        } else {
            // assume resource has not used the parameters
            // to split the result list according to the
            // passed in paging parameters
            Integer page = paging.getPage();
            Integer pageSize = paging.getPageSize();

            // build response
            response.setPage(page);
            response.setPageSize(pageSize);
            response.setTotalElements( list.size() );
            response.setTotalPages( 1+(int)Math.ceil(list.size() / pageSize) );

            int actualListSize = list.size();
            int maxIndex = page * pageSize;
            int minIndex = ( page - 1 )*pageSize;
            if (actualListSize >= minIndex) {
                for ( int i=minIndex; i<actualListSize && i<maxIndex; i++ ) {
                    Object elm = list.get(i);
                    if ( transformer != null ) {
                        resultList.add( transformer.transform( elm ) );
                    } else {
                        resultList.add( elm );
                    }
                }
                response.setList( resultList );

                if ( maxIndex < actualListSize ) {
                    response.setNext( name() + "?page="+(page+1) );
                    if ( sorting.setInURL() ) {
                        response.setNext( response.getNext() + sortingUrlParam( sorting ) );
                    }
                }
                if ( minIndex > 0 ) {
                    response.setPrevious( name() + "?page="+(page-1));
                    if ( sorting.setInURL() ) {
                        response.setPrevious( response.getPrevious() + sortingUrlParam( sorting ));
                    }
                }
            }
        }

        return response;
	}

    private String sortingUrlParam( SortingParameter sorting ) {
        return "&sortBy="+sorting.getSortBy() + "&sortOrder="+sorting.getSortOrder();
    }

    private PagingParameter pagingParameter( Map<String, String[]> queryParams ) {
        String page = getFirst(queryParams, "page");
        String pageSize = getFirst(queryParams, "pageSize");
        Integer pageInt = 1;
        Integer pageSizeInt = 10;
        if ( page != null ) {
            pageInt = Integer.parseInt( page );
        }
        if ( pageSize != null ) {
            pageSizeInt = Integer.parseInt( pageSize );
        }
        return new PagingParameter( pageInt, pageSizeInt );
    }

    private SortingParameter sortingParameter( Map<String, String[]> queryParams ) {
        String sortBy = getFirst(queryParams, "sortBy");
        String sortOrder = getFirst(queryParams, "sortOrder");
        boolean sortingParamterInUrl = false;
        if ( sortBy == null ) {
            sortBy = "name";
        } else {
            sortingParamterInUrl = true;
        }
        if ( sortOrder == null ) {
            sortOrder = SortingParameter.SORT_ORDER_ASCENDING;
        } else {
            sortingParamterInUrl = true;
        }
        return new SortingParameter( sortBy, sortOrder, sortingParamterInUrl);
    }

}
