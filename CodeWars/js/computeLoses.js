var name = location.pathname.match(/\/profile\/([^\/]+)\//)[1];
var url = `http://russianaicup.ru/profile/${name}/contest4/page/`;
var pagesCount = parseInt($('.pagination span[pageindex]:last').text());

var loses = {};

var requests = [];
for(var i = 1; i <= pagesCount; i++) {
    requests.push($.get(url + i, function (html) {
        var doc = (new DOMParser()).parseFromString(html, 'text/html');
        
        var games = $(doc).find(`td a[href="/profile/${name}"]:not([title])`);
        games.each(function () {
            var isWin = $(this).index() === 0;
            var td = $(this).parent();
            if (!isWin) {
                var oppName = td.find(`[href]:not([href*="/profile/${name}"])`).text();
                loses[oppName] = (loses[oppName] || 0) + 1;
            }
        });
    }));
}

$.when.apply($, requests).done(function() {
    console.log(`Loses of ${name}`)
    for (var k in loses) {
        if (loses.hasOwnProperty(k)) {
            console.log(`${k}: ${loses[k]}`);
        }
    }
});