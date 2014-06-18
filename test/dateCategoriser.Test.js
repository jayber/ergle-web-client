DateCategoriserTest = TestCase("DateCategoriser");

DateCategoriserTest.prototype.testDateCategoriser = function () {

    /*:DOC += <div class="events">
        <ul>
     <li style="display: none">
        <div class="hidden dateCategory">Today</div>
     </li>
     <li>
        <div style="display: none" class="notHidden dateCategory">Today</div>
     </li>
     <li>
        <div class="hidden dateCategory">Today</div>
     </li>
     <li>
        <div class="notHidden dateCategory">Yesterday</div>
     </li>
     </ul>
     </div> */

    hideDuplicateDateCategories();

    $(".hidden").each(function () {
        assertTrue($(this).is(":hidden"));
    });

    $(".notHidden").each(function () {
        assertFalse($(this).is(":hidden"));
    });
};