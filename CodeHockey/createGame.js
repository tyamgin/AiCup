// Нужно перейти на страницу "создать бой", и выбрать себя и свою версию в первой позиции

var numberOfUsers = 30;
var interval = 2.6; // in minutes
var username = 'tyamgin';
var contestId = '1';
var hisPosition = '2';

var opponent = 'Romka';
var gamesWithOpponent = 0;

var createGameWith = function(opponent, callback) {
	$('#participant' + hisPosition).attr('value', '');
	$('#participant' + hisPosition).attr('value', opponent);
	
	setTimeout(function() {
		$('[value="Создать"]').click();
		setTimeout(callback, interval * 60 * 1000);
	}, 5000);
}

$.get('http://russianaicup.ru/contest/' + contestId + '/standings', function(page) { 
	var users = []; 
	if (opponent && gamesWithOpponent) {
		for(var i = 0; i < gamesWithOpponent; i++)
			users.push(opponent);
	} else {
		$('<div>' + page + '</div>').find('td a span').slice(0, numberOfUsers).each(function() { 
			users.push($(this).html()); 
		});
	}
	
	$('.form-horizontal').attr('target', '_blank');
	
	var createGame = function(users) {
		if (users.length > 0) {
			createGameWith(users[0], function() {
				createGame(users.slice(1)); 
			});
		}
	};
	createGame(users.reverse());
	console.log(users);
})
