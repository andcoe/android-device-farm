class Device {

    constructor(id) {
        this.id = id;
    }

    setManufacturer(manufacturer) {
        this.manufacturer = manufacturer;
    }

    setModel(model) {
        this.model = model;
    }
}

module.exports = Device;