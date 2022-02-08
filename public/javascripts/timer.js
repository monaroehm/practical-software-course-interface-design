class Timer {

    constructor(onTick) {
        this._timerIntervall = null;
        this.onTick = onTick;
        this.tStart = null;
        this.isRunning = false;
        this.finalDuration = null;
    }

    start() {
        this.tStart = new Date().getTime();
        this.isRunning = true;
        this._timerIntervall = setInterval(() => {
            const t = new Date().getTime();
            const duration = t - this.tStart;
            this.onTick();//duration
        }, 1)
    }

    duration() {
        let now = new Date().getTime();
        let duration = now - this.tStart;
        return duration
    }

    durationAsString() {
        let now = new Date().getTime();
        let duration = now - this.tStart;
        return this.timeAsString(duration);
    }

    timeAsString(dur) {
        let duration = dur;
        let min = Math.floor((duration % (1000 * 60 * 60)) / (1000 * 60));
        let s = Math.floor((duration % (1000 * 60)) / 1000);
        let ms = Math.floor(duration % 1000);
        if (s < 10) {
            s = "0" + s
        }
        if (min < 10) {
            min = "0" + min
        }
        
        let hundertstelSekunde;
        //schneidet letzte digit von ms ab
        if (ms == 0) {
            hundertstelSekunde = "00";
        } else {
            hundertstelSekunde = Math.floor(ms / 10)
        }
        if (hundertstelSekunde < 10) {
            hundertstelSekunde = "0" + hundertstelSekunde;
        }
        return min + ":" + s + ":" + hundertstelSekunde;
    }

    get_seconds() {
        let now = new Date().getTime();
        let duration = now - this.tStart;
        return Math.floor((duration % (1000 * 60)) / 1000);
    }

    stop() {
        this.isRunning = false;
        this.finalDuration = this.duration();
        clearInterval(this._timerIntervall);
    }
}