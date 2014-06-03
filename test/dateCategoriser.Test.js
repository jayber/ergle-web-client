
DateCategoriserTest = TestCase("DateCategoriser");

DateCategoriserTest.prototype.testDateCategoriser = function() {

    /*:DOC += <div class="events">
     <div class="notHidden dateCategory">Today</div>
     <div class="hidden dateCategory">Today</div>
     <div class="hidden dateCategory">Today</div>
     <div class="notHidden dateCategory">Yesterday</div>
     </div> */

    hideDuplicateDateCategories();

    $(".hidden").each(function() {
        assertTrue($(this).is(":hidden"));
    });

    $(".notHidden").each(function() {
        assertFalse($(this).is(":hidden"));
    });
};