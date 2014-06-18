
function highlightTags() {
    var tags = tagsOrderedByFrequency();

    var top;
    if (tags.length>4) {
        top = tags.slice(0,5);
    } else {
        top = tags;
    }

    var css = createCSS(top);

    setTagStyles(css);
}

function createCSS(top) {
    var colours = ["#FFC040","#FFC0CB","#1e90ff","#259F40","#800080"];
    var result = "";
    for (var i = 0; i<top.length; i++) {
        result = result + "." + top[i] + " { background-color: " + colours[i] + " !important;}\n";
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

function tagsOrderedByFrequency() {
    var tagClasses = $("[class*='tag_']").map(function() {
            var classValue = $(this).attr("class");
            if (classValue.indexOf(" ")>-1) {
                return classValue.split(" ")[1];
            } else {
                return classValue;
            }
        }
    );
    var tagCountMap = {};
    tagClasses.each(function() {
        if (this in tagCountMap) {
            tagCountMap[this] = tagCountMap[this] +1;
        } else {
            tagCountMap[this] = 1;
        }
    });

    var tagNames = [];
    var i =0;
    for (var key in tagCountMap) {
        if (tagCountMap.hasOwnProperty(key)) {
            tagNames[i++] = key;
        }
    }
    return tagNames.sort(function(a,b) {
        return tagCountMap[b] - tagCountMap[a];
    })
}