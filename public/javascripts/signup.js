function signupEvent() {
    let username = document.getElementById("signup_user").value;
    let password = document.getElementById("signup_pw").value;

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

    fetch('/validateSignup', {
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
            window.location.href = "/";
        } else {
            alert("Es gibt bereits einen Nutzer mit diesem Namen. Bitte versuchen sie es mit einem anderen Benutzernamen.");
        }
    })
}