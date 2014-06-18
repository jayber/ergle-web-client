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

function submitEvent() {
    $.post(
        "/events/",
        $("#eventForm").find("form").serialize()
    ).done(function(data) {
            alert(data);
        });
}

function loadDocumentFragments() {
    var promises = loadFragments($(document));

    $.when(promises).done(function () {
        highlightTags();
        stackFileVersions();
        hideDuplicateDateCategories();
        bindEvents($(document));
        showNow($(document));
    });
}

function loadFragments(context) {
    return $('.loadable', context).map(function () {
        var self = $(this);
        var url = $(this).attr('href');
        if (url != '/wrapper') {
            $.get(url, function (data) {
                self.html(data);
                self.removeClass("loadable");
                if (data == "") {
                    self.removeClass("body");
                } else {
                    loadFragments(self);
                }
            });
        }
    });
}
