// запускать на http://russianaicup.ru/game/create
var name = 'tyamgin';
var names = "GreenTea,Milanin,mixei4,morozec,oreshnik,Leos,stem,TonyK,spacistor,TouchAI,Adler".split(',');

var interval = 21 * 60 * 1000;
var gamesPerInterval = 5;

for(var i = 0; i < 1000; i += gamesPerInterval) {
    for (var j = 0; j < gamesPerInterval; j++) {
        var opp = names[(i + j) % names.length];
        setTimeout(function(opp) {
            $('#participant1').val(name);
            $('#participant2').val(opp);
            var form = $('#participant2').parents('form');
            form.attr('target', '_blank');

            setTimeout(function() {
                $.ajax({
                    type: "POST",
                    url: location.href,
                    data: form.serialize(),
                    dataType: "json"
                });
                setTimeout(function() {
                    $('#participant1').val('');
                    $('#participant2').val('');
                }, 500);
            }, 2000);

        }, parseInt(i / gamesPerInterval) * interval + j * 5000, opp);
    }
}