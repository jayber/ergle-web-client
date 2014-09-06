function firstFiveTags() {
    var tagClasses = $("[class*='tag_']").map(function() {
            var classValue = $(this).attr("class");
            if (classValue.indexOf(" ")>-1) {
                return classValue.split(" ")[1];
            } else {
                return classValue;
            }
        }
    ).toArray();

    var uniques = [];

    for (var i=0;i<tagClasses.length;i++) {
        if ($.inArray(tagClasses[i], uniques) == -1) {
            uniques[uniques.length] = tagClasses[i];
        }
    }

    var finalIndex =  uniques.length < 5 ? uniques.length : 5;
    return uniques.slice(0, finalIndex);
}

function highlightTags() {
    var top = firstFiveTags();
    var css = createCSS(top);

    setTagStyles(css);
}

function createCSS(top) {
    var colours = ["#FFC040","#FF3030","#1e90ff","#259F40","#800080"];
    var result = "";
    for (var i = 0; i<top.length; i++) {
        result = result + "." + top[i] + " { background-color: " + colours[i] + " !important;}\n";
        result = result + "ul.tagList span." + top[i] + " { border: 0; color: #ffffff; background-image:url('/assets/images/whiteTag.svg') !important;}\n";
    }
    return result;
}

function setTagStyles(css) {
    var styleElem = document.getElementById("tagStyles");

    if (!styleElem) {

        styleElem = document.createElement('style');
        styleElem.type = 'text/css';
        styleElem.id = "tagStyles";

        // Append the Style element to the Head
        var head = document.getElementsByTagName('head')[0];
        head.appendChild(styleElem);
    }

    if(styleElem.styleSheet){
        styleElem.styleSheet.cssText = css;
    }
    else{
        styleElem.innerHTML = css;
    }

}