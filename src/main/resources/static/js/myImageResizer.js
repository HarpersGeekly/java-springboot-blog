$('.postDescription').each(function() {
    if ($(this).find('img').length) {
        // there is an image in this div, do something...
        let maxWidth = 740;
        let ratio = 0;
        let img = $(this);

        if (img.width() > maxWidth) {
            ratio = img.height() / img.width();
            img.css("height", (maxWidth * ratio));
            img.css("width", maxWidth);
            // To prevent the DOM bouncing around during resizing, hide it first.
            // Add a "hidden" (display:none in css) class to the html element $() then remove it after resizing is complete
            $('.postDescription').removeClass('hidden');
            // img.attr("height", (maxWidth * ratio));
            // img.attr("width", maxWidth);
        }
    }
        $('.postDescription').removeClass('hidden');
});

$('.postDescription').on('load', function() {
    if ($(this).find('img').length) {
        // there is an image in this div, do something...
        let maxWidth = 740;
        let ratio = 0;
        let img = $(this);

        if (img.width() > maxWidth) {
            ratio = img.height() / img.width();
            img.css("height", (maxWidth * ratio));
            img.css("width", maxWidth);
            $('.postDescription').removeClass('hidden');
            // img.attr("height", (maxWidth * ratio));
            // img.attr("width", maxWidth);
        }
    }
        $('.postDescription').removeClass('hidden');
});