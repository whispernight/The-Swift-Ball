package theHardestGame.pac;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.opengl.texture.source.AssetTextureSource;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;


public class GameLogicController extends BaseGameActivity implements IAccelerometerListener{
	
	
	
	PlayerProfileManager playerProfileManager;
	
	public LevelController levelController;

	private Camera camera;
	protected PhysicsWorld mPhysicsWorld;

	public Texture mTexture;
	public TextureRegion enemyTextureRegion;
	
	public Texture mFinishLineTexture;
	public TextureRegion mFinishLineTextureRegion;
	
	public Texture mBlockTexture;
	public TextureRegion mBlockTextureRegion;
	
	private float mGravityX;
	private float mGravityY;
	private final Vector2 mTempVector = new Vector2();

	public TiledTextureRegion mCircleFaceTextureRegion;
	
	public Texture mEnemyTexture;
	
	public Texture mDiamantTexture;
	public TextureRegion mDiamantTextureRegion;
	
	private RepeatingSpriteBackground mGrassBackground;
	private RepeatingSpriteBackground mMenuBackground;
	
	public Texture mBackgroundTexture;
	public TextureRegion mBackgroundTextureRegion;

	private Sound mGameOverSound;
	private Sound mMunchSound;
	
	public Texture mLevelMenuTexture;
	public TextureRegion mLevelTextureRegion;
	
	static GameLogicController gameLogicController;
	
	public static GameLogicController getInstance(){return gameLogicController;}
	
	//LEVEL CHOOSE MENU BUTTONS
	public Texture mLevelChooseBackTexture;
	public TextureRegion mLevelChooseBackTextureRegion;
	
	public Texture mLevelChooseMenuTexture;
	public TextureRegion mLevelChooseMenuTextureRegion;

	public Texture mLevelChooseNextTexture;
	public TextureRegion mLevelChooseNextTextureRegion;
	
	public Texture mLevelButtonDisabled;
	public TextureRegion mLevelButtonNextDisabledRegion;
	public TextureRegion mLevelButtonBackDisabledRegion;
	
	//LEVEL number
	
	public Texture mLevelTexture1;
	public TextureRegion mLevelTextureRegion1;
	public TextureRegion mLevelTextureRegion2;
	public TextureRegion mLevelTextureRegion3;
	public TextureRegion mLevelTextureRegionLocked;
	
	//Level sign textures
	public Texture mLevelSignTextures;
	public TextureRegion mLevelCompletedRegion;
	public TextureRegion mLevelUnCompletedRegion;
	
	long lDateTime = -1;
	int currentPage;
	
