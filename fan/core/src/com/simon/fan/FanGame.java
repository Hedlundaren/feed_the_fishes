package com.simon.fan;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class FanGame extends ApplicationAdapter {

    public Data data = new Data();
    Texture calm_fish_texture;
    Texture angry_fish_texture;
    SpriteBatch batch;

    Texture godray_texture;

    Texture background_texture;
    SpriteBatch background_batch;
    Sprite background;
    Sprite ball;
    Sprite godray;

    Texture overlay_texture;
    SpriteBatch overlay_batch;
    Sprite overlay;


    Vector2 position;
	Vector2 velocity;

	float deltaTime;
	float time;
    Ball[] All_balls = new Ball[data.no_balls];
    Godray[] All_godrays = new Godray[data.no_godrays];

    MyInputProcessor inputProcessor;
    OrthographicCamera camera;
    ShapeRenderer shapeRenderer;

    Random r;
    //int blood_counter = 0;


    @Override
	public void create () {
        r = new Random();

        System.out.println("Game created");
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera(data.screen_width, data.screen_height);
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();
        inputProcessor = new MyInputProcessor();
        Gdx.input.setInputProcessor(inputProcessor);

        background_batch = new SpriteBatch();
        background_texture = new Texture("fishtank_background.png");
        background = new Sprite(background_texture);

        overlay_batch = new SpriteBatch();
        overlay_texture = new Texture("overlay.png");
        overlay = new Sprite(overlay_texture);


        calm_fish_texture = new Texture("fish_calm.png");
        angry_fish_texture = new Texture("fish_angry.png");
        ball = new Sprite(calm_fish_texture);

        godray_texture = new Texture("godray.png");
        godray = new Sprite(godray_texture);

        deltaTime = 1.7f;

        for(int i = 0; i < data.no_godrays; i++) {
            Godray godray = new Godray();
            godray.batch = new SpriteBatch();
            All_godrays[i] = godray;
        }

        for(int i = 0; i < data.no_balls; i++){
			Random r = new Random();



            Ball ball = new Ball();
			batch = new SpriteBatch();

			position = new Vector2(r.nextInt(data.screen_width), r.nextInt(data.screen_height));
			velocity = new Vector2(r.nextFloat()*500.0f, r.nextFloat()*500.0f);

			time = 0.0f;

			ball.batch = batch;
			ball.velocity = velocity;
			ball.position = position;
			All_balls[i] = ball;

		}

	}

	@Override
	public void render () {
		time += 0.1;
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        //Background image
        background_batch.begin();
        background.setPosition(0,0);
        background.setScale(1,1);
        background.setAlpha(0.9f);
        background.draw(background_batch);
        background_batch.end();

        for(int i = 0; i < data.no_godrays; i++){
            All_godrays[i].batch.begin();
            godray.setPosition((float) ((-500.0f + i)*Math.sin(0.05*time + i) + (data.screen_width/data.no_godrays/10)*i - 150), data.screen_height);
            godray.setScale((float) (15 + i*0.1),15);
            godray.setAlpha(0.5f);
            godray.setRotation((float) (3.0f*Math.sin(time*0.07 +0.02*i)));
            godray.draw(All_godrays[i].batch);
            All_godrays[i].batch.end();
        }

        float degree_rotation;
		for(int i = 0; i < data.no_balls; i++){

            float size_factor = (float) (1.0f + i*0.05);

            All_balls[i].velocity.x *= 0.97f;
            All_balls[i].velocity.y *= 0.97f;
			All_balls[i].position.x += 0.1f * All_balls[i].velocity.x * Gdx.graphics.getDeltaTime();
			All_balls[i].position.y += 0.1f * All_balls[i].velocity.y * Gdx.graphics.getDeltaTime();

            All_balls[i].batch.setProjectionMatrix(camera.combined);
            // Check boundaries
            float friction = 0.6f;
			if(All_balls[i].position.x < 0.0f ){
                All_balls[i].velocity.x *= -1.0f * friction;
                All_balls[i].position.x = 0.0f;
			}
            if(All_balls[i].position.x > data.screen_width - calm_fish_texture.getWidth()){
                All_balls[i].velocity.x *= -1.0f * friction;
                All_balls[i].position.x = data.screen_width - calm_fish_texture.getWidth();
            }
            if(All_balls[i].position.y < 0.0f){
                All_balls[i].velocity.y *= -1.0f * friction;
                All_balls[i].position.y = 0.0f;
            }
            if(All_balls[i].position.y > data.screen_height - calm_fish_texture.getWidth()){
                All_balls[i].velocity.y *= -1.0f * friction;
                All_balls[i].position.y = data.screen_height - calm_fish_texture.getWidth();
            }

            // Interaction
            if(inputProcessor.screenTouched){
                Vector2 direction = new Vector2(inputProcessor.finger.x - All_balls[i].position.x, inputProcessor.finger.y - All_balls[i].position.y);
                float distance = (float) Math.sqrt( Math.pow(direction.x, 2.0f) + Math.pow(direction.y, 2.0f));
                if(distance > 40.0f){
                    All_balls[i].velocity.x += Gdx.graphics.getDeltaTime() * 30000000.0f * direction.x / (distance * distance);
                    All_balls[i].velocity.y += Gdx.graphics.getDeltaTime() * 30000000.0f * direction.y / (distance * distance);
                }
                degree_rotation = (float) -(Math.atan2(direction.x, direction.y)*180.0f/Math.PI) + 90.0f;
                ball.setTexture(angry_fish_texture);

                // Create Lines
//                Gdx.gl.glLineWidth(5);
//                shapeRenderer.setProjectionMatrix(camera.combined);
//                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//                shapeRenderer.setColor(0.9f, 0.9f, 0.9f, 0.3f);
//                shapeRenderer.line(new Vector2(All_balls[i].position.x + All_balls[i].img.getWidth()/2.0f, All_balls[i].position.y + All_balls[i].img.getHeight()/2.0f), inputProcessor.finger);
//                shapeRenderer.end();

                if(All_balls[i].velocity.x > 0)
                    ball.setScale(data.ball_size*size_factor, data.ball_size*size_factor);
                else
                    ball.setScale(data.ball_size*size_factor, -data.ball_size*size_factor);
                
            }else{
                degree_rotation = 0.0f;
                ball.setTexture(calm_fish_texture);

                if(All_balls[i].velocity.x > 0)
                    ball.setScale(data.ball_size*size_factor, data.ball_size*size_factor);
                else
                    ball.setScale(-data.ball_size*size_factor, data.ball_size*size_factor);
            }

            //Draw
			All_balls[i].batch.begin();
			ball.setPosition( (float) (All_balls[i].position.x + (3.0f + i*0.05)*Math.sin(time*0.6 + i*0.05)), (float) (All_balls[i].position.y + (5.0f + i*0.1)*Math.sin(time*0.9 + i)));


            ball.setRotation(degree_rotation);
			ball.draw(All_balls[i].batch);
			All_balls[i].batch.end();
		}
        
        overlay_batch.begin();
        overlay.draw(overlay_batch);
        overlay_batch.end();

	}

	@Override
	public void dispose () {
		for(int i = 0; i < data.no_balls; i++){
			All_balls[i].batch.dispose();
            calm_fish_texture.dispose();
		}
	}

}



