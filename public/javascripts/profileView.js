function profile_changeAchievements(achievementsJson) {
    const achievements = new Map(Object.entries(JSON.parse(achievementsJson)));
    const div = document.getElementById("profile_achievements");
    for (const id_as_key of achievements.entries()) {
        const name = id_as_key[1].name;
        const pic = document.createElement("img");
        pic.id = "profile_" + id_as_key[0];
        switch (name) {
            case "ScoreAchievement":
                pic.src = "assets/images/game.png";
                pic.title = id_as_key[1].description;
                break;
            case "HighscoreAchievement":
                pic.src = "assets/images/sports(1).png";
                pic.title = id_as_key[1].description;
                break;
            case "FriendsAchievement":
                pic.src = "assets/images/game(1).png";
                pic.title = id_as_key[1].description;
                break;
            case "GamesPlayedAchievement":
                pic.src = "assets/images/sports.png";
                pic.title = id_as_key[1].description;
                break;
        }
        pic.style.width = "20%";
        pic.style.height = "auto";
        pic.style.marginRight = "3%";
        div.appendChild(pic);
    }
}

function profile_displayFriends(jsonFriendsMap) {
    const friends = JSON.parse(jsonFriendsMap);
    const list = document.getElementById("friends_list");

    for (let i = 0; i < friends.length; i++) {
        {
            const row = document.createElement("li");
            const rowAnchor = document.createElement("a")
            const rowInput = document.createTextNode(friends[i]);
            rowAnchor.appendChild(rowInput)
            rowAnchor.onclick = function () {
                profile_showFriend(this.innerHTML);
            };
            row.appendChild(rowAnchor)
            list.appendChild(row);
        }
    }
}

function profile_displayFriendsOfFriend(jsonFriendsMap) {
    const friends = JSON.parse(jsonFriendsMap);
    const list = document.getElementById("friends_list");

    for (let i = 0; i < friends.length; i++) {
        {
            const row = document.createElement("li");
            const rowAnchor = document.createElement("a")
            const rowInput = document.createTextNode(friends[i]);
            rowAnchor.appendChild(rowInput)
            row.appendChild(rowAnchor)
            list.appendChild(row);
        }
    }
}

function profile_showFriend(friendsName) {
    fetch('/showFriend', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            friendsName: friendsName
        })
    }).then(res => {
        if (res.ok) {
            window.location.href = "friend"
        }
        if (!res.ok) {
            alert("Error while updating friends page on server.")
        }
    })
}

function profileView_showPicture(JSON_id) {
    const picture_id = JSON.parse(JSON_id);
    switch (picture_id) {
        case 1:
            document.getElementById("profile_profilePic").src = "assets/images/Girl.jpg";
            break;
        case 2:
            document.getElementById("profile_profilePic").src = "assets/images/Boy.jpg";
            break;
        case 3:
            document.getElementById("profile_profilePic").src = "assets/images/Girl2.jpg";
            break;
        case 4:
            document.getElementById("profile_profilePic").src = "assets/images/Boy2.jpg";
            break;
        default:
            document.getElementById("profile_profilePic").src = "assets/images/profile-icon.png";
            break;
    }
}

