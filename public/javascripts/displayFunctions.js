function hideDuplicateDateCategories() {
    $(".events").each(function () {
        var currentCat = "";
        $(".dateCategory", this).each(function () {
            if ($(this).parents("li").is(":visible")) {
                var thisCat = $(this).html();
                if (currentCat != thisCat) {
                    currentCat = thisCat;
                    $(this).show();
                } else {
                    $(this).hide();
                }
            }
        })
    });
}

function stackFileVersions() {
    $(".events").each(function () {
        var latestVersionMap = {};
        var uniqueVersionMap = {};
        $("a.eventTitle", this).each(function () {
            var thisTitle = $(this).html();
            var listElement = $(this).parents(".events ul.eventList li");
            if (thisTitle in uniqueVersionMap) {
                listElement.hide();
                latestVersionMap[thisTitle] = uniqueVersionMap[thisTitle];
            } else {
                uniqueVersionMap[thisTitle] = listElement;
            }

        });

        for (var key in latestVersionMap) {
            if (latestVersionMap.hasOwnProperty(key)) {
                var element = latestVersionMap[key];
                if (element.children(".sheaf").length == 0) {
                    element.children().first().after(
                        '<a class="sheaf" href="">\
                            <div class="versions versions1"><div class="left">earlier versions are hidden</div><div class="right">+ show versions on timeline</div><div style="clear: both;"></div> </div>\
                            <div class="versions versions2"></div>\
                        </a>'
                    );

                    $(".sheaf", element).click(function (e) {
                        unstackVersions($(this).parent().find("a.eventTitle").html(), $(this).parents(".eventList"));
                        return false;
                    });
                }

            }
        }
    });
}

function unstackVersions(title, context) {
    $("a.eventTitle", context).each(function () {
        var thisTitle = $(this).html();
        if (title == thisTitle) {
            var listElement = $(this).parents(".events ul.eventList li");
            listElement.show();
            $(".sheaf", listElement).hide();
        }
    });
    hideDuplicateDateCategories();
}

function showNow(context) {
    $(".events", context).each(function() {
        $(".future", $(this)).last().after("<div class=\"now\"><span>now</span></div>")
    })
}