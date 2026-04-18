To run, run the jar file with this command. 
Run with this command both below and even more below



```bash
java --add-exports java.desktop/sun.awt=ALL-UNNAMED \
  --add-opens java.desktop/sun.awt=ALL-UNNAMED \
  --add-opens java.desktop/java.awt.peer=ALL-UNNAMED \
  --add-opens java.desktop/sun.lwawt=ALL-UNNAMED \
  --add-opens java.desktop/sun.lwawt.macosx=ALL-UNNAMED \
  --add-opens java.desktop/sun.java2d=ALL-UNNAMED \
  --enable-native-access=ALL-UNNAMED \
  -jar JABR-1.0.jar

