
jQuery(document).ready(function()
{
    var cache;

    function initializeStepCache( url ) {
        $.ajax({
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

    cache = {};
    initializeStepCache( "/bank/" );
    $('#master').load('html/root.html', function() {
        $.ajax({
                url: "/bank/",
                success: function(data) {
                    $.each( data, function(idx, elm) {
                        $('#code').append( "<a href='" + elm.href + "'>"+elm.name+"</a></br>");
                    });
                },
                error: function() {
                    alert("Error");
                }
        });
    });
})