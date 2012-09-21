package theHardestGame.pac;

import java.util.ArrayList;

import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.util.Path;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class LevelController   {

	
	public Scene scene;
	private PhysicsWorld mPhysicsWorld;
	private GameLogicController gameLogicController;
	private SoundManager soundManager;
	
	
    public GameLogicController getGameLogicController() {
		return gameLogicController;
	}

	Player mPlayer;
	
	private ArrayList<Shape> enemyList;
	private ArrayList<Shape> goodsList;
	private ArrayList<Shape> endPointList;
	
	int levelId;
	
	boolean isGameFinished = false;
	
	protected int mCameraWidth;
	public int getCameraWidth() {
		return mCameraWidth;
	}
	public ArrayList<Shape> getEndPointList(){return endPointList;}
	public ArrayList<Shape> getGoodsList(){return goodsList;}

	public int getCameraHeight() {
		return mCameraHeight;
	}
	final FixtureDef wallFixtureDefF = PhysicsFactory.createFixtureDef(0, 0.0f, 0.0f);
	protected int mCameraHeight;
	
	public LevelController(GameLogicController gameLogicController) {
		levelId = 0;
		soundManager = new SoundManager();
		
		
		this.gameLogicController = gameLogicController;
	}
	
	public void addSoundManager(Sound s1, Sound s2){
		soundManager.SetSound(s1, s2);
	}
	
	public void setCurrentLevel(int levelId){
		this.levelId = levelId;
	}
	
	
	
	public void showSignCompleted(){
		enemyList.clear();
		Shape box = new Sprite(160, 120, 100,100, gameLogicController.mLevelCompletedRegion)
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if(GameLogicController.getInstance().checkTouchTime())
				{
					GameLogicController.getInstance().playerProfileManager.incraseUnlockedLevelNumber(GameLogicController.getInstance().levelController.levelId);
					GameLogicController.getInstance().getEngine().setScene(gameLogicController.createLevelSubmenu(gameLogicController.currentPage));
				}
				return true;
			}
		};
		scene.registerTouchArea(box);
		scene.getTopLayer().addEntity(box);
	}
	
	public void showSignUncompleted(){
		enemyList.clear();
		Shape box = new Sprite(160, 120, 100, 100, gameLogicController.mLevelUnCompletedRegion)
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if(GameLogicController.getInstance().checkTouchTime())
				{	
						//GameLogicController.getInstance().getEngine().setScene(gameLogicController.createLevelSubmenuPage1());
						gameLogicController.getEngine().setScene(gameLogicController.newGameLevelScene(levelId));
				}
				return true;
			}
		};
		scene.registerTouchArea(box);
		scene.getTopLayer().addEntity(box);
	}
	
	public void callbackCollisionWithEndPoint(){

		if(goodsList.size()==0)
		{
			if(!isGameFinished)
			{
				isGameFinished = true;
				showSignCompleted();
				soundManager.playGameOver();
			}
			
			
		}

	}
	
	public void callbackCollisionGoods(int i){
		Shape goodShape = goodsList.get(i);
		scene.getBottomLayer().removeEntity(goodShape);
		goodsList.remove(i);
	}
	
	public void callbackCollisionEnemy(){
		//animacija
		//restart 

		isGameFinished = true;
		showSignUncompleted();
		//soundManager.playGameOver();
	}
	
	public ArrayList<Shape> getEnemyList(){return enemyList;}
	
	public void createBackground(){
		
	/*	TextureRegion btr = gameLogicController.mFinishLineTextureRegion;
		
		Sprite part = new Sprite(0, 0, 128, 128, btr);
		scene.getBottomLayer().addEntity(part);
		
		part = new Sprite(0, 127, 128, 128, btr);
		scene.getBottomLayer().addEntity(part);
		
		part = new Sprite(0, 255, 128, 128, btr);
		scene.getBottomLayer().addEntity(part);
		
		part = new Sprite(127, 0, 128, 128, btr);
		scene.getBottomLayer().addEntity(part);
		
		part = new Sprite(127,127 , 128, 128, btr);
		scene.getBottomLayer().addEntity(part);
		
		part = new Sprite(127, 255, 128, 128, btr);
		scene.getBottomLayer().addEntity(part);
		
		part = new Sprite(255,0 , 128, 128, btr);
		scene.getBottomLayer().addEntity(part);
		
		part = new Sprite(255, 127, 128, 128, btr);
		scene.getBottomLayer().addEntity(part);
		
		part = new Sprite(255, 255, 128, 128, btr);
		scene.getBottomLayer().addEntity(part);
		
		part = new Sprite(382, 0, 128, 128, btr);
		scene.getBottomLayer().addEntity(part);
		
		part = new Sprite(382, 128, 128, 128, btr);
		scene.getBottomLayer().addEntity(part);
		
		part = new Sprite(380, 255, 128, 128, btr);
		scene.getBottomLayer().addEntity(part);*/
		
	}
	
	public void createPlayer(TiledTextureRegion mCircleFaceTextureRegion, int x, int y){
		//mPlayer = new Player(x, y, mCameraHeight/10,mCameraHeight/10,mCircleFaceTextureRegion, this);
		mPlayer = new Player(x, y, 30,30,mCircleFaceTextureRegion, this);
		FixtureDef FIXTURE = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		Body body;
		body = PhysicsFactory.createCircleBody(mPhysicsWorld, mPlayer, BodyType.DynamicBody, FIXTURE);
		
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mPlayer, body, true, true, false, false));
		scene.getTopLayer().addEntity(mPlayer);
		
	}
	
	
	
	public void createEndPoint(int x, int y, int sizeX, int sizeY){
		Sprite endPoint = new Sprite(x, y, sizeX, sizeY, gameLogicController.mFinishLineTextureRegion);
		scene.getBottomLayer().addEntity(endPoint);
		endPointList.add(endPoint);
	}
	
	public void loadLevel(int levelId){
		isGameFinished = false;
		this.setCurrentLevel(levelId);
		
		if(levelId == 1)
			loadLevel1();
		else if(levelId == 2)
			loadLevel2();
		else if(levelId == 3)
			loadLevel3();
		else if(levelId == 4)
			loadLevel4();
		else if(levelId == 5)
			loadLevel5();
		else if(levelId == 6)
			loadLevel6();
		else if(levelId == 7)
			loadLevel7();
		else if(levelId == 8)
			loadLevel8();
		else if(levelId == 9)
			loadLevel9();
	}
	
	public void addBoxShape(float x, float y, int height,int weight, TextureRegion textureRegion){
		Shape box = new Sprite(x, y, height, weight, textureRegion);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, box, BodyType.StaticBody, wallFixtureDefF);
		scene.getTopLayer().addEntity(box);
	}
	
	public void addEnemy(Path path, int speed){
		//final FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(3, 0.0f, 0.0f);
		EnemyManager enemy = new EnemyManager(200, 200, 30, 30, gameLogicController.enemyTextureRegion);
		//PhysicsFactory.createCircleBody(this.mPhysicsWorld, enemy, BodyType.StaticBody, fixtureDef);
		enemy.addPath(path, speed);
		scene.getTopLayer().addEntity(enemy);
		enemyList.add(enemy);
		
	}
	
	public void addGoods(float x, float y, int height,int weight, TextureRegion textureRegion){
		Sprite good = new Sprite(x, y, height, weight, textureRegion);
		scene.getBottomLayer().addEntity(good);
		goodsList.add(good);
	}
	
	
	
	public void loadLevel1(){
		endPointList = new ArrayList<Shape>();
		enemyList = new ArrayList<Shape>();
		goodsList = new ArrayList<Shape>();
		createPlayer(gameLogicController.mCircleFaceTextureRegion, 30, 100);
		createEndPoint(426,139,32,32);
		
		Path path = new Path(3).to(203, 286).to(205,2).to(203, 286);
		addEnemy(path,5);
		
		addBoxShape(2, 255, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(2, 2, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(395, 255, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(395, 1, 64, 64, gameLogicController.mBlockTextureRegion);
	
		addGoods(292, 71, 30, 30, gameLogicController.mDiamantTextureRegion);
		addGoods(43, 177, 30, 30, gameLogicController.mDiamantTextureRegion);
		addGoods(43, 144, 30, 30, gameLogicController.mDiamantTextureRegion);
		addGoods(43, 111, 30, 30, gameLogicController.mDiamantTextureRegion);
		addGoods(33, 217, 30, 30, gameLogicController.mDiamantTextureRegion);
		addGoods(33, 74, 30, 30, gameLogicController.mDiamantTextureRegion);
		
		createBackground();
	}
	
	public void loadLevel7(){
		endPointList = new ArrayList<Shape>();
		enemyList = new ArrayList<Shape>();
		goodsList = new ArrayList<Shape>();
		createPlayer(gameLogicController.mCircleFaceTextureRegion, 30, 100);
		createEndPoint(420-2, 280-2, 32, 32);
		
		Path path = new Path(3).to(98, 303).to(95, 15).to(98, 303);
		addEnemy(path,5);
		
		path = new Path(3).to(147, 76).to(167, 300).to(147, 76);
		addEnemy(path,5);
	
		path = new Path(3).to(121, 215).to(246, 67).to(121, 215);
		addEnemy(path,5);
		
		path = new Path(3).to(90, 13).to(226, 13).to(90, 13);
		addEnemy(path,5);
		
		path = new Path(3).to(206, 127).to(70, 17).to(206, 127);
		addEnemy(path,5);
	
		path = new Path(5).to(288, 215).to(150, 124).to(255, 10).to(241, 110).to(288, 215);
		addEnemy(path,5);
		
		addBoxShape(2, 2, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(2, 128, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(2, 256, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(2, 192, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(396, 64, 16, 256, gameLogicController.mBlockTextureRegion);
		
		addGoods(210, 174, 30, 30, gameLogicController.mDiamantTextureRegion);
		createBackground();
	}
	
	public void loadLevel9(){
		endPointList = new ArrayList<Shape>();
		enemyList = new ArrayList<Shape>();
		goodsList = new ArrayList<Shape>();
		createPlayer(gameLogicController.mCircleFaceTextureRegion, 2, 153);
		createEndPoint(426, 153, 32, 32);
		
		addBoxShape(96, 66, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(96, 153, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(96, 237, 32, 32, gameLogicController.mBlockTextureRegion);
		
		addBoxShape(181, 66, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(181, 153, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(181, 237, 32, 32, gameLogicController.mBlockTextureRegion);
		
		addBoxShape(270, 66, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(270, 153, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(270, 237, 32, 32, gameLogicController.mBlockTextureRegion);
		
		addBoxShape(355, 66, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(355, 153, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(355, 237, 32, 32, gameLogicController.mBlockTextureRegion);
		
		Path path = new Path(3).to(51,109).to(391,109).to(51,109);
		addEnemy(path,4);
		
		path = new Path(3).to(391,196).to(51,196).to(391,196);
		addEnemy(path,4);
		
		path = new Path(3).to(138,19).to(138,280).to(138,19);
		addEnemy(path,4);
		
		path = new Path(3).to(232,19).to(232,280).to(229,19);
		addEnemy(path,4);
		
		path = new Path(3).to(315,280).to(315,19).to(315,280);
		addEnemy(path,4);
		
		addGoods(232, 205, 30, 30, gameLogicController.mDiamantTextureRegion);
		addGoods(322, 116, 30, 30, gameLogicController.mDiamantTextureRegion);
		addGoods(321, 205, 30, 30, gameLogicController.mDiamantTextureRegion);
		addGoods(147, 205, 30, 30, gameLogicController.mDiamantTextureRegion);
		addGoods(147, 119, 30, 30, gameLogicController.mDiamantTextureRegion);
		addGoods(235, 119, 30, 30, gameLogicController.mDiamantTextureRegion);
	}

	public void loadLevel8(){
		endPointList = new ArrayList<Shape>();
		enemyList = new ArrayList<Shape>();
		goodsList = new ArrayList<Shape>();
		createPlayer(gameLogicController.mCircleFaceTextureRegion, 2, 2);
		createEndPoint(5, 288, 32, 32);
		
		Path path = new Path(3).to(103,3).to(102,125).to(103,3);
		addEnemy(path,2);
		
		path = new Path(7).to(420,7).to(300,108).to(420,209).to(300,274).to(300,108).to(420, 209).to(420,7);
		addEnemy(path,5);
		
		path = new Path(3).to(234, 200).to(234, 286).to(234, 200);
		addEnemy(path,3);
		
		path = new Path(3).to(140, 200).to(140, 286).to(140, 200);
		addEnemy(path,3);
		
		addBoxShape(1, 60, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(32, 60, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(64, 60, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(139, 60, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(171, 60, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(203, 60, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(234, 60, 32, 32, gameLogicController.mBlockTextureRegion);
		
		addBoxShape(234, 166, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(203, 166, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(172, 166, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(140, 166, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(108, 166, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(76, 166, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(45, 166, 32, 32, gameLogicController.mBlockTextureRegion);
		
		addBoxShape(45, 198, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(45, 230, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(45, 261, 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(45, 293, 32, 32, gameLogicController.mBlockTextureRegion);
		
		addGoods(102, 60, 30, 30, gameLogicController.mDiamantTextureRegion);
		addGoods(372, 64, 30, 30, gameLogicController.mDiamantTextureRegion);
		addGoods(352, 211, 30, 30, gameLogicController.mDiamantTextureRegion);
		addGoods(84, 253, 30, 30, gameLogicController.mDiamantTextureRegion);
		addGoods(195, 253, 30, 30, gameLogicController.mDiamantTextureRegion);
	}
	
	public void loadLevel2(){
		endPointList = new ArrayList<Shape>();
		goodsList = new ArrayList<Shape>();
		enemyList = new ArrayList<Shape>();
		createPlayer(gameLogicController.mCircleFaceTextureRegion, 2, 145);
		createEndPoint(425, 145, 32, 32);
		
		addBoxShape(2, 255, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(2, 191, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(2, 2, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(2, 65, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(65, 254, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(65, 2, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(395, 255, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(395, 192, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(395, 66, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(331, 254, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(331, 2, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(395, 2, 64, 64, gameLogicController.mBlockTextureRegion);
		
		Path path = new Path(3).to(289, 283).to(289, 2).to(289, 283);
		addEnemy(path,5);
		
		path = new Path(3).to(250,2).to(250, 283).to(250,2);
		addEnemy(path,5);
		
		path = new Path(3).to(163, 2).to(163, 283).to(163, 2);
		addEnemy(path,5);
		
		path = new Path(3).to(207, 283).to(206, 2).to(207, 283);
		addEnemy(path,5);
		
		path = new Path(3).to(82, 68).to(82, 220).to(82, 68);
		addEnemy(path,5);
		
		path = new Path(3).to(350, 68).to(350, 220).to(350, 68);
		addEnemy(path,5);
		createBackground();
	}

	public void createFrame(){
		
		//	205-175-149 	cdaf95
		final Shape ground = new Rectangle(0, mCameraHeight - 2, mCameraWidth, 2);
		ground.setColor(0.7f, 0.5f, 0.3f);
		final Shape roof = new Rectangle(0, 0, mCameraWidth, 2);
		roof.setColor(0.7f, 0.5f, 0.3f);
		final Shape left = new Rectangle(0, 0, 2, mCameraHeight);
		left.setColor(0.7f, 0.5f, 0.3f);
		final Shape right = new Rectangle(mCameraWidth - 2, 0, 2, mCameraHeight);
		right.setColor(0.7f, 0.5f, 0.3f);
		
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);
		
		scene.getBottomLayer().addEntity(ground);
		scene.getBottomLayer().addEntity(roof);
		scene.getBottomLayer().addEntity(left);
		scene.getBottomLayer().addEntity(right);
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public PhysicsWorld getmPhysicsWorld() {
		return mPhysicsWorld;
	}

	public void setmPhysicsWorld(PhysicsWorld mPhysicsWorld) {
		this.mPhysicsWorld = mPhysicsWorld;
	}
	
	
	public void loadLevel3(){
		endPointList = new ArrayList<Shape>();
		goodsList = new ArrayList<Shape>();
		enemyList = new ArrayList<Shape>();
		createPlayer(gameLogicController.mCircleFaceTextureRegion, 68, 220);
		createEndPoint(425, 156, 32, 32);
		//createEndPoint(425, 154, 32, 32);
		//createEndPoint(425, 122, 32, 32);
		
		addBoxShape(0, 0, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(0, 64, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(0, 128, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(0, 192, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(0, 256, 64, 64, gameLogicController.mBlockTextureRegion);
		
		addBoxShape(64, 256, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(128, 256, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(192, 256, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(256, 256, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(320, 256, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(384, 256, 64, 64, gameLogicController.mBlockTextureRegion);
		
		addBoxShape(64, 0, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(128, 0, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(192, 0, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(256, 0, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(320, 0, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(384, 0, 64, 64, gameLogicController.mBlockTextureRegion);
		
		Path path = new Path(7).to(128, 222).to(188, 72).to(258, 222).to(347, 68).to(258,222).to(188, 72).to(128,222);
		addEnemy(path,10);
		
		path = new Path(9).to(383, 222).to(312, 70).to(224, 221).to(155, 71).to(143,217).to(155,71).to(224, 221).to(312,70).to(383, 222);
		addEnemy(path,10);
		
		addGoods(161, 148, 30, 30, gameLogicController.mDiamantTextureRegion);
		addGoods(311, 148, 30, 30, gameLogicController.mDiamantTextureRegion);
		createBackground();
	}
	
	public void loadLevel4(){
		endPointList = new ArrayList<Shape>();
		goodsList = new ArrayList<Shape>();
		enemyList = new ArrayList<Shape>();
		createPlayer(gameLogicController.mCircleFaceTextureRegion, 248, 163);
		createEndPoint(2, 209, 32, 32);
		
		addBoxShape(2, 255, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(2, 1, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(2, 65, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(2, 128, 64, 64, gameLogicController.mBlockTextureRegion);
		
		Path path = new Path(6).to(122, 48).to(84, 149).to(106, 247).to(238, 261).to(241, 37).to(122,48);
		addEnemy(path,2);
		createBackground();
	}
	
	public void loadLevel6(){
		endPointList = new ArrayList<Shape>();
		goodsList = new ArrayList<Shape>();
		enemyList = new ArrayList<Shape>();
		createPlayer(gameLogicController.mCircleFaceTextureRegion, 34, 82);
		createEndPoint(379, 251, 32, 32);
		
		addBoxShape(1,1 , 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(1,65 , 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(1,97 , 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(1,129 , 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(1,193 , 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(1,257 , 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(133,1 , 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(133,64 , 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(133, 128 , 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(133, 192 , 64, 64, gameLogicController.mBlockTextureRegion);
		
		addBoxShape(274, 255 , 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(274, 192 , 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(274, 127 , 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(274, 63 , 64, 64, gameLogicController.mBlockTextureRegion);
		
		addBoxShape(427, 219 , 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(338, 218 , 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(348, 250 , 32, 32, gameLogicController.mBlockTextureRegion);
		addBoxShape(410, 251 , 32, 32, gameLogicController.mBlockTextureRegion);
		
		addGoods(82, 159, 30, 30, gameLogicController.mDiamantTextureRegion);
		addGoods(222, 156, 30, 30, gameLogicController.mDiamantTextureRegion);
		addGoods(378, 111, 30, 30, gameLogicController.mDiamantTextureRegion);
		
		
		Path path = new Path(7).to(69, 5).to(99, 141).to(67, 239).to(147, 287)
		.to(67, 239).to(99, 141).to(69, 5);
		addEnemy(path,5);
		
		path = new Path(7).to(147, 287).to(241, 206).to(198, 84).to(287, 5).to(198, 84).to(241, 206).to(147, 287);
		addEnemy(path,5);
		
		path = new Path(3).to(339, 69).to(427, 69).to(339, 69);
		addEnemy(path,2);
		
		createBackground();
	}
	
	public void loadLevel5(){
		endPointList = new ArrayList<Shape>();
		goodsList = new ArrayList<Shape>();
		enemyList = new ArrayList<Shape>();
		createPlayer(gameLogicController.mCircleFaceTextureRegion, 29, 145);
		createEndPoint(410, 220, 32, 32);
		createEndPoint(410, 84, 32, 32);
		
		addGoods(143, 200, 30, 30, gameLogicController.mDiamantTextureRegion);
		addGoods(143, 92, 30, 30, gameLogicController.mDiamantTextureRegion);
		
		addBoxShape(0, 257, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(0, 193, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(0, 0, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(0, 64, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(0, 161, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(0, 128, 64, 64, gameLogicController.mBlockTextureRegion);
		
		addBoxShape(64, 193, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(64, 257, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(64, 64, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(64, 0, 64, 64, gameLogicController.mBlockTextureRegion);
		
		addBoxShape(128, 256, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(192, 256, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(256, 256, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(320, 256, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(383, 256, 64, 64, gameLogicController.mBlockTextureRegion);
		
		addBoxShape(128, 0, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(192, 0, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(255, 0, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(319, 0, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(383, 0, 64, 64, gameLogicController.mBlockTextureRegion);
		
		addBoxShape(188, 122, 64, 64, gameLogicController.mBlockTextureRegion);
		addBoxShape(316,122, 64,64, gameLogicController.mBlockTextureRegion);
		addBoxShape(393, 122, 64, 64, gameLogicController.mBlockTextureRegion);
		
		Path path = new Path(3).to(267, 217).to(267, 68).to(267, 217);
		addEnemy(path,5);
		createBackground();
	}
	
}
