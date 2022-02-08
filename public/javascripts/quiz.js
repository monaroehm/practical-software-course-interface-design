let timer = new Timer(onTimerTick);
let answeredQuestions = 0;
let rightAnswered = 0;
let wrongAnswered = 0;
let KI_answeredQuestions = 0;
let KI_errors = 0;
let difference = 0;
let maxAmountQuestions = 15;
let distance_enemy;
let distance_user;
let isRunning = false;
let isLoser = false;
const enemyInterval = 7000;
const KI_fehlerWsk = 0.2;
let mode;

function quiz_onload() {
    inkEvent.onload();
    changeTiresEvent.onload();
    items.fetchItems();
    setMode();
    quiz_generate_Question();
}

async function setMode() {
    mode = await fetch('/getMode')
        .then(res => {
            return res.text();
        });

    if (mode === undefined) {
        mode = "standard";
    } else if (mode === "chaos") {
        document.getElementById("quiz_itemBar").style.display = "flex";
    }
}

function quiz_enterKeySubmit() {
    if (event.keyCode === 13) {
        quiz_checkAnswer();
    }
}

function quiz_generate_Question() {
    document.getElementById("quiz_firstFactor").innerHTML = (Math.floor(Math.random() * Math.floor(11))).toString();
    document.getElementById("quiz_secondFactor").innerHTML = (Math.floor(Math.random() * Math.floor(11))).toString();
}


async function quiz_checkAnswer() {
    const content = document.getElementById("quiz_content");
    const answer = document.getElementById("quiz_answer").value;
    const firstFactor = document.getElementById("quiz_firstFactor").innerHTML;
    const secondFactor = document.getElementById("quiz_secondFactor").innerHTML;
    const feedbackText = document.getElementById("quiz_feedback");

    answeredQuestions++;

    if (firstFactor * secondFactor === parseInt(answer)) {
        feedbackText.innerHTML = "Richtig!"
        rightAnswered++;
        updateTachometer();

        if (items.hasBoostItem) {
            rightAnswered++;
        }

        quiz_generate_Question();
        //reset input field
        document.getElementById("quiz_answer").value = "";
    } else {
        wrongAnswered++;

        disableInput();
        content.classList.add("error");
        feedbackText.innerHTML = "Falsch! Richtig wäre " + firstFactor * secondFactor
        feedbackText.classList.add("elementToFadeInAndOut");
        setTimeout(function () {
            quiz_generate_Question();
            enableInput();
            //reset input field
            document.getElementById("quiz_answer").value = "";
            content.classList.remove("error");
            feedbackText.classList.remove("elementToFadeInAndOut");
            document.getElementById("quiz_answer").focus();
        }, 3000);
    }

    change_KI_view();
    update_view_2D();

    if (rightAnswered >= maxAmountQuestions) {
        quiz_end();
        return;
    }
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

async function quiz_startlights() {
    document.getElementById("quiz_lights").style.display = "block";
    await sleep(6000);
    document.getElementById("quiz_lights").style.display = "none";
}

async function quiz_start() {
    await quiz_startlights();


    isRunning = true;

    if (mode === "chaos") {
        items.enableItemsOnStart();
        setEventTriggers(changeTiresEvent.trigger, inkEvent.trigger);
        queueEvent();
    }
    isLoser = false;
    KI_answeredQuestions = 0;
    answeredQuestions = 0;
    difference = 0;

    start_gif();
    startRaceTrack();
    timer.start();
    setInterval(updateTachometer, 1000);

    document.getElementById("quiz_start").style.opacity = "0.07";
    document.getElementById("quiz_question").style.visibility = "visible";
    document.getElementById("quiz_bubble_content").innerHTML = "Weiter so! Du schaffst das!";
    document.getElementById("quiz_bubble_content").style.marginBottom = "10%";

    setKI();
    enableInput();
}

function onTimerTick() {
    document.getElementById("quiz_timer").innerHTML = timer.durationAsString();
}

function quiz_end() {
    timer.stop();
    isRunning = false;

    disableInput();
    stop_gif();
    updateTotalScore(); // also loads Endscreen
    storeCurrentGameInDB();
}

function calculateGameScore() {
    // X Axis: timePerTask
    // Y Axis: Multiplikationsfaktor mit exponentiellem Zerfall?

    let timePerTask = timer.finalDuration / answeredQuestions;
    let difficultyFactor = (mode === "chaos") ? 3 : 2;

    const rawScore = (1 / timePerTask) * rightAnswered * difficultyFactor * (isLoser ? 0 : 1);
    return Math.floor(rawScore * 10000);
}

function updateTotalScore() {
    fetch('/updateScore', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            score: calculateGameScore()
        })
    }).then(res => {
        if (res.ok) {
            loadEndScreen();
        }
        if (!res.ok) {
            alert("Error while saving score on server.")
        }
    })
}

function winOrLoseOutput() {
    if (isLoser) {
        document.getElementById("end_trophy").style.display = "none";
        return "verloren!";
    } else {
        return "gewonnen!";
    }
}

async function getHighscore() {
    const response = await fetch('/getHighscore');
    return response.text();
}

function calculateUserEndTimePerTask() {
    if (answeredQuestions === 0) {
        return timer.finalDuration;
    }
    return timer.finalDuration / answeredQuestions;
}

function calculateEnemyEndTimePerTask() {
    if (KI_answeredQuestions === 0) {
        return timer.finalDuration;
    }
    return timer.finalDuration / KI_answeredQuestions;
}

