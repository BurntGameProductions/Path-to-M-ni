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
package old.tnf.ptm.assets.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import old.tnf.ptm.assets.AssetHelper;
import org.terasology.assets.ResourceUrn;
import org.terasology.assets.format.AbstractAssetFileFormat;
import org.terasology.assets.format.AssetDataFile;
import org.terasology.assets.module.annotations.RegisterAssetFileFormat;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@RegisterAssetFileFormat
public class OggSoundFileFormat extends AbstractAssetFileFormat<OggSoundData> {
    public OggSoundFileFormat() {
        super("ogg");
    }

    @Override
    public OggSoundData load(ResourceUrn urn, List<AssetDataFile> inputs) throws IOException {
        String path = AssetHelper.resolveToPath(inputs.get(0));

        FileHandle handle = new FileHandle(Paths.get(path).toFile());
        return new OggSoundData(Gdx.audio.newSound(handle));
    }
}
