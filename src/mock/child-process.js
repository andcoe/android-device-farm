



let oldest = null;
let recent = null;

class MockChildProcess{

    static exec(command, callback) {
        let nextCommand = MockChildProcess.dequeue();
        if (command === nextCommand.command) {
            callback(nextCommand.code, nextCommand.stdout, nextCommand.stderr)
        } else {
            throw Error(`expected: ${nextCommand.command} but command recieved was: ${command}`)
        }
    }

    static enqueue(mockCommand){
        if(recent ===null){
            oldest = mockCommand;
            recent = mockCommand;
        }else{
            recent.nextCommand = mockCommand;
            recent = mockCommand;
        }

    }

    static dequeue(){
        let dequeued = oldest;
        if(dequeued.nextCommand === null){
            oldest = null;
            recent = null;
        }else {
            oldest = dequeued.nextCommand;
        }
        return dequeued;
    }
}

module.exports = MockChildProcess;