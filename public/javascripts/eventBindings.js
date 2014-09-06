function bindEvents(context) {

    $(".dropdown", context).hover(function () {
        $(".dropdownContent").hide();
        $(".dropdownContent", $(this)).show();
    });

    $("#contacts", context).find("a").click(function (e) {
        if (e.ctrlKey) {
            var pathname = window.location.pathname;
            var uri = $(this).attr("href");
            var queryN = uri.indexOf("?");
            uri = queryN == -1 ? uri.substr(1) : uri.substr(1, queryN-1);
            window.location.href = pathname + "&" + uri + window.location.search;
            return false;
        }
    });

    $(".events .comments textarea, .eventForm textarea.text", context).keyup(function (e) {
        while ($(this).outerHeight() < this.scrollHeight + parseFloat($(this).css("borderTopWidth")) + parseFloat($(this).css("borderBottomWidth"))) {
            $(this).height($(this).height() + 1);
        }
    });

    $(".events .comments textarea").focus(function (e) {
        $("span", $(this).parent()).show()
    }).blur(function () {
        if (this.value == "") {
            $("span.controls", $(this).parent()).hide()
        }
    });

    $(".comments button", context).click(function () {
        submitComment($(this).parents(".events .comments").find("textarea"));
    });

    $(".comments textarea", context).keypress(function (event) {
        var keyCode = (event.which ? event.which : event.keyCode);
        if (keyCode === 10 || keyCode == 13 && event.ctrlKey) {
            submitComment($(this));
            return false;
        }
        return true;
    });

    $("#eventFormFake").click(function () {
        $(this).hide();
        var eventFormReal = $("#eventFormReal");
        eventFormReal.show();

        showCorrectFields();
    });

    $(".eventFormMenu a").click(function (event) {
        $(".eventFormMenu a").removeClass("selected");
        $(event.target).addClass("selected");
        showCorrectFields();
        return false;
    });

    $(document).click(function (event) {
        if ((!($(event.target).is(".eventForm, .eventForm input, .eventForm textarea, .eventFormMenu a"))) &&
            fieldsAreEmpty()) {
            $("#eventFormReal").hide();
            $("#eventFormFake").show();
        }

        if (!($(event.target).is(".dropdown, .dropdownContent"))) {
            $(".dropdownContent").hide();
        }
    });

    /*$("#eventForm button",context).click(function () {
     submitEvent();
     return false;
     });*/
}

function fieldsAreEmpty() {
    var empty = true;
    $(".eventForm .field, .eventForm .text").each(function () {
        empty = $(this).val() == "" && empty;
    });
    return empty;
}

function showCorrectFields() {
    var type;
    var eventForm = $("#eventFormReal");
    eventForm.find("section").css("display", "none");
    eventForm.find("input").attr("disabled", true);
    var fieldsSection;
    if ($("div.eventFormMenu a.selected").is(".intent")) {
        fieldsSection = $("#intentFields");
        type = "intent";
    } else {
        fieldsSection = $("#messageFields");
        type = "message";
    }
    $("#typeField").val(type).removeAttr("disabled");
    fieldsSection.css("display", "block");
    fieldsSection.children().removeAttr("disabled");
    fieldsSection.children().first().focus();
}