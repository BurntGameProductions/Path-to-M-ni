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

package old.tnf.ptm.game.chunk;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonValue;
import old.tnf.ptm.TextureManager;

import java.util.ArrayList;

public class SpaceEnvConfig {
    public final ArrayList<TextureAtlas.AtlasRegion> junkTexs;
    public final float junkDensity;
    public final ArrayList<TextureAtlas.AtlasRegion> farJunkTexs;
    public final float farJunkDensity;

    public SpaceEnvConfig(JsonValue json, TextureManager textureManager) {
        String junkTexDirStr = json.getString("junkTexs");
        junkTexs = textureManager.getPack(junkTexDirStr);
        junkDensity = json.getFloat("junkDensity");
        String farJunkTexDirStr = json.getString("farJunkTexs");
        farJunkTexs = textureManager.getPack(farJunkTexDirStr);
        farJunkDensity = json.getFloat("farJunkDensity");
    }
}
