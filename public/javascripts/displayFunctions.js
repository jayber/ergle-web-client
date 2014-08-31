function categoriseByDay() {
    $(".events").each(function () {
        var currentCategoryName;
        var dayElement;
        $(".events ul.dayList > li[day]").each(function () {
            var day = $(this).attr("day");
            if (day != currentCategoryName) {
                $(this).wrap("<li class='dayTemplate "+$(this).attr("class")+"'><ul class='tagList'></ul></li>");
                currentCategoryName = day;
                dayElement = $(this).parent();
                dayElement.before("<span class='dateCategory'>"+day+"</span>");
            } else {
                dayElement.append(this);
            }
        });
    });
    $("li.dayTemplate ul.tagList").append("<div style='clear:both;'></div>");
}

function categoriseDaysByTag() {
    $(".events ul.dayList > li.dayTemplate").each(function () {
        var tagCategories = {};
        $(this).find("ul.tagList > li[tag]").each(function () {
            var tag = $(this).attr("tag");
            if (tag in tagCategories) {
                tagCategories[tag].append(this);
            } else {
                var html = "<li";
                if (tag != "") {
                    html = html + " class='defaultTag tag_" +tag + "'";
                }
                html = html + "><ul class='eventTypeList'></ul></li>";
                $(this).wrap(html);
                if (tag != null && tag != "") {
                    $(this).parents("li.defaultTag").prepend("<div class='tag_" +tag+"'><span class='tag tag_" +tag+"'>" + tag + "</span></div>");
                }
                tagCategories[tag] = $(this).parent();
            }
        });
        var emptyTag = tagCategories[""];
        if (emptyTag!=null) {
            var parent = emptyTag.parent();
            emptyTag.detach();
            parent.prepend(emptyTag);
        }
    });
    $("ul.tagList ul.eventList").append("<div style='clear:both;'></div>");
}

function categoriseDayTagsByType() {
    $(".events ul.dayList > li.dayTemplate > ul.tagList > li").each(function () {
        var typeCategories = {};
        $(this).find(" ul.eventTypeList > li[eventtype]").each(function () {
            var type = $(this).attr("eventtype");
            if (type in typeCategories) {
                typeCategories[type].append(this);
            } else {
                $(this).wrap("<li><ul class='eventList'></ul></li>");
                $(this).parent().before("<div class='"+type+"'>"+type+"s</div>");
                typeCategories[type] = $(this).parent();
            }
        });
    });
    $("ul.tagList ul.eventTypeList").append("<div style='clear:both;'></div>");
}

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
    $(".events > ul.eventList", context).each(function () {
        $(".future", $(this)).last().after("<div class=\"now\">coming up</div>")
    });
    $(".events > ul.dayList", context).each(function () {
        $(".future", $(this)).last().parents("ul.dayList > li").after("<div class=\"now\">coming up</div>")
    })
}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}