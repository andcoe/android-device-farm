from flask import Flask

app = Flask(__name__)

port = 6555


@app.route("/")
def index():
    global port
    if port == 6556:
        port = 6555
    else:
        port = port + 1

    return str(port)


if __name__ == "__main__":
    app.run()
