// запускать на http://russianaicup.ru/game/create
var name = 'tyamgin';
var names = [
"Antmsu",
"NighTurs",
"Commandos",
"Recar",
"core2duo",
"TonyK",
"r2d2",
"mortido",
"cheeser",
"Oxidize",
"WildCat",
"mustang",
"ud1",
"byserge",
"Megabyte",
"Spasitel",
"OrickBy",
"Romka",
"serlis",
"GreenTea",
"Belonogov",
"Adler",
"En_taro_adun",
"Rety",
"novich-OK",
"rekcahd",
"Fireworks",
"mixei4",
"Karkun",
"login_169605",
"Milanin",
"DVS",
"vzverev78",
"Levatol",
"dedoo",
"MucmuK",
"Crabar",
"Laur_lct",
"Thief911",
"jetblack",
"AntonT",
"morozec",
"wntgd",
"oreshnik",
"MikeWazowski",
"Equinox",
"-XraY-",
"Khao",
"ivanodiit",
"Tranvick",
"Clasker",
"savfod",
"Hohol",
"VolodymyrMelnyk",
"NorwegianHacker",
"andrey11",
"Ederigo",
"_.kiler2004._",
"mi5",
].reverse();

for(var i = 0; i < 1000; i += 5) {
    for (var j = 0; j < 5; j++) {
        var opp = names[(i + j) % names.length];
        setTimeout(function(opp) {
            $('#participant1').val(name);
            $('#participant2').val(opp);
            var form = $('#participant2').parents('form');
            form.attr('target', '_blank');

            setTimeout(function() {
                form.submit();
                setTimeout(function() {
                    $('#participant1').val('');
                    $('#participant2').val('');
                }, 500);
            }, 2000);

        }, parseInt(i / 5) * 21 * 60 * 1000 + j * 5000, opp);
    }
}