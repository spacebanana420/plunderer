{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  packages = with pkgs; [
    scala_3
    git
  ];

  shellHook = ''
clear && echo "Yakumo dev environment created
The following shortcuts were created:
  * $yakumo - launches yakumo.jar in the current directory
  * $getyakumo - downloads the latest Yakumo from the repository's main branch
"
'';
  yakumo = "scala yakumo.jar";
  getyakumo = "git clone https://github.com/spacebanana420/yakumo";
}
