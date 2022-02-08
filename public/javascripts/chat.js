let timer;
let user_id;

function open_chat() {
    document.getElementById("chat_show").style.display = "block";
    document.getElementById("chat_button").style.display = "none";
    chat_loadFriendsList();
    getUserId();

}

function close_chat() {
    document.getElementById("chat_show").style.display = "none";
    document.getElementById("chat_button").style.display = "block";
    clear();
}

function chat_loadFriendsList() {
    fetch("/getFriends")
        .then(res => res.text())
        .then(res => chat_displayFriends(res))
}

function chat_displayFriends(jsonFriendsMap) {
    const friends = JSON.parse(jsonFriendsMap);
    const list = document.getElementById("friends_list_chat");

    for (let i = 0; i < friends.length; i++) {
        let row = document.createElement("li");
        let rowAnchor = document.createElement("a")
        let rowInput = document.createTextNode(friends[i]);
        rowAnchor.appendChild(rowInput)
        rowAnchor.onclick = function () {
            endInterval();
            chat_showFriend(this.innerHTML);
            update(this.innerHTML);
        };
        row.appendChild(rowAnchor)
        list.appendChild(row);
    }
}

function chat_showFriend(friendname) {
    const buI = document.getElementById("chat_messagelist_buI");
    buI.innerHTML = '';
    const button_send = document.createElement("a");
    button_send.innerHTML = "senden";
    button_send.onclick = function () {
        chat_sendMessage(friendname);
    };
    const button_send_id = document.createAttribute("id");
    button_send_id.value = "chat_send_button";
    button_send.setAttributeNode(button_send_id);
    document.getElementById("chat_messagelist_buI").appendChild(button_send);

    const name = document.getElementById("chat_containerLabel_Freund");
    name.innerHTML = "Verlauf mit " + friendname;

    const input_text = document.createElement("input");
    const input_text_id = document.createAttribute("id");
    const input_text_placeholder = document.createAttribute("placeholder");
    const input_text_auto = document.createAttribute("autocomplete");

    input_text_id.value = "chat_input_text";
    input_text_placeholder.value = "schreiben...";
    input_text_auto.value = "off";
    input_text.setAttributeNode(input_text_id);
    input_text.setAttributeNode(input_text_placeholder);
    input_text.setAttributeNode(input_text_auto);
    input_text.addEventListener("keyup", function (event) {
        if (event.keyCode === 13) {
            event.preventDefault();
            document.getElementById("chat_send_button").click();

        }
    })
    document.getElementById("chat_messagelist_buI").appendChild(input_text);
    chat_loadChatHistory(friendname);
}

function chat_sendMessage(friendname) {
    let input = document.getElementById("chat_input_text").value;

    if (!input) {
        alert("Das Senden einer NAchricht ohne Inhalt ist nicht möglich!");
        return;
    }
    fetch("/sendMessage", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            message_text: input,
            receiver_name: friendname,
        }),
    }).then(res => chat_showFriend(friendname));
}

function chat_loadChatHistory(friendsname) {
    fetch("/getChatHistory", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            receiver_name: friendsname,
        }),
    })
        .then(res => res.text())
        .then(res => chat_displayChatHistory(res))
}

function chat_displayChatHistory(jsonMessageMap) {
    let listM = document.getElementById("friends_listM").getElementsByTagName("p");
    for (let k = listM.length - 1; k >= 0; k--) {
        let item = listM [k];
        item.parentNode.removeChild(item);
    }
    const message = new Map(Object.entries(JSON.parse(jsonMessageMap)));
    const list = document.getElementById("friends_listM");

    for (let id_as_key of message.entries()) {
        const input = id_as_key[1].message_text;
        const time = id_as_key[1].time;
        const row = document.createElement("p");
        const rowChild = document.createElement("p");
        const row_class = document.createAttribute("class");
        const rowChild_class = document.createAttribute("class");
        rowChild_class.value = "chat_Messagelist_time";
        const sender = id_as_key[1].user_id_sender;
        if (sender == user_id) {
            row_class.value = "chat_Messagelist_user";
        } else {
            row_class.value = "chat_Messagelist_friend";
        }
        row.setAttributeNode(row_class);
        rowChild.setAttributeNode(rowChild_class);
        const rowChildInput = document.createTextNode(time);
        const rowInput = document.createTextNode(input);

        rowChild.appendChild(rowChildInput);
        row.appendChild(rowInput)
        row.appendChild(rowChild);
        list.appendChild(row);

    }
    const chat = document.getElementById("chat_Messagelist");
    chat.scrollTo(0, chat.scrollHeight);
}

function clear() {
    const list = document.getElementById("friends_list_chat").getElementsByTagName("li");
    for (let k = list.length - 1; k >= 0; k--) {
        let item = list[k];
        item.parentNode.removeChild(item);
    }

    const listM = document.getElementById("friends_listM");
    listM.innerHTML = '';
    const buI = document.getElementById("chat_messagelist_buI");
    buI.innerHTML = '';
    const name = document.getElementById("chat_containerLabel_Freund");
    name.innerText = "Wähle einen Freund aus um ihm Nachrichten zu Schreiben!";
    endInterval();
}

function update(friendsname) {
    timer = setInterval(chat_loadChatHistory, 2000, friendsname);
}

function endInterval() {
    clearInterval(timer);
    timer = null;
}

function getUserId() {
    fetch("/getUserId")
        .then(res => res.text())
        .then(res => user_id = JSON.parse(res))
}
