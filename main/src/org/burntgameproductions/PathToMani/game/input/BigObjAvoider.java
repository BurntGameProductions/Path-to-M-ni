

package org.burntgameproductions.PathToMani.game.input;

import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.common.ManiMath;
import org.burntgameproductions.PathToMani.Const;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.planet.Planet;

public class BigObjAvoider {

  public static final float MAX_DIST_LEN = 2 * (Const.MAX_GROUND_HEIGHT + Const.ATM_HEIGHT);
  private Vector2 myProj;

  public BigObjAvoider() {
    myProj = new Vector2();
  }

  public float avoid(ManiGame game, Vector2 from, Vector2 dest, float toDestAngle) {
    float toDestLen = from.dst(dest);
    if (toDestLen > MAX_DIST_LEN) toDestLen = MAX_DIST_LEN;
    float res = toDestAngle;
    Planet p = game.getPlanetMan().getNearestPlanet(from);
    Vector2 pPos = p.getPos();
    float pRad = p.getFullHeight();
    if (dest.dst(pPos) < pRad) pRad = p.getGroundHeight();
    myProj.set(pPos);
    myProj.sub(from);
    ManiMath.rotate(myProj, -toDestAngle);
    if (0 < myProj.x && myProj.x < toDestLen) {
      if (ManiMath.abs(myProj.y) < pRad) {
        toDestLen = myProj.x;
        res = toDestAngle + 45 * ManiMath.toInt(myProj.y < 0);
      }
    }
    Vector2 sunPos = p.getSys().getPos();
    float sunRad = Const.SUN_RADIUS;
    myProj.set(sunPos);
    myProj.sub(from);
    ManiMath.rotate(myProj, -toDestAngle);
    if (0 < myProj.x && myProj.x < toDestLen) {
      if (ManiMath.abs(myProj.y) < sunRad) {
        res = toDestAngle + 45 * ManiMath.toInt(myProj.y < 0);
      }
    }
    return res;
  }
}