var tags = document.querySelectorAll('.tag10');
var uri = document.documentURI.replace(new RegExp('.+\\/(?:ru|en)\\/(.+)'),'\1harvard-news.html');
for (var tag of tags) {
    var anchor = document.createElement('a');
    var tagName = tag.textContent.replace('&', '%26').replace('+', '%2b');
    anchor.setAttribute('href', uri + '?tag=' + tagName);
    tag.parentNode.insertBefore(anchor, tag);
    anchor.appendChild(tag);
}