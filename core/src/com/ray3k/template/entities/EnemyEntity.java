package com.ray3k.template.entities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationState.AnimationStateAdapter;
import com.esotericsoftware.spine.AnimationState.TrackEntry;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.Slot;
import com.ray3k.template.Utils;
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
    public Slot bboxSlot;
    public Slot attackBbboxSlot;
    public Rectangle bboxRectangle = new Rectangle();
    public Rectangle attackBboxRectangle = new Rectangle();
    public static final float DAMAGE = 30f;
    public float health;
    public boolean hitPlayer;
    
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
    
        bboxSlot = skeleton.findSlot("bbox");
        attackBbboxSlot = skeleton.findSlot("attack-bbox");
        health = 100f;
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        movement(delta);
        attack(delta);
    
    
        depth = GameScreen.CHARACTER_MIN_DEPTH + (int) y;
    
        Utils.localVerticiesToAABB(bboxRectangle, bboxSlot);
        Utils.localVerticiesToAABB(attackBboxRectangle, attackBbboxSlot);
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
                    hitPlayer = false;
                }
                break;
            case ATTACK:
                if (attackBbboxSlot.getAttachment() != null) {
                    attackPlayer(delta);
                }
                break;
        }
    }
    
    private void attackPlayer(float delta) {
        if (!player.destroy && !hitPlayer && Intersector.overlaps(player.bboxRectangle, attackBboxRectangle)) {
            player.hurt(DAMAGE);
            hitPlayer = true;
            gameScreen.assetManager.get("sfx/kick.mp3", Sound.class).play();
        }
    }
    
    private void animationComplete(TrackEntry entry) {
        switch (mode) {
            case ATTACK:
            case HURT:
                mode = Mode.STAND;
                animationState.setAnimation(0, "stand", false);
                attackTimer = MathUtils.random(ATTACK_TIMER_MIN, ATTACK_TIMER_MAX);
                break;
            case DEAD:
                destroy = true;
                break;
        }
    }
    
    public void hurt(float damage) {
        if (mode != Mode.DEAD) {
            health -= damage;
            if (health <= 0) {
                gameScreen.assetManager.get("sfx/die.mp3", Sound.class).play();
                mode = Mode.DEAD;
                animationState.setAnimation(0, "die", false);
                setSpeed(0);
            } else {
                GameScreen.hurtSounds.random().play();
                mode = Mode.HURT;
                animationState.setAnimation(0, "hurt-1", false);
                setSpeed(0);
            }
        }
    }
    
    @Override
    public void draw(float delta) {
        gameScreen.shapeDrawer.filledRectangle(bboxRectangle, new Color(0, 1, 1, .5f));
        if (attackBbboxSlot.getAttachment() != null) gameScreen.shapeDrawer.filledRectangle(attackBboxRectangle, new Color(1, 0, 1, .5f));
    }
    
    @Override
    public void destroy() {
    
    }
}
