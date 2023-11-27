package com.github.cfogrady.dim.modifier.data.firmware;

import com.github.cfogrady.vb.dim.sprite.SpriteData;

import java.util.ArrayList;
import java.util.List;

public interface FirmwareData {

    List<SpriteData.Sprite> getSmallAttacks();

    List<SpriteData.Sprite> getBigAttacks();

    List<SpriteData.Sprite> getTypes();

}
