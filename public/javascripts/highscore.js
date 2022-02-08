function generateTable(scoreboardJson) {
    const scoreMap = new Map(Object.entries(JSON.parse(scoreboardJson)));
    let scoreArr = [[]];


    for (const e of scoreMap.entries()) {
        scoreArr.push([e[0], e[1]])
    }

    scoreArr.sort(function (a, b) {
        return a[1] - b[1];
    });
    scoreArr.reverse();

    const table = document.getElementById("highscore_tbody");

    for (let i = 0; i < scoreArr.length; i++) {
        // create a new row
        let newRow = table.insertRow(table.length);
        for (let j = 0; j < scoreArr[i].length; j++) {
            // create a new cell
            let cell = newRow.insertCell(j);

            // add value to the cell
            cell.innerHTML = scoreArr[i][j];
        }
    }
}

function loadScoreboard() {
    fetch("/getScoreboard")
        .then(res => res.text())
        .then(res => generateTable(res))
}

function loadHighscoreboard() {
    fetch("/getHighscoreboard")
        .then(res => res.text())
        .then(res => generateTable(res))
}

function loadGamesPlayedboard() {
    fetch("/getGamesPlayedboard")
        .then(res => res.text())
        .then(res => generateTable(res))
}

function highscore_changeToScore() {
    $("#highscore_tbody tr").remove();
    document.getElementById("highscore_value").innerHTML = "Punkte";
    loadScoreboard();
}

function highscore_changeToHighscore() {
    $("#highscore_tbody tr").remove();
    document.getElementById("highscore_value").innerHTML = "Rekord";
    loadHighscoreboard();
}

function highscore_changeToGamesPlayed() {
    $("#highscore_tbody tr").remove();
    document.getElementById("highscore_value").innerHTML = "Spielanzahl";
    loadGamesPlayedboard();
}