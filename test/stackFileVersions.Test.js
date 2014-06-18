
StackFileVersionsTest = TestCase("StackFileVersions");

StackFileVersionsTest.prototype.testStackFileVersions = function() {

    /*:DOC += <div class="events">
        <ul class="eventList">
            <li class="notHidden versioned"><div class="title"><a class="eventTitle">title1</a></div></li>
            <li class="notHidden versioned"><div class="title"><a class="eventTitle">title2</a></div></li>
            <li class="hidden"><div class="title"><a class="eventTitle">title1</a></div></li>
            <li class="hidden"><div class="title"><a class="eventTitle">title1</a></div></li>
            <li class="notHidden"><div class="title"><a class="eventTitle">title3</a></div></li>
            <li class="hidden"><div class="title"><a class="eventTitle">title2</a></div></li>
        </ul>
     </div> */

    stackFileVersions();

    $(".hidden").each(function() {
        assertTrue($(this).is(":hidden"));
        assertEquals(0, $(this).children("a").length);
    });

    $(".notHidden").each(function() {
        assertFalse($(this).is(":hidden"));
        if ($(this).is(".versioned")) {
            assertEquals(1, $(this).children("a").length);
        } else {
            assertEquals(0, $(this).children("a").length);
        }
    });

};


StackFileVersionsTest.prototype.testUnstackStackFileVersions = function() {

    /*:DOC += <div class="events">
        <ul id="context" class="eventList">
            <li class="notHidden versioned"><div class="title"><a class="eventTitle">title1</a></div>
                <a class="sheaf" href=""></a>
            </li>
            <li class="notHidden versioned"><div class="title"><a class="eventTitle">title2</a></div></li>
            <li class="hidden1" style="display: none"><div class="title"><a class="eventTitle">title1</a></div></li>
            <li class="hidden1" style="display: none"><div class="title"><a class="eventTitle">title1</a></div></li>
            <li class="notHidden"><div class="title"><a class="eventTitle">title3</a></div></li>
            <li class="hidden" style="display: none"><div class="title"><a class="eventTitle">title2</a></div></li>
        </ul>
        <ul id="notContext" class="eventList">
            <li class="notHidden versioned"><div class="title"><a class="eventTitle">title1</a></div>
                <a class="sheaf" href=""></a>
            </li>
            <li class="notHidden versioned"><div class="title"><a class="eventTitle">title2</a></div></li>
            <li class="hidden1" style="display: none"><div class="title"><a class="eventTitle">title1</a></div></li>
            <li class="hidden1" style="display: none"><div class="title"><a class="eventTitle">title1</a></div></li>
            <li class="notHidden"><div class="title"><a class="eventTitle">title3</a></div></li>
            <li class="hidden" style="display: none"><div class="title"><a class="eventTitle">title2</a></div></li>
        </ul>
     </div> */

    unstackVersions("title1", $("#context"));

    $("#context .hidden1").each(function() {
        assertNotEquals("none",$(this).css("display"));
    });

    $("#context.sheaf").each(function() {
        assertEquals("none",$(this).css("display"));
    });

    $("#notContext .hidden1").each(function() {
        assertEquals("none",$(this).css("display"));
    });

    $("#notContext .sheaf").each(function() {
        assertNotEquals("none",$(this).css("display"));
    });
};