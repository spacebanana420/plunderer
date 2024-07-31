echo "Building Lightweight JAR..."
scalac src/*.scala src/*/*.scala -d build/yakumo-scala.jar
echo "Building fat JAR..."
scala-cli --power package src --assembly --preamble=false -f --jvm 11 -o build/yakumo.jar
echo "Building native binary (Linux, static)"
native-image --no-fallback --static -O3 -jar build/yakumo.jar -o build/yakumo

echo "Packaging Linux native binary"
7z a -mx8 -mmt0 build/yakumo-x86_64.zip build/yakumo
