package com.simon.fan;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Simon on 2016-08-11.
 */


public class MyInputProcessor implements InputProcessor {

    Vector2 finger = new Vector2(0.0f, 0.0f);
    boolean screenTouched = false;

    public boolean keyDown(int keycode) {
        return false;
    }

    public boolean keyUp(int keycode) {
        return false;
    }

    public boolean keyTyped(char character) {
        return false;
    }

    public boolean touchDown(int x, int y, int pointer, int button) {
        finger.x = x;
        finger.y = 1920 - y;
        screenTouched = true;

        return false;
    }

    public boolean touchUp(int x, int y, int pointer, int button) {
        finger.x = x;
        finger.y = 1920 - y;
        screenTouched = false;

        return false;
    }

    public boolean touchDragged(int x, int y, int pointer) {
        finger.x = x;
        finger.y = 1920 - y;

        return false;
    }

    public boolean mouseMoved (int x, int y) {
        return false;
    }

    public boolean scrolled (int amount) {
        return false;
    }
}