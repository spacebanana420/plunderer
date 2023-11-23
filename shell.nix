{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  packages = with pkgs; [
    scala_3
    git
  ];

  shellHook = ''
clear && echo "Yakumo dev environment created
The following shortcuts were created:
  * yakumo - launches yakumo.jar in the current directory
  * getyakumo - downloads the latest Yakumo from the repository's main branch
  * buildyakumo - builds yakumo if you are in the root of the project
Type the shortcuts with the character '$' at the start (example: $getyakumo)
"
'';
  yakumo = "scala yakumo.jar";
  getyakumo = "git clone https://github.com/spacebanana420/yakumo";
  buildyakumo = "scalac src/*.scala src/*/*.scala -d yakumo.jar";
}
