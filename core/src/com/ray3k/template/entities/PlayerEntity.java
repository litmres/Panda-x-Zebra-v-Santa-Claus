package com.ray3k.template.entities;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationState.AnimationStateAdapter;
import com.esotericsoftware.spine.AnimationState.TrackEntry;
import com.esotericsoftware.spine.Skeleton;
import com.ray3k.template.Core.Binding;
import com.ray3k.template.screens.GameScreen;

public class PlayerEntity extends Entity {
    public enum Mode {
        STAND, WALK, HURT, ATTACK, DEAD
    }
    public Mode mode;
    public int attackQueue;
    public static final String[][] combos = {{"combo1-1", "combo1-2", "combo1-3", "combo1-4"}, {"combo2-1", "combo2-2", "combo2-3", "combo2-4"}, {"combo3-1", "combo3-2", "combo3-3", "combo3-4"}};
    public int attackIndex;
    public int comboIndex;
    public static final float WALK_H_SPEED = 500f;
    public static final float WALK_V_SPEED = 250f;
    private GameScreen gameScreen;
    
    @Override
    public void create() {
        gameScreen = GameScreen.gameScreen;
        
        skeleton = new Skeleton(GameScreen.characterSkeletonData);
        animationState = new AnimationState(GameScreen.characterAnimationStateData);
        
        animationState.setAnimation(0, "stand", true);
        skeleton.setSkin(core.characterSkin.skin);
        skeleton.setScale(.5f, .5f);
        
        animationState.addListener(new AnimationStateAdapter() {
            @Override
            public void complete(TrackEntry entry) {
                animationComplete(entry);
            }
        });
        
        mode = Mode.STAND;
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        if (gameScreen.isBindingJustPressed(Binding.ATTACK)) {
            attackQueue++;
        }
        
        movementControls(delta);
    
        attackControls(delta);
        
        movement(delta);
    }
    
    public void movementControls(float delta) {
        switch (mode) {
            case STAND:
                if (gameScreen.isAnyBindingPressed(Binding.LEFT, Binding.RIGHT, Binding.UP, Binding.DOWN)) {
                    mode = Mode.WALK;
                    animationState.setAnimation(0, "walk", true);
                }
                break;
            case WALK:
                if (!gameScreen.isAnyBindingPressed(Binding.LEFT, Binding.RIGHT, Binding.UP, Binding.DOWN)) {
                    mode = Mode.STAND;
                    animationState.setAnimation(0, "stand", true);
                    setSpeed(0);
                }
                break;
        }
    }
    
    public void attackControls(float delta) {
        switch (mode) {
            case STAND:
            case WALK:
                if (gameScreen.isBindingJustPressed(Binding.ATTACK)) {
                    mode = Mode.ATTACK;
                    comboIndex = MathUtils.random(2);
                    attackIndex = 0;
                    animationState.setAnimation(0, combos[comboIndex][attackIndex], false);
                    attackQueue = 0;
                    setSpeed(0);
                }
                break;
        }
    }
    
    public void movement(float delta) {
        switch (mode) {
            case STAND:
            case WALK:
                if (gameScreen.isBindingPressed(Binding.LEFT)) {
                    deltaX = -WALK_H_SPEED;
                    skeleton.setScaleX(-Math.abs(skeleton.getScaleX()));
                } else if (gameScreen.isBindingPressed(Binding.RIGHT)){
                    deltaX = WALK_H_SPEED;
                    skeleton.setScaleX(Math.abs(skeleton.getScaleX()));
                } else {
                    deltaX = 0;
                }
    
                if (gameScreen.isBindingPressed(Binding.UP)) {
                    deltaY = WALK_V_SPEED;
                } else if (gameScreen.isBindingPressed(Binding.DOWN)) {
                    deltaY = -WALK_V_SPEED;
                } else {
                    deltaY = 0;
                }
                break;
        }
    }
    
    private void animationComplete(TrackEntry entry) {
        switch (mode) {
            case ATTACK:
                if (attackQueue > 0 && attackIndex + 1< combos[comboIndex].length) {
                    attackQueue--;
                    attackIndex++;
                    animationState.setAnimation(0, combos[comboIndex][attackIndex], false);
                } else {
                    mode = Mode.STAND;
                    animationState.setAnimation(0, "stand", true);
                }
        }
    }
    
    @Override
    public void draw(float delta) {
    
    }
    
    @Override
    public void destroy() {
    
    }
}
