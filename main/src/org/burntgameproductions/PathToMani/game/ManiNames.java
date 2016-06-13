

package org.burntgameproductions.PathToMani.game;

import com.badlogic.gdx.files.FileHandle;
import org.burntgameproductions.PathToMani.files.FileManager;

import java.util.ArrayList;

public class ManiNames {
  public final ArrayList<String> planets;
  public final ArrayList<String> systems;

  public ManiNames() {
    planets = readList("planet");
    systems = readList("system");
  }

  private ArrayList<String> readList(String entityType) {
    ArrayList<String> list = new ArrayList<String>();
    FileHandle f = FileManager.getInstance().getConfigDirectory().child(entityType + "Names.txt");
    String lines = f.readString();
    for (String line : lines.split("\n")) {
      if (line.isEmpty()) continue;
      list.add(line);
    }
    return list;
  }
}