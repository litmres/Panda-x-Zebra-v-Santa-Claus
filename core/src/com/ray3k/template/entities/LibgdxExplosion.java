package com.ray3k.template.entities;

import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;
import com.ray3k.template.Core;

public class LibgdxExplosion extends Entity {
    private static SkeletonData skeletonData;
    private static AnimationStateData animationStateData;
    
    @Override
    public void create() {
        if (skeletonData == null) {
            skeletonData = Core.core.assetManager.get(("spine/libgdx-explosion.json"));
            animationStateData = new AnimationStateData(skeletonData);
        }
        setSkeletonData(skeletonData, animationStateData);
        animationState.setAnimation(0, "animation", true);
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
    
    }
    
    @Override
    public void draw(float delta) {
    
    }
    
    @Override
    public void destroy() {
    
    }
}
