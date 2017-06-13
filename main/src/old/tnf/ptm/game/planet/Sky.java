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
package old.tnf.ptm.game.planet;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import old.tnf.ptm.Const;
import com.tnf.ptm.common.PtmColor;
import com.tnf.ptm.common.PtmMath;
import old.tnf.ptm.game.*;
import old.tnf.ptm.game.PtmObject;
import old.tnf.ptm.game.dra.Dra;
import old.tnf.ptm.game.dra.DraLevel;
import old.tnf.ptm.game.dra.RectSprite;

import java.util.ArrayList;
import java.util.List;

public class Sky implements PtmObject {

    private final Planet myPlanet;
    private final RectSprite myFill;
    private final RectSprite myGrad;
    private final ArrayList<Dra> myDras;
    private final ColorSpan mySkySpan;
    private final Vector2 myPos;

    public Sky(PtmGame game, Planet planet) {
        myPlanet = planet;
        myDras = new ArrayList<Dra>();

        myFill = new RectSprite(game.getTexMan().getTexture("planetStarCommons/whiteTex"), 5, 0, 0, new Vector2(), DraLevel.ATM, 0f, 0, PtmColor.col(.5f, 0), false);
        myDras.add(myFill);
        myGrad = new RectSprite(game.getTexMan().getTexture("planetStarCommons/grad"), 5, 0, 0, new Vector2(), DraLevel.ATM, 0f, 0, PtmColor.col(.5f, 0), false);
        myDras.add(myGrad);
        SkyConfig config = planet.getConfig().skyConfig;
        mySkySpan = ColorSpan.rgb(config.dawn, config.day);
        myPos = new Vector2();
        updatePos(game);
    }

    private void updatePos(PtmGame game) {
        Vector2 camPos = game.getCam().getPos();
        Vector2 planetPos = myPlanet.getPos();
        if (planetPos.dst(camPos) < myPlanet.getGroundHeight() + Const.MAX_SKY_HEIGHT_FROM_GROUND) {
            myPos.set(camPos);
            return;
        }
        myPos.set(planetPos);
    }

    @Override
    public void update(PtmGame game) {
        updatePos(game);

        Vector2 planetPos = myPlanet.getPos();
        PtmCam cam = game.getCam();
        Vector2 camPos = cam.getPos();
        float distPerc = 1 - (planetPos.dst(camPos) - myPlanet.getGroundHeight()) / Const.MAX_SKY_HEIGHT_FROM_GROUND;
        if (distPerc < 0) {
            return;
        }
        if (1 < distPerc) {
            distPerc = 1;
        }

        Vector2 sysPos = myPlanet.getSys().getPos();
        float angleToCam = PtmMath.angle(planetPos, camPos);
        float angleToSun = PtmMath.angle(planetPos, sysPos);
        float dayPerc = 1 - PtmMath.angleDiff(angleToCam, angleToSun) / 180;
        float skyIntensity = PtmMath.clamp(1 - ((1 - dayPerc) / .75f));
        float skyColorPerc = PtmMath.clamp((skyIntensity - .5f) * 2f + .5f);
        mySkySpan.set(skyColorPerc, myGrad.tint);
        mySkySpan.set(skyColorPerc, myFill.tint);
        float gradPerc = PtmMath.clamp(2 * skyIntensity);
        float fillPerc = PtmMath.clamp(2 * (skyIntensity - .5f));
        myGrad.tint.a = gradPerc * distPerc;
        myFill.tint.a = fillPerc * PtmMath.clamp(1 - (1 - distPerc) * 2) * .37f;

        float viewDist = cam.getViewDist();
        float sz = 2 * viewDist;
        myGrad.setTexSz(sz);
        myFill.setTexSz(sz);

        float angleCamToSun = angleToCam - angleToSun;
        float relAngle;
        if (PtmMath.abs(PtmMath.norm(angleCamToSun)) < 90) {
            relAngle = angleToCam + 180 + angleCamToSun;
        } else {
            relAngle = angleToCam - angleCamToSun;
        }
        myGrad.relAngle = relAngle - 90;
    }

    @Override
    public boolean shouldBeRemoved(PtmGame game) {
        return false;
    }

    @Override
    public void onRemove(PtmGame game) {
    }

    @Override
    public void receiveDmg(float dmg, PtmGame game, Vector2 pos, DmgType dmgType) {
    }

    @Override
    public boolean receivesGravity() {
        return false;
    }

    @Override
    public void receiveForce(Vector2 force, PtmGame game, boolean acc) {
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
    public void handleContact(PtmObject other, ContactImpulse impulse, boolean isA, float absImpulse,
                              PtmGame game, Vector2 collPos) {
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
