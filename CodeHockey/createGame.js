var interval = 5; // in minutes
var username = 'tyamgin';

$('#participant1').attr('value', username);
$('.form-horizontal').attr('target', '_blank');

var createGame = function() {
	$('#participant1').attr('value', username);
	$('#participant2').attr('value', '');
    $('.complete-with-random-button').click();
    setTimeout(function() {
        $('[value="Создать"]').click();
    }, 5000);
}

setInterval(createGame, interval * 60 * 1000);
