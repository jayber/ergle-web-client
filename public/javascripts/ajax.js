function submitComment(comment) {
    var context = comment.parents(".events li");
    $.ajax({
        type: "POST",
        url: "/comments?eventId=" + context.attr("id"),
        data: comment.val(),
        contentType: "text/plain; charset=UTF-8"
    }).done(function (data) {
        context.find(".comments ul").append(data);
        comment.val("");
        comment.blur();
    }).fail(function (jqXHR, textStatus, errorThrown) {
        context.find(".comments ul").append("<li class=\"error\">" + textStatus + "; " + errorThrown + "</li>");
    })
}

function loadDocumentFragments() {
    var promises = loadFragments($(document));
    $.when.apply($, promises).then(function () {
        if (getParameterByName("zoom") != "day") {
            stackFileVersions();
            hideDuplicateDateCategories();
        }
        highlightTags();
        showNow();
        jumpToAnchor();
    });

    bindEvents($(document));
}

function processNewData(context, data) {
    var dom = $.parseHTML(data);
    $(dom).find("ul.dayList > li").each(function () {
        $(".events > ul.loadable").addClass("dayList");
        $(".events > ul.empty").hide();
        var day = getDayCategory(context, $(this));
        var tag = getTagCategory(day, $(this));
        addElementToType(tag, $(this));
    });

    $(dom).find("ul.eventList > li").each(function () {
        $(".events > ul.loadable").addClass("eventList");
        $(".events > ul.empty").hide();
        context.append($(this));
    });

    if ($(dom).is(".wrapper")) {
        $(dom).each(function () {
            context.html($(this));
        });
    }
    bindEvents(context);
}

function loadFragments(context) {
    /* this creates a lot of ajax requests, which might not be good news for the server. Better to run all these
     * requests through one (web socket) connection?
     */
    var promises = $('.loadable', context).map(function () {
        var self = $(this);
        var url = $(this).attr('href');
        if (url != '/wrapper') {
            return $.get(url).then(function (data) {
                processNewData(self, data);
                self.removeClass("loadable");
                if (data == "") {
                    self.removeClass("body");
                }
                return loadFragments(self);
            });
        }
    });

    return promises;
}
