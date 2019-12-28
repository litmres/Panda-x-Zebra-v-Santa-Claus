package com.ray3k.template.entities;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationState.AnimationStateAdapter;
import com.esotericsoftware.spine.AnimationState.TrackEntry;
import com.esotericsoftware.spine.Skeleton;
import com.ray3k.template.screens.GameScreen;

public class EnemyEntity extends Entity {
    public enum Mode {
        STAND, WALK, HURT, ATTACK, DEAD
    }
    public static final float WALK_H_SPEED = 250;
    public static final float WALK_V_SPEED = 100f;
    public static final float BORDER_H = 150;
    public static final float BORDER_V = 150;
    private GameScreen gameScreen;
    private PlayerEntity player;
    public float attackTimer;
    public Mode mode;
    public static final float ATTACK_TIMER_MIN = .25f;
    public static final float ATTACK_TIMER_MAX = 1f;
    
    @Override
    public void create() {
        gameScreen = GameScreen.gameScreen;
        player = PlayerEntity.player;
    
        skeleton = new Skeleton(GameScreen.characterSkeletonData);
        animationState = new AnimationState(GameScreen.characterAnimationStateData);
    
        animationState.setAnimation(0, "walk", true);
        skeleton.setSkin("elf");
        skeleton.setScale(.5f, .5f);
    
        animationState.addListener(new AnimationStateAdapter() {
            @Override
            public void complete(TrackEntry entry) {
                animationComplete(entry);
            }
        });
    
        mode = Mode.WALK;
        attackTimer = MathUtils.random(ATTACK_TIMER_MIN, ATTACK_TIMER_MAX);
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        movement(delta);
        attack(delta);
    
    
        depth = GameScreen.CHARACTER_MIN_DEPTH + (int) y;
    }
    
    private void movement(float delta) {
        switch (mode) {
            case STAND:
                if (x < player.x - BORDER_H || x > player.x + BORDER_H || y < player.y - BORDER_V || y > player.y + BORDER_V) {
                    mode = Mode.WALK;
                    animationState.setAnimation(0, "walk", true);
                }
                break;
            case WALK:
                if (x < player.x) {
                    if (x < player.x - BORDER_H) {
                        deltaX = WALK_H_SPEED;
                        skeleton.setScaleX(Math.abs(skeleton.getScaleX()));
                    } else {
                        deltaX = 0;
                    }
                } else if (x > player.x) {
                    if (x > player.x + BORDER_H) {
                        deltaX = -WALK_H_SPEED;
                        skeleton.setScaleX(-Math.abs(skeleton.getScaleX()));
                    } else {
                        deltaX = 0;
                    }
                }
    
                if (y < player.y) {
                    if (y < player.y - BORDER_H) {
                        deltaY = WALK_V_SPEED;
                    } else {
                        deltaY = 0;
                    }
                } else if (y > player.y) {
                    if (y > player.y + BORDER_H) {
                        deltaY = -WALK_V_SPEED;
                    } else {
                        deltaY = 0;
                    }
                }
    
                if (x >= player.x - BORDER_H && x <= player.x + BORDER_H && y >= player.y - BORDER_V && y <= player.y + BORDER_V) {
                    mode = Mode.STAND;
                    animationState.setAnimation(0, "stand", true);
                    attackTimer = MathUtils.random(ATTACK_TIMER_MIN, ATTACK_TIMER_MAX);
                }
                break;
        }
    }
    
    private void attack(float delta) {
        switch (mode) {
            case STAND:
                attackTimer -= delta;
                if (attackTimer <= 0) {
                    mode = Mode.ATTACK;
                    animationState.setAnimation(0, "combo1-1", false);
                }
                break;
            case ATTACK:
                
                break;
        }
    }
    
    private void animationComplete(TrackEntry entry) {
        switch (mode) {
            case ATTACK:
                mode = Mode.STAND;
                animationState.setAnimation(0, "stand", false);
                attackTimer = MathUtils.random(ATTACK_TIMER_MIN, ATTACK_TIMER_MAX);
                break;
        }
    }
    
    @Override
    public void draw(float delta) {
    
    }
    
    @Override
    public void destroy() {
    
    }
}
