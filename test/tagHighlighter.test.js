HighlightTagsTest = TestCase("HighlightTags");

HighlightTagsTest.prototype.testHighlightTags = function() {

    /*:DOC += <div>
     <div class="tag_test1"></div>
     <div class="tag_test1"></div>
     <div class="tag_test2"></div>
     <div class="tag_test2"></div>
     <div class="tag_test2"></div>
     <div class="tag_test2"></div>
     <div class="tag_test3"></div>
     <div class="tag_test3"></div>
     <div class="tag_test3"></div>
     <div class="tag_test3"></div>
     <div class="tag_test3"></div>
     <div class="tag_test3"></div>
     <div class="tag_test4"></div>
     <div class="tag_test4"></div>
     <div class="tag_test4"></div>
     <div class="tag_test4"></div>
     <div class="tag_test4"></div>
     <div class="tag_test5"></div>
     <div class="tag_test5"></div>
     <div class="tag_test5"></div>
     <div class="tag_test6"></div>
     </div> */

    var list = ['tag_test3','tag_test4','tag_test2','tag_test5','tag_test1','tag_test6'];
    assertEquals(list, tagsOrderedByFrequency());
};