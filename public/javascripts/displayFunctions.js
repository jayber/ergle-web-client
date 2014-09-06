function getDayCategory(context, element) {
    var day = element.attr("day");
    var dayElement = context.find("li[day=\"" + day + "\"]");
    if (dayElement.size()==0) {
        dayElement = $($.parseHTML("<li day=\"" + day + "\" class='dayTemplate " + element.attr("class") + "'><span class='dateCategory'>" + day + "</span><ul class='tagList'><div style='clear:both;'></div></ul></li>"));
        context.children().last().before(dayElement);
    }
    return dayElement.find("ul.tagList");
}

function getTagCategory(context, element) {
    var tag = element.attr("tag");
    var tagElement = context.find("li[tag=" + tag + "]");
    if (tagElement.size()==0) {
        var html = "<li tag='"+tag+"' ";
        if (tag != "") {
            html = html + " class='defaultTag tag_" + tag + "'><div class='tag_" + tag + "'><span class='tag tag_" + tag + "'>" + tag + "</span></div";
        }
        html = html + "><ul class='eventTypeList'><div style='clear:both;'></div></ul></li>";
        tagElement = $($.parseHTML(html));
        if (tag=="") {
            context.prepend(tagElement);
        } else {
            context.children().last().before(tagElement);
        }
    }
    return tagElement.find("ul.eventTypeList");
}

function addElementToType(context, element) {
    var type = element.attr("eventtype");
    var typeElement = context.find("li[eventtype=" + type + "]");
    if (typeElement.size()==0) {
        typeElement = $($.parseHTML("<li eventtype='" + type + "'><div class='" + type + "'>" + type + "s</div><ul class='eventList'><div style='clear:both;'></div></ul></li>"));
        context.children().last().before(typeElement);
    }
    typeElement.find("ul.eventList").children().last().before(element);
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

function showNow() {
    $(".events > ul.eventList").each(function () {
        if ($(".now", $(this)).size()==0) {
            $(".future", $(this)).last().after("<div class=\"now\">coming up</div>");
        }
    });
    $(".events > ul.dayList").each(function () {
        if ($(".now", $(this)).size()==0) {
            $(".future", $(this)).last().parents("ul.dayList > li").after("<div class=\"now\">coming up</div>");
        }
    })
}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function jumpToAnchor() {
    var anchor = location.hash;
    if (anchor!="") {
        anchor = anchor.substr(1) ;
        //seems crazy, but this is the nearest to making sure everything (including images) is loaded before scrolling,
        //otherwise the offset will be wrong.
        setTimeout(function(){
            var top = document.getElementById(anchor).offsetTop;
            window.scrollTo(0, top);
        },500);
    }
}