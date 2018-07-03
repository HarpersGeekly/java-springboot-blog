// ============================= RETURN TO TOP ON CLICK =======================================

$(window).scroll(function () {
    if ($(this).scrollTop() > 500) {
        $('.scrollUp').fadeIn("slow");
    } else {
        $('.scrollUp').fadeOut();
    }
});
$('.scrollUp').click(function () {
    $("html, body").animate({
        scrollTop: 0
    }, 500);
    return false;
});