let eventTimer = undefined;
const randomTimeFactor = 10000;
let gameEventTriggers = undefined;

function setEventTriggers(...gameEventTriggersParam) {
    gameEventTriggers = gameEventTriggersParam;
}

function queueEvent() {
    const lowerBoundRandom = 20000;
    //triggers event every 20-30 seconds: at least lowerBoundRandom seconds, randomTimeFactor + lowerBoundRandom determines max seconds (upper bound)
    eventTimer = setTimeout(triggerRandomEvent, Math.floor(Math.random() * randomTimeFactor) + lowerBoundRandom);
}

function triggerRandomEvent() {
    const random = Math.floor(Math.random() * gameEventTriggers.length);
    const randomEventTrigger = gameEventTriggers[random];
    if (items.skipItemsAvailable > 0) {
        items.enableSkipButton();
    }
    disableInput();
    randomEventTrigger();
}
