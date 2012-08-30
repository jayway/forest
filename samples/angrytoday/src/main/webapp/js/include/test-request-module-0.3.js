(function( $ ){

    var data = {};

    // initializes the test data
    function init() {
        data.books = {};
        data.books.g1f4gd = { title: "Dragon Tattoo", author: "a001", readingProgress: 0.43, rating: 4 };
        data.books.kjei23 = { title: "Kong King", author: "a002", readingProgress: 0.0, rating: 1 };
        data.books.iwuycz = { title: "Daughter of Samantha", author: "a003", readingProgress: 1.0, rating: 5 };

        data.bookdescription.ff87a-2cbd-be = { title: "Vampire Slaughterhouse", author: "a004" };
        data.bookdescription.feacc-33a1-0f = { title: "Dawn of Yesteryear", author: "a001" };

        data.authors.a001 = { name: "Doe John" };
        data.authors.a002 = { name: "Samantha Livingston" };
        data.authors.a003 = { name: "Martha King" };
        data.authors.a004 = { name: "Burt von Neumann" };

        data.urlTemplates = {
            book:            "/books/{book}",
            note:            "/books/{book}/notes/{note}",
            bookdescription: "/bookdescriptions/{bookdescription}",
            authors:         "/authors/{author}"
        };

        data.urls = {
            '^/$' : root,
            '^/books$' : books,
            '^/books/([a-z0-9-]+)$' : book,
            '^/books/([a-z0-9-]+)/notes$' : notes,
            '^/books/([a-z0-9-]+)/notes/([a-z0-9-]+)$' : note,
            '^/bookdescriptions$' : bookdescriptions,
            '^/bookdescriptions/([a-z0-9-]+)$' : bookdescription,
            '^/authors$' : authors,
            '^/authors/([a-z0-9-]+)$' : author
        };
    }


    $.ajax( args ) {
        // override behaviour to just lookup in local data structure
    }

    // need to implement discovery of all resources


})( jQuery );

