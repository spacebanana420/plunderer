# Plunderer
TCP server and Client for transferring files with its own unique protocol.

The server opens and waits for a connection request. Once a client connects to the server, it has to send the correct password to proceed. The client will upload the file to the server, alongside information about the file's name and size. The connection is not encrypted.

# Download & how to use

## Download

Grab the program from the release page once it comes out

## Requirements
* Scala (recommended version 3.0 or above)

I am not distributing jars that include the whole runtime, that's why Scala is specifically necessary.

## How to use
Launch the jar program with ```scala server.jar```. You will be prompted to choose if you want to open a server or connect to one using the built-in client.

### Server
The server requires a port, and defaults to 42069.

If it doesn't exist, a config.txt file will be created where the program exists. This configuration file is necessary and contains the following options:
* ```password```: The password for your server connections. Default is test123, and you should change it to something secure of course.
* ```maxperfile```: The maximum allowed file size per file, in gigabytes.
* ```maxtotal```: The maximum allowed size for the sum of all the file sizes in the directory. This is unused for now.

### Client

The client requires an IP and port, and defaults to localhost and 42069.

When you open the client, a file browser opens, where you can select the file you want to upload. After choosing a file, you can connect to the server.

Type the password so the server accepts your connection. If the password is correct, the server's directory isn't full and your file doesn't exceed the server's size limit, your file will be uploaded.

# Building from source
You require Scala 3.0 or higher

Open a terminal in the root of the project and type ```scalac src/*.scala -d server.jar``` or, if you have Bash on your system, just run build.sh: ```bash build.sh```
