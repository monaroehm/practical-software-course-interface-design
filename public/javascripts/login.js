function loginEvent() {
    let username = document.getElementById("login_user").value;
    let password = document.getElementById("login_pw").value;

    if (!username && !password) {
        alert("Der Zugang zur Rennstrecke erfordert deinen Benutzernamen und ein Passwort.");
        return;
    } else if (!username) {
        alert("Der Zugang zur Rennstrecke erfordert deinen Benutzernamen.");
        return;
    } else if (!password) {
        alert("Der Zugang zur Rennstrecke erfordert ein Passwort.");
        return;
    }

    fetch('/validateLogin', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username: username,
            password: password
        })
    }).then(res => {
        if (res.ok) {
            window.location.href = "mainpage"
        } else {
            alert("Es ist ein Fehler aufgetreten. Stellen sie sicher, dass ihre angegebenen Logindaten vollständig und korrekt sind.")
        }
    })
}

function login_enterKeySubmit() {
    if (event.keyCode === 13) {
        loginEvent();
    }
}
