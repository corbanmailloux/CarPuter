# Corban Mailloux, 2014

import pyfirmata

# Adjust that the port match your system, see samples below:
# On Linux: /dev/tty.usbserial-A6008rIF, /dev/ttyACM0, 
# On Windows: \\.\COM1, \\.\COM2
PORT = '\\.\COM3'
red = None
green = None
botton = None
board = None

def main():
    global board
    global red
    global green
    global button

    # Creates a new board 
    print("Setting up the connection to the board...")
    board = pyfirmata.Arduino(PORT)
    print("Ready!")

    it = pyfirmata.util.Iterator(board)
    it.start()

    # Setup the digital pins
    button = board.get_pin('d:6:i')
    button.enable_reporting()

    green = board.get_pin('d:3:p')
    red = board.get_pin('d:2:o')

    while (True):
        if button.read():
            # Block the button to avoid multiple triggers.
            while (button.read()):
                pass
            # Do stuff on release
            windowUp()

        board.pass_time(0.025)

    board.exit()




def windowUp():
    red.write(0)
    for i in range(0, 100):
        if button.read():
            # Block the button to avoid multiple triggers.
            while (button.read()):
                pass
            # Do stuff on release
            break
        green.write(i/100)
        board.pass_time(0.05)
    green.write(0)
    red.write(1)



if __name__ == "__main__":
    main()