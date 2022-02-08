function friend_loadFriendsList() {
    fetch("/getfriendsFriends")
        .then(res => res.text())
        .then(res => profile_displayFriends(res))
}

function friend_loadFriendsOfFriendsList() {
    fetch("/getfriendsFriends")
        .then(res => res.text())
        .then(res => profile_displayFriendsOfFriend(res))
}

function friend_loadAchievements() {
    fetch("/getfriendsAchievements")
        .then(res => res.text())
        .then(res => profile_changeAchievements(res))
}

function friend_loadPicture() {
    fetch("/getfriendsPicture")
        .then(res => res.text())
        .then(res => profileView_showPicture(res))
}