echo "Building Yakumo..."
scalac src/*.scala src/*/*.scala -d yakumo.jar && scala yakumo.jar && rm yakumo.jar