	@Override
	public Engine onLoadEngine() {
		
		
		currentPage = 0;
		playerProfileManager = new PlayerProfileManager(this);

		gameLogicController = this;
		levelController = new LevelController(this);

		levelController.mCameraWidth = 460;
		levelController.mCameraHeight = 320;
		
		//width = 240
		//height = 320
		
		

		camera = new Camera(0, 0, levelController.mCameraWidth, levelController.mCameraHeight);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(levelController.mCameraWidth, levelController.mCameraHeight), camera).setNeedsSound(true));
	}
	
	@Override
	public void onLoadResources() {
		this.mMenuBackground = new RepeatingSpriteBackground(levelController.mCameraWidth, levelController.mCameraHeight, this.mEngine.getTextureManager(), new AssetTextureSource(this, "gfx/test.jpg"));
		this.mGrassBackground = new RepeatingSpriteBackground(levelController.mCameraWidth, levelController.mCameraHeight, this.mEngine.getTextureManager(), new AssetTextureSource(this, "gfx/background_parket_128.png"));
		
		try {
			this.mGameOverSound = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, "gfx/game_over.ogg");
			this.mMunchSound = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, "gfx/wagner_the_ride_of_the_valkyries.ogg");
			levelController.addSoundManager(mGameOverSound, mMunchSound);
		} catch (final IOException e) {
			Debug.e("Error", e);
		}
		
		this.mTexture = new Texture(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		this.mCircleFaceTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mTexture, this, "gfx/silver_ball.png", 0, 32, 2, 1); // 64x32
		
		this.mMenuTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuResetTextureRegion = TextureRegionFactory.createFromAsset(this.mMenuTexture, this, "gfx/menu_black_button_play.png", 0, 0);
		this.mMenuQuitTextureRegion = TextureRegionFactory.createFromAsset(this.mMenuTexture, this, "gfx/menu_black_button_quit.png", 0, 50);
		this.mMenuSettingsTextureRegion = TextureRegionFactory.createFromAsset(this.mMenuTexture, this, "gfx/menu_black_button_settings.png", 0, 100);
		
		this.mSubMenuTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuOkTextureRegion = TextureRegionFactory.createFromAsset(this.mSubMenuTexture, this, "gfx/menu_black_button_ok.png", 0, 0);
		this.mMenuBackTextureRegion = TextureRegionFactory.createFromAsset(this.mSubMenuTexture, this, "gfx/menu_black_button_back.png", 0, 50);
		
		this.mEnemyTexture  = new Texture(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		//enemyTextureRegion = TextureRegionFactory.createFromAsset(this.mEnemyTexture, this, "gfx/enemy_32_red.png", 0, 0);
		enemyTextureRegion = TextureRegionFactory.createFromAsset(this.mEnemyTexture, this, "gfx/ball_enemy.png", 0, 0);
		
		this.mDiamantTexture  = new Texture(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mDiamantTextureRegion = TextureRegionFactory.createFromAsset(this.mDiamantTexture, this, "gfx/diamant_1.png", 0, 0);
		
		mBackgroundTexture = new Texture(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mBackgroundTextureRegion = TextureRegionFactory.createFromAsset(this.mBackgroundTexture, this, "gfx/background_parket_128.png", 0, 0);
		
		
		this.mBlockTexture  = new Texture(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mBlockTextureRegion = TextureRegionFactory.createFromAsset(this.mBlockTexture, this, "gfx/block_box1.png", 0, 0);
		
		this.mLevelMenuTexture  = new Texture(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mLevelTextureRegion = TextureRegionFactory.createFromAsset(this.mLevelMenuTexture, this, "gfx/button_level_1.png", 0, 0);
		
		this.mFinishLineTexture  = new Texture(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mFinishLineTextureRegion = TextureRegionFactory.createFromAsset(this.mFinishLineTexture, this, "gfx/finish_line32.png", 0, 0);


		//LEVEL MENU BUTTONS
		mLevelChooseBackTexture = new Texture(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mLevelChooseBackTextureRegion = TextureRegionFactory.createFromAsset(this.mLevelChooseBackTexture, this, "gfx/menuLevel_black_back.png", 0, 0);

		mLevelChooseMenuTexture = new Texture(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mLevelChooseMenuTextureRegion = TextureRegionFactory.createFromAsset(this.mLevelChooseMenuTexture, this, "gfx/menuLevel_black_up_.png", 0, 0);

		mLevelChooseNextTexture = new Texture(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mLevelChooseNextTextureRegion = TextureRegionFactory.createFromAsset(this.mLevelChooseNextTexture, this, "gfx/menuLevel_next.png", 0, 0);

		mLevelButtonDisabled = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mLevelButtonBackDisabledRegion = TextureRegionFactory.createFromAsset(this.mLevelButtonDisabled, this, "gfx/menuLevel_black_back_disabled.png", 0, 0);
		mLevelButtonNextDisabledRegion = TextureRegionFactory.createFromAsset(this.mLevelButtonDisabled, this, "gfx/menuLevel_next_disabled.png", 0, 64);
		

		
		mLevelTexture1 = new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mLevelTextureRegion1 = TextureRegionFactory.createFromAsset(this.mLevelTexture1, this, "gfx/level1.png", 0, 0);
		mLevelTextureRegion2 = TextureRegionFactory.createFromAsset(this.mLevelTexture1, this, "gfx/level2.png", 0, 64);
		mLevelTextureRegionLocked = TextureRegionFactory.createFromAsset(this.mLevelTexture1, this, "gfx/level_locked_1.png", 0, 128);
		mLevelTextureRegion3 = TextureRegionFactory.createFromAsset(this.mLevelTexture1, this, "gfx/level3.png", 0, 192);
		
		mLevelSignTextures = new Texture(128,128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mLevelCompletedRegion = TextureRegionFactory.createFromAsset(this.mLevelSignTextures, this, "gfx/ok_button.png", 0, 0);
		mLevelUnCompletedRegion = TextureRegionFactory.createFromAsset(this.mLevelSignTextures, this, "gfx/fail_button.png", 0, 64);

		
		
		this.mEngine.registerUpdateHandler(new FPSLogger());
		this.mEngine.getTextureManager().loadTextures(this.mLevelButtonDisabled,this.mLevelSignTextures, this.mMenuTexture, this.mSubMenuTexture, this.mTexture, this.mEnemyTexture, this.mFinishLineTexture, this.mBlockTexture, this.mDiamantTexture, this.mLevelMenuTexture
				, this.mLevelChooseBackTexture, this.mLevelChooseMenuTexture, this.mLevelChooseNextTexture, this.mLevelTexture1, this.mBackgroundTexture);

	}

	public Scene newGameLevelScene(int levelId){
		
		Scene scene = new Scene(2);
	
		this.mPhysicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), false);



		levelController.setScene(scene);
		levelController.setmPhysicsWorld(mPhysicsWorld);

		levelController.createFrame();

		levelController.loadLevel(levelId);
		
		this.enableAccelerometerSensor(this);
		
		scene.registerUpdateHandler(this.mPhysicsWorld);

		return scene;
	}

	@Override
	public Scene onLoadScene() {
		//return newGameLevelScene(1);
		return onLoadMenuScene();
	}

	@Override
	public void onLoadComplete() {
		// TODO Auto-generated method stub

	}
	
	final int con = 10;

	@Override
	public void onAccelerometerChanged(AccelerometerData pAccelerometerData) {
		this.mGravityX = pAccelerometerData.getY() * 2;
		this.mGravityY = pAccelerometerData.getX() * 2;
		
		if(this.mGravityX > con)
			this.mGravityX = con;
		if(this.mGravityY > con)
			this.mGravityY = con;
		
		if(this.mGravityX < con * (-1))
			this.mGravityX = con * (-1);
		if(this.mGravityY < con * (-1))
			this.mGravityY = con * (-1);
		
		
		this.mTempVector.set(this.mGravityX, this.mGravityY);
		this.mPhysicsWorld.setGravity(this.mTempVector);
	}

	//MENU
	protected static final int MENU_PLAY = 0;
	protected static final int MENU_QUIT = MENU_PLAY + 1;


	private Texture mMenuTexture;
	protected TextureRegion mMenuResetTextureRegion;
	protected TextureRegion mMenuQuitTextureRegion;
	protected TextureRegion mMenuSettingsTextureRegion;
	


	public Scene onLoadMenuScene() {

		//onLoadMainScene();
		return createMenuScene();

	}
	
	public boolean checkTouchTime(){
		
		if(lDateTime == -1)
		{
			lDateTime = new Date().getTime();
			return true;
		}
		
		long lDateCurrentTime = new Date().getTime();
		if(lDateCurrentTime - lDateTime > 500)
		{
			lDateTime = lDateCurrentTime;
			return true;
		}
		return false;
	}
	
	protected Scene createQuitScene() {
		
		Scene scene = new Scene(2);
		//scene.setBackground(this.mMenuBackground);
		Sprite buttonLevel = new Sprite(100, 100, 250, 70, this.mMenuOkTextureRegion)
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if(checkTouchTime())
					GameLogicController.getInstance().finish();
				
				return true;
			}
		};
		
		scene.registerTouchArea(buttonLevel);
		scene.getTopLayer().addEntity(buttonLevel);
		
		buttonLevel = new Sprite(100, 170, 250, 70, this.mMenuBackTextureRegion)
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if(checkTouchTime())
					GameLogicController.getInstance().mEngine.setScene(GameLogicController.getInstance().createMenuScene());
				return true;
			}
		};
		scene.registerTouchArea(buttonLevel);
		scene.getTopLayer().addEntity(buttonLevel);
		return scene;
	}
	

	protected Scene createMenuScene() {
		
		Scene scene = new Scene(2);
		//scene.setBackground(this.mMenuBackground);
		Sprite buttonLevel = new Sprite(100, 50, 250, 70, this.mMenuResetTextureRegion)
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if(checkTouchTime())
					GameLogicController.getInstance().mEngine.setScene(GameLogicController.getInstance().createLevelSubmenu(currentPage));
				return true;
			}
		};
		buttonLevel.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		scene.registerTouchArea(buttonLevel);
		scene.getTopLayer().addEntity(buttonLevel);
		
		buttonLevel = new Sprite(100, 130, 250, 70, this.mMenuSettingsTextureRegion)
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				return true;
			}
		};
		buttonLevel.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		scene.registerTouchArea(buttonLevel);
		scene.getTopLayer().addEntity(buttonLevel);
		
		buttonLevel = new Sprite(100, 210, 250, 70, this.mMenuQuitTextureRegion)
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if(checkTouchTime())
				{
					//GameLogicController.getInstance().mEngine.setScene(GameLogicController.getInstance().createQuitScene());
					GameLogicController.getInstance().finish();
				}
				return true;
			}
		};
		buttonLevel.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		scene.registerTouchArea(buttonLevel);
		scene.getTopLayer().addEntity(buttonLevel);
		
		return scene;
	}
	

	
	
	public void createLevelMenuButtonLayout(Scene scene, boolean backButton){
		int height = 150;
		int weight = 50;
		int x = 200; 
		int y = 200;
		Sprite buttonLevel = new Sprite(x, y, height, weight, mLevelChooseBackTextureRegion)
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if(checkTouchTime())
					GameLogicController.getInstance().mEngine.setScene(newGameLevelScene(1));
				return true;
			}
		};
		scene.registerTouchArea(buttonLevel);
		scene.getTopLayer().addEntity(buttonLevel);
	}

	/*
	 * LevelId = 100 - 130
	 * */
	//Scene levelSubMenuPage1;
	
	public Scene createLevelSubmenu(int id){
		Scene scene = new Scene(2);
		//createLevelMenuButtonLayout(scene, false);
		if(id == 0)
			addMenuLevelItemsPage1(scene);
		else 
			addMenuLevelItemsPage2(scene);
		
		//scene.setBackground(this.mMenuBackground);
		return scene;
	}
	

	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if(pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			//this.finish();
			this.mEngine.setScene(createMenuScene());
			return true;
		} 
		if(pKeyCode == KeyEvent.KEYCODE_DPAD_CENTER && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			//this.finish();
			this.mEngine.setScene(createMenuScene());
			return true;
		} 
		else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
	}


	//SUB MENU

	//private MenuScene mSubMenuSceneQuite;

	private Texture mSubMenuTexture;
	private TextureRegion mMenuOkTextureRegion;
	private TextureRegion mMenuBackTextureRegion;
	


	protected void onLoadSubMenuResources(){

		

		this.mSubMenuTexture = new Texture(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		this.mMenuOkTextureRegion = TextureRegionFactory.createFromAsset(this.mSubMenuTexture, this, "gfx/menu_black_button_ok.png", 0, 0);
		this.mMenuBackTextureRegion = TextureRegionFactory.createFromAsset(this.mSubMenuTexture, this, "gfx/menu_black_button_back.png", 0, 50);

		this.mEngine.getTextureManager().loadTexture(this.mSubMenuTexture);
	}
	

	//set up all menu level items
	public void addMenuLevelItemsPage1(Scene scene){
		
		int height = 80;
		int weight = 80;
		int x = 10;
		int y = 10;
		Sprite buttonLevel;
		if(playerProfileManager.isLevelUnlocked(1))
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegion1)
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					if(checkTouchTime())
						GameLogicController.getInstance().mEngine.setScene(newGameLevelScene(1));
					return true;
				}
			};
			scene.registerTouchArea(buttonLevel);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		else
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegionLocked);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		
		x += weight + 10;
		if(playerProfileManager.isLevelUnlocked(2))
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegion2)
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					if(checkTouchTime())
						GameLogicController.getInstance().mEngine.setScene(newGameLevelScene(2));
					return true;
				}
			};
			scene.registerTouchArea(buttonLevel);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		else
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegionLocked);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		
		x += weight + 10;
		if(playerProfileManager.isLevelUnlocked(3))
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegion3)
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					if(checkTouchTime())
						GameLogicController.getInstance().mEngine.setScene(newGameLevelScene(3));
					return true;
				}
			};
			scene.registerTouchArea(buttonLevel);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		else
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegionLocked);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		
		x += weight + 10;
		if(playerProfileManager.isLevelUnlocked(4))
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegion2)
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					if(checkTouchTime())
						GameLogicController.getInstance().mEngine.setScene(newGameLevelScene(4));
					return true;
				}
			};
			scene.registerTouchArea(buttonLevel);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		else
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegionLocked);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		
		x += weight + 10;
		if(playerProfileManager.isLevelUnlocked(5))
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegion2)
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					if(checkTouchTime())
						GameLogicController.getInstance().mEngine.setScene(newGameLevelScene(5));
					return true;
				}
			};
			scene.registerTouchArea(buttonLevel);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		else
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegionLocked);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		
		// NOV NIVO
		x = 10;
		y += height + 10;
		
		if(playerProfileManager.isLevelUnlocked(6))
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegion2)
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					if(checkTouchTime())
						GameLogicController.getInstance().mEngine.setScene(newGameLevelScene(6));
					return true;
				}
			};
			scene.registerTouchArea(buttonLevel);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		else
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegionLocked);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		
		x += weight + 10;
		if(playerProfileManager.isLevelUnlocked(7))
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegion2)
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					if(checkTouchTime())
						GameLogicController.getInstance().mEngine.setScene(newGameLevelScene(7));
					return true;
				}
			};
			scene.registerTouchArea(buttonLevel);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		else
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegionLocked);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		
		x += weight + 10;
		if(playerProfileManager.isLevelUnlocked(8))
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegion2)
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					if(checkTouchTime())
						GameLogicController.getInstance().mEngine.setScene(newGameLevelScene(8));
					return true;
				}
			};
			scene.registerTouchArea(buttonLevel);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		else
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegionLocked);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		
		x += weight + 10;
		if(playerProfileManager.isLevelUnlocked(9))
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegion2)
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					if(checkTouchTime())
						GameLogicController.getInstance().mEngine.setScene(newGameLevelScene(9));
					return true;
				}
			};
			scene.registerTouchArea(buttonLevel);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		else
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegionLocked);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		
		x += weight + 10;
		if(playerProfileManager.isLevelUnlocked(10))
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegion2)
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					if(checkTouchTime())
						GameLogicController.getInstance().mEngine.setScene(newGameLevelScene(10));
					return true;
				}
			};
			scene.registerTouchArea(buttonLevel);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		else
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegionLocked);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		
		y += height + 10;
		x = 20;
		height = 120;
		weight = 120;
		
		//BUTTONS
		
		buttonLevel = new Sprite(x, y, height, weight, mLevelButtonBackDisabledRegion);
		scene.getTopLayer().addEntity(buttonLevel);
		x += weight + 30;
		
		buttonLevel = new Sprite(x, y, height, weight, mLevelChooseMenuTextureRegion)
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if(checkTouchTime())
					GameLogicController.getInstance().mEngine.setScene(GameLogicController.getInstance().createMenuScene());
				return true;
			}
		};
		scene.registerTouchArea(buttonLevel);
		scene.getTopLayer().addEntity(buttonLevel);
		x += weight + 30;
		
		buttonLevel = new Sprite(x, y, height, weight, mLevelChooseNextTextureRegion)
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if(checkTouchTime())
				{
					currentPage++;
					GameLogicController.getInstance().getEngine().setScene(createLevelSubmenu(currentPage));
				}
				return true;
			}
		};
		scene.registerTouchArea(buttonLevel);
		scene.getTopLayer().addEntity(buttonLevel);

	}
	
public void addMenuLevelItemsPage2(Scene scene){
		
		int height = 80;
		int weight = 80;
		int x = 10;
		int y = 10;
		Sprite buttonLevel;
		if(playerProfileManager.isLevelUnlocked(11))
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegion1)
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					if(checkTouchTime())
						GameLogicController.getInstance().mEngine.setScene(newGameLevelScene(11));
					return true;
				}
			};
			scene.registerTouchArea(buttonLevel);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		else
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegionLocked);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		
		x += weight + 10;
		if(playerProfileManager.isLevelUnlocked(12))
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegion2)
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					if(checkTouchTime())
						GameLogicController.getInstance().mEngine.setScene(newGameLevelScene(12));
					return true;
				}
			};
			scene.registerTouchArea(buttonLevel);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		else
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegionLocked);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		
		x += weight + 10;
		if(playerProfileManager.isLevelUnlocked(13))
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegion3)
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					if(checkTouchTime())
						GameLogicController.getInstance().mEngine.setScene(newGameLevelScene(13));
					return true;
				}
			};
			scene.registerTouchArea(buttonLevel);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		else
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegionLocked);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		
		x += weight + 10;
		if(playerProfileManager.isLevelUnlocked(14))
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegion2)
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					if(checkTouchTime())
						GameLogicController.getInstance().mEngine.setScene(newGameLevelScene(14));
					return true;
				}
			};
			scene.registerTouchArea(buttonLevel);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		else
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegionLocked);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		
		x += weight + 10;
		if(playerProfileManager.isLevelUnlocked(15))
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegion2)
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					if(checkTouchTime())
						GameLogicController.getInstance().mEngine.setScene(newGameLevelScene(15));
					return true;
				}
			};
			scene.registerTouchArea(buttonLevel);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		else
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegionLocked);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		
		// NOV NIVO
		x = 10;
		y += height + 10;
		
		if(playerProfileManager.isLevelUnlocked(16))
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegion2)
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					if(checkTouchTime())
						GameLogicController.getInstance().mEngine.setScene(newGameLevelScene(16));
					return true;
				}
			};
			scene.registerTouchArea(buttonLevel);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		else
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegionLocked);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		
		x += weight + 10;
		if(playerProfileManager.isLevelUnlocked(17))
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegion2)
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					if(checkTouchTime())
						GameLogicController.getInstance().mEngine.setScene(newGameLevelScene(17));
					return true;
				}
			};
			scene.registerTouchArea(buttonLevel);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		else
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegionLocked);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		
		x += weight + 10;
		if(playerProfileManager.isLevelUnlocked(18))
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegion2)
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					if(checkTouchTime())
						GameLogicController.getInstance().mEngine.setScene(newGameLevelScene(18));
					return true;
				}
			};
			scene.registerTouchArea(buttonLevel);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		else
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegionLocked);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		
		x += weight + 10;
		if(playerProfileManager.isLevelUnlocked(19))
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegion2)
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					if(checkTouchTime())
						GameLogicController.getInstance().mEngine.setScene(newGameLevelScene(19));
					return true;
				}
			};
			scene.registerTouchArea(buttonLevel);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		else
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegionLocked);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		
		x += weight + 10;
		if(playerProfileManager.isLevelUnlocked(20))
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegion2)
			{
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					if(checkTouchTime())
						GameLogicController.getInstance().mEngine.setScene(newGameLevelScene(20));
					return true;
				}
			};
			scene.registerTouchArea(buttonLevel);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		else
		{
			buttonLevel = new Sprite(x, y, height, weight, mLevelTextureRegionLocked);
			scene.getTopLayer().addEntity(buttonLevel);
		}
		
		y += height + 10;
		x = 20;
		height = 120;
		weight = 120;
		
		//BUTTONS
		
		buttonLevel = new Sprite(x, y, height, weight, mLevelChooseBackTextureRegion)
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if(checkTouchTime())
				{
					currentPage--;
					GameLogicController.getInstance().getEngine().setScene(createLevelSubmenu(currentPage));
				}
				return true;
			}
		};
		scene.registerTouchArea(buttonLevel);
		scene.getTopLayer().addEntity(buttonLevel);
		x += weight + 30;
		
		buttonLevel = new Sprite(x, y, height, weight, mLevelChooseMenuTextureRegion)
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if(checkTouchTime())
					GameLogicController.getInstance().mEngine.setScene(GameLogicController.getInstance().createMenuScene());
				return true;
			}
		};
		scene.registerTouchArea(buttonLevel);
		scene.getTopLayer().addEntity(buttonLevel);
		x += weight + 30;
		
		buttonLevel = new Sprite(x, y, height, weight, mLevelButtonNextDisabledRegion);
		scene.getTopLayer().addEntity(buttonLevel);

	}

}
