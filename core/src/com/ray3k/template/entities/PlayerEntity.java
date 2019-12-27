package com.ray3k.template.entities;

import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Skeleton;
import com.ray3k.template.screens.GameScreen;

public class PlayerEntity extends Entity {
    @Override
    public void create() {
        skeleton = new Skeleton(GameScreen.characterSkeletonData);
        animationState = new AnimationState(GameScreen.characterAnimationStateData);
        
        animationState.setAnimation(0, "stand", true);
        skeleton.setScale(.5f, .5f);
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
