package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.EarthquakeEffect;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;
import com.ray3k.template.Core;
import com.ray3k.template.JamScreen;
import com.ray3k.template.entities.EnemyController;
import com.ray3k.template.entities.EntityController;
import com.ray3k.template.entities.PlayerEntity;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class GameScreen extends JamScreen {
    public static GameScreen gameScreen;
    public static final Color BG_COLOR = new Color();
    private Action action;
    private Core core;
    public AssetManager assetManager;
    private Batch batch;
    public Stage stage;
    public Skin skin;
    public ShapeDrawer shapeDrawer;
    public EntityController entityController;
    private VfxManager vfxManager;
    private EarthquakeEffect vfxEffect;
    public static SkeletonData characterSkeletonData;
    public static AnimationStateData characterAnimationStateData;
    public static final int CHARACTER_MIN_DEPTH = 1000;
    public static Array<Sound> hurtSounds;
    
    public GameScreen(Action action) {
        BG_COLOR.set(Color.LIGHT_GRAY);
        
        gameScreen = this;
        this.action = action;
        core = Core.core;
        assetManager = core.assetManager;
        batch = core.batch;
        vfxManager = core.vfxManager;
        vfxEffect = new EarthquakeEffect();
        vfxManager.addEffect(vfxEffect);
        vfxEffect.setSpeed(0);
        vfxEffect.setAmount(0);
        vfxEffect.rebind();
    
        Music music = assetManager.get("bgm/game.mp3");
        music.setLooping(true);
        music.setVolume(core.bgm);
        music.play();
        
        characterSkeletonData = assetManager.get("spine/character.json");
        characterAnimationStateData = new AnimationStateData(characterSkeletonData);
        
        stage = new Stage(new ScreenViewport(), core.batch);
        skin = assetManager.get("skin/skin.json");
        shapeDrawer = new ShapeDrawer(core.batch, skin.getRegion("white"));
        shapeDrawer.setPixelSize(.5f);
        
        InputMultiplexer inputMultiplexer = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(inputMultiplexer);
        
        camera = new OrthographicCamera();
        camera.position.set(512, 288, 0);
        viewport = new FitViewport(1024, 576, camera);
        
        entityController = new EntityController();
        PlayerEntity player = new PlayerEntity();
        player.setPosition(512, 288);
        entityController.add(player);
        
        entityController.add(new EnemyController());
        
        hurtSounds = new Array<>();
        hurtSounds.add(assetManager.get("sfx/hurt1.mp3"));
        hurtSounds.add(assetManager.get("sfx/hurt2.mp3"));
        hurtSounds.add(assetManager.get("sfx/hurt3.mp3"));
    }
    
    @Override
    public void act(float delta) {
        entityController.act(delta);
        stage.act(delta);
        vfxEffect.update(delta);
    }
    
    @Override
    public void draw(float delta) {
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        vfxManager.cleanUpBuffers();
        vfxManager.beginCapture();
        Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        entityController.draw(delta);
        batch.end();
        vfxManager.endCapture();
        vfxManager.applyEffects();
        vfxManager.renderToScreen();

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        vfxManager.resize(width, height);
        viewport.update(width, height);
        
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void dispose() {
        vfxEffect.dispose();
    }
    
    @Override
    public void hide() {
        vfxEffect.dispose();
        Music music = assetManager.get("bgm/game.mp3");
        music.stop();
    }
}
