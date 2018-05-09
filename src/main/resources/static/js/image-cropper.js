$(document).ready(function () {
    // vars
    let result = document.querySelector('.result'),
        img_result = document.querySelector('.img-result'),
        img_w = document.querySelector('.img-w'),
        img_h = document.querySelector('.img-h'),
        options = document.querySelector('.options'),
        save = document.querySelector('.save'),
        cropped = document.querySelector('.cropped'),
        dwn = document.querySelector('.download'),
        upload = document.querySelector('#file-input'),
        confirm = document.querySelector('.confirm'),
        cropper = '';

    // on change show image with crop options
    upload.addEventListener('change', (e) => {
        if (e.target.files.length) {
            // start file reader
            const reader = new FileReader();
            reader.onload = (e) => {
                if (e.target.result) {
                    // create new image
                    let img = document.createElement('img');
                    img.id = 'image';
                    img.src = e.target.result;
                    // clean result before
                    result.innerHTML = '';
                    // append new image
                    result.appendChild(img);
                    // show save btn and options
                    save.classList.remove('hide');
                    options.classList.remove('hide');
                    // init cropper
                    cropper = new Cropper(img);
                }
            };
            reader.readAsDataURL(e.target.files[0]);
        }
    });

    let file;
    let spinner = $('.saving-gif');

    let croppingImage = function() {
        // get result to data uri
        let imgSrc = cropper.getCroppedCanvas({
            // width: img_w.value // input value
        }).toDataURL();

        let blobBin = atob(imgSrc.split(',')[1]);
        let array = [];
        for (let i = 0; i < blobBin.length; i++) {
            array.push(blobBin.charCodeAt(i));
        }
        file = new Blob([new Uint8Array(array)], {type: 'image/png'});

        // remove hide class of img
        spinner.addClass('hide');
        cropped.classList.remove('hide');
        img_result.classList.remove('hide');
        // show image cropped
        cropped.src = imgSrc;
        confirm.classList.remove('hide');
        dwn.classList.remove('hide');
        dwn.download = 'imagename.png';
        dwn.setAttribute('href', imgSrc);
    };
    // save on click
    $('.container').on("click", ".save", (e) => {
        e.preventDefault();
        spinner.removeClass("hide");
        setTimeout(croppingImage, 500);

    });

    $('.container').on("click", ".confirm", (e) => {
        e.preventDefault();

        $('.confirm').addClass("pulse");

        // let croppedImage = $('.cropped').attr('src');
        // console.log(croppedImage);
        // let croppedImageName = $('.cropped').attr('name');
        // console.log("cropped name attribute: " + croppedImageName);

        let form = new FormData($("#data-edit-profile-picture-form")[0]);
        form.append("croppedImage", file);
        let request = $.ajax({
            url: "/profile/edit/fileupload",
            type: "POST",
            data: form,
            enctype: 'multipart/form-data',
            acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i,
            processData: false,
            contentType: false,
            cache: false,
        });
        request.done(function (user) {

            console.log("back from profile request.done");
            console.log(user);
            console.log(user.username);
            console.log(user.id);
            console.log(user.profilePicture);


            // select the image with jquery
            // change the src attr
            // with user.profile_picture
            $('.profilePic').attr("src", '/uploads/' + user.profilePicture);
            // console.log(successMsg);

            // alert(data.files[data.index].error);
            // $('.modal.in').modal('toggle');
        });

        request.fail((a, b, c) => {
            alert("File size too large");
            $('#profilePictureError').removeClass('hidden');
        // .css("display","none");
            console.log(a, b, c)
        });
        return false;
    });

    $('.postTextBodyDiv img').each(function () {

        let maxWidth = 700;
        let ratio = 0;
        let img = $(this);

        if (img.width() > maxWidth) {
            ratio = img.height() / img.width();
            img.css("height", (maxWidth * ratio));
            img.css("width", maxWidth);
            // img.attr("height", (maxWidth * ratio));
            // img.attr("width", maxWidth);
        }
    });

    /*$('.postTextBodyDiv img').load(function () {

        let maxWidth = 700;
        let ratio = 0;
        let img = $(this);

        if (img.width() > maxWidth) {
            ratio = img.height() / img.width();
            img.css("height", (maxWidth * ratio));
            img.css("width", maxWidth);
            // img.attr("height", (maxWidth * ratio));
            // img.attr("width", maxWidth);
        }
    });
*/
});