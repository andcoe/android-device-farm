class Device {

    constructor(id) {
        this.id = id;
        this.inUse = false;
        this.inUseUntil = null;
    }

    setManufacturer(manufacturer) {
        this.manufacturer = manufacturer;
    }

    setModel(model) {
        this.model = model;
    }
}

module.exports = Device;