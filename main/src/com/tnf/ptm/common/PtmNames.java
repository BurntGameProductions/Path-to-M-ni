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

import com.badlogic.gdx.utils.JsonValue;
import com.tnf.ptm.assets.Assets;
import com.tnf.ptm.assets.json.Json;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;

public class PtmNames {
    public final ArrayList<String> planets;
    public final ArrayList<String> systems;

    public PtmNames() {
        planets = readList(new ResourceUrn("core:planetNamesConfig"));
        systems = readList(new ResourceUrn("core:systemNamesConfig"));
    }

    private ArrayList<String> readList(ResourceUrn fileName) {
        Json json = Assets.getJson(fileName);
        JsonValue rootNode = json.getJsonValue();

        ArrayList<String> list = new ArrayList<>();
        for (JsonValue node : rootNode) {
            list.add(node.name());
        }

        json.dispose();

        return list;
    }
}
