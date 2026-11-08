# BombaMod (Fabric, Minecraft 1.21.4)

Mod kliencki. Klawisz **G** otwiera listę graczy z serwera: głowa, nick, ping i ekwipunek
(hełm, napierśnik, spodnie, buty, przedmiot w łapce). Ekwipunek widać tylko u graczy,
których Twoja gra aktualnie renderuje. Kliknięcie gracza otwiera okno z przyciskiem akcji.

Przycisk akcji wysyła komendę ze stałej `COMMAND` w `BombaMod.java`
(domyślnie `/msg <nick> Czesc!`). Zmień ją na komendę własnego serwera.

## Jak dostać plik .jar przez GitHub

1. Utwórz nowe repozytorium na GitHubie i wrzuć do niego wszystkie pliki z tego folderu
   (razem z ukrytym folderem `.github`).
2. Wejdź w zakładkę **Actions**. Build uruchomi się sam po wrzuceniu plików
   (albo kliknij **Build** i **Run workflow**).
3. Po zakończeniu otwórz ukończone uruchomienie i pobierz z sekcji **Artifacts** plik `bombamod-1.21.4`.
   W środku jest `bombamod-1.0.0.jar`.
4. Wrzuć .jar do `.minecraft/mods`, razem z Fabric Loader 1.21.4 i Fabric API.

## Budowanie lokalnie

Java 21 i Gradle 8.12 lub nowszy: `gradle build`. Wynik: `build/libs/bombamod-1.0.0.jar`.
