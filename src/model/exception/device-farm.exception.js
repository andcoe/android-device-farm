class DeviceFarmException extends Error {
    constructor(message, code) {
        super(message);
        this.code = code;
    }
}

module.exports = DeviceFarmException;