// tpt = time per task
async function showEndscreen() {
    document.getElementById("quiz_end").style.display = "block";
    document.getElementById("end_msg").innerHTML = '' + "Du hast " + winOrLoseOutput();
    document.getElementById("end_msg2").innerHTML = '' + "Du bekommst " + calculateGameScore() + " Punkte!";
    document.getElementById("end_highscore").innerHTML = '' + "Dein Highscore: " + await getHighscore();
    document.getElementById("end_errors_user").innerHTML = '' + wrongAnswered;
    document.getElementById("end_time").innerHTML = timer.timeAsString(timer.finalDuration);
    document.getElementById("end_tpt_user").innerHTML = timer.timeAsString(calculateUserEndTimePerTask());
    document.getElementById("end_tpt_enemy").innerHTML = timer.timeAsString(calculateEnemyEndTimePerTask());
    document.getElementById("end_errors_enemy").innerHTML = '' + KI_errors;
    if (!isLoser) {
        confetti();
        if (mode === "standard") {
            items.earnNewRandomItem(); // also alerts user about which item
        }
    }
}

function storeCurrentGameInDB() {
    fetch("/storeGame", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            duration: timer.duration(),
            score: calculateGameScore()
        }),
    }).then(res => {
        if (!res.ok) {
            alert("Error while sending game information to server.")
        }
    })
}

function quiz_exit() {
    document.getElementById("quiz_exit").style.display = "block";
}

function quiz_confirm_exit() {
    window.location.href = 'mainpage';
}

function quiz_dismiss_exit() {
    document.getElementById("quiz_exit").style.display = "none";
}

function change_KI_view() {
    let newDifference = KI_answeredQuestions - rightAnswered;
    if (newDifference <= 0 && difference > 0) {
        document.getElementById("quiz_insideCar").style.display = "block";
        document.getElementById("quiz_nearCar").style.display = "none";
        document.getElementById("quiz_farawayCar").style.display = "none";
        document.getElementById("quiz_bubble_content").innerHTML = "Weiter so! Du schaffst das!";

    }
    if (newDifference >= 5 && difference < 5) {
        document.getElementById("quiz_insideCar").style.display = "none";
        document.getElementById("quiz_nearCar").style.display = "none";
        document.getElementById("quiz_farawayCar").style.display = "block";
        document.getElementById("quiz_bubble_content").innerHTML = "Ohjeee! Beeil dich, um schnell aufzuholen!";

    }
    if (newDifference > 0 && newDifference < 5 && (difference <= 0 || difference >= 5)) {
        document.getElementById("quiz_insideCar").style.display = "none";
        document.getElementById("quiz_nearCar").style.display = "block";
        document.getElementById("quiz_farawayCar").style.display = "none";
        document.getElementById("quiz_bubble_content").innerHTML = "Nur ein bisschen schneller! Dann hast du ihn!";

    }
    difference = newDifference;
}

function setKI() {
    setInterval(repeatOften, enemyInterval);
}

function repeatOften() {
    if (isRunning) {
        if (KI_answeredQuestions === maxAmountQuestions) {
            isLoser = true;
            quiz_end();
            return;
        }
        if (Math.random() < KI_fehlerWsk) {
            KI_errors++;
        } else {
            KI_answeredQuestions++;
            change_KI_view();
            update_view_2D();
        }
    }
}

function enableInput() {
    document.getElementById("quiz_eingabe_button").disabled = false;
    document.getElementById("quiz_answer").disabled = false;
}

function disableInput() {
    document.getElementById("quiz_eingabe_button").disabled = true;
    document.getElementById("quiz_answer").disabled = true;
}

function update_view_2D() {
    distance_enemy = KI_answeredQuestions * (48 / maxAmountQuestions);
    distance_user = rightAnswered * (48 / maxAmountQuestions);
    document.getElementById("quiz_car_user").style.setProperty("-webkit-transform", "translate3d(" + distance_user + "vw, 0, 0)")
    document.getElementById("quiz_car_enemy").style.setProperty("-webkit-transform", "translate3d(" + distance_enemy + "vw, 0, 0)")
}

function start_gif() {
    document.getElementById("quiz_nogif").style.display = "none";
    document.getElementById("quiz_insideCar").style.display = "block";
}

function stop_gif() {
    document.getElementById("quiz_nogif").style.display = "block";
    document.getElementById("quiz_insideCar").style.display = "none";
    document.getElementById("quiz_nearCar").style.display = "none";
    document.getElementById("quiz_farawayCar").style.display = "none";
}

function updateTachometer() {
    let durationInSeconds = timer.duration() / 1000;
    let startPositionInDeg = -120;
    let newPositionInDeg = startPositionInDeg + (rightAnswered * 500) / durationInSeconds;
    if (newPositionInDeg > 120) {
        newPositionInDeg = 120;
    }
    document.getElementById("line").style.setProperty('-webkit-transform', "rotate(" + newPositionInDeg + "deg)");
}

function loadEndScreen() {
    fetch("/getNewAchievements")
        .then(res => res.text())
        .then(res => showEndscreen())
}

let raceTrackInterval = null

function startRaceTrack() {
    const dt = 10
    const tDurchlauf = 3000
    const elem = document.getElementById("quiz_track2D");
    let t = 0;
    raceTrackInterval = setInterval(frame, dt);

    function frame() {
        t += dt;
        const xRel = (t % tDurchlauf) / tDurchlauf
        setPos(elem, 1 - xRel - 1)
    }

    function setPos(elem, x) {
        elem.style.left = (x * 100) + '%' // 42%
    }
}

