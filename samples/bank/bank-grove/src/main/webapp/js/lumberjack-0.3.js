
jQuery(document).ready(function()
{
    var cache = {};
    var rootUrl = "/bank/";
    var _ignoreHashChange = false;

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

    function refreshCache() {
        initializeStepCache( rootUrl );
    }

    function findStep( step ) {
        if ( cache[ step ] ) {
            return cache[ step ];
        } else {
            refreshCache();
            if ( cache[ step ]) {
                return cache[ step ];
            } else {
                alert( "Could not find "+ step );
            }
        }
    }

    function executeScenario( view, methodArguments ) {
        if ( methodArguments[ view.useCase ] ) {
            initializeStepCache( methodArguments[ view.useCase ] );
        }
        $.each( view.steps, function(i, step) {
            var rel = findStep( step );
            var url = rel.href;
            if ( methodArguments[ step ] ) {
                url += '?' + $.URLDecode( methodArguments[ step ] );
            }
            invoke( view.render, step, url );
        });
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

    function parseHash() {
        var hashString = location.hash.substring(1);
        var hash = { "arguments": {}, hash: viewList[0].name };
        if ( hashString.indexOf( "?" ) != -1 ) {
            var nameSplit = hashString.split( "?" );
            hash.hash = nameSplit[0];
            // k1=v1&k2=v2&...
            var arguments = nameSplit[1].split( "&" );

            for ( idx in arguments ) {
                if ( arguments[idx].indexOf("=") != -1 ) {
                    var argument = arguments[idx].split("=");
                    hash.arguments[ argument[0] ] = $.URLDecode( argument[1] );
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

    function getHref( link ) {
        var rel = link.rel;
        var argument;
        var colon = link.rel.indexOf(":");
        if ( colon > 0 ) {
            rel = link.rel.substring(0,colon);
            argument = link.rel.substring(colon+1);
        }
        for ( idx in viewList ) {
            var href;
            if ( viewList[idx].useCase == rel ) {
                href = viewList[idx].name;
                if ( argument  ) {
                    href += "?" + rel + "=" + $.URLEncode( argument );
                }
                return href;
            }
        }
    }


    function renderMain( rel, data ) {
        var ul = $('#accounts');
        $.each( data.list, function(i, link) {
            ul.append( "<li><a href='#" + getHref( link ) + "'>"+link.name+"</a></li>" );
        });
    }

    function renderAccount( rel, data ) {
        if ( rel == "transactions" ) {
            renderAccountTransactions( data );
        } else if ( rel == "description" ) {
            $('#name').append(data.name);
        }
    }

    function renderAccountTransactions( data ) {
        // setup links
        $('#header').empty().append("Page "+data.page+" of "+data.totalPages);
        addClick( data.next, "next", renderAccountTransactions );
        addClick( data.previous, "previous", renderAccountTransactions );

        // render table
        var tableString = "";
        $.each( data.list, function( i,transaction ) {
            tableString += listTransaction(i,transaction );
        });
        $('#transactions').empty().append( tableString );

        // operations
        $('#changeDescriptionAction').unbind().click( function() { executeInteraction( changeAccountNameInteraction ) } );
    }

    function addClick( url, id, callback ) {
        var anchor = $('#'+id);
        if ( url ) {
            var hash = parseHash();
            // since rel="next" is understood we can construct
            // the link to the next just needs the parameters
            var param = url.substring( url.indexOf('?')+1, url.length );
            hash.arguments.transactions = $.URLEncode( param );
            anchor.attr('href', toUrl( hash )).unbind().click(
            function() {
                clickDataLink( callback, url );
                history( anchor.attr('href'), true  );
                return false;
            }).removeClass("disabled");
        } else {
            anchor.unbind().click( function() { return false; } ).addClass("disabled");
        }
    }

    function history( hash, blockEvent ) {
        _ignoreHashChange = blockEvent;
        $.bbq.pushState( hash );
    }

    function clickDataLink( callback, url ) {
        $.ajax({ url: url, success: callback });
    }

    function listTransaction( i, transaction ) {
        var clazz = i%2==0 ? "even" : "odd";
        return "<tr class='"+clazz+"'><td>"+new Date( transaction.date.fastTime ).toString()+"</td>"+
         "<td>"+transaction.amount+"</td><td>"+transaction.description+"</td><td>"+transaction.balance+"</td></tr>";
    }

    function renderTransaction( rel, data ) {

    }

    function renderChangeAccountName( step, state ) {
        if ( step == "description" ) {
            $('#accountName').attr("value", state.description.name );
        }
    }

    function inputChangeAccountName( step, state, continuation ) {
        if ( step == "changename" ) {
            $('#changeNameButton').click( function() { inputChangeAccountName(null, state, continuation); } );
        } else {
            state.changename = $('#accountName').attr("value");
            continuation( state );
        }
    }

    function executeInteraction( interaction ) {
        var hash = parseHash();
        $('#master').load('html/'+ interaction.name+'.html', function() {
            var useCase = interaction.useCase;
            if ( hash.arguments[ useCase ] ) {
                initializeStepCache( hash.arguments[ useCase ] );
            }

            runInteraction( 0, interaction, hash.arguments);
        });
    }

    function runInteraction( index, interaction, state ) {
        index = invocationStep( index, interaction, state );

        if ( index < interaction.steps.length ) {
            interaction.inputHandler( interaction.steps[ index ], state, function( state ) { runInteraction(index, interaction, state)});
        } else {
            // refresh
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
                url: step.href,
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
                error: function() {
                    shouldProceed = false;
                }
            });
        }
        return index;
    }

    var mainView = {
        name  : "main",
        render : renderMain,
        steps : [ "discover" ]
    };

    var accountView = {
        name : "account",
        render : renderAccount,
        useCase : "AccountsResourceId",
        steps : [ "transactions", "description" ]
    };

    var transactionView = {
        name : "transaction",
        render : renderTransaction,
        useCase : "AccountResourceId",
        steps : [ "describe" ]
    };

    var viewList = [ mainView, accountView, transactionView ];

    var changeAccountNameInteraction = {
        name         : "changeAccountName",
        render       : renderChangeAccountName,
        inputHandler : inputChangeAccountName,
        useCase      : "AccountsResourceId",
        steps        : [ "description", "changename" ]
    };

    var transferMoneyInteraction = {
        name     : "transferMoney",
        render   : null,
        useCase  : "AccountsResourceId",
        steps    : [ "transfer" ]
    };

    var interactionList = [ changeAccountNameInteraction, transferMoneyInteraction ];

    function hashChange(e) {
        if ( _ignoreHashChange ) {
            _ignoreHashChange = false;
        } else {
            var hash = parseHash();
            $.each( viewList, function(i, view) {
                if ( view.name == hash.hash ) {
                    $('#master').load('html/'+ view.name+'.html', function() {
                        executeScenario( view, hash.arguments );
                    });
                };
            });
        }
    }

    // setup of hash change listener
    $(function(){
        $(window).bind( "hashchange", hashChange );
    });
    // Manually trigger the initial hashchange event.
    hashChange();
})

/*
  Enste problem med rel er IdResource - den falder udenfor
  eller hvad? Generelt for søge resultater: lister af links:
  hvordan skal det fungere??? Så får man en links tilbage
  { href, rel, jsonTemplate }

  Named resource har ikke nogen rel.
  rel : AccountsResourceId=http://localhost:8080/accounts/11111/
  href : http://localhost:8080/accounts/id
  jsonTemplate: "11111"

discovery af id metoden:

  rel : AccountsResourceId
  href : http://localhost:8080/accounts/id
  jsonTemplate: "",

  /accounts/id?argument1=11111
  samme resultat som
  /accounts/11111/

=> #account?useCase=http://localhost:8080/accounts/11111/&transactions=2

"next":"http://localhost:8080/bank/accounts/11/transactions?page=2"

=>
"links" : [
{
    url: "http://localhost:8080/bank/accounts/11/transactions?page=2&sortBy=name",
    rel: "next",
    method: "GET"
}, ...
]

-----
 TODO:
 1. Fix link generation on server side
 4. Implement a command (Change Description, Transfer Money)
 5. Think about caching

-------

1. click interaction button
2. load html template
3. run steps saving all state in a state object
4. if step cannot be run (missing argument) create callback function
   for the current step and assign to continue button on html
5. When user clicks continue button the step is again tried to
   be executed
6. If 400, 409 do as step 4
7. If 401 push a login step that after completion
   will continue step




*/

