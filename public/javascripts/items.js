items = {

    // items can be used 2 times if owned at least 2 times
    enableItemsOnStart: function () {
        if (items.boostItemsOwned > 0) {
            items.boostIcon = document.getElementById("quiz_boostIcon");
            items.boostIcon.classList.remove("quiz_itemDisabled");
            items.boostIcon.onclick = items.useBoostItem;
        }

        items.skipIcon = document.getElementById("quiz_SkipIcon");
    },

    hasBoostItem: false,
    usedBoostItem: false,
    boostIcon: undefined,
    boostItemsAvailable: 0,
    boostItemsOwned: 0, // gets defined in onload

    // increases the gain: one solved question counts as 2 for 10 seconds
    useBoostItem: async function () {
        items.boostIcon.onclick = undefined;
        items.hasBoostItem = true;
        items.boostIcon.classList.add("quiz_boostOn");
        items.boostItemsAvailable--;
        items.boostItemsOwned--;
        items.updateItemsOwnedInDB();
        document.getElementById("quiz_boostItemsAvailable").innerHTML = items.boostItemsAvailable;
        setTimeout(items.actionAfterBoost, 10000);
    },

    actionAfterBoost: function () {
        items.hasBoostItem = false;

        // enable boost button again
        items.boostIcon.onclick = items.useBoostItem;
        items.boostIcon.classList.remove("quiz_boostOn");

        // if used 2 times already or dont own more (already used as many as owned)
        if (items.boostItemsAvailable === 0) {
            // disable button
            items.boostIcon.classList.add("quiz_itemDisabled");
            items.boostIcon.onclick = undefined;
        }
    },

    skipIcon: undefined,
    skipItemsAvailable: 0,
    skipItemsOwned: 0,

    useSkipEventItem: function () {
        if (document.getElementById("quiz_inkCanvas").style.display === "block") {
            inkEvent.actionWhenEventCleared();
        } else if (document.getElementById("quiz_changeTiresPanel").style.display === "block") {
            changeTiresEvent.actionWhenEventCleared();
        }
        items.skipItemsAvailable--;
        items.skipItemsOwned--;
        items.updateItemsOwnedInDB();
        document.getElementById("quiz_skipItemsAvailable").innerHTML = items.skipItemsAvailable;
    },

    disableSkipButton: function () {
        items.skipIcon.classList.add("quiz_itemDisabled");
        items.skipIcon.onclick = undefined;
    },

    enableSkipButton: function () {
        items.skipIcon.classList.remove("quiz_itemDisabled");
        items.skipIcon.onclick = items.useSkipEventItem;
    },

    // gets called in onload of window
    fetchItems: function () {
        fetch("/getUserHasItemsForCurrentUser")
            .then(res => res.json())
            .then(res => items.setItemsOwned(res));
    },

    // sets items owned for logged in user from db
    setItemsOwned: function (itemsListJson) {
        // set amount by amount saved in database(JSON) (if there is no User_has_Item for an item because user never earned
        // it then it is never set and the default of 0 is used as defined in the xxxOwned variable)
        for (let i = 0; i < itemsListJson.length; i++) {
            let user_has_item = itemsListJson[i];
            if (user_has_item.item_name === "Schub") {
                items.boostItemsOwned = user_has_item.amount;
            } else if (user_has_item.item_name === "Überspringen") {
                items.skipItemsOwned = user_has_item.amount;
            }
        }

        if (items.boostItemsOwned > 2) {
            items.boostItemsAvailable = 2;
            document.getElementById("quiz_boostItemsAvailable").innerHTML = items.boostItemsAvailable;
        } else {
            items.boostItemsAvailable = this.boostItemsOwned;
            document.getElementById("quiz_boostItemsAvailable").innerHTML = items.boostItemsOwned;
        }

        if (items.skipItemsOwned > 2) {
            items.skipItemsAvailable = 2;
            document.getElementById("quiz_skipItemsAvailable").innerHTML = items.skipItemsAvailable;
        } else {
            items.skipItemsAvailable = this.skipItemsOwned;
            document.getElementById("quiz_skipItemsAvailable").innerHTML = items.skipItemsOwned;
        }
    },

    updateItemsOwnedInDB: function () {
        fetch('/updateItemsOwned', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                boostItemsOwned: items.boostItemsOwned,
                skipItemsOwned: items.skipItemsOwned
            })
        });
    },

    earnNewRandomItem: function () {
        fetch("/earnNewRandomItem")
            .then(res => res.json())
            .then(res => items.alertEarnedItem(res));
    },

    alertEarnedItem: function (itemNameJson) {
        const itemName = itemNameJson[0];
        const text = "Du hast ein neues \"" + itemName + "\"-Item erhalten!";
        const alertElement = document.getElementById("quiz_earnedItemAlert");
        alertElement.innerText = text;
        alertElement.classList.add("elementToFadeInAndOut");
        setTimeout(function () {
            const alertElement = document.getElementById("quiz_earnedItemAlert");
            alertElement.classList.remove("elementToFadeInAndOut");
        }, 9000, this);
    }
}
