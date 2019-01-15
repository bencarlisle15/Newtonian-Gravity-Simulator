package com.carlisle.ben.newtoniangravitysimulator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class DrawView extends View implements View.OnTouchListener, Runnable {
	private final Paint g = new Paint();
	private final ArrayList<Planet> planets = new ArrayList<>();
    private static int numberOfPlanets;
	private final Rect rect = new Rect();
    private MainActivity main;
    private boolean stopped = false;
    private Bitmap background;
    private Mode mode;

    public DrawView(Context context) {
		super(context);
		create();
	}

	public DrawView(Context context, AttributeSet aSet) {
		super(context, aSet);
		create();
	}

	public DrawView(Context context, AttributeSet aSet, int dStyle) {
		super(context, aSet, dStyle);
		create();
	}

	private void create() {
		setOnTouchListener(this);
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		matrix.setScale(1.5F, 3F);
		Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.star);
		background = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
		mode = new DragMode();
	}

	public void setMain(MainActivity m) {
		main = m;
	}

	public void onStop() {
		stopped = true;
	}

	public void onResume() {
		stopped = false;
	}

	public static int colorChooser() {
        numberOfPlanets++;
        switch (numberOfPlanets % 6) {
            case 0:
                return Color.GREEN;
            case 1:
                return Color.CYAN;
            case 2:
                return Color.MAGENTA;
            case 3:
                return Color.WHITE;
            case 4:
                return Color.YELLOW;
            default:
                return Color.RED;
        }
	}

	public void onDraw(Canvas canvas) {
		int shorterSide = getShorterSide();
        if (shorterSide == 0) {
		    return;
        }
        g.setAntiAlias(false);
		rect.set(mode.getBounds(getHeight(), getWidth()));
		canvas.drawBitmap(background, null, rect, g);
		g.setColor(Color.WHITE);
		g.setStyle(Paint.Style.STROKE);
		g.setStrokeWidth(10);
		g.setStyle(Paint.Style.FILL);
		Position planetPosition;
		float planetSize;
		for (int i = 0; i < planets.size(); i++) {
			Planet planet = planets.get(i);
			g.setColor(planet.getColor());
            planetPosition = mode.getPlanetPosition(planet);
            planetSize = mode.getPlanetSize(planet);
			canvas.drawCircle(planetPosition.getX(), planetPosition.getY(), planetSize, g);
		}
		mode.drawNewPlanet(canvas, getHeight(), getWidth(), g);
	}

    private int getShorterSide() {
        return getWidth() < getHeight() ? getWidth() : getHeight();
    }

    @Override
	public boolean onTouch(View v, MotionEvent e) {
        mode.onTouch(e, getHeight(), getWidth(), planets);
		return true;
	}


	public void run() {
		while (true) {
		    if (!stopped) {
                move();
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
		}
	}

	private void move() {
        for (int i = 0; i < planets.size(); i++) {
            Planet planet = planets.get(i);
            if (planet.getMass() * 10 >= getWidth() || planet.getMass() >= getHeight()) {
                planets.remove(i);
                i--;
                main.notifyMessage("*POOF* Planet is Too Large and Gone");
                continue;
            }
            float xForce = 0;
            float yForce = 0;
            for (int t = 0; t < planets.size(); t++) {
                Planet otherPlanet = planets.get(t);
                if (planet != otherPlanet) {
                    float xRadius = planet.getPosition().getX() - otherPlanet.getPosition().getX();
                    float yRadius = planet.getPosition().getY() - otherPlanet.getPosition().getY();
                    float radius = (float) Math.sqrt(Math.pow(xRadius, 2) + Math.pow(yRadius, 2));
                    if (radius < planet.getMass() * 10 + otherPlanet.getMass() * 10) {
                        float vXMomentum = (planet.getMass() * planet.getXSpeed() + otherPlanet.getMass() * otherPlanet.getXSpeed())
                                / (planet.getMass() + otherPlanet.getMass());
                        float vYMomentum = (planet.getMass() * planet.getYSpeed() + otherPlanet.getMass() * otherPlanet.getYSpeed())
                                / (planet.getMass() + otherPlanet.getMass());
                        planet.setSpeed(vXMomentum, vYMomentum);
                        otherPlanet.setSpeed(vXMomentum, vYMomentum);
                        planet.setPosition(new Position(planet.getPosition().getX() + planet.getXSpeed(), planet.getPosition().getY() + planet.getYSpeed()));
                        Planet largerPlanet = planet.getMass() > otherPlanet.getMass() ? planet : otherPlanet;
                        Planet smallerPlanet = largerPlanet == planet ? otherPlanet : planet;
                        planets.remove(smallerPlanet);
                        largerPlanet.setMass(largerPlanet.getMass() + smallerPlanet.getMass());
                        while (largerPlanet.getPosition().getX() <= largerPlanet.getMass() * 10)
                            largerPlanet.setPosition(new Position(largerPlanet.getPosition().getX() + 1, largerPlanet.getPosition().getY()));
                        while (largerPlanet.getPosition().getX() >= getWidth() - largerPlanet.getMass() * 10)
                            largerPlanet.setPosition(new Position(largerPlanet.getPosition().getX() - 1, largerPlanet.getPosition().getY()));
                        while (largerPlanet.getPosition().getY() <= largerPlanet.getMass() * 10)
                            largerPlanet.setPosition(new Position(largerPlanet.getPosition().getX(), largerPlanet.getPosition().getY() + 1));
                        while (largerPlanet.getPosition().getY() >= getHeight() - largerPlanet.getMass() * 10)
                            largerPlanet.setPosition(new Position(largerPlanet.getPosition().getX(), largerPlanet.getPosition().getY() - 1));
                        if (largerPlanet.getMass() * 20 >= getWidth() || largerPlanet.getMass() * 20 >= getHeight()) {
                            planets.remove(largerPlanet);
                            main.notifyMessage("*POOF* Planet is Too Large and Gone");
                        }
                        continue;
                    }
                    float g1 = 6.67408F;
                    float force = (float) (-g1 * planet.getMass() * otherPlanet.getMass() / Math.pow(radius, 2));
                    float theta;
                    if (xRadius != 0)
                        theta = (float) Math.atan(yRadius / xRadius);
                    else
                        theta = 90;
                    if (xRadius < 0)
                        theta += Math.PI;
                    xForce += Math.cos(theta) * force;
                    yForce += Math.sin(theta) * force;
                }
            }
            float xAccel = xForce / planet.getMass();
            float yAccel = yForce / planet.getMass();
            if (planet.getPosition().getX() <= planet.getMass() * 10)
                planet.setSpeed(Math.abs(planet.getXSpeed()), planet.getYSpeed());
            else if (planet.getPosition().getX() >= getWidth() - planet.getMass() * 10)
                planet.setSpeed(-Math.abs(planet.getXSpeed()), planet.getYSpeed());
            if (planet.getPosition().getY() <= planet.getMass() * 10)
                planet.setSpeed(planet.getXSpeed(), Math.abs(planet.getYSpeed()));
            else if (planet.getPosition().getY() >= getHeight() - planet.getMass() * 10)
                planet.setSpeed(planet.getXSpeed(), -Math.abs(planet.getYSpeed()));
            planet.setSpeed(planet.getXSpeed() + xAccel, planet.getYSpeed() + yAccel);
            planet.setPosition(new Position((planet.getPosition().getX() + planet.getXSpeed()), (planet.getPosition().getY() + planet.getYSpeed())));
        }
		repaint();
	}

	private void repaint() {
		postInvalidate();
	}

	void restart() {
		main.notifyMessage("*KABLAM* All Planets Imploded");
		for (int i = 0; i < planets.size(); i++) {
			planets.remove(i--);
		}
		mode.reset();
	}

	void increase() {
        Planet planet;
        float newSpeed;
		for (int i = 0; i < planets.size(); i++) {
		    planet = planets.get(i);
            if (Math.abs(planet.getXSpeed()) > Math.abs(planet.getYSpeed())) {
				newSpeed = speedUp(planet.getXSpeed());
				planet.setSpeed(newSpeed, planet.getYSpeed() * newSpeed / planet.getXSpeed());
			} else if (planet.getYSpeed() != 0){
				newSpeed = speedUp(planet.getYSpeed());
				planet.setSpeed(planet.getXSpeed() * newSpeed / planet.getYSpeed(), newSpeed);
			}
		}
    }

	void decrease() {
		for (Planet planet : planets)
			planet.setSpeed(planet.getXSpeed() / 2, planet.getYSpeed() / 2);
	}

	void change() {
		mode = mode.getNextMode();
		main.notifyMessage(mode.getMessage());
	}

	private float speedUp(float f) {
		if (f > 0)
			return (float) (30 / (1 + Math.pow(Math.E, -f / 4)) - 14.5);
		return (float) (-30 / (1 + Math.pow(Math.E, f / 4)) + 14.5);
	}
}
