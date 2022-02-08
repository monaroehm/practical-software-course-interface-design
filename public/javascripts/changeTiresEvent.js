changeTiresEvent = {

    panel: undefined,
    panelDefaultContent: undefined,
    tiresDropped: 0,

    trigger: function () {
        changeTiresEvent.tiresDropped = 0;
        changeTiresEvent.resetPanelData();
        changeTiresEvent.showPanel();
    },

    onload: function () {
        const panel = document.getElementById("quiz_changeTiresPanel");
        changeTiresEvent.panel = panel;
        changeTiresEvent.panelDefaultContent = panel.innerHTML;
    },

    hidePanel: function () {
        changeTiresEvent.panel.style.display = "none";
    },

    showPanel: function () {
        changeTiresEvent.panel.style.display = "block";
    },

    resetPanelData: function () {
        changeTiresEvent.panel.innerHTML = changeTiresEvent.panelDefaultContent;
    },

    removeTireLeft: function (tireElement) {
        $(tireElement).animate({left: "-=5%", opacity: "0"}, {duration: 1000}, "easeinout");
        tireElement.display = "none";
    },

    removeTireRight: function (tireElement) {
        $(tireElement).animate({left: "+=5%", opacity: "0"}, {duration: 1000}, "easeinout");
        tireElement.display = "none";
    },

    actionWhenEventCleared: function () {
        changeTiresEvent.hidePanel();
        queueEvent();
        enableInput();
        items.disableSkipButton();
    },

    allowDrop: function (ev) {
        ev.preventDefault();
    },

    drag: function (ev) {
        ev.dataTransfer.setData("text", ev.target.id);
    },

    drop: function (ev) {
        ev.preventDefault();
        const data = ev.dataTransfer.getData("text");
        const draggedElement = document.getElementById(data);
        ev.target.appendChild(draggedElement);

        // prevents being able to drop multiple items in already filled container
        ev.target.ondragover = "";

        // picture fills out whole container (that has same size as tire)
        draggedElement.style.padding = "0";
        draggedElement.style.margin = "0";
        draggedElement.style.width = "100%";
        draggedElement.style.height = "100%";
        draggedElement.classList.add("noAnimation");

        // prevents being able to move item out again after dropped in a correct container
        draggedElement.draggable = false;

        changeTiresEvent.tiresDropped++;
        if (changeTiresEvent.tiresDropped === 4) {
            changeTiresEvent.actionWhenEventCleared();
        }
    }
}