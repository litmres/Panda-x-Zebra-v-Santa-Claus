package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.rafaskoberg.gdx.typinglabel.TypingConfig;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import com.ray3k.template.Core;
import com.ray3k.template.Core.CharacterSkin;
import com.ray3k.template.JamScreen;

import static com.ray3k.template.Core.CharacterSkin.PANDA;
import static com.ray3k.template.Core.CharacterSkin.ZEBRA;

public class StoryScreen extends JamScreen {
    private Action action;
    private Stage stage;
    private Skin skin;
    private Core core;
    private final static Color BG_COLOR = new Color(Color.WHITE);
    
    public StoryScreen(Action action) {
        this.action = action;
    }
    
    @Override
    public void show() {
        core = Core.core;
        skin = core.skin;
        
        stage = new Stage(new FitViewport(1024, 576), core.batch);
        Gdx.input.setInputProcessor(stage);
        
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        final Image fg = new Image(skin, "white");
        fg.setColor(Color.BLACK);
        fg.setFillParent(true);
        fg.setTouchable(Touchable.disabled);
        stage.addActor(fg);
        fg.addAction(Actions.sequence(Actions.fadeOut(.3f)));
        
        root.defaults().space(30);
        TypingConfig.INTERVAL_MULTIPLIERS_BY_CHAR.put('\n', .5f);
        TypingLabel typingLabel = new TypingLabel("...and Sir, our children will no longer cower in fear with the ever" +
                "looming threat of death. For sacrificing even one life for the supposed joy of others is one too many." +
                "You will no longer feast at the expense of the innocent.\n-John, President of the Free World.\n\nChoose your character", skin);
        typingLabel.setAlignment(Align.center);
        typingLabel.setWrap(true);
        root.add(typingLabel).growX().pad(25).colspan(2);
        
        root.row();
        ImageButton imageButton = new ImageButton(skin, "panda");
        root.add(imageButton);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);
                fg.addAction(Actions.sequence(Actions.fadeIn(.3f), action));
                Sound sound = core.assetManager.get("sfx/panda-select.mp3");
                sound.play();
                core.characterSkin = PANDA;
            }
        });
    
        imageButton = new ImageButton(skin, "zebra");
        root.add(imageButton);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);
                fg.addAction(Actions.sequence(Actions.fadeIn(.3f), action));
                Sound sound = core.assetManager.get("sfx/zebra-select.mp3");
                sound.play();
                core.characterSkin = ZEBRA;
            }
        });
    }
    
    @Override
    public void act(float delta) {
        stage.act(delta);
    }
    
    @Override
    public void draw(float delta) {
        Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}
