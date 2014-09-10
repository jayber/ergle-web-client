
function getDayCategory(context, element) {
    var day = element.attr("day");
    var index = context.attr("index");
    var future = element.attr("class");
    var size = parseInt(context.attr("size"));
    var dayElement = $(".dayList tr[day=\"" + day + "\"].dataRow");
    var categoryTime = element.attr("categoryTime");
    if (dayElement.size()==0) {
        var cols = [{first:true, class: "even"}];
        for (var i=1; i<size; i++) {
            cols[i] = {first:false, class: i%2==1?"odd":"even"};
        }
        var dayData = {categoryTime: categoryTime, day: day, future: future, cols: cols, width : Math.floor(100/size)};
        dayElement = $(render("dayTemplate",dayData));
        var elements = $(".dayList tr.dayTemplate.dataRow");
        var parent = $(".dayList");
        insert(elements, categoryTime, dayElement, parent);
    }
    return dayElement.find("ul.tagList[index="+index+"]");
}

function insert(elements, time, element, parent) {
    var i = 0;
    if (elements.length>0) {
        var currentTime = parseInt($(elements[i]).attr("categoryTime"));
        while (currentTime > time && i < elements.length - 1) {
            i++;
            currentTime = parseInt($(elements[i]).attr("categoryTime"));
        }

        if (i==elements.length - 1) {
            $(elements[i]).after(element);
        } else {
            $(elements[i]).prev().before(element);
        }
    } else {
        parent.prepend(element);
    }
}

function getTagCategory(context, element) {
    var tag = element.attr("tag");
    var tagElement = context.find("li[tag=" + tag + "]");
    if (tagElement.size()==0) {
        var tagData = {tag: tag, tagClass: "tag_"+tag, notEmpty: tag!=""};
        tagElement = $(render("tagTemplate", tagData));
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
        var typeData = {type: type};
        typeElement = $(render("eventTypeTemplate", typeData));
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
    $(".eventList").each(function () {
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

function render(tmpl_name, tmpl_data) {
    if ( !render.tmpl_cache ) {
        render.tmpl_cache = {};
    }

    if ( ! render.tmpl_cache[tmpl_name] ) {
        var tmpl_dir = '/assets/client-templates';
        var tmpl_url = tmpl_dir + '/' + tmpl_name + '.html';

        var tmpl_string;
        $.ajax({
            url: tmpl_url,
            method: 'GET',
            async: false,
            success: function(data) {
                tmpl_string = data;
            }
        });

        render.tmpl_cache[tmpl_name] = Handlebars.compile(tmpl_string);
    }

    return render.tmpl_cache[tmpl_name](tmpl_data);
}