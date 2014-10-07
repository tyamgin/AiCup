var numberOfUsers = 50;
var interval = 2.6; // in minutes
var username = 'tyamgin';

$.get('http://russianaicup.ru/contest/1/standings', function(page) { 
	var users = []; 
	$('<div>' + page + '</div>').find('td a span').slice(0, numberOfUsers).each(function() { 
		users.push($(this).html()); 
	});
	
	$('.form-horizontal').attr('target', '_blank');
	
	var createGame = function(users) {
		if (users.length > 0) {
			$('#participant1').attr('value', '');
			$('#participant1').attr('value', users[0]);
			
			setTimeout(function() {
				$('[value="Создать"]').click();
				setTimeout(function() {
					createGame(users.slice(1)); 
				}, interval * 60 * 1000);
			}, 5000);
		}
	};
	createGame(users.reverse());
})
