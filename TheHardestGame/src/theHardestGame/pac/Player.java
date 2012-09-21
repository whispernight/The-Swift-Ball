package theHardestGame.pac;

import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

public class Player extends AnimatedSprite   {
	
	LevelController levelController;
	int frameCount;

	public Player(float pX, float pY, float pTileWidth, float pTileHeight,
			TiledTextureRegion pTiledTextureRegion, LevelController nLevelController) {
		super(pX, pY, pTileWidth, pTileHeight, pTiledTextureRegion);
		// TODO Auto-generated constructor stub
		frameCount = 0;
		levelController = nLevelController;
		
		levelController.scene.registerUpdateHandler(new IUpdateHandler(){

			@Override
			public void onUpdate(float pSecondsElapsed) {
				// TODO Auto-generated method stub
				
			
			}

			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
		
		});
	
		
	}	
	

	
	private boolean onBeforePositionChanged(){
		
		//speed up
		if(frameCount < 2){
			frameCount++;
			return true;
		}
		frameCount = 0;
		
		int enemyListSize = levelController.getEnemyList().size();
		for(int i = 0; i < enemyListSize; i++)
			if(this.collidesWith(levelController.getEnemyList().get(i)))
			{
				levelController.callbackCollisionEnemy();
				return false;
			}
		
		for(int i = 0; i < levelController.getGoodsList().size(); i++)
			if(this.collidesWith(levelController.getGoodsList().get(i)))
			{
				levelController.callbackCollisionGoods(i);
				return false;
			}


		for(int i = 0; i < levelController.getEndPointList().size(); i++)
			if(this.collidesWith(levelController.getEndPointList().get(i)))
			{
				levelController.callbackCollisionWithEndPoint();
				return false;
			}
		
		
		return true;
	}

	@Override
	protected void onManagedUpdate(final float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);
		onBeforePositionChanged();
	}

}
