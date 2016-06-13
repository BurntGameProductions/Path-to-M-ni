

package org.burntgameproductions.PathToMani.game.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.common.ManiMath;
import org.burntgameproductions.PathToMani.game.*;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;
import org.burntgameproductions.PathToMani.ui.UiDrawer;
import org.burntgameproductions.PathToMani.Const;
import org.burntgameproductions.PathToMani.ManiApplication;
import org.burntgameproductions.PathToMani.common.ManiColor;
import org.burntgameproductions.PathToMani.game.planet.Planet;
import org.burntgameproductions.PathToMani.game.planet.PlanetManager;
import org.burntgameproductions.PathToMani.game.planet.ManiSystem;
import org.burntgameproductions.PathToMani.game.planet.SunSingleton;
import org.burntgameproductions.PathToMani.game.ship.FarShip;

import java.util.ArrayList;
import java.util.List;

public class BorderDrawer {

  private static final float MAX_DRAW_DIST = (Const.MAX_GROUND_HEIGHT + Const.ATM_HEIGHT) * 2;
  public static final float TISHCH_SZ = .02f;
  public static final float BORDER_ICON_SZ = .12f;
  public static final float MAX_ICON_DIST = Const.ATM_HEIGHT;
  private final ArrayList<Tishch> myTishches;
  private final Vector2 myTmpVec = new Vector2();

  public BorderDrawer(float r, ManiApplication cmp) {
    TextureAtlas.AtlasRegion tex = cmp.getTexMan().getTex("ui/tishch", null);
    int hCellCount = (int) (r / TISHCH_SZ);
    int vCellCount = (int) (1 / TISHCH_SZ);
    float hStep = r / hCellCount;
    float vStep = 1f / vCellCount;
    float x = hStep/2;
    float y = vStep/2;
    myTishches = new ArrayList<Tishch>();
    for (int i = 0; i < vCellCount; i++) {
      Tishch t = new Tishch(x, y, r, TISHCH_SZ, tex);
      myTishches.add(t);
      Tishch t2 = new Tishch(r - x, y, r, TISHCH_SZ, tex);
      myTishches.add(t2);
      y += vStep;
    }
    x = 1.5f * TISHCH_SZ;
    y = TISHCH_SZ /2;
    for (int i = 1; i < hCellCount - 1; i++) {
      Tishch t = new Tishch(x, y, r, TISHCH_SZ, tex);
      myTishches.add(t);
      Tishch t2 = new Tishch(x, 1 - y, r, TISHCH_SZ, tex);
      myTishches.add(t2);
      x += hStep;
    }
  }

  public void draw(UiDrawer drawer, ManiApplication cmp) {
    ManiGame g = cmp.getGame();
    ManiCam cam = g.getCam();
    Vector2 camPos = cam.getPos();
    ManiShip hero = g.getHero();
    drawTishches(drawer, g, cam, camPos);
    MapDrawer mapDrawer = g.getMapDrawer();
    FactionManager factionManager = g.getFactionMan();
    float heroDmgCap = hero == null ? Float.MAX_VALUE : HardnessCalc.getShipDmgCap(hero);

    List<ManiObject> objs = g.getObjMan().getObjs();
    for (int i = 0, objsSize = objs.size(); i < objsSize; i++) {
      ManiObject o = objs.get(i);
      if ((o instanceof ManiShip)) {
        ManiShip ship = (ManiShip) o;
        Vector2 shipPos = ship.getPosition();
        Faction shipFaction = ship.getPilot().getFaction();
        float shipSize = ship.getHull().config.getSize();
        float shipAngle = ship.getAngle();
        maybeDrawIcon(drawer, shipPos, cam, shipSize, shipAngle, mapDrawer, factionManager, hero, shipFaction, o, heroDmgCap, ship.getHull().config.getIcon());
      }
      if ((o instanceof StarPort)) {
        StarPort sp = (StarPort) o;
        maybeDrawIcon(drawer, sp.getPosition(), cam, StarPort.SIZE, sp.getAngle(), mapDrawer, null, null, null, null, -1, mapDrawer.getStarPortTex());
      }
    }

    List<FarShip> farShips = g.getObjMan().getFarShips();
    for (int i = 0, farObjsSize = farShips.size(); i < farObjsSize; i++) {
      FarShip ship = farShips.get(i);
      Vector2 shipPos = ship.getPos();
      Faction shipFaction = ship.getPilot().getFaction();
      float shipSize = ship.getHullConfig().getSize();
      float shipAngle = ship.getAngle();
      maybeDrawIcon(drawer, shipPos, cam, shipSize, shipAngle, mapDrawer, factionManager, hero, shipFaction, ship, heroDmgCap, ship.getHullConfig().getIcon());
    }
    List<StarPort.MyFar> farPorts = g.getObjMan().getFarPorts();
    for (int i = 0, sz = farPorts.size(); i < sz; i++) {
      StarPort.MyFar sp = farPorts.get(i);
      maybeDrawIcon(drawer, sp.getPos(), cam, StarPort.SIZE, sp.getAngle(), mapDrawer, null, null, null, null, -1, mapDrawer.getStarPortTex());
    }
  }

  private void maybeDrawIcon(UiDrawer drawer, Vector2 pos, ManiCam cam, float objSize,
                             float objAngle, MapDrawer mapDrawer, FactionManager factionManager, ManiShip hero,
                             Faction objFac, Object shipHack, float heroDmgCap, TextureAtlas.AtlasRegion icon)
  {
    Vector2 camPos = cam.getPos();
    float closeness = 1 - pos.dst(camPos) / MAX_ICON_DIST;
    if (closeness < 0) return;
    float camAngle = cam.getAngle();
    ManiMath.toRel(pos, myTmpVec, camAngle, camPos);
    float len = myTmpVec.len();
    float newLen = len - .25f * objSize;
    myTmpVec.scl(newLen / len);

    if (cam.isRelVisible(myTmpVec)) return;

    float sz = BORDER_ICON_SZ * closeness;
    float prefX = drawer.r / 2 - sz/2;
    float prefY = .5f - sz/2;
    float r = prefX / prefY;
    boolean prefXAxis = myTmpVec.y == 0 || r < ManiMath.abs(myTmpVec.x / myTmpVec.y);
    float mul = ManiMath.abs(prefXAxis ? (prefX / myTmpVec.x) : (prefY / myTmpVec.y));
    myTmpVec.scl(mul);
    myTmpVec.add(drawer.r/2, .5f);

    mapDrawer.drawObjIcon(sz, myTmpVec, objAngle - camAngle, factionManager, hero, objFac, heroDmgCap, shipHack, icon, drawer);
  }

  private void drawTishches(UiDrawer drawer, ManiGame g, ManiCam cam, Vector2 camPos) {
    PlanetManager pMan = g.getPlanetMan();
    Planet np = pMan.getNearestPlanet();
    if (np != null && np.getPos().dst(camPos) < np.getFullHeight()) return;
    for (int i = 0, myTishchesSize = myTishches.size(); i < myTishchesSize; i++) {
      Tishch t = myTishches.get(i);
      t.reset();
    }

    float camAngle = cam.getAngle();
    ArrayList<Planet> planets = pMan.getPlanets();
    for (int i = 0, planetsSize = planets.size(); i < planetsSize; i++) {
      Planet p = planets.get(i);
      Vector2 objPos = p.getPos();
      float objRad = p.getFullHeight();
      apply0(camPos, camAngle, objPos, objRad);
    }
    ManiSystem sys = pMan.getNearestSystem(camPos);
    apply0(camPos, camAngle, sys.getPos(), SunSingleton.SUN_HOT_RAD);
    for (int i = 0, myTishchesSize = myTishches.size(); i < myTishchesSize; i++) {
      Tishch t = myTishches.get(i);
      t.draw(drawer);
    }
  }

  private void apply0(Vector2 camPos, float camAngle, Vector2 objPos, float objRad) {
    float dst = objPos.dst(camPos);
    float distPerc = (dst - objRad) / MAX_DRAW_DIST;
    if (distPerc < 1) {
      float relAngle = ManiMath.angle(camPos, objPos) - camAngle;
      float angularWHalf = ManiMath.angularWidthOfSphere(objRad, dst);
      apply(distPerc, angularWHalf, relAngle);
    }
  }

  private void apply(float distPerc, float angularWHalf, float relAngle) {
    for (int i = 0, myTishchesSize = myTishches.size(); i < myTishchesSize; i++) {
      Tishch t = myTishches.get(i);
      if (ManiMath.angleDiff(t.myAngle, relAngle) < angularWHalf) {
        t.setDistPerc(distPerc);
      }
    }
  }

  private static class Tishch {
    private final float myX;
    private final float myY;
    private final TextureAtlas.AtlasRegion myTex;
    private final float myMaxSz;
    private final Color myCol;
    private float myPerc;
    private final float myAngle;

    public Tishch(float x, float y, float r, float maxSz, TextureAtlas.AtlasRegion tex) {
      myX = x;
      myY = y;
      myTex = tex;
      myMaxSz = maxSz * .9f;
      Vector2 pos = new Vector2(x, y);
      Vector2 centah = new Vector2(r / 2, .5f);
      myAngle = ManiMath.angle(centah, pos, true);
      myCol = new Color(ManiColor.UI_DARK);
    }

    public void draw(UiDrawer drawer) {
      float sz = myPerc * myMaxSz;
      myCol.a = myPerc;
      drawer.draw(myTex, sz, sz, sz/2, sz/2, myX, myY, 0, myCol);
    }

    public void setDistPerc(float distPerc) {
      float closeness = 1 - distPerc;
      if (closeness < myPerc) return;
      myPerc = closeness;
    }

    public void reset() {
      myPerc = 0;
    }
  }
}