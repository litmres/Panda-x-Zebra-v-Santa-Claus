package com.ray3k.template.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.ray3k.template.screens.GameScreen;

public class EnemyController extends Entity {
    public float enemyDelayInitial = 1f;
    public float enemyDelay = 4f;
    public float enemyDelayDelta = .2f;
    public float enemyDelayMin = 1f;
    public float enemyTimer;
    private GameScreen gameScreen;
    private EntityController entityController;
    
    @Override
    public void create() {
        gameScreen = GameScreen.gameScreen;
        entityController = gameScreen.entityController;
        enemyTimer = enemyDelayInitial;
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        enemyTimer -= delta;
        if (enemyTimer < 0) {
            EnemyEntity enemy = new EnemyEntity();
            enemy.setPosition(MathUtils.random(Gdx.graphics.getWidth()), MathUtils.random(Gdx.graphics.getHeight()));
            entityController.add(enemy);
            
            enemyDelay -= enemyDelayDelta;
            if (enemyDelay < enemyDelayMin) {
                enemyDelay = enemyDelayMin;
            }
            
            enemyTimer = enemyDelay;
        }
    }
    
    @Override
    public void draw(float delta) {
    
    }
    
    @Override
    public void destroy() {
    
    }
}
