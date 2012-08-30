(function( $ ){

    var data = {};
    var root;
    var views;
    var cache = {};
    var _ignoreHashChange = false;
    var defaultView;

    $.setRoot = function( rootUrl ) {
        root = rootUrl;
        return $;
    }

    $.setViews = function( viewArray ) {
        views = viewArray;
        defaultView = viewArray[0];

        // install view template cache
        var newDiv = $("<div id='viewcache'></div>");
        $('#master').after( newDiv.hide() );
        viewCache = newDiv;

        // install hash change listener
        $(function(){
            $(window).bind( "hashchange", hashChange );
        });
        // Manually trigger the initial hashchange event.
        hashChange();
        return $;
    }

    $.fn.addPaginationClick = function( url, callback, step ) {
        if ( url ) {
            var hash = parseHash();
            // since rel="next" is understood we can construct
            // the link to the next just needs the parameters
            var param = url.substring( url.indexOf('?')+1, url.length );
            hash.arguments[ step ] = $.URLEncode( param );
            this.attr('href', toUrl( hash )).unbind().click(
            function() {
                $.ajax({ url: url, success: callback });
                history( $(this).attr('href'), true  );
                return false;
            }).removeClass("disabled");
        } else {
            this.unbind().click( disabled ).addClass("disabled");
        }
        return this;
    }

    $.fn.appendViewLink = function( view, link ) {
        this.append( "<a href='#" + view.name + "?useCase=" + relativeUrl( link.uri )+ "'>"+link.name+"</a>" );
        return this;
    }

    $.fn.clickLink = function( view, link ) {
        this.unbind().click( function() {
            window.location = '#' + view.name + "?useCase=" + relativeUrl( link.uri );
        });
        return this;
    }

    $.fn.viewLink = function( view, relURI, url ) {
        var rel = getRel( relURI );
        var dependencies = matchUrlAndTemplate( rel.urlTemplate, url );
        var entities = "";
        var first = true;
        $.each( dependencies, function(key, value) {
            entities += (first ? '?' : '&') + key + '=' value;
            first = false;
        });
        this.unbind().click( function() {
            window.location = '#' + view.name + entities;
        });
        return this;
    }

    $.fn.navigationLink = function( view, relURI, url ) {
        // when clicked figure out if we are leaving or staying
        // in the same view. If we are leaving let renderView
        // handle the rendering (it is a whole view that
        // needs to be set up. If we are staying in the
        // same view only a small piece of the url is
        // changing, so only partially update the view.

        /*
          #view?entity1=id1&...&entityn=idn&rel=(p1=v1,...)
        */
        this.unbind().click( function() {
            if ( window.location.startsWith( '#' + view.name ) ) {
                // diff the url parameters and invoke the changed rel:s

            } else {
                // we are changing the view to let renderView handle it
                renderView();
            }
        });
    }


    $.fn.interactionLink = function( relURI, ids, callback ) {
        var rel = getRel( relURI );

        this.unbind().click( function() {
            var queryDTO = {};
            // I would need some field ID, parameter name, parameter type
            $.each( ids, function( index, id ) {
                var elm = $.find( id );
                queryDTO[ elm.name() ] = elm.value();
            });
            $.ajax({
                async: false,
                url: url,
                type: rel.method,
                contentType: "application/json",
                data: queryDTO,
                success: function( data ) {
                    callback( data );
                },
                error: function(obj, msg, xhr) {
                    // error handling
                }
            });
        });
    }

    // parsing the string of form 'view?entity=a&entity2=b&lookup=(urlencoded)&...'
    function parseParameters( parameterString ) {
        var result = {};
        if ( -1 != parameterString.indexOf('?') ) {
            var split = parameterString.split('?');
            result.view = split[0];

            var args = split[1].split('&');
            $.each( args, function( i, val) {

            });
        } else {
            result.view = parameterString;
        }
        return result;
    }

    function findView( viewName ) {
        var view;
        $.each( views, function( i, current ) {
            if ( current.name == viewName ) {
                view = current;
                return false;
            }
        });
        return view;
    }

    function relativeUrl( uri ) {
        var idx = uri.indexOf( root );
        return uri.substring( idx + root.length );
    }

    $.fn.interactionClick = function( interaction, state ) {
        this.unbind().click( function() { executeInteraction( interaction, state ) } );
        return this;
    };

    function disabled() {
        return false;
    }

    function findNamespaces( url ) {
        var namespaces = [ url ];
        $.ajax({
            async: false,
            url: url,
            success: function( data ) {
                $.each( data.links, function(idx, elm) {
                    if ( !elm.rel ) {
                        namespaces.push( elm.uri );
                    }
                });
            },
            error: function() {
                namespaces = [];
            }
        });
        return namespaces;
    }

    function initializeStepCache( url) {
        $.ajax({
            async: false,
            url: url,
            success: function( data ) {
                $.each( data.links, function( idx, elm) {
                    if ( elm.rel ) {
                        cache[ elm.rel ] = elm;
                    } else {
                        initializeStepCache( elm.uri );
                    }
                });
            },
            error: function( jqXHR, textStatus, errorThrown ) {
                if ( jqXHR.status == 404 ) {
                    regenerateUseCase();
                } else {
                    alert( "Error: "+url+''+ jqXHR.status );
                }
           }
        });
    }

    function refreshCache( useCase ) {
        if ( !useCase ) {
            initializeStepCache( root );
        } else if ( useCase.indexOf('http') == 0 ) {
            initializeStepCache( useCase );
        } else {
            initializeStepCache( root + useCase );
        }
    }

    function findStep( step ) {
        if ( cache[ step ] ) {
            return cache[ step ];
        } else {
            refreshCache( null );
            if ( cache[ step ]) {
                return cache[ step ];
            } else {
                throw "step not found";
            }
        }
    }

    function invoke( render, url ) {
        $.ajax({
            async: false,
            url: url,
            success: function( data ) {
                render( data );
            }
        });
    }

    function parseHash( state ) {
        var hashString = location.hash.substring(1);
        if ( !state ) {
            var hash = { "arguments": {}, view: defaultView };
        } else {
            var hash = { "arguments": state, view: defaultView };
        }
        if ( hashString.indexOf( "?" ) != -1 ) {
            var nameSplit = hashString.split( "?" );
            hash.view = findView( nameSplit[0] );
            // k1=v1&k2=v2&...
            var arguments = nameSplit[1].split( "&" );

            for ( idx in arguments ) {
                if ( arguments[idx].indexOf("=") != -1 ) {
                    var argument = arguments[idx].split("=");
                    // do not override already set arguments
                    if ( !hash.arguments[ argument[0]]) {
                        hash.arguments[ argument[0] ] = $.URLDecode( argument[1] );
                    }
                }
            }
        } else {
            if ( hashString != "" ) {
                hash.view = findView( hashString );
            }
        }
        if ( !hash.view ) {
            throw "view not found";
        }
        return hash;
    }

    function toUrl( hash ) {
        var argumentString = "?";
        var first = true;
        $.each( hash.arguments, function( key, value ) {
            if ( !first ) { argumentString += "&"; }
            else first = false;
            if ( key != 'useCase' ) {
                argumentString += key + '=' + $.URLEncode( value );
            }
        });
        if ( hash.useCase ) {
            return '#' + hash.view.name + argumentString + 'useCase=' + hash.useCase;
        } else {
            return '#' + hash.view.name + argumentString;
        }
    }

    function history( hash, blockEvent ) {
        _ignoreHashChange = blockEvent;
        $.bbq.pushState( hash );
    }

    function clickDataLink( callback, url ) {
        $.ajax({ url: url, success: callback });
    }

    function executeScenario( hash ) {
        cache = {};
        refreshCache( hash.arguments.useCase );
        $.each( hash.view.steps, function(i, step) {
            var rel = findStep( step.rel );
            var url = rel.uri;
            if ( hash.arguments[ step.rel ] ) {
                url += '?' + $.URLDecode( hash.arguments[ step.rel ] );
            }
            invoke( step.render, url );
        });
    }

    function regenerateUseCase() {
        var hash = parseHash();

        var obsolete = hash.arguments.useCase
        if ( obsolete.substr(-1) === '/' ) {
            obsolete = obsolete.substring( 0, obsolete.length-1);
        }
        var current = { segments:obsolete.split('/'), url:root };
        var namespaces = findNamespaces( current.url );
        $.each( current.segments, function(i, segment) {
            var nextNamespaces = tryForward( segment, namespaces, current );
            if ( nextNamespaces.length > 0 ) {
                namespaces = nextNamespaces;
            }
        });
        // smoke test: with the regenerated useCase can
        // we find all needed rel for the view?
        // so sometimes broken urls will dissolve
        var useCase = relativeUrl( current.url );
        if ( smokeTest( useCase, hash.view ) ) {
            // change url
            hash.useCase = useCase;
            history( toUrl( hash ), false );
        } else {
            alert('Not found');
            throw 'unable to regenerate use case';
        }
    }

    function tryForward( segment, namespaces, current ) {
        var nextNamespaces = [];
        var canUse = true;
        // make sure the segment is not a namespace segment
        $.each( namespaces, function(i, namespace) {
            var segments = namespace.split( '/' );
            if ( segments[ segments.length-1 ] === segment ) {
                canUse = false;
                return false;
            }
        });
        if ( canUse ) {
            $.each( namespaces, function(i, namespace) {
                var testUrl = namespace + segment + '/';
                nextNamespaces = findNamespaces( testUrl );
                if ( nextNamespaces.length > 0 ) {
                    current.url = testUrl;
                    return false;
                };
            });
        }
        return nextNamespaces;
    }

    function smokeTest( url, view ) {
        cache = {};
        refreshCache( url );
        var ok = true;
        $.each( view.steps, function(i, step) {
            try {
                findStep( step.rel );
            } catch( e) {
                ok = false;
                return false;
            }
        });
        return ok;
    }

    function executeInteraction( interaction, state ) {
        cache = {};
        var hash = parseHash( state );
        refreshCache( hash.arguments.useCase );
        loadTemplate( interaction.name, function() { runInteraction( 0, interaction, hash.arguments); });
    }

    function loadTemplate( name, callback ) {
        var viewCache = $('#viewcache');

        if ( name ) {
            var cached = viewCache.find('#'+name+"Template");
            if ( cached.length == 0 ) {
                viewCache.append( $("<div id='"+name+"Template'></div>"));

                $('#'+name+"Template").load('html/'+ name+'.html', function() {
                    $('#master').empty().append( $("#"+name+"Template").children().clone() );
                    callback();
                });
            } else {
                $('#master').empty().append( $("#"+name+"Template").children().clone() );
                callback();
            }
        } else {
            callback();
        }
    }

    $.htmlEncode = function(value){
      return $('<div/>').text(value).html().replace( /\n/g, '<br>' );
      return $;
    }

    $.htmlDecode = function(value){
      return $('<div/>').html(value).text();
      return $;
    }

    function runInteraction( index, interaction, state ) {
        index = invocationStep( index, interaction, state );

        if ( index < interaction.steps.length ) {
            interaction.inputHandler( interaction.steps[ index ], state, function( state ) { runInteraction(index, interaction, state)});
        } else {
            // refresh Change this. A full refresh is too much
            hashChange(null);
        }
    }

    function invocationStep( index, interaction, state ) {
        var shouldProceed = true;
        while ( shouldProceed ) {
            var stepName = interaction.steps[ index ];
            var step = findStep( stepName );
            var data = state[ step.name ];
            if ( step.jsonTemplate != null ) {
                if ( data ) {
                    // handle argument substitution
                    data = '"' + data + '"';
                } else {
                    return index;
                }
            }
            $.ajax({
                async: false,
                url: step.uri,
                type: step.method,
                contentType: "application/json",
                data: data,
                success: function( data ) {
                    index++;
                    shouldProceed = index < interaction.steps.length;
                    if ( step.method == "GET" ) {
                        state[ step.rel ] = data;
                        interaction.render( step.rel, state );
                    }
                },
                error: function(obj, msg, xhr) {
                    shouldProceed = false;
                }
            });
        }
        return index;
    }

    function hashChange(e) {
        if ( _ignoreHashChange ) {
            _ignoreHashChange = false;
        } else {
            var hash = parseHash();
            loadTemplate( hash.view.name, function() { executeScenario( hash )});
        }
    }

})( jQuery );

