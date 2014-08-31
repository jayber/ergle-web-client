function submitComment(comment) {
    var context = comment.parents(".events li");
    $.ajax({
        type: "POST",
        url: "/comments?eventId="+context.attr("id"),
        data: comment.val(),
        contentType: "text/plain; charset=UTF-8"
    }).done(function(data) {
        context.find(".comments ul").append(data);
        comment.val("");
        comment.blur();
    }).fail(function(jqXHR,textStatus,errorThrown) {
        context.find(".comments ul").append("<li class=\"error\">"+ textStatus +"; "+ errorThrown+"</li>");
    })
}

function loadDocumentFragments() {
    loadAndProcess($(document));
}

function loadAndProcess(context) {
    var promises = loadFragments(context);
    $.when(promises).done(function() {
        processLoadedPage(context)
    });
}

function processLoadedPage(context) {
    var zoom = getParameterByName("zoom");
    if (zoom=='day') {
        categoriseByDay();
        categoriseDaysByTag();
        categoriseDayTagsByType();
    } else {
        stackFileVersions();
        hideDuplicateDateCategories();
    }
    highlightTags();
    bindEvents(context);
    showNow(context);
}

function loadFragments(context) {
    return $('.loadable', context).map(function () {
        var self = $(this);
        var url = $(this).attr('href');
        if (url != '/wrapper') {
            return $.get(url).done( function (data) {
                self.html(data);
                self.removeClass("loadable");
                if (data == "") {
                    self.removeClass("body");
                } else {
                    loadAndProcess(self);
                }
            });
        }
    });
}
