package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class DecalEntity extends Entity {
    public Sprite sprite;
    public float scaleX = 1f;
    public float scaleY = 1f;
    
    @Override
    public void create() {
    
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
    
    }
    
    @Override
    public void draw(float delta) {
        sprite.setCenter(x, y);
        sprite.setScale(scaleX, scaleY);
        sprite.draw(batch);
    }
    
    @Override
    public void destroy() {
    
    }
}
