// File upload box drops down. Once user selects a file, javascript checks if it's a valid image type: jpg, jpeg, png, gif.' +
// ' It also checks file size is less than 5MB along with resetting the forms on both conditions. ' +
// 'Using Cropper.js the image can now be cropped. After clicking 'save', the cropped image is compressed by 90% and once ' +
// ''confirm' is clicked, the file is appended to the form and sent via ajax to the FileUploadController where it will be ' +
// 'saved as a users profile.

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
        form = document.querySelector('data-edit-profile-picture-form'),
        cropper = '';

    // on change show image with crop options
        upload.addEventListener('change', (e) => {
            if (e.target.files.length) {

                let file = event.target.files[0];

                if(file.size>5*1024*1024) {
                    alert("Images must be less than 5MB please");
                    $(form).get(0).reset();
                    return;
                }

                if(!file.type.match(/gif|png|jpg|jpeg/)) {
                    alert("Not a valid image type");
                    $(form).get(0).reset();
                    return;
                    }

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
                console.log(e.target.files[0].size / 1024 / 1024 + "MB");
            }
        });

    let file;
    let spinner = $('.saving-gif');

    // $('#file-input').bind('change', function() {
    //     if(this.files[0].size > 3145728) {
    //         alert('Hold up! This file is too large at: ' + this.files[0].size / 1024 / 1024 + "MB. Please select a file less than 3MB.");
    //     }
    // });

    let croppingImage = function() {
        // get result to data uri
        let imgSrc = cropper.getCroppedCanvas({
            // width: img_w.value // input value
        }).toDataURL("image/jpeg",0.9);

        let blobBin = atob(imgSrc.split(',')[1]);
        let array = [];
        for (let i = 0; i < blobBin.length; i++) {
            array.push(blobBin.charCodeAt(i));
        }
        file = new Blob([new Uint8Array(array)], {type: 'image/png'});

        console.log("file size: " + file.size / 1024 / 1024 + "MB");
        spinner.addClass('hide');
        // remove hide class of img
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
            // select the image with jquery
            // change the src attr
            // with user.profile_picture
            $('.profilePic').attr("src", '/uploads/' + user.profilePicture);

        });

        request.fail((a, b, c) => {
            alert("File size too large");
            $('#profilePictureError').removeClass('hidden');
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