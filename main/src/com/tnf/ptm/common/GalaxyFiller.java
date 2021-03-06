/*
 * Copyright 2017 TheNightForum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tnf.ptm.common;

import com.badlogic.gdx.math.Vector2;
import com.tnf.ptm.entities.Faction;
import com.tnf.ptm.entities.StarPort;
import com.tnf.ptm.entities.item.TradeConfig;
import com.tnf.ptm.entities.planet.Planet;
import com.tnf.ptm.entities.planet.PtmSystem;
import com.tnf.ptm.handler.input.AiPilot;
import com.tnf.ptm.handler.input.ExplorerDestProvider;
import com.tnf.ptm.handler.input.Guardian;
import com.tnf.ptm.handler.input.MoveDestProvider;
import com.tnf.ptm.handler.input.NoDestProvider;
import com.tnf.ptm.handler.input.Pilot;
import com.tnf.ptm.entities.maze.Maze;
import com.tnf.ptm.entities.planet.ConsumedAngles;
import com.tnf.ptm.entities.planet.PlanetManager;
import com.tnf.ptm.entities.planet.SysConfig;
import com.tnf.ptm.entities.ship.FarShip;
import com.tnf.ptm.entities.ship.hulls.HullConfig;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;

public class GalaxyFiller {
    public static final float STATION_CONSUME_SECTOR = 45f;
    private Vector2 myMainStationPos;
    private HullConfig myMainStationHc;

    public GalaxyFiller() {
    }

    private Vector2 getPosForStation(PtmSystem sys, boolean mainStation, ConsumedAngles angles) {
        Planet p;
        ArrayList<Planet> planets = sys.getPlanets();
        float angleToSun;
        if (mainStation) {
            p = planets.get(planets.size() - 2);
            angleToSun = p.getAngleToSys() + 20 * PtmMath.toInt(p.getToSysRotSpd() > 0);
        } else {
            int pIdx = PtmMath.intRnd(planets.size() - 1);
            p = planets.get(pIdx);
            angleToSun = 0;
            for (int i = 0; i < 10; i++) {
                angleToSun = PtmMath.rnd(180);
                if (!angles.isConsumed(angleToSun, STATION_CONSUME_SECTOR)) {
                    break;
                }
            }
        }
        angles.add(angleToSun, STATION_CONSUME_SECTOR);
        float stationDist = p.getDist() + p.getFullHeight() + Const.PLANET_GAP;
        Vector2 stationPos = new Vector2();
        PtmMath.fromAl(stationPos, angleToSun, stationDist);
        stationPos.add(p.getSys().getPos());
        return stationPos;
    }

    private FarShip build(PtmGame game, ShipConfig cfg, Faction faction, boolean mainStation, PtmSystem sys,
                          ConsumedAngles angles) {
        HullConfig hullConf = cfg.hull;

        MoveDestProvider dp;
        Vector2 pos;
        float detectionDist = Const.AI_DET_DIST;
        TradeConfig tradeConfig = null;
        if (hullConf.getType() == HullConfig.Type.STATION) {
            pos = getPosForStation(sys, mainStation, angles);
            dp = new NoDestProvider();
            tradeConfig = sys.getConfig().tradeConfig;
        } else {
            pos = getEmptySpace(game, sys);
            boolean isBig = hullConf.getType() == HullConfig.Type.BIG;
            dp = new ExplorerDestProvider(game, pos, !isBig, hullConf, sys);
            if (isBig) {
                if (faction == Faction.LAANI) {
                    tradeConfig = sys.getConfig().tradeConfig;
                }
            } else {
                detectionDist *= 1.5;
            }
        }
        Pilot pilot = new AiPilot(dp, true, faction, true, "something", detectionDist);
        float angle = mainStation ? 0 : PtmMath.rnd(180);
        boolean hasRepairer;
        hasRepairer = faction == Faction.LAANI;
        int money = cfg.money;
        FarShip s = game.getShipBuilder().buildNewFar(game, pos, null, angle, 0, pilot, cfg.items, hullConf, null, hasRepairer, money, tradeConfig, true);
        game.getObjMan().addFarObjNow(s);
        ShipConfig guardConf = cfg.guard;
        if (guardConf != null) {
            ConsumedAngles ca = new ConsumedAngles();
            for (int i = 0; i < guardConf.density; i++) {
                float guardianAngle = 0;
                for (int j = 0; j < 5; j++) {
                    guardianAngle = PtmMath.rnd(180);
                    if (!ca.isConsumed(guardianAngle, guardConf.hull.getApproxRadius())) {
                        ca.add(guardianAngle, guardConf.hull.getApproxRadius());
                        break;
                    }
                }
                createGuard(game, s, guardConf, faction, guardianAngle);
            }
        }
        return s;
    }

    public void fill(PtmGame game) {
        if (DebugOptions.NO_OBJS) {
            return;
        }
        createStarPorts(game);
        ArrayList<PtmSystem> systems = game.getPlanetMan().getSystems();

        ShipConfig mainStationCfg = game.getPlayerSpawnConfig().mainStation;
        ConsumedAngles angles = new ConsumedAngles();
        FarShip mainStation = build(game, mainStationCfg, Faction.LAANI, true, systems.get(0), angles);
        myMainStationPos = new Vector2(mainStation.getPos());
        myMainStationHc = mainStation.getHullConfig();

        for (PtmSystem sys : systems) {
            SysConfig sysConfig = sys.getConfig();
            for (ShipConfig shipConfig : sysConfig.constAllies) {
                int count = (int) (shipConfig.density);
                for (int i = 0; i < count; i++) {
                    build(game, shipConfig, Faction.LAANI, false, sys, angles);
                }
            }
            for (ShipConfig shipConfig : sysConfig.constEnemies) {
                int count = (int) (shipConfig.density);
                for (int i = 0; i < count; i++) {
                    build(game, shipConfig, Faction.EHAR, false, sys, angles);
                }
            }
            angles = new ConsumedAngles();
        }
    }

    private void createStarPorts(PtmGame game) {
        PlanetManager planetManager = game.getPlanetMan();
        ArrayList<Planet> biggest = new ArrayList<Planet>();
        for (PtmSystem s : planetManager.getSystems()) {
            float minH = 0;
            Planet biggestP = null;
            int bi = -1;
            ArrayList<Planet> ps = s.getPlanets();
            for (int i = 0; i < ps.size(); i++) {
                Planet p = ps.get(i);
                float gh = p.getGroundHeight();
                if (minH < gh) {
                    minH = gh;
                    biggestP = p;
                    bi = i;
                }
            }
            for (int i = 0; i < ps.size(); i++) {
                if (bi == i || bi == i - 1 || bi == i + 1) {
                    continue;
                }
                Planet p = ps.get(i);
                link(game, p, biggestP);
            }

            for (Planet p : biggest) {
                link(game, p, biggestP);
            }
            biggest.add(biggestP);
        }

    }

    private void link(PtmGame game, Planet a, Planet b) {
        if (a == b) {
            throw new AssertionError("Linking planet to itself");
        }
        Vector2 aPos = StarPort.getDesiredPos(a, b, false);
        StarPort.MyFar sp = new StarPort.MyFar(a, b, aPos, false);
        PtmMath.free(aPos);
        game.getObjMan().addFarObjNow(sp);
        Vector2 bPos = StarPort.getDesiredPos(b, a, false);
        sp = new StarPort.MyFar(b, a, bPos, false);
        PtmMath.free(bPos);
        game.getObjMan().addFarObjNow(sp);
    }

    private void createGuard(PtmGame game, FarShip target, ShipConfig guardConf, Faction faction, float guardRelAngle) {
        Guardian dp = new Guardian(game, guardConf.hull, target.getPilot(), target.getPos(), target.getHullConfig(), guardRelAngle);
        Pilot pilot = new AiPilot(dp, true, faction, false, null, Const.AI_DET_DIST);
        boolean hasRepairer = faction == Faction.LAANI;
        int money = guardConf.money;
        FarShip e = game.getShipBuilder().buildNewFar(game, dp.getDest(), null, guardRelAngle, 0, pilot, guardConf.items,
                guardConf.hull, null, hasRepairer, money, null, true);
        game.getObjMan().addFarObjNow(e);
    }

    private Vector2 getEmptySpace(PtmGame game, PtmSystem s) {
        Vector2 res = new Vector2();
        Vector2 sPos = s.getPos();
        float sRadius = s.getConfig().hard ? s.getRadius() : s.getInnerRad();

        for (int i = 0; i < 100; i++) {
            PtmMath.fromAl(res, PtmMath.rnd(180), PtmMath.rnd(sRadius));
            res.add(sPos);
            if (game.isPlaceEmpty(res, true)) {
                return res;
            }
        }
        throw new AssertionError("could not generate ship position");
    }

    public Vector2 getPlayerSpawnPos(PtmGame game) {
        Vector2 pos = new Vector2(Const.SUN_RADIUS * 2, 0);

        if ("planet".equals(DebugOptions.SPAWN_PLACE)) {
            Planet p = game.getPlanetMan().getPlanets().get(0);
            pos.set(p.getPos());
            pos.x += p.getFullHeight();
        } else if (DebugOptions.SPAWN_PLACE.isEmpty() && myMainStationPos != null) {
            PtmMath.fromAl(pos, 90, myMainStationHc.getSize() / 2);
            pos.add(myMainStationPos);
        } else if ("maze".equals(DebugOptions.SPAWN_PLACE)) {
            Maze m = game.getPlanetMan().getMazes().get(0);
            pos.set(m.getPos());
            pos.x += m.getRadius();
        } else if ("trader".equals(DebugOptions.SPAWN_PLACE)) {
            HullConfig cfg = game.getHullConfigs().getConfig(new ResourceUrn("core:bus"));
            for (FarObjData fod : game.getObjMan().getFarObjs()) {
                FarObj fo = fod.fo;
                if (!(fo instanceof FarShip)) {
                    continue;
                }
                if (((FarShip) fo).getHullConfig() != cfg) {
                    continue;
                }
                pos.set(fo.getPos());
                pos.add(cfg.getApproxRadius() * 2, 0);
                break;
            }

        }
        return pos;
    }

    public Vector2 getMainStationPos() {
        return myMainStationPos;
    }

}
