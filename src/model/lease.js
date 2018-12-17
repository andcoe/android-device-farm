class Lease {

    constructor(device, inUseUntil) {
        this.id = Math.floor(Math.random() * 1000000000).toString(); //TODO: improve this with unique ids
        this.inUseUntil = inUseUntil;
        this.device = device;
    }
}

module.exports = Lease;