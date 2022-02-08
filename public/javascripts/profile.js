function profile_updateProfile() {
    const newUsername = document.getElementById("profile_changeUsername").value;
    const picSrc = document.getElementById("profile_profilePic").src;

    if (!newUsername) {
        alert("Zum Bearbeiten des Profils wird ein Name benötigt!");
        return;
    }
    fetch('/updateProfile', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username: newUsername,
            picSrc: picSrc
        })
    }).then(res => {
        if (res.ok) {
            document.title = newUsername + "'s Profil";
            document.getElementById("profile_name").textContent = newUsername;
        }
        if (!res.ok) {
            alert("Benutzername ist schon vorhanden!")
        }
    })
    document.getElementsByClassName("profile_bg-modal")[0].style.display = "none";
}


function profile_onEditProfile() {
    document.getElementById("profile_changeUsername").value = document.getElementById("profile_name").textContent;
    const e = document.getElementsByClassName("profile_bg-modal");
    e[0].style.display = 'flex';
}

function profile_cancel() {
    document.getElementsByClassName("profile_bg-modal")[0].style.display = 'none';
    profile_loadPicture();
}

function profile_img_clicked(element) {
    document.getElementById("profile_profilePic").src = element.src;
}

function profile_loadFriendsList() {
    fetch("/getFriends")
        .then(res => res.text())
        .then(res => profile_displayFriends(res))
}

function profile_loadAchievements() {
    fetch("/getAchievements")
        .then(res => res.text())
        .then(res => profile_changeAchievements(res))
}

function profile_validateNewFriend() {
    let friendsName = document.getElementById("profile_searchfriend").value;

    if (!friendsName) {
        alert("Zum Hinzufügen eines Freundes wird ein Name benötigt!");
        return;
    }

    fetch('/validateNewFriend', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            friendsName: friendsName
        })
    }).then(res => res.text())
        .then(res => profile_changeInput(res))
        .then(res => profile_getFriendAchievement())
}

function profile_getFriendAchievement() {
    fetch("/getNewFriendAchievement")
        .then(res => res.text())
        .then(res => profile_updateFriendAchievement(res))
}

function profile_updateFriendAchievement(friendAchievement) {
    const achievements = new Map(Object.entries(JSON.parse(friendAchievement)));
    const div = document.getElementById("profile_achievements");
    for (const id_as_key of achievements.entries()) {
        const pic = document.createElement("img");
        pic.id = "profile_" + id_as_key[0];
        pic.src = "assets/images/game(1).png";
        pic.title = id_as_key[1].description;
        pic.style.width = "20%";
        pic.style.height = "auto";
        pic.style.marginRight = "3%";
        div.appendChild(pic);
    }
}

function profile_changeInput(isAdded) {
    const inputField = document.getElementById("profile_searchfriend");
    if (JSON.parse(isAdded)) {
        const list = document.getElementById("friends_list");
        const row = document.createElement("li");
        const rowAnchor = document.createElement("a");
        const rowInput = document.createTextNode(document.getElementById("profile_searchfriend").value);
        rowAnchor.appendChild(rowInput);
        rowAnchor.onclick = function () {
            profile_showFriend(this.innerHTML);
        };
        row.appendChild(rowAnchor);
        list.appendChild(row);
        notifyFriendAdded();
    } else {
        notifyFriendError();
    }
}

function profile_loadPicture() {
    fetch("/getPicture")
        .then(res => res.text())
        .then(res => profileView_showPicture(res))
}

function notifyFriendAdded() {
    $(".profile_notify").toggleClass("active");
    $("#notifyType").toggleClass("success");

    setTimeout(function () {
        $(".profile_notify").removeClass("active");
        $("#notifyType").removeClass("success");
    }, 2000);
}

function notifyFriendError() {
    $(".profile_notify").addClass("active");
    $("#notifyType").addClass("failure");

    setTimeout(function () {
        $(".profile_notify").removeClass("active");
        $("#notifyType").removeClass("failure");
    }, 2000);
}



