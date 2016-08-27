

package org.burntgameproductions.PathToMani.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.ManiApplication;
import org.burntgameproductions.PathToMani.game.BeaconHandler;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;
import org.burntgameproductions.PathToMani.ui.ManiInputManager;

public class ShipMouseControl implements ShipUiControl {
  private final TextureAtlas.AtlasRegion myMoveCursor;
  private final TextureAtlas.AtlasRegion myAttackCursor;
  private final TextureAtlas.AtlasRegion myFollowCursor;
  private final Vector2 myMouseWorldPos;

  private TextureAtlas.AtlasRegion myCursor;

  public ShipMouseControl(ManiApplication cmp) {
    myMoveCursor = cmp.getTexMan().getTex("ui/cursorMove", null);
    myAttackCursor = cmp.getTexMan().getTex("ui/cursorAttack", null);
    myFollowCursor = cmp.getTexMan().getTex("ui/cursorFollow", null);
    myMouseWorldPos = new Vector2();
  }

  @Override
  public void update(ManiApplication cmp, boolean enabled) {
    ManiGame g = cmp.getGame();
    ManiShip h = g.getHero();
    myCursor = null;
    if (h != null) {
      myMouseWorldPos.set(Gdx.input.getX(), Gdx.input.getY());
      g.getCam().screenToWorld(myMouseWorldPos);
      ManiInputManager im = cmp.getInputMan();
      boolean clicked = im.getPtrs()[0].pressed;
      boolean onMap = im.isScreenOn(g.getScreens().mapScreen);
      BeaconHandler.Action a = g.getBeaconHandler().processMouse(g, myMouseWorldPos, clicked, onMap);
      if (a == BeaconHandler.Action.ATTACK) {
        myCursor = myAttackCursor;
      } else if (a == BeaconHandler.Action.FOLLOW) {
        myCursor = myFollowCursor;
      } else {
        myCursor = myMoveCursor;
      }
    }
  }

  @Override
  public boolean isLeft() {
    return false;
  }

  @Override
  public boolean isRight() {
    return false;
  }

  @Override
  public boolean isUp() {
    return false;
  }

  @Override
  public boolean isDown() {
    return false;
  }

  @Override
  public boolean isShoot() {
    return false;
  }

  @Override
  public boolean isShoot2() {
    return false;
  }

  @Override
  public boolean isAbility() {
    return false;
  }

  @Override
  public TextureAtlas.AtlasRegion getInGameTex() {
    return myCursor;
  }

  @Override
  public void blur() {

  }
}
