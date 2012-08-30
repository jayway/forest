(function( $ ){

    $.fn.navigationClick = function( view, relURI, url ) {
        //var clientURL = view.name + ...;
        /*
          #view?resource1=id1&...&resourcen=idn&rel=(p1=v1,...)
        */
        this.unbind().click( function() {
            if ( -1 != window.location.indexOf( '#' + view.name ) ) {
                // diff the url parameters and invoke the changed rel:s
                var current = parseParameters( window.location );
                var target  = parseParameters( clientURL );
            } else {
                // we are changing the view to let renderView handle it
                renderView( clientURL );
            }
        });
    }

    $.fn.updateClick = function( relURI, url ) {
        // link == { rel: "name", url: "..." }

        // typisk vil rel være unik i applikationen, dvs
        // applikationen kan lokalt slå op dens definition
        // (input/output param + evt hard codede parameter e.g. paging size).
        // Men der findes også standard rel: items, item, next, previous
        // subresource. Hvordan genkendes items??? Vi ved den har en id
        // metode med rel:book. Så klienten kan depende på items:book
        /*
           next, last, first, previous: hvis de skal opdatere
           urlen, så er det til samme uc
        */
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

    function renderView( clientURL ) {
        // 1. parse clientURL
        // 2. refresh the rel cache
        // 3. lookup view definition
        // 4. iterate through all rel and invoke with callbacks to render functions
    }



    $.parseParametersTest = function( parameterString ) {
        return parseParameters( parameterString );
    }

    // parsing the string of form 'view?entity=a&entity2=b&lookup=(urlencoded)&...'
    function parseParameters( parameterString ) {
        var result = {};
        if ( -1 != parameterString.indexOf('?') ) {
            var split = parameterString.split('?');
            result.view = split[0];

            result.resources = {};
            result.parameters = {};
            var args = split[1].split('&');
            $.each( args, function( i, val) {
                var param = val.split( '=' );
                if ( param.length == 2 ) {
                    if ( param[1].indexOf('(') == 0 ) {
                        var decoded = $.URLDecode( param[1].substring(1, param[1].length-1 ) );
                        result.parameters[ param[0] ] = {};
                        parse( decoded, result.parameters[ param[0] ] );
                    } else {
                        result.resources[ param[0] ] = param[1];
                    }
                }
            });
        } else {
            result.view = parameterString;
        }
        return result;
    }


    function parse( parameterString, appendTo ) {
        var keyValues = parameterString.split( '&' );
        $.each( keyValues, function( i, keyValue) {
            var split = keyValue.split( '=' );
            if ( split.length == 2 ) {
                appendTo[ split[0] ] = $.parseJSON( split[1] );
            }
        });
    }


})( jQuery );

