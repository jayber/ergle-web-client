function bindEvents(context) {
    $("#contacts",context).find("a").click(function (e) {
        if (e.ctrlKey) {
            window.location.href = window.location.href + "&" + $(this).attr("href").substr(1);
            return false;
        }
    });

    $(".events .comments textarea",context).keyup(function (e) {
        while ($(this).outerHeight() < this.scrollHeight + parseFloat($(this).css("borderTopWidth")) + parseFloat($(this).css("borderBottomWidth"))) {
            $(this).height($(this).height() + 1);
        }
    }).focus(function (e) {
        $("span", $(this).parent()).show()
    }).blur(function () {
        if (this.value == "") {
            $("span.controls", $(this).parent()).hide()
        }
    });

    $(".events .comments button",context).click(function () {
        submitComment($(this).parents(".events .comments").find("textarea"));
    });

    $(".events .comments textarea",context).keypress(function (event) {
        var keyCode = (event.which ? event.which : event.keyCode);
        if (keyCode === 10 || keyCode == 13 && event.ctrlKey) {
            submitComment($(this));
            return false;
        }
        return true;
    });

    $("#eventFormFake").click(function() {
        $(this).hide();
        $("#eventFormReal").show();
    });

    $(document).click(function(event) {
        if (! ($(event.target).is(".eventForm, .eventForm input"))) {
            $("#eventFormReal").hide();
            $("#eventFormFake").show();
        }
    });

    /*$("#eventForm button",context).click(function () {
        submitEvent();
        return false;
    });*/
}

