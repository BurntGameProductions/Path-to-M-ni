

package org.burntgameproductions.PathToMani.game.planet;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import org.burntgameproductions.PathToMani.common.ManiColor;
import org.burntgameproductions.PathToMani.common.ManiMath;
import org.burntgameproductions.PathToMani.game.*;
import org.burntgameproductions.PathToMani.game.dra.Dra;
import org.burntgameproductions.PathToMani.game.dra.DraLevel;
import org.burntgameproductions.PathToMani.Const;
import org.burntgameproductions.PathToMani.game.dra.RectSprite;

import java.util.ArrayList;
import java.util.List;

public class Sky implements ManiObject {

  private final Planet myPlanet;
  private final RectSprite myFill;
  private final RectSprite myGrad;
  private final ArrayList<Dra> myDras;
  private final ColorSpan mySkySpan;
  private final Vector2 myPos;

  public Sky(ManiGame game, Planet planet) {
    myPlanet = planet;
    myDras = new ArrayList<Dra>();

    myFill = new RectSprite(game.getTexMan().getTex("planetStarCommons/whiteTex", null), 5, 0, 0, new Vector2(), DraLevel.ATM, 0f, 0, ManiColor.col(.5f, 0), false);
    myDras.add(myFill);
    myGrad = new RectSprite(game.getTexMan().getTex("planetStarCommons/grad", null), 5, 0, 0, new Vector2(), DraLevel.ATM, 0f, 0, ManiColor.col(.5f, 0), false);
    myDras.add(myGrad);
    SkyConfig config = planet.getConfig().skyConfig;
    mySkySpan = ColorSpan.rgb(config.dawn, config.day);
    myPos = new Vector2();
    updatePos(game);
  }

  private void updatePos(ManiGame game) {
    Vector2 camPos = game.getCam().getPos();
    Vector2 planetPos = myPlanet.getPos();
    if (planetPos.dst(camPos) < myPlanet.getGroundHeight() + Const.MAX_SKY_HEIGHT_FROM_GROUND) {
      myPos.set(camPos);
      return;
    }
    myPos.set(planetPos);
  }

  @Override
  public void update(ManiGame game) {
    updatePos(game);

    Vector2 planetPos = myPlanet.getPos();
    ManiCam cam = game.getCam();
    Vector2 camPos = cam.getPos();
    float distPerc = 1 - (planetPos.dst(camPos) - myPlanet.getGroundHeight()) / Const.MAX_SKY_HEIGHT_FROM_GROUND;
    if (distPerc < 0) return;
    if (1 < distPerc) distPerc = 1;

    Vector2 sysPos = myPlanet.getSys().getPos();
    float angleToCam = ManiMath.angle(planetPos, camPos);
    float angleToSun = ManiMath.angle(planetPos, sysPos);
    float dayPerc = 1 - ManiMath.angleDiff(angleToCam, angleToSun) / 180;
    float skyIntensity = ManiMath.clamp(1 - ((1 - dayPerc) / .75f));
    float skyColorPerc = ManiMath.clamp((skyIntensity - .5f) * 2f + .5f);
    mySkySpan.set(skyColorPerc, myGrad.tint);
    mySkySpan.set(skyColorPerc, myFill.tint);
    float gradPerc = ManiMath.clamp(2 * skyIntensity);
    float fillPerc = ManiMath.clamp(2 * (skyIntensity - .5f));
    myGrad.tint.a = gradPerc * distPerc;
    myFill.tint.a = fillPerc * ManiMath.clamp(1 - (1 - distPerc) * 2) * .37f;

    float viewDist = cam.getViewDist();
    float sz = 2 * viewDist;
    myGrad.setTexSz(sz);
    myFill.setTexSz(sz);

    float angleCamToSun = angleToCam - angleToSun;
    float relAngle;
    if (ManiMath.abs(ManiMath.norm(angleCamToSun)) < 90) relAngle = angleToCam + 180 + angleCamToSun;
    else relAngle = angleToCam - angleCamToSun;
    myGrad.relAngle = relAngle - 90;
  }

  @Override
  public boolean shouldBeRemoved(ManiGame game) {
    return false;
  }

  @Override
  public void onRemove(ManiGame game) {
  }

  @Override
  public void receiveDmg(float dmg, ManiGame game, Vector2 pos, DmgType dmgType) {
  }

  @Override
  public boolean receivesGravity() {
    return false;
  }

  @Override
  public void receiveForce(Vector2 force, ManiGame game, boolean acc) {
  }

  @Override
  public Vector2 getPosition() {
    return myPos;
  }

  @Override
  public FarObj toFarObj() {
    return new FarSky(myPlanet);
  }

  @Override
  public List<Dra> getDras() {
    return myDras;
  }

  @Override
  public float getAngle() {
    return 0;
  }

  @Override
  public Vector2 getSpd() {
    return null;
  }

  @Override
  public void handleContact(ManiObject other, ContactImpulse impulse, boolean isA, float absImpulse,
                            ManiGame game, Vector2 collPos)
  {
  }

  @Override
  public String toDebugString() {
    return null;
  }

  @Override
  public Boolean isMetal() {
    return null;
  }

  @Override
  public boolean hasBody() {
    return false;
  }
}
