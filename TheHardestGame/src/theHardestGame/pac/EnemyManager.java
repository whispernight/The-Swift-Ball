package theHardestGame.pac;

import java.util.Random;

import org.anddev.andengine.entity.shape.modifier.LoopShapeModifier;
import org.anddev.andengine.entity.shape.modifier.PathModifier;
import org.anddev.andengine.entity.shape.modifier.RotationModifier;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.Path;

public class EnemyManager extends Sprite  {

	Random randomGenerator;
	public EnemyManager(float pX, float pY, float pWidth, float pHeight,
			TextureRegion pTextureRegion) {
		super(pX, pY, pWidth, pHeight, pTextureRegion);
		// TODO Auto-generated constructor stub
		randomGenerator = new Random( 19580427 );
	}
	
	public void addPath(Path path, int speed){
		this.addShapeModifier(new LoopShapeModifier(new RotationModifier(1,0,360)));
		this.addShapeModifier(new LoopShapeModifier(new PathModifier(speed, path)));
	}

}
