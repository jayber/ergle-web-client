definitions = {
    layout: function (elem, args) {
        elem.children().css('float', 'left');
        elem.next().css('clear', 'left');
    },
    position: function (elem, args) {
        if (args == 'fixed') {
            elem.css('top', '0');
            elem.next().css('padding-top', elem.height() + 'px');
        }
    },
    textVerticalAlign: function (elem, args) {
        if (args == 'middle') {
            elem.wrapInner('<div></div>');
            var container = elem.children().first();
            container.css('display', 'table-cell');
            container.css('verticalAlign', 'middle');
            container.css('height', elem.height() + 'px');
        }
    },
    placement: function (elem, args) {
        if (args == 'center') {
            var unfilled = elem.parent().outerWidth() - elem.outerWidth();
            var half = unfilled / 2;
            var perc = (half / elem.parent().outerWidth()) * 100;
            elem.css('margin-left', perc + '%');
        }
    },
    layoutConstraints: function (elem, args) {
        if (args == 'wgrow') {
            var currentTotal = 0;
            var siblings = elem.siblings();
            siblings.filter(":visible").each(function () {
                currentTotal = currentTotal + $(this).outerWidth(true);
            });

            var remainder = elem.parent().innerWidth() - currentTotal;
            var thisMarginAndPadding = elem.outerWidth(true) - elem.width();
            remainder = remainder - thisMarginAndPadding;
            elem.css('width', remainder + 'px');
        }
    }
}