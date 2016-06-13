

package org.burntgameproductions.PathToMani.game.ship;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import org.burntgameproductions.PathToMani.common.ManiMath;
import org.burntgameproductions.PathToMani.game.Faction;
import org.burntgameproductions.PathToMani.game.FactionManager;
import org.burntgameproductions.PathToMani.game.ManiObject;
import org.burntgameproductions.PathToMani.game.input.Pilot;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.dra.Dra;
import org.burntgameproductions.PathToMani.game.dra.RectSprite;

import java.util.ArrayList;
import java.util.List;

public class Door {
  public static final float SPD_LEN = .4f;
  public static final float SENSOR_DIST = 3f;
  public static final float DOOR_LEN = 1.1f;
  public static final float MAX_OPEN_AWAIT = DOOR_LEN / SPD_LEN;
  private final PrismaticJoint myJoint;
  private final RectSprite myS;
  private float myOpenAwait;

  public Door(PrismaticJoint joint, RectSprite s) {
    myJoint = joint;
    myS = s;
  }

  public void update(ManiGame game, ManiShip ship) {
    Vector2 doorPos = getBody().getPosition();
    boolean open = myOpenAwait <= 0 && shouldOpen(game, ship, doorPos);
    if (open) {
      myOpenAwait = MAX_OPEN_AWAIT;
      myJoint.setMotorSpeed(SPD_LEN);
      game.getSoundMan().play(game, game.getSpecialSounds().doorMove, doorPos, ship);
    } else if (myOpenAwait > 0) {
      myOpenAwait -= game.getTimeStep();
      if (myOpenAwait < 0) {
        myJoint.setMotorSpeed(-SPD_LEN);
        game.getSoundMan().play(game, game.getSpecialSounds().doorMove, doorPos, ship);
      }
    }

    Vector2 shipPos = ship.getPosition();
    float shipAngle = ship.getAngle();
    ManiMath.toRel(doorPos, myS.getRelPos(), shipAngle, shipPos);
  }

  private boolean shouldOpen(ManiGame game, ManiShip ship, Vector2 doorPos) {
    Faction faction = ship.getPilot().getFaction();
    FactionManager factionManager = game.getFactionMan();
    List<ManiObject> objs = game.getObjMan().getObjs();
    for (int i = 0, objsSize = objs.size(); i < objsSize; i++) {
      ManiObject o = objs.get(i);
      if (o == ship) continue;
      if (!(o instanceof ManiShip)) continue;
      ManiShip ship2 = (ManiShip) o;
      Pilot pilot2 = ship2.getPilot();
      if (!pilot2.isUp()) continue;
      if (factionManager.areEnemies(pilot2.getFaction(), faction)) continue;
      if (ship2.getPosition().dst(doorPos) < SENSOR_DIST) return true;
    }
    return false;
  }

  public void collectDras(ArrayList<Dra> dras) {
    dras.add(myS);
  }

  public Body getBody() {
    return myJoint.getBodyB();
  }

  public void onRemove(ManiGame game) {
    World w = game.getObjMan().getWorld();
    Body doorBody = getBody();
    w.destroyJoint(myJoint);
    w.destroyBody(doorBody);
  }
}