$('.postTextBodyDiv img').each(function() {

    let maxWidth = 700;
    let ratio = 0;
    let img = $(this);

    if(img.width() > maxWidth) {
        ratio = img.height() / img.width();
        img.css("height", (maxWidth * ratio));
        img.css("width", maxWidth);
        // img.attr("height", (maxWidth * ratio));
        // img.attr("width", maxWidth);
    }
});

$('.postTextBodyDiv img').load(function() {

    let maxWidth = 700;
    let ratio = 0;
    let img = $(this);

    if(img.width() > maxWidth) {
        ratio = img.height() / img.width();
        img.css("height", (maxWidth * ratio));
        img.css("width", maxWidth);
        // img.attr("height", (maxWidth * ratio));
        // img.attr("width", maxWidth);
    }
});