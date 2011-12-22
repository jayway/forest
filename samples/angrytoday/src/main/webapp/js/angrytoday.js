jQuery(document).ready(function() {

    function $$(val) {
        return $('#master').find('#'+val);
    }

// Views
    function renderMain( step, data ) {
        if ( step == "description" ) {
            $$('description').empty().append( data );
            $$('create').interactionClick( angryPost );
        } else if ( step == "discover" ) {
            renderDiscover( data );
        }
    }

    function renderDiscover( data ) {
        renderNavigation( data, renderDiscover );
        var template = $$('template');
        var ul = $$('posts').empty();
        $.each( data.list, function(i, link) {
            var li = template.clone().removeAttr('id').appendTo( ul ).show();
            var tags = li.find('#tags');
            $.each( link.tags, function(j, tag) {
                tags.append( "<li class='tag'>"+tag+"</li>" );
            });
            li.find('#post').append( link.name );
            if ( link.comment ) {
                li.find('#comment').append( link.comment );
            } else {
                li.find('#comment').remove();
            }
            li.find('#details').clickLink( angryView, link );
        });
    }

    function renderNavigation( data, callback ) {
        var navigation = $$('navigation');
        navigation.find('#header').empty().append("Page "+data.page+" of "+data.totalPages);
        navigation.find('#previous').addPaginationClick( data.previous, callback, "discover" );
        navigation.find('#next').addPaginationClick( data.next, callback, "discover" );
    }

    function renderAngry( step, data ) {
        if ( step == "description" ) {
            var tags = $$('tags').empty();
            $.each( data.tags, function(i, tag) {
                var tagElm = $("<li class='tag'>"+tag+" <img id='closing' src='/images/CloseIcon-small.png'></li>");
                tags.append( tagElm );
                tagElm.find('#closing').interactionClick( unAngryTag, {untag: tag} );
            });
            $('<button>Add</button>').interactionClick( addAngryTag ).appendTo( tags );
            $$('addComment').interactionClick( angryComment );
            $$('angrypost').empty().append( data.message );
        } else if ( step == "comments" ) {
            renderComments( data );
        }
        // fix buttons: addComment
    }

    function renderComments(data) {
        renderNavigation( data, renderComments );
        var comments = $$('comments').empty();
        $.each( data.list, function(i, comment) {
            var padding = (comment.uri.split('/').length-6) * 15;
            var button = $('<button>...</button>').interactionClick( angryComment, { useCase: comment.uri} );
            $('<div class="comment" style="margin-left:'+padding+'px;"/>')
                .append( comment.name )
                .append( button )
                .appendTo( comments );
        });
    }

// Interactions
    function inputAngryPost( step, state, continuation ) {
        var text = $.htmlEncode( $$('newPost').val() );
        if ( !text ) {
            alert("You cannot post an empty message! That is not angry enough!!!");
            return;
        }
        state.create = text;
        continuation( state );
    }

    function inputAngryComment( step, state, continuation ) {
        centerPopup( "input" ).find('#title').empty().append("New comment");
        var input = $$('input').fadeIn("slow");
        $$('cancel').unbind().click( function() { input.fadeOut("slow") });
        enterTextField($$('textinput'), function( value ) {
            state.create = value;
            input.fadeOut("slow");
            continuation( state );
        });
    }

    function tagInputHandler( step, state, continuation ) {
        centerPopup( "input" ).find('#title').empty().append("Add tag");
        var input = $$('input').fadeIn("slow");
        $$('cancel').unbind().click( function() { input.fadeOut("slow") });
        enterTextField( $$('textinput'), function( value ) {
            state.tag = value;
            input.fadeOut("slow");
            continuation( state );
        });
    }

    function enterTextField( field, callback ) {
        field.focus().unbind().keypress( function(event) {
            if ( event.which == 13 ) {
                callback( field.val() );
            }
        });
    }

    function centerPopup( name ){
        return $$(name).css({
            "position": "absolute",
            "top": 400,
            "left": 300
        });
    }

    var mainView = {
        name  : "main",
        render : renderMain,
        steps : [ "description", "discover" ]
    };

    var angryView = {
        name : "angry",
        render : renderAngry,
        steps : [ "description", "comments" ]
    };

    var angryPost = {
        inputHandler : inputAngryPost,
        steps        : [ "create" ]
    };

    var angryComment = {
        inputHandler : inputAngryComment,
        steps    : [ "create" ]
    };

    var addAngryTag = {
        inputHandler : tagInputHandler,
        steps : [ "tag" ]
    };

    var unAngryTag = {
        steps : [ "untag" ]
    };

    $.setRoot(  "/angrytoday/" ).setViews( [ mainView, angryView ] );
})

