var username = 'tyamgin';
var contestId = '4'; // final
var lose = {}, win = {};
var pages = 27;

var computeLoses = function(page) {

	if (page > pages) {
		console.log(lose);
		console.log(win);
		return;
	}

	$.get('http://russianaicup.ru/profile/' + username + '/contest' + contestId + '/page/' + page, function(html) {
	  var doc = $(html);
	  doc.find('.gamesTable tbody tr').each(function() {
		  var tr = $(this);
		  var a = tr.find('td[style="padding-left: 0; padding-right: 0;"] a[title]');
		  var myPos = -1;
		  for(var i = 0; i < a.length; i++)
			 if (a.eq(i).find('span').html() === username)
				 myPos = i;
		  var r = tr.find('td.right:first div').eq(myPos);
		  var t = tr.find('td.right:first div').eq(1 - myPos);

		  if (r.html() !== null && t.html() !== null) {
			 var myPts = parseInt(r.html()),
				 hisPts = parseInt(t.html()),
				 hisName = a.eq(1 - myPos).find('span').html();
			 if (!lose.hasOwnProperty(hisName)) {
				lose[hisName] = 0;
				win[hisName] = 0;
		     }
			 lose[hisName] += hisPts;
			 win[hisName] += myPts;
		  }
	  });
	  computeLoses(page + 1);
	});
};

computeLoses(1);