inkEvent = {

    //get defined in quiz_onload()
    canvas: undefined,
    context: undefined,
    inkInterval: undefined,

    trigger: function () {
        inkEvent.showCanvas();
        inkEvent.drawInk();
        inkEvent.inkInterval = setInterval(inkEvent.checkIfCanvasCleared, 10);
    },

    showCanvas: function () {
        inkEvent.canvas.style.display = "block";
    },

    hideCanvas: function () {
        inkEvent.canvas.style.display = "none";
    },

    isFirstMove: false,

    drawInk: function () {
        inkEvent.context.clearRect(0, 0, inkEvent.canvas.width, inkEvent.canvas.height);

        inkEvent.context.globalCompositeOperation = 'source-over';
        inkEvent.context.strokeStyle = 'black';
        inkEvent.context.lineWidth = 3;

        inkEvent.context.beginPath();
        inkEvent.context.arc(80, 75, 50, 0, 2 * Math.PI);
        inkEvent.context.fill();

        inkEvent.context.beginPath();
        inkEvent.context.arc(120, 90, 25, 0, 2 * Math.PI);
        inkEvent.context.fill();

        inkEvent.context.beginPath();
        inkEvent.context.arc(250, 110, 90, 0, 2 * Math.PI);
        inkEvent.context.fill();

        inkEvent.context.beginPath();
        inkEvent.context.arc(340, 150, 40, 0, 2 * Math.PI);
        inkEvent.context.fill();

        inkEvent.context.beginPath();
        inkEvent.context.arc(90, 205, 15, 0, 2 * Math.PI);
        inkEvent.context.fill();

        inkEvent.context.beginPath();
        inkEvent.context.arc(450, 35, 10, 0, 2 * Math.PI);
        inkEvent.context.fill();

        inkEvent.context.beginPath();
        inkEvent.context.arc(350, 75, 60, 0, 2 * Math.PI);
        inkEvent.context.fill();

        inkEvent.isFirstMove = true;
    },

    canvasx: undefined,
    canvasy: undefined,
    last_mousex: 0,
    last_mousey: 0,
    mousex: 0,
    mousey: 0,

    onload: function () {
        inkEvent.canvas = document.getElementById("quiz_inkCanvas");
        inkEvent.context = inkEvent.canvas.getContext("2d");

        //Mousemove
        $(inkEvent.canvas).on('mousemove', function (e) {
            inkEvent.canvasx = $(inkEvent.canvas).offset().left;
            inkEvent.canvasy = $(inkEvent.canvas).offset().top;
            inkEvent.mousex = e.clientX - inkEvent.canvasx;
            inkEvent.mousey = e.clientY - inkEvent.canvasy;

            // isFirstMove to avoid erasing a line in the circle on first move since last_mousex and last_mousey would
            // still be somewhere inside the circle and not the user mouse position
            if (inkEvent.isFirstMove) {
                inkEvent.last_mousex = inkEvent.mousex = e.clientX - inkEvent.canvasx;
                inkEvent.last_mousey = inkEvent.mousey = e.clientY - inkEvent.canvasy;
                inkEvent.isFirstMove = false;
            } else {
                inkEvent.context.beginPath();

                inkEvent.context.globalCompositeOperation = 'destination-out';
                inkEvent.context.lineWidth = 20;

                inkEvent.context.moveTo(inkEvent.last_mousex, inkEvent.last_mousey);
                inkEvent.context.lineTo(inkEvent.mousex, inkEvent.mousey);
                inkEvent.context.lineJoin = inkEvent.context.lineCap = 'round';
                inkEvent.context.stroke();
            }
            inkEvent.last_mousex = inkEvent.mousex;
            inkEvent.last_mousey = inkEvent.mousey;
        });
    },

    countPixels: function () {
        let pixelsCount = 0;
        let p = inkEvent.context.getImageData(0, 0, inkEvent.context.canvas.width, inkEvent.context.canvas.height).data;
        for (let y = 0, i = 0; y < inkEvent.context.canvas.height; y++)
            for (let x = 0; x < inkEvent.context.canvas.width; x++, i += 4) {
                if (p[i + 3]) { //if (a pixel)
                    pixelsCount++;
                }
            }
        return pixelsCount;
    },

    checkIfCanvasCleared: function () {
        if (inkEvent.countPixels() < 5000) {
            inkEvent.actionWhenEventCleared();
        }
    },

    actionWhenEventCleared: function () {
        clearInterval(inkEvent.inkInterval);
        enableInput();
        inkEvent.hideCanvas();
        queueEvent();
        items.disableSkipButton();
    }
}