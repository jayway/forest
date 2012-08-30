
describe("A suite", function() {
  it("contains spec with an expectation", function() {
    expect(true).toBe(true);
  });
});

describe("A real suite", function() {
    it("has the correct behaviour", function() {
        var parsed = $.parseParametersTest( "chapterView?book=1234&chapter=abcd&search=(a%3D10%26q%3D%22mads%22)" );
        expect(parsed.view).toBe("chapterView");
        expect(parsed.resources.book ).toBe("1234");
        expect(parsed.resources.chapter ).toBe("abcd");
        expect(parsed.parameters.search.a ).toBe( 10 );
        expect(parsed.parameters.search.q ).toBe( "mads" );
    });
});

describe("A simple view", function() {
    it("can render default", function() {
        var callback = false;
        $.setRoot('/').setViews([{ "main": "define view" }]);
        expect( callback ).toBe( true );
        // check to see that the render methods are indeed invoked

    });
});


(function() {
  var jasmineEnv = jasmine.getEnv();
  jasmineEnv.updateInterval = 250;

  /**
   Create the `HTMLReporter`, which Jasmine calls to provide results of each spec and each suite. The Reporter is responsible for presenting results to the user.
   */
  var htmlReporter = new jasmine.HtmlReporter();
  jasmineEnv.addReporter(htmlReporter);

  /**
   Delegate filtering of specs to the reporter. Allows for clicking on single suites or specs in the results to only run a subset of the suite.
   */
  jasmineEnv.specFilter = function(spec) {
    return htmlReporter.specFilter(spec);
  };

  /**
   Run all of the tests when the page finishes loading - and make sure to run any previous `onload` handler

   ### Test Results

   Scroll down to see the results of all of these specs.
   */
  var currentWindowOnload = window.onload;
  window.onload = function() {
    if (currentWindowOnload) {
      currentWindowOnload();
    }
    execJasmine();
  };

  function execJasmine() {
    jasmineEnv.execute();
  }
})();