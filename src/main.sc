require: slotfilling/slotFilling.sc
  module = sys.zb-common
  
require: text/text.sc
    module = zenbot-common
    
require: where/where.sc
    module = zenbot-common
    
require: common.js
    module = zenbot-common

require: hangmanGameData.csv
    name = HangmanGameData
    var = $HangmanGameData

patterns:
    $Word = $entity<HangmanGameData> || converter = function ($parseTree) {
        var id = $parseTree.HangmanGameData[0].value;
        return $HangmanGameData[id].value;
        };

theme: /

    state: Start
        q!: $regex</start>
        a: Привет! Я бот для игры в угадай столицу
        a: Я называю страну, а ты - её столицу, в конце игры я показываю, сколько ты угадал
        buttons:
            "Начать игру" -> /Инициализация
            
    state: NoMatch
        event!: noMatch
        a: Я не понял. Вы сказали: {{$request.query}}
    
    state: reset
        q!: reset
        script:
            $session = {};
            $client = {};
        go!: /

    state: Старт игры
        script:
            var rand = $reactions.random(192);
            var parseTree = {
                Countries: [
                    { value: rand }
                ]
            };
            $session.selectedCountry = $global.$converters.countryConverter(parseTree).name
            $session.correctCapital= $global.$converters.country2CapitalConverter(parseTree).name
            log($global.$converters.countryConverter(parseTree));
        InputText: 
            prompt = Какая столица у страны {{$session.selectedCountry}}?
            varName = guessedCity
            html = 
            htmlEnabled = false
            then = /Проверка
            actions = 

    state: Проверка
        if: $session.correctCapital== $session.guessedCity
            script:
                $session.guessedCapitalCounter++
            a: Правильно!
        else: 
            script:
                $session.unguessedCapitalCounter++
            a: Ошибочка(
        buttons:
            "Играем дальше" -> /Старт игры
            "Подвести итоги" -> /Результаты

    state: Инициализация
        script:
            $session.guessedCapitalCounter = 0
            $session.unguessedCapitalCounter = 0
        a: Отлично, начинаем)
        go!: /Старт игры

    state: Результаты
        a: Угадано: {{$session.guessedCapitalCounter}}
           Не угадано: {{$session.unguessedCapitalCounter}}
        buttons:
            "Попробовать еще раз" -> /Инициализация
            "Закончить" -> /reset