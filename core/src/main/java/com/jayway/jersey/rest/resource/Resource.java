package com.jayway.jersey.rest.resource;

import com.jayway.jersey.rest.RestfulJerseyService;
import com.jayway.jersey.rest.constraint.Constraint;
import com.jayway.jersey.rest.constraint.ConstraintEvaluator;
import com.sun.jersey.api.json.JSONJAXBContext;
import com.sun.jersey.api.json.JSONUnmarshaller;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all resources.
 *
 * This class has the ability to reflectively generate an HTML
 * representation that lists the resources' capabilities
 *
 */
abstract public class Resource {

    Logger log = LoggerFactory.getLogger( Resource.class );

    /**
     * lists the capabilities of this resource.
     *
     * @return string containing html
     */
    public void capabilitiesHtml() {
        try {
            ServletOutputStream os = role(HttpServletResponse.class).getOutputStream();
            List<ResourceMethod> methods = new ArrayList<ResourceMethod>();

            os.print( "<html><h1>" );
            Class<? extends Resource> clazz = this.getClass();
            os.print( clazz.getName() );
            os.println( "</h1>" );
            for ( Method method : clazz.getDeclaredMethods() ) {
                if ( method.isSynthetic() ) continue;
                methods.add(new ResourceMethod(method));
            }
            role( HtmlHelper.class).addResourceMethods( os, methods );

            os.print( "<h2>Index</h2>" );
            if ( this instanceof IndexResource ) {
                ResourceMethod method = findMethod("index");
                if ( !method.isConstraintFalse() ) {
                    try {
                        writeJsonToStream(((IndexResource) this).index(), os);
                    } catch ( Exception e ) {
                        os.print( "Exception thrown when executing 'index'" );
                    }
                }
            }
            os.print("</html>");
            os.flush();
        } catch (IOException e) {
            log.error("Error handling servlet output stream", e);
        }
    }

    public Response post(String method, MultivaluedMap<String,String> formParams, InputStream stream) {
        ResourceMethod m = findMethod( method );
        if ( !m.isCommand() || m.isNotFound() || m.isConstraintFalse() || m.isIdSubResource() ) throw notFound();

        Object[] arguments = stream == null ? arguments(m.method, formParams ) : arguments( m.method, stream );
        return invokeCommand(m.method, this, arguments );
    }


    private Object[] arguments( Method m, InputStream stream ) {
        String contentType = role( HttpServletRequest.class ).getContentType();
        if ( contentType.equals( MediaType.APPLICATION_JSON )) {
            if ( m.getParameterTypes().length == 0 ) return new Object[0];

            Object parse = JSONValue.parse(new InputStreamReader(stream));
            if ( parse == null ) throw badRequest();
            if ( RestfulJerseyService.basicTypes.contains( parse.getClass() ) ) {
                return new Object[] { basicType( parse, m.getParameterTypes()[0]) };
            }
            if ( !(parse instanceof JSONArray) ) throw badRequest();
            JSONArray parsed = (JSONArray) parse;
            Object[] args = new Object[ m.getParameterTypes().length ];
            for ( int i=0; i<args.length; i++) {
                Class<?> type = m.getParameterTypes()[i];
                Object argument = parsed.get(i);
                if ( RestfulJerseyService.basicTypes.contains( type ) ) {
                    args[i] = basicType(argument, type);
                } else {
                    try {
                        Class<?> dtoClass = type.newInstance().getClass();
                        JAXBContext context = JSONJAXBContext.newInstance( dtoClass );
                        JSONUnmarshaller jsonUnmarshaller = JSONJAXBContext.getJSONUnmarshaller( context.createUnmarshaller() );
                        StringReader reader = new StringReader(((JSONObject) argument).toJSONString());
                        args[i] = jsonUnmarshaller.unmarshalFromJSON( reader, dtoClass );
                    } catch ( Exception e ) {
                        throw badRequest( e );
                    }
                }

                // TODO list and maps

            }
            return args;
        }
        // TODO support other types
        throw new WebApplicationException(Response.Status.UNSUPPORTED_MEDIA_TYPE);
    }

    private Object basicType( Object value, Class<?> type ) {
        if ( value instanceof Long && type == Integer.class ) {
            return ((Long) value).intValue();
        } else {
            return value;
        }
    }

    private Object[] arguments( Method m, MultivaluedMap<String, String> formParams ) {
        if ( m.getParameterTypes().length == 0 ) {
            return new Object[0];
        }
        Object[] args = new Object[m.getParameterTypes().length];

        for ( int i=0; i<args.length; i++ ) {
            Class<?> type = m.getParameterTypes()[i];
            args[i] = mapArguments( type, formParams, "argument"+(i+1) );
        }
        return args;
        //Class<?> dto = m.getParameterTypes()[0];
        //return new Object[]{ populateDTO( dto, formParams, dto.getSimpleName() ) };
    }

    private boolean checkConstraint(Method method) {
        for ( Annotation a : method.getAnnotations() ) {
            if ( a.annotationType().getAnnotation(Constraint.class) != null ) {
                if ( !constraintEvaluator( a ) ) return false;
            }
        }
        return true;
    }

    private boolean constraintEvaluator( Annotation annotation ) {
        if ( annotation == null ) return true;
        Constraint constraint = annotation.annotationType().getAnnotation(Constraint.class);
        try {
            ConstraintEvaluator<Annotation, ContextMap> constraintEvaluator = constraint.value().newInstance();
            return constraintEvaluator.isValid( annotation, RestfulJerseyService.getContextMap());

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return true;
    }

    public Resource invokePathMethod( String path ) {
        ResourceMethod method = findMethod(path);
        if ( method.isSubResource() ) {
            try {
                return (Resource) method.method.invoke(this);
            } catch ( IllegalAccessException e) {
                e.printStackTrace();
                throw internalServerError( e );
            } catch ( InvocationTargetException e) {
                if ( e.getCause() instanceof RuntimeException ) {
                    throw (RuntimeException) e.getCause();
                }
                log.error("Error invoking resource method", e);
                throw internalServerError( e );
            }
        } else if ( ( method.isIdSubResource() || method.isNotFound() ) && this instanceof IdResource ) {
            ResourceMethod id = findMethod("id");
            if ( id.isConstraintFalse() ) throw notFound();
            return ((IdResource) this).id( path );
        }
        throw notFound();
    }


    protected <T> T role(Class<T> clazz) {
        ContextMap map = RestfulJerseyService.getContextMap();
        return map.get(clazz);
    }

    protected <T> void addRole( Class<T> clazz, Object instance ) {
        ContextMap map = RestfulJerseyService.getContextMap();
        map.put(clazz, instance);
    }

    public ResourceMethod findMethod( String name ) {
        Class<? extends Resource> clazz = this.getClass();
        for ( Method method : clazz.getDeclaredMethods() ) {
            if ( method.isSynthetic() ) continue;
            if ( method.getName().equals( name ) ) {
                return new ResourceMethod( method );
            }
        }
        return new ResourceMethod();
    }

    public void invokeDelete() {
        if ( this instanceof DeletableResource) {
            ResourceMethod delete = findMethod("delete");
            if ( delete.isConstraintFalse() ) throw notFound();
            (( DeletableResource) this).delete();
        } else {
            throw notAllowed();
        }
    }

    protected <T extends Resource> Response invokeCommand(Method method, T instance, Object... arguments) {
        try {
            method.invoke( instance, arguments );
            return successResponse();
        } catch (InvocationTargetException e) {
            if ( e.getCause() instanceof WebApplicationException ) throw (WebApplicationException) e.getCause();
            else if ( e.getCause() instanceof RuntimeException ) {
                log.error( e.getCause().getMessage(), e);
                throw (RuntimeException) e.getCause();
            }
            throw badRequest( e );
        } catch (IllegalAccessException e) {
            log.error("Could not access command", e);
            throw internalServerError( e );
        }
    }

    private Response successResponse() {
        return Response.ok( "Operation Completed Successfully" ).status(200).build();
    }

    /**
     * Special case for handling html. Data object are converted
     * to JSON when serializing to HTML.
     *
     * @param get
     * @throws IOException
     */
    public void getHtml( String get ) throws IOException {
        writeJsonToStream( get(get), role(HttpServletResponse.class).getOutputStream());
    }

    private void writeJsonToStream(Object result, ServletOutputStream os) throws IOException {
        Providers ps = role(Providers.class);
        MessageBodyWriter uw = ps.getMessageBodyWriter(result.getClass(), result.getClass(), new Annotation[0], MediaType.APPLICATION_JSON_TYPE);
        uw.writeTo(result, result.getClass(), result.getClass(), new Annotation[0], MediaType.APPLICATION_JSON_TYPE, null, os );
        os.flush();
    }

    public Object get( String get ) {
        ResourceMethod m = findMethod(get);
        if ( m.isSubResource() || m.isNotFound() || m.isConstraintFalse() || m.isIdSubResource() ) throw notFound();

        if ( m.isCommand() ) {
            Response response = Response.status( HttpServletResponse.SC_METHOD_NOT_ALLOWED).entity( new HtmlHelper().createForm( m.method, HttpMethod.POST ) ).build();
            throw new WebApplicationException( response );
        }

        MultivaluedMap<String, String> queryParams = role(UriInfo.class).getQueryParameters();
        if ( queryParams.size() == 0 && m.method.getParameterTypes().length > 0) {
            return new HtmlHelper().createForm(m.method, HttpMethod.GET );
        } else {
            try {
                if ( m.method.getParameterTypes().length == 0 ) {
                    return m.method.invoke( this );
                }
                Object[] args = new Object[m.method.getParameterTypes().length];
                for ( int i=0; i<args.length; i++) {
                    Class<?> type = m.method.getParameterTypes()[i];
                    args[i] = mapArguments( type, queryParams, "argument"+(i+1));
                }
                return m.method.invoke( this, args );
                //Class<?> dto = m.method.getParameterTypes()[0];
                //return m.method.invoke(this, populateDTO( dto, queryParams, dto.getSimpleName() ) );
            } catch ( IllegalAccessException e) {
                throw internalServerError( e );
            } catch ( InvocationTargetException e) {
                throw internalServerError( e );
            }
        }
    }

    private Object mapArguments( Class<?> dto, MultivaluedMap<String, String> formParams, String prefix ) {
        try {
            if ( RestfulJerseyService.basicTypes.contains( dto ) ) {
                return mapBasic( dto, formParams.getFirst( prefix ) );
            }
            // TODO handle list & map

            return populateDTO(dto, formParams, prefix + "." + dto.getSimpleName());
        } catch (Exception e) {
            throw badRequest( e );
        }
    }

    private Object populateDTO(Class<?> dto, MultivaluedMap<String, String> formParams, String prefix ) throws Exception {
        Object o = dto.newInstance();
        for ( Field f : o.getClass().getDeclaredFields() ) {
            if ( Modifier.isFinal( f.getModifiers() ) ) continue;
            f.setAccessible(true);
            String value = formParams.getFirst(prefix + "." + f.getName());

            if ( value == null ) {
                Object innerDto = populateDTO( f.getType(), formParams, prefix + "." +f.getName() );
                f.set( o, innerDto );
            } else {
                f.set( o, mapBasic( f.getType(), value ));
            }
        }
        return o;
    }

    private Object mapBasic( Class<?> clazz, String value ) {
        if( clazz == String.class ) {
            return value;
        } else if ( clazz == Integer.class ) {
            return Integer.valueOf( value );
        } else if ( clazz == Long.class ) {
            return Long.valueOf( value );
        } else if ( clazz == Double.class ) {
            return Double.valueOf( value );
        } else if ( clazz == Boolean.class ) {
            return Boolean.valueOf( value );
        } else if ( clazz.isEnum() ) {
            return Enum.valueOf((Class<Enum>) clazz, value);
        } else {
            return null;
        }
    }

    private WebApplicationException notFound() {
        return new WebApplicationException( Response.Status.NOT_FOUND );
    }

    private WebApplicationException notAllowed() {
        return new WebApplicationException( HttpServletResponse.SC_METHOD_NOT_ALLOWED );
    }

    private WebApplicationException internalServerError( Exception e ) {
        log.error( "Internal error", e );
        return new WebApplicationException( e, Response.Status.INTERNAL_SERVER_ERROR );
    }

    private WebApplicationException badRequest( Exception e ) {
        return new WebApplicationException( e, Response.Status.BAD_REQUEST );
    }

    private WebApplicationException badRequest() {
        return new WebApplicationException( Response.Status.BAD_REQUEST );
    }

    class ResourceMethod {
        private MethodType type = MethodType.NOT_FOUND;
        private Method method;
        private String name;

        public ResourceMethod() {}
        public ResourceMethod( Method method ) {
            this.method = method;
            this.name = method.getName();

            if ( Modifier.isAbstract( method.getModifiers()) ) return;
            if ( !Modifier.isPublic( method.getModifiers())) return;
            if ( !checkConstraint(method) ) {
                type = MethodType.CONSTRAINT_FALSE;
                return;
            }

            //if ( argumentCheck( method.getParameterTypes() ) ) {
            handleReturnType( method.getReturnType() );
            //}
        }

        private boolean argumentCheck( Class<?>[] parameterTypes ) {
            return parameterTypes.length <= 1;
        }

        private void handleReturnType( Class<?> returnType ) {
            if ( returnType.equals( Void.TYPE ) ) {
                type = MethodType.COMMAND;
            } else if ( Resource.class.isAssignableFrom( returnType ) ) {
                if ( method.getParameterTypes().length == 0 ) type = MethodType.SUBRESOURCE;
                else type = MethodType.ID_RESOURCE;
            } else {
                type = MethodType.QUERY;
            }
        }
        public MethodType type() {
            return type;
        }
        public String name() {
            return name;
        }

        public boolean isCommand() {
            return type == MethodType.COMMAND;
        }
        public boolean isQuery() {
            return type == MethodType.QUERY;
        }
        public boolean isSubResource() {
            return type == MethodType.SUBRESOURCE;
        }
        public boolean isNotFound() {
            return type == MethodType.NOT_FOUND;
        }
        public boolean isConstraintFalse() {
            return type == MethodType.CONSTRAINT_FALSE;
        }
        public boolean isIdSubResource() {
            return type == MethodType.ID_RESOURCE;
        }
    }

    private enum MethodType {
        COMMAND, QUERY, SUBRESOURCE, NOT_FOUND, CONSTRAINT_FALSE, ID_RESOURCE
    }

}
