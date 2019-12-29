package com.ray3k.template.entities;

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
    private int enemyMaxCount = 35;
    private int enemyCounter;
    private float santaDelay = 7f;
    
    @Override
    public void create() {
        gameScreen = GameScreen.gameScreen;
        entityController = gameScreen.entityController;
        enemyTimer = enemyDelayInitial;
        enemyCounter = enemyMaxCount;
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        if (enemyCounter > 0) {
            enemyTimer -= delta;
            if (enemyTimer < 0) {
                enemyCounter--;
                EnemyEntity enemy = new EnemyEntity();
                enemy.setPosition(MathUtils.random(gameScreen.viewport.getWorldWidth()), MathUtils.random(gameScreen.viewport.getWorldHeight() - 250f));
                entityController.add(enemy);
    
                enemyDelay -= enemyDelayDelta;
                if (enemyDelay < enemyDelayMin) {
                    if (enemyCounter == 0) enemyDelay = santaDelay;
                    else enemyDelay = enemyDelayMin;
                }
    
                enemyTimer = enemyDelay;
            }
        } else if (enemyCounter == 0) {
            enemyTimer -= delta;
            if (enemyTimer < 0) {
                SantaEntity enemy = new SantaEntity();
                enemy.setPosition(MathUtils.random(gameScreen.viewport.getWorldWidth()), MathUtils.random(gameScreen.viewport.getWorldHeight()));
                entityController.add(enemy);
                enemyCounter--;
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
