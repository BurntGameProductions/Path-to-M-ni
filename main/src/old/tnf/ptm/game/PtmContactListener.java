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
package old.tnf.ptm.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.tnf.ptm.common.PtmMath;
import old.tnf.ptm.game.projectile.Projectile;

public class PtmContactListener implements ContactListener {
    private final PtmGame myGame;

    public PtmContactListener(PtmGame game) {
        myGame = game;
    }

    @Override
    public void beginContact(Contact contact) {
        PtmObject oA = (PtmObject) contact.getFixtureA().getBody().getUserData();
        PtmObject oB = (PtmObject) contact.getFixtureB().getBody().getUserData();

        boolean aIsProj = oA instanceof Projectile;
        if (!aIsProj && !(oB instanceof Projectile)) {
            return;
        }

        Projectile proj = (Projectile) (aIsProj ? oA : oB);
        PtmObject o = aIsProj ? oB : oA;
        proj.setObstacle(o, myGame);
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        PtmObject soa = (PtmObject) contact.getFixtureA().getBody().getUserData();
        PtmObject sob = (PtmObject) contact.getFixtureB().getBody().getUserData();
        if (soa instanceof Projectile && ((Projectile) soa).getConfig().density <= 0) {
            return;
        }
        if (sob instanceof Projectile && ((Projectile) sob).getConfig().density <= 0) {
            return;
        }

        float absImpulse = calcAbsImpulse(impulse);
        Vector2 collPos = contact.getWorldManifold().getPoints()[0];
        soa.handleContact(sob, impulse, true, absImpulse, myGame, collPos);
        sob.handleContact(soa, impulse, false, absImpulse, myGame, collPos);
        myGame.getSpecialSounds().playColl(myGame, absImpulse, soa, collPos);
        myGame.getSpecialSounds().playColl(myGame, absImpulse, sob, collPos);
    }

    private float calcAbsImpulse(ContactImpulse impulse) {
        float absImpulse = 0;
        int pointCount = impulse.getCount();
        float[] normImpulses = impulse.getNormalImpulses();
        for (int i = 0; i < pointCount; i++) {
            float normImpulse = normImpulses[i];
            normImpulse = PtmMath.abs(normImpulse);
            if (absImpulse < normImpulse) {
                absImpulse = normImpulse;
            }
        }
        return absImpulse;
    }
}
