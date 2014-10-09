var numberOfUsers = 60;
var interval = 2.6; // in minutes
var username = 'tyamgin';
var contestId = '1';

$.get('http://russianaicup.ru/contest/' + contestId + '/standings', function(page) { 
	var users = []; 
	$('<div>' + page + '</div>').find('td a span').slice(0, numberOfUsers).each(function() { 
		users.push($(this).html()); 
	});
	
	$('.form-horizontal').attr('target', '_blank');
	
	var createGame = function(users) {
		if (users.length > 0) {
			$('#participant2').attr('value', '');
			$('#participant2').attr('value', users[0]);
			
			setTimeout(function() {
				$('[value="Создать"]').click();
				setTimeout(function() {
					createGame(users.slice(1)); 
				}, interval * 60 * 1000);
			}, 5000);
		}
	};
	createGame(users.reverse());
	console.log(users);
})
