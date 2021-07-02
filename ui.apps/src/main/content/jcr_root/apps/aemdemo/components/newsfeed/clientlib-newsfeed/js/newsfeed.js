var showMoreButton = document.getElementById('show-more');
var tag = window.location.search.replace('?tag=','').replace('%26', '&').replace('%2b', '+').replace('%20', ' ');
if (showMoreButton != null) {
    showMoreButton.onclick = function () {
        var locale = document.documentElement.lang;
        var url = '/content/aemdemo/en/harvard-news/jcr:content/root/container/newsfeed.html';
        if (locale === 'ru') {
            url = '/content/aemdemo/ru/harvard-news/jcr:content/root/container/newsfeed.html';
        }
        var offset = document.getElementById('offset').innerText;
        jQuery.ajax({
            url : url,
            method : 'GET',
            data: {'offset':offset, 'tag':tag},
            success: function (msg) {
                var parser = new DOMParser();
                var doc = parser.parseFromString(msg, 'text/html');
                document.getElementById('offset').innerText = doc.getElementById('offset').innerText;
                document.getElementById('newsfeed').insertAdjacentHTML('beforeend', doc.getElementById('newsfeed').innerHTML);
            }
        });
    }
}
