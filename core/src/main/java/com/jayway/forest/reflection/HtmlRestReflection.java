package com.jayway.forest.reflection;

import com.jayway.forest.roles.Linkable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

public final class HtmlRestReflection implements RestReflection {
	
	public static final RestReflection INSTANCE = new HtmlRestReflection();
	
	private HtmlRestReflection() {
	}

	@Override
	public Object renderCapabilities(Capabilities capabilities) {
        StringBuilder results = new StringBuilder( );
        results.append("<h1>"+ capabilities.getName()  +"</h1>");
        if (!capabilities.getQueries().isEmpty()) {
            results.append("<h2>Queries</h2>");
            results.append("<ul>");
	    	for (ResourceMethod method: capabilities.getQueries()) {
	    		appendMethod( results, method );
	    	}
            results.append("</ul>");
        }
        if (!capabilities.getCommands().isEmpty()) {
            results.append("<h2>Commands</h2>");
            results.append("<ul>");
	    	for (ResourceMethod method: capabilities.getCommands()) {
	    		appendMethod( results, method );
	    	}
            results.append("</ul>");
        }
        if (!capabilities.getResources().isEmpty() || !capabilities.getDiscovered().isEmpty()) {
            results.append("<h2>Sub Resources</h2>");
            results.append("<ul>");

            for (ResourceMethod method: capabilities.getResources()) {
	            appendMethod(results, method );
	    	}
            for (Linkable resource: capabilities.getDiscovered()) {
                if ( resource == null ) continue;
                results.append("<li><a href='").append(resource.id()).append("/'>").append(resource.name()).append("</a></li>");
            }
            results.append("</ul>");
        }
        if (capabilities.getDescriptionResult() != null ) {
            results.append("<h2>Description</h2>").append( capabilities.getDescriptionResult());
        }
		return results.toString();
	}

    private void appendMethod( StringBuilder sb, ResourceMethod method ) {
        sb.append("<li><a href='").append( method.name() );
        if ( method.isSubResource() ) {
            sb.append("/");
        }
        sb.append("'>").append( method.name() ).append("</a>");
        if ( method.isDocumented() ) {
            sb.append(" <i>(").append( method.documentation() ).append("</i>)");
        }
        sb.append("</li>");
    }

	@Override
	public Object renderCommandForm(Method method) {
		return createForm(method, "POST");
	}

	@Override
	public Object renderQueryForm(Method method) {
		return createForm(method, "GET");
	}

    /**
     * Generates an HTML form based on the method argument.
     * It will reflectively look at the argument for the method,
     * which has to be a single argument of DTO type, and
     * construct the form based on that
     *
     * @param method
     * @return html form getting the parameters needed for the method
     */
    protected String createForm( Method method, String httpMethod ) {
        Class<?>[] types = method.getParameterTypes();
        StringBuilder sb = new StringBuilder();
        sb.append( "<html><body><form name='generatedform' action='").append(method.getName()).
                append("' method='").append(httpMethod).append("' >" );

        for ( int i=0; i<types.length; i++ ) {
            Class<?> type = types[i];
            create( "argument"+(i+1), type, sb, type.getSimpleName() );
        }
        /*for ( Class<?> type : types ) {
            createForm( type.getSimpleName(), type, sb, type.getSimpleName() );
        }*/
        return sb.append( "<input type='submit' /></form></body></html>" ).toString();
    }

    private void create( String legend, Class<?> dto, StringBuilder sb, String typeName ) {
        sb.append("<fieldset><legend>").append(legend).append("</legend>");
        if ( ReflectionUtil.basicTypes.contains( dto ) ) {
            sb.append(typeName).append(": <input type='text' ").
                    append("name='").append( legend ).append("'/></br>");
        } else if ( List.class.isAssignableFrom( dto ) ) {
            // TODO
        } else if ( Map.class.isAssignableFrom( dto ) ) {
            // TODO
        } else {
            // dto type
            createForm( dto.getSimpleName(), dto, sb, legend + "." + dto.getSimpleName() );
        }

        sb.append("</fieldset>");
    }

    private void createForm( String legend, Class<?> dto, StringBuilder sb, String fieldPath ) {
        sb.append("<fieldset><legend>").append(legend).append("</legend>");
        for ( Field f : dto.getDeclaredFields() ) {
            if ( Modifier.isFinal(f.getModifiers())) continue;
            String name = f.getName();
            Class<?> type = f.getType();
            // this must be one of the getters
            if (ReflectionUtil.basicTypes.contains(  type ) ) {
                sb.append(name).append(": <input type='").
                        append( name.equals("password")? "password": "text" ).
                        append("' name='").append( fieldPath ).append( "." ).append( name).append("'/></br>");
            } else if ( type.isEnum() ) {
                sb.append(name).append( ": <select name='").append( fieldPath ).append( "." ).append(name).append("'>");
                for ( Object o : type.getEnumConstants() ) {
                    sb.append( "<option value='").append(o).append("'>").append(o).append("</option>");
                }
                sb.append("</select></br>");
            } else {
                // for now assume DTO subtype
                // TODO List & Map, (any other???)
                createForm( name, type, sb, fieldPath + "." + name );
            }
        }
        sb.append("</fieldset>");
    }

}
