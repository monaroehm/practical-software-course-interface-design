function main_hoverPlay() {
    if (document.getElementById("main_garage").style.display !== "none" || document.getElementById("main_hoverPlay").style.display !== "none") {
        document.getElementById("main_garage").style.display = "none";
        document.getElementById("main_hoverPlay").style.display = "block";
    }
}

function main_unhoverPlay() {
    if (document.getElementById("main_garage").style.display !== "none" || document.getElementById("main_hoverPlay").style.display !== "none") {
        document.getElementById("main_garage").style.display = "block";
        document.getElementById("main_hoverPlay").style.display = "none";
    }
}

function main_hoverProfile() {
    if (document.getElementById("main_garage").style.display !== "none" || document.getElementById("main_hoverProfile").style.display !== "none") {
        document.getElementById("main_garage").style.display = "none";
        document.getElementById("main_hoverProfile").style.display = "block";
    }
}

function main_unhoverProfile() {
    if (document.getElementById("main_garage").style.display !== "none" || document.getElementById("main_hoverProfile").style.display !== "none") {
        document.getElementById("main_garage").style.display = "block";
        document.getElementById("main_hoverProfile").style.display = "none";
    }
}

function main_hoverInventory() {
    if (document.getElementById("main_garage").style.display !== "none" || document.getElementById("main_hoverInventory").style.display !== "none") {
        document.getElementById("main_garage").style.display = "none";
        document.getElementById("main_hoverInventory").style.display = "block";
    }
}

function main_unhoverInventory() {
    if (document.getElementById("main_garage").style.display !== "none" || document.getElementById("main_hoverInventory").style.display !== "none") {
        document.getElementById("main_garage").style.display = "block";
        document.getElementById("main_hoverInventory").style.display = "none";
    }
}

function main_hoverBlueprint() {
    if (document.getElementById("main_garage").style.display !== "none" || document.getElementById("main_hoverBlueprint").style.display !== "none") {
        document.getElementById("main_garage").style.display = "none";
        document.getElementById("main_hoverBlueprint").style.display = "block";
    }
}

function main_unhoverBlueprint() {
    if (document.getElementById("main_garage").style.display !== "none" || document.getElementById("main_hoverBlueprint").style.display !== "none") {
        document.getElementById("main_garage").style.display = "block";
        document.getElementById("main_hoverBlueprint").style.display = "none";
    }
}

function main_toggleOverlay() {
    if (document.getElementById("main_garageOverlay").style.display === "none") {
        document.getElementById("main_garageOverlay").style.display = "block";
        document.getElementById("main_garage").style.display = "none";
    } else {
        document.getElementById("main_garageOverlay").style.display = "none";
        document.getElementById("main_garage").style.display = "block";
    }
}

function pop() {
    document.getElementById("main_garage").style.display = "block";
    document.getElementById("main_grayGarage").style.display = "none";
}

function hide() {
    document.getElementById("main_hoverPlay").style.display = "none";
    document.getElementById("main_garage").style.display = "none";
    document.getElementById("main_grayGarage").style.display = "block";
}

function main_toggleModeSelection() {
    const dialog = document.querySelector('dialog'),
        closebutton = document.getElementById('close_dialog');

    if (!dialog.hasAttribute('open')) {
        //show the dialog
        dialog.setAttribute('open', 'open');
        //after displaying the dialog, focus the closebutton inside it
        closebutton.focus();
        closebutton.addEventListener('click', main_toggleModeSelection);
        hide();
    } else {
        dialog.removeAttribute('open');
        pop();
    }
}

function main_setMode(mode) {
    fetch('/setMode', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            mode: mode
        })
    }).then(res => {
        if (res.ok) {
            window.location.href = "quiz"
        } else {
            alert("Es ist ein Fehler aufgetreten. Stellen sie sicher, dass ihre angegebenen Modedaten korrekt sind.")
        }
    })
}

async function main_openInventory() {
    const itemContainer = document.getElementById("main_inventory");
    itemContainer.innerHTML = "";
    let itemListJson;
    let userHasItemListJson;

    await fetch("/getAllItems")
        .then(res => res.json())
        .then(res => itemListJson = res);

    await fetch("/getUserHasItemsForCurrentUser")
        .then(res => res.json())
        .then(res => userHasItemListJson = res);


    for (let i = 0; i < itemListJson.length; i++) {
        console.log("hello");
        let item = itemListJson[i];
        let itemDiv = document.createElement("div");
        itemDiv.classList.add("main_item");

        let itemName = document.createElement("p");
        itemName.innerText = item.name;
        itemName.style.fontWeight = "bold";
        let itemPicture = document.createElement("img");
        itemPicture.src = item.path;
        let itemDescription = document.createElement("p");
        itemDescription.innerText = item.description;
        let amount = document.createElement("p");
        console.log(userHasItemListJson);
        for (let j = 0; j < userHasItemListJson.length; j++) {
            console.log("hello");
            console.log(userHasItemListJson[j].item_name);
            if (item.name === userHasItemListJson[j].item_name) {
                amount.innerText = "in Besitz: " + userHasItemListJson[j].amount;
            }
        }

        if (amount.innerText === "") {
            amount.innerText = "in Besitz: 0";
        }

        itemDiv.append(itemName, itemPicture, itemDescription, amount);
        itemContainer.appendChild(itemDiv);
    }
    document.getElementById("main_inventoryBackground").style.display = "block";
}

function main_closeInventory() {
    document.getElementById("main_inventoryBackground").style.display = "none";
}


// --------------------blueprint-----------------

let slideIndex = 1; // 1..n

function changeToPreviousSlide() {
    showSlide(slideIndex -= 1);
}

function changeToNextSlide() {
    showSlide(slideIndex += 1);
}

function currentSlide(newSlideIndex) {
    showSlide(slideIndex = newSlideIndex);
}

function showSlide(n) {
    var i;
    var slides = document.getElementsByClassName("mySlides");
    var dots = document.getElementsByClassName("dot");
    if (n > slides.length) {
        slideIndex = slides.length
    }
    if (n < 1) {
        slideIndex = 1
    }
    hideUnusedArrow(slideIndex);

    for (i = 0; i < slides.length; i++) {
        slides[i].style.display = "none";
    }
    for (i = 0; i < dots.length; i++) {
        dots[i].className = dots[i].className.replace(" dot_active", "");
    }
    slides[slideIndex-1].style.display = "block";
    dots[slideIndex-1].className += " dot_active";
}

function main_openBlueprint() {
    const slideshowContainer = document.getElementsByClassName("slideshow-container")[0]
    slideshowContainer.style.display = "block"
    showSlide(slideIndex);
}

function main_closeBlueprint() {
    const slideshowContainer = document.getElementsByClassName("slideshow-container")[0]
    slideshowContainer.style.display = "none"
}

function hideUnusedArrow(slideIndex) {
    var slides = document.getElementsByClassName("mySlides");
    if (slideIndex === 1) {
        document.getElementsByClassName("prev")[0].style.display = "none";
    } else {document.getElementsByClassName("prev")[0].style.display = "block";}
    if (slideIndex === slides.length) {
        document.getElementsByClassName("next")[0].style.display = "none";
    } else {document.getElementsByClassName("next")[0].style.display = "block";}
}