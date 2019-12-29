package com.ray3k.template.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationState.AnimationStateAdapter;
import com.esotericsoftware.spine.AnimationState.TrackEntry;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.Slot;
import com.ray3k.template.Core.Binding;
import com.ray3k.template.Utils;
import com.ray3k.template.screens.GameScreen;

public class PlayerEntity extends Entity {
    public static PlayerEntity player;
    public enum Mode {
        STAND, WALK, HURT, ATTACK, DEAD
    }
    public Mode mode;
    public int attackQueue;
    public static final String[][] combos = {{"combo1-1", "combo1-2", "combo1-3", "combo1-4"}, {"combo2-1", "combo2-2", "combo2-3", "combo2-4"}, {"combo3-1", "combo3-2", "combo3-3", "combo3-4"}};
    public static final float[] damages = {15f, 15f, 30f, 40f};
    public int attackIndex;
    public int comboIndex;
    public static final float WALK_H_SPEED = 500f;
    public static final float WALK_V_SPEED = 250f;
    private GameScreen gameScreen;
    public Slot bboxSlot;
    public Slot attackBbboxSlot;
    public Rectangle bboxRectangle = new Rectangle();
    public Rectangle attackBboxRectangle = new Rectangle();
    public float health;
    public Array<EnemyEntity> enemiesHit;
    public static final float LEVEL_BORDER_LEFT = 100f;
    public static final float LEVEL_BORDER_RIGHT = 100f;
    public static final float LEVEL_BORDER_BOTTOM = 10f;
    public static final float LEVEL_BORDER_TOP = 200f;
    
    @Override
    public void create() {
        player = this;
        gameScreen = GameScreen.gameScreen;
        
        skeleton = new Skeleton(GameScreen.characterSkeletonData);
        animationState = new AnimationState(GameScreen.characterAnimationStateData);
        
        animationState.setAnimation(0, "stand", true);
        skeleton.setSkin(core.characterSkin.skin);
        skeleton.setScale(.5f, .5f);
        
        animationState.addListener(new AnimationStateAdapter() {
            @Override
            public void start(TrackEntry entry) {
                animationStart(entry);
            }
    
            @Override
            public void complete(TrackEntry entry) {
                animationComplete(entry);
            }
        });
        
        mode = Mode.STAND;
        bboxSlot = skeleton.findSlot("bbox");
        attackBbboxSlot = skeleton.findSlot("attack-bbox");
        health = 100f;
        
        enemiesHit = new Array<>();
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        if (gameScreen.isBindingJustPressed(Binding.ATTACK)) {
            attackQueue++;
        }
        
        Utils.localVerticiesToAABB(bboxRectangle, bboxSlot);
        Utils.localVerticiesToAABB(attackBboxRectangle, attackBbboxSlot);
        
        movementControls(delta);
    
        attackControls(delta);
        
        movement(delta);
        
        depth = GameScreen.CHARACTER_MIN_DEPTH + (int) y;
        
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
                    enemiesHit.clear();
                    setSpeed(0);
                }
                break;
            case ATTACK:
                if (attackBbboxSlot.getAttachment() != null) {
                    attackEnemies(delta);
                }
                break;
        }
    }
    
    public void attackEnemies(float delta) {
        for (Entity entity : gameScreen.entityController.entities) {
            if (entity instanceof EnemyEntity) {
                EnemyEntity enemy = (EnemyEntity) entity;
                if (enemy.health > 0 && !enemiesHit.contains(enemy, true) && Intersector.overlaps(enemy.bboxRectangle, attackBboxRectangle)) {
                    enemy.hurt(damages[attackIndex]);
                    enemiesHit.add(enemy);
                    gameScreen.assetManager.get("sfx/kick.mp3", Sound.class).play(core.sfx);
                }
            }
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
                
                if (x < LEVEL_BORDER_LEFT) {
                    x = LEVEL_BORDER_LEFT;
                    deltaX = 0;
                } else if (x > Gdx.graphics.getWidth() - LEVEL_BORDER_RIGHT) {
                    x = Gdx.graphics.getWidth() - LEVEL_BORDER_RIGHT;
                    deltaX = 0;
                }
                
                if (y < LEVEL_BORDER_BOTTOM) {
                    y = LEVEL_BORDER_BOTTOM;
                    deltaY = 0;
                } else if (y > Gdx.graphics.getHeight() - LEVEL_BORDER_TOP) {
                    y = Gdx.graphics.getHeight() - LEVEL_BORDER_TOP;
                    deltaY = 0;
                }
                break;
        }
    }
    
    private void animationStart(TrackEntry entry) {
        switch (mode) {
            case ATTACK:
                gameScreen.assetManager.get("sfx/whoosh.mp3", Sound.class).play(core.sfx);
                break;
        }
    }
    
    private void animationComplete(TrackEntry entry) {
        switch (mode) {
            case ATTACK:
            case HURT:
                if (attackQueue > 0 && attackIndex + 1< combos[comboIndex].length) {
                    attackQueue--;
                    attackIndex++;
                    enemiesHit.clear();
                    animationState.setAnimation(0, combos[comboIndex][attackIndex], false);
                } else {
                    mode = Mode.STAND;
                    animationState.setAnimation(0, "stand", true);
                }
                break;
            case DEAD:
                destroy = true;
        }
    }
    
    public void hurt(float damage) {
        if (mode != Mode.DEAD) {
            health -= damage;
            if (health <= 0) {
                gameScreen.assetManager.get("sfx/die.mp3", Sound.class).play(core.sfx);
                mode = Mode.DEAD;
                animationState.setAnimation(0, "die", false);
                setSpeed(0);
            } else {
                GameScreen.hurtSounds.random().play(core.sfx);
                mode = Mode.HURT;
                animationState.setAnimation(0, "hurt-1", false);
                setSpeed(0);
            }
        }
    }
    
    @Override
    public void draw(float delta) {
//        gameScreen.shapeDrawer.filledRectangle(bboxRectangle, new Color(0, 1, 0, .5f));
//        if (attackBbboxSlot.getAttachment() != null) gameScreen.shapeDrawer.filledRectangle(attackBboxRectangle, new Color(1, 0, 0, .5f));
    }
    
    @Override
    public void destroy() {
    
    }
}
