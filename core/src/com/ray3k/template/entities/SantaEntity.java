package com.ray3k.template.entities;

import com.badlogic.gdx.audio.Sound;

public class SantaEntity extends EnemyEntity {
    @Override
    public void create() {
        super.create();
        skeleton.setSkin("santa");
        gameScreen.assetManager.get("sfx/santa-intro.mp3", Sound.class).play(core.sfx);
        health = 1000;
    }
    
    @Override
    public void destroy() {
        super.destroy();
        gameScreen.win();
    }
}
