echo "Building Yakumo..."
scalac src/*.scala -d yakumo.jar && scala yakumo.jar && rm yakumo.jar
