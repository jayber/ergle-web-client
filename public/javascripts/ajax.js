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
    var firstOnes = loadTimelines($(document));
    firstOnes.done(function () {
            var promises = loadLoadable($(document));
            $.when.apply($, promises).always(function () {
                stackFileVersions();
                if (getParameterByName("zoom") != "day") {
                    hideDuplicateDateCategories();
                }
                highlightTags();
                showNow();
                jumpToAnchor();
            });
    });

    bindDocumentEvents();
}

function loadTimelines(context) {
    var promise = $.Deferred().resolve();
    $('.timeline', context).each(function () {
        var timeline = $(this);
        if (promise == "nothing") {
            promise = loader(timeline);
        } else {
            promise = promise.then(function() {return loader(timeline)});
        }
    });
    return promise;
}

function processNewData(context, data) {
    var dom = $.parseHTML(data);
    $(dom).find("ul.dayList > li").each(function () {
        var day = getDayCategory(context, $(this));
        var tag = getTagCategory(day, $(this));
        addElementToType(tag, $(this));
    });

    $(dom).find("ul.eventList > li").each(function () {
        context.append($(this));
    });

    if ($(dom).is(".wrapper")) {
        $(dom).each(function () {
            context.html($(this));
        });
    }
    bindEvents(context);
}

function loadLoadable(context) {
    /* this creates a lot of ajax requests, which might not be good news for the server. Better to run all these
     * requests through one (web socket) connection?
     */
    return $('.loadable', context).map(function () {
        return loader($(this))
    });
}

function loader(context) {
    var self = context;
    var url = context.attr('href');
    if (url != '/wrapper') {
        return $.ajax({
            url: url,
            method: 'GET',
            async: true
        }).done(function (data) {
            processNewData(self, data);
            self.removeClass("loadable");
            if (data == "") {
                self.removeClass("body");
            }
            loadLoadable(context);
        });
    }
}