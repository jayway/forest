(function( $ ){
    var root;
    var views = {};
    var cache = {};
    var _ignoreHashChange = false;
    var defaultView;

    $.setRoot = function( rootUrl ) {
        root = rootUrl;
        return $;
    }

    $.setViews = function( viewList ) {
        $.each( viewList, function(i,elm) {
            views[ elm.name ] = elm;
        });
        defaultView = viewList[0].name;

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
        this.append( "<a href='#" + view.name + "?useCase=" + $.URLEncode( link.uri )+ "'>"+link.name+"</a>" );
        return this;
    }

    $.fn.clickLink = function( view, link ) {
        this.unbind().click( function() {
            window.location = '#' + view.name + "?useCase=" + $.URLEncode( link.uri );
        });
        return this;
    }

    $.fn.interactionClick = function( interaction, state ) {
        this.unbind().click( function() { executeInteraction( interaction, state ) } );
        return this;
    };

    function disabled() {
        return false;
    }

    function initializeStepCache( url ) {
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
            error: function( ) {
                alert( "Error: "+url );
            }
        });
    }

    function refreshCache( useCase ) {
        var url = useCase ? useCase : root;
        initializeStepCache( url );
    }

    function findStep( step ) {
        if ( cache[ step ] ) {
            return cache[ step ];
        } else {
            refreshCache( null );
            if ( cache[ step ]) {
                return cache[ step ];
            } else {
                alert( "Could not find "+ step );
            }
        }
    }

    function invoke( render, step, url ) {
        $.ajax({
            async: false,
            url: url,
            success: function( data ) {
                render( step, data );
            }
        });
    }

    function parseHash( state ) {
        var hashString = location.hash.substring(1);
        if ( !state ) {
            var hash = { "arguments": {}, hash: defaultView };
        } else {
            var hash = { "arguments": state, hash: defaultView };
        }
        if ( hashString.indexOf( "?" ) != -1 ) {
            var nameSplit = hashString.split( "?" );
            hash.hash = nameSplit[0];
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
                hash.hash = hashString;
            }
        }
        return hash;
    }

    function toUrl( hash ) {
        var argumentString = "?";
        var first = true;
        $.each( hash.arguments, function( key, value ) {
            if ( !first ) { argumentString += "&"; }
            else first = false;
            argumentString += key + '=' + $.URLEncode( value );
        });
        return '#' + hash.hash + argumentString;
    }

    function history( hash, blockEvent ) {
        _ignoreHashChange = blockEvent;
        $.bbq.pushState( hash );
    }

    function clickDataLink( callback, url ) {
        $.ajax({ url: url, success: callback });
    }

    function executeScenario( view, methodArguments ) {
        refreshCache( methodArguments.useCase );
        $.each( view.steps, function(i, step) {
            var rel = findStep( step );
            var url = rel.uri;
            if ( methodArguments[ step ] ) {
                url += '?' + $.URLDecode( methodArguments[ step ] );
            }
            invoke( view.render, step, url );
        });
    }

    function executeInteraction( interaction, state ) {
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
            if ( !views[ hash.hash ] ) return;
            loadTemplate( hash.hash, function() { executeScenario( views[ hash.hash ], hash.arguments ) });
        }
    }

})( jQuery );

