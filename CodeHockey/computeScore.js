var gamesPerPage = 20; // const

var username = 'tyamgin';
var games = 59;
var pages = (parseInt(games / gamesPerPage) + 2) * 2;

var gamesCnt = 0;
var mySum = 0

var calculate = function(page) {
	if (page > pages) {
		var totalSum = gamesCnt * 3;
		console.log(mySum + ' / ' + totalSum + ' (' + (mySum / totalSum) + ')');
		return;
	}

	$.get('http://russianaicup.ru/profile/tyamgin/allGames/page/' + page, function(html) {
	  var doc = $(html);
	  doc.find('.gamesTable tbody tr').each(function() {
		  var tr = $(this);
		  var a = tr.find('td[style="padding-left: 0; padding-right: 0;"] a[title]');
		  var myPos = -1;
		  for(var i = 0; i < a.length; i++)
			 if (a.eq(i).find('span').html() === username)
				 myPos = i;
		  var r = tr.find('td.right:first div').eq(myPos);
		  if (gamesCnt < games && r.html() !== null) {
			 var myPts = parseInt(r.html());
			 mySum += myPts;
			 gamesCnt++;
		  }
	  });
	  calculate(page + 1);
	});
};

calculate(1);