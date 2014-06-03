function bindEvents(context) {
    $("#contacts",context).find("a").click(function (e) {
        if (e.ctrlKey) {
            window.location.href = window.location.href + "&" + $(this).attr("href").substr(1);
            return false;
        }
    });

    $(".events .comment textarea",context).keyup(function (e) {
        while ($(this).outerHeight() < this.scrollHeight + parseFloat($(this).css("borderTopWidth")) + parseFloat($(this).css("borderBottomWidth"))) {
            $(this).height($(this).height() + 1);
        }
    }).focus(function (e) {
        $("span", $(this).parent()).show()
    }).blur(function () {
        if (this.value == "") {
            $("span", $(this).parent()).hide()
        }
    });

    $(".events .comment button",context).click(function () {
        submitComment($(this).parents(".events .comment").find("textarea").val());
    })
}

