class Device {

    constructor(id) {
        this.id = id;
        this.manufacturer = null;
        this.model = null;
        this.port = null;
    }

    setManufacturer(manufacturer) {
        this.manufacturer = manufacturer;
    }

    setModel(model) {
        this.model = model;
    }

    setPort(port) {
        this.port = port;
    }
}

module.exports = Device;