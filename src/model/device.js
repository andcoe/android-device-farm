class Device {

    constructor(id) {
        this.id = id;
        this.manufacturer = null;
        this.model = null;
    }

    setManufacturer(manufacturer) {
        this.manufacturer = manufacturer;
    }

    setModel(model) {
        this.model = model;
    }
}

module.exports = Device;