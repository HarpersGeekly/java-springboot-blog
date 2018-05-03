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
                    img.src = e.target.result
                    // clean result before
                    result.innerHTML = '';
                    // append new image
                    result.appendChild(img);
                    // show save btn and options
                    save.classList.remove('hide');
                    options.classList.remove('hide');
                    confirm.classList.remove('hide');
                    // init cropper
                    cropper = new Cropper(img);
                }
            };
            reader.readAsDataURL(e.target.files[0]);
        }
    });

    var file;

    // save on click
    save.addEventListener('click', (e) => {
        e.preventDefault();
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
        cropped.classList.remove('hide');
        img_result.classList.remove('hide');
        // show image cropped
        cropped.src = imgSrc;
        dwn.classList.remove('hide');
        dwn.download = 'imagename.png';
        dwn.setAttribute('href', imgSrc);
    });

    $('.confirm').on("click", function (e) {
        e.preventDefault();

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

        });

        request.fail((a, b, c) => {
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