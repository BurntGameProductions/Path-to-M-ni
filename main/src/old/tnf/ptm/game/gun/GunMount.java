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

package old.tnf.ptm.game.gun;

import com.badlogic.gdx.math.Vector2;
import old.tnf.ptm.Const;
import old.tnf.ptm.common.PtmMath;
import old.tnf.ptm.game.input.Shooter;
import old.tnf.ptm.game.ship.PtmShip;
import old.tnf.ptm.game.ship.hulls.HullConfig;
import old.tnf.ptm.game.Faction;
import old.tnf.ptm.game.PtmGame;
import old.tnf.ptm.game.PtmObject;
import old.tnf.ptm.game.dra.Dra;
import old.tnf.ptm.game.item.Gun;
import old.tnf.ptm.game.item.ItemContainer;
import old.tnf.ptm.game.ship.hulls.GunSlot;

import java.util.List;

public class GunMount {
    private final Vector2 myRelPos;
    private final boolean myFixed;
    private PtmGun myGun;
    private boolean myDetected;
    private float myRelGunAngle;

    public GunMount(GunSlot gunSlot) {
        myRelPos = gunSlot.getPosition();
        myFixed = !gunSlot.allowsRotation();
    }

    public void update(ItemContainer ic, PtmGame game, float shipAngle, PtmShip creator, boolean shouldShoot, PtmShip nearestEnemy, Faction faction) {
        if (myGun == null) {
            return;
        }
        if (!ic.contains(myGun.getItem())) {
            setGun(game, creator, null, false, 0);
            return;
        }

        if (creator.getHull().config.getType() != HullConfig.Type.STATION) {
            myRelGunAngle = 0;
        }
        myDetected = false;
        if (!myFixed && nearestEnemy != null) {
            Vector2 creatorPos = creator.getPosition();
            Vector2 nePos = nearestEnemy.getPosition();
            float dst = creatorPos.dst(nePos) - creator.getHull().config.getApproxRadius() - nearestEnemy.getHull().config.getApproxRadius();
            float detDst = game.getPlanetMan().getNearestPlanet().isNearGround(creatorPos) ? Const.AUTO_SHOOT_GROUND : Const.AUTO_SHOOT_SPACE;
            if (dst < detDst) {
                Vector2 mountPos = PtmMath.toWorld(myRelPos, shipAngle, creatorPos);
                boolean player = creator.getPilot().isPlayer();
                float shootAngle = Shooter.calcShootAngle(mountPos, creator.getSpd(), nePos, nearestEnemy.getSpd(), myGun.getConfig().clipConf.projConfig.spdLen, player);
                if (shootAngle == shootAngle) {
                    myRelGunAngle = shootAngle - shipAngle;
                    myDetected = true;
                    if (player) {
                        game.getMountDetectDrawer().setNe(nearestEnemy);
                    }
                }
                PtmMath.free(mountPos);
            }
        }

        float gunAngle = shipAngle + myRelGunAngle;
        myGun.update(ic, game, gunAngle, creator, shouldShoot, faction);
    }

    public Gun getGun() {
        return myGun == null ? null : myGun.getItem();
    }

    public void setGun(PtmGame game, PtmObject o, Gun gun, boolean underShip, int slotNr) {
        List<Dra> dras = o.getDras();
        if (myGun != null) {
            List<Dra> dras1 = myGun.getDras();
            dras.removeAll(dras1);
            game.getDraMan().removeAll(dras1);
            myGun.getItem().setEquipped(0);
            myGun = null;
        }
        if (gun != null) {
            if (gun.config.fixed != myFixed) {
                throw new AssertionError("tried to set gun to incompatible mount");
            }
            myGun = new PtmGun(game, gun, myRelPos, underShip);
            myGun.getItem().setEquipped(slotNr);
            List<Dra> dras1 = myGun.getDras();
            dras.addAll(dras1);
            game.getDraMan().addAll(dras1);
        }
    }

    public boolean isFixed() {
        return myFixed;
    }

    public Vector2 getRelPos() {
        return myRelPos;
    }

    public boolean isDetected() {
        return myDetected;
    }
}
