function bindDocumentEvents() {

    $(".dropdown").hover(function () {
        $(".dropdownContent").hide();
        $(".dropdownContent", $(this)).show();
    });

    $("#contacts").find("a").click(function (e) {
        if (e.ctrlKey) {
            var pathname = window.location.pathname;
            var uri = $(this).attr("href");
            var queryN = uri.indexOf("?");
            uri = queryN == -1 ? uri.substr(1) : uri.substr(1, queryN - 1);
            window.location.href = pathname + "&" + uri + window.location.search;
            return false;
        }
    });

    $(document).click(function (event) {
        if ((!$(event.target).is(".eventForm")) &&
            ($(event.target).parents(".eventForm").length==0) &&
            fieldsAreEmpty()) {
            $("#eventFormReal").hide();
            $("#eventFormFake").show();
        }

        if (!($(event.target).is(".dropdown, .dropdownContent"))) {
            $(".dropdownContent").hide();
        }
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

    $(".eventForm .fileInput").change(sendFiles);

    $(".eventForm textarea.text").keyup(fixHeight);
}

function bindEvents(context) {
//this assumes 'context' is exclusive, so handlers are not added multiple times to elements

    $(".comments textarea", context).keyup(fixHeight);

    $(".comments textarea", context).focus(function (e) {
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

    $(".attachment a", context).click(removeAttachedFile);

    /*$("#eventForm button",context).click(function () {
     submitEvent();
     return false;
     });*/
}

function fixHeight(e) {
    while ($(this).outerHeight() < this.scrollHeight + parseFloat($(this).css("borderTopWidth")) + parseFloat($(this).css("borderBottomWidth"))) {
        $(this).height($(this).height() + 1);
    }
}

function removeAttachedFile(event) {
    $(event.target).parents(".attachment").remove();
    var container = $(".attachedFiles");
    if (container.children().length==0) {
        container.hide();
    }
    return false;
}

function sendFiles(event) {
    var files = event.target.files[0];
    var url = "/files/?email=" + loggedInEmail + "&filename=" + files.name;

    $.ajax({
        dataType: "html",
        url: url,
        type: "POST",
        data: files,
        processData: false,
        contentType: "text/plain; charset=x-user-defined-binary"
    }).done( function(data, textStatus, jqXHR) {
        var container = $(".attachedFiles");
        var theData = $.parseHTML(data);
        loadLoadable(theData);
        bindEvents(theData);
        container.append(theData);
        container.show();
    }).fail(function(jqXHR, textStatus, errorMessage) {
        alert("failed: "+errorMessage);
    });

    return false;
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
    eventForm.find(".optional").css("display", "none");
    eventForm.find(".optional input").attr("disabled", true);
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
    var inputs = fieldsSection.find("input, textarea");
    inputs.removeAttr("disabled");
    inputs.first().focus();
}