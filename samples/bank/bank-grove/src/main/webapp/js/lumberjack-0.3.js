
jQuery(document).ready(function()
{
    var cache = {};

    function initializeStepCache( url ) {
        $.ajax({
            async: false,
            url: url,
            success: function( data ) {
                $.each( data, function( idx, elm) {
                    if ( elm.rel ) {
                        cache[ elm.rel ] = elm;
                    } else {
                        initializeStepCache( elm.href );
                    }
                });
            },
            error: function( ) {
                alert( "Error: "+url );
            }
        });
    }

    function View() {
        
    }

    initializeStepCache( "/bank/" );
    $('#master').load('html/root.html' );



    $(function(){
        // Override the default behavior of all `a` elements so that, when
        // clicked, their `href` value is pushed onto the history hash
        // instead of being navigated to directly.
        $("a").click(function(){
            var href = $(this).attr( "href" );

            // Push this URL "state" onto the history hash.
            $.bbq.pushState({ url: href });

            // Prevent the default click behavior.
            return false;
        });

        // Bind a callback that executes when document.location.hash changes.
        $(window).bind( "hashchange", function(e) {
            // In jQuery 1.4, use e.getState( "url" );
            var url = $.bbq.getState( "url" );

            // In this example, whenever the event is triggered, iterate over
            // all `a` elements, setting the class to "current" if the
            // href matches (and removing it otherwise).
            $("a").each(function(){
            var href = $(this).attr( "href" );

            if ( href === url ) {
                $(this).addClass( "current" );
            } else {
                $(this).removeClass( "current" );
            }
        });

    // You probably want to actually do something useful here..
    });

    // Since the event is only triggered when the hash changes, we need
    // to trigger the event now, to handle the hash the page may have
    // loaded with.
    $(window).trigger( "hashchange" );
    });
})

