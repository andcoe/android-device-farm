# pip3 install M2Crypto adb
# adb kill-server
# python3 setupDevices.py

import os.path as op

from adb import adb_commands
from adb import sign_m2crypto


# KitKat+ devices require authentication
signer = sign_m2crypto.M2CryptoSigner(
    op.expanduser('~/.android/adbkey'))
# Connect to the device
device = adb_commands.AdbCommands()
print(device)
device.ConnectDevice(
    rsa_keys=[signer])
# Now we can use Shell, Pull, Push, etc!
print(device.Shell('echo asdf'))