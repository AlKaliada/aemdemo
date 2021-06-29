var isLikeClicked = false;
var isDislikeClicked = false;
var likeButton = document.getElementById('like');
var dislikeButton = document.getElementById('dislike');
likeButton.onclick = function() {
    if (isLikeClicked) {
        isLikeClicked = false;
        likeButton.classList.remove('like_clicked')
        $.ajax({
            url : '/bin/like',
            method : 'POST',
            data: {'likeCounter':'decrement', 'url':document.documentURI},
            success: function (msg){
                console.log(msg);
                document.getElementById('like_value').innerHTML = msg;
            }
        });
    } else {
        if (isDislikeClicked){
            isDislikeClicked = false;
            dislikeButton.classList.remove('dislike_clicked')
            $.ajax({
                url : '/bin/like',
                method : 'POST',
                data: {'dislikeCounter':'decrement', 'url':document.documentURI},
                success: function (msg){
                    console.log(msg);
                    document.getElementById('dislike_value').innerHTML = msg;
                }
            });
        }
        likeButton.classList.add('like_clicked');
        isLikeClicked = true;
        $.ajax({
            url : '/bin/like',
            method : 'POST',
            data: {'likeCounter':'increment', 'url':document.documentURI},
            success: function (msg){
                console.log(msg);
                document.getElementById('like_value').innerHTML = msg;
            }
        });
    }
};
dislikeButton.onclick = function () {
    if (isDislikeClicked) {
        isDislikeClicked = false;
        dislikeButton.classList.remove('dislike_clicked')
        $.ajax({
            url : '/bin/like',
            method : 'POST',
            data: {'dislikeCounter':'decrement', 'url':document.documentURI},
            success: function (msg){
                console.log(msg);
                document.getElementById('dislike_value').innerHTML = msg;
            }
        });
    } else {
        if (isLikeClicked){
            isLikeClicked = false;
            likeButton.classList.remove('like_clicked')
            $.ajax({
                url : '/bin/like',
                method : 'POST',
                data: {'likeCounter':'decrement', 'url':document.documentURI},
                success: function (msg){
                    console.log(msg);
                    document.getElementById('like_value').innerHTML = msg;
                }
            });
        }
        dislikeButton.classList.add('dislike_clicked');
        isDislikeClicked = true;
        $.ajax({
            url : '/bin/like',
            method : 'POST',
            data: {'dislikeCounter':'increment', 'url':document.documentURI},
            success: function (msg){
                console.log(msg);
                document.getElementById('dislike_value').innerHTML = msg;
            }
        });
    }
};