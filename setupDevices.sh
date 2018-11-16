set -e

adb -s 3204486bc15611b5 tcpip 5555
sleep 1
adb -s 3204486bc15611b5 forward tcp:6555 tcp:5555
sleep 1

adb -s HT73R0205355 tcpip 5555
sleep 1
adb -s HT73R0205355 forward tcp:6556 tcp:5555
sleep 1
