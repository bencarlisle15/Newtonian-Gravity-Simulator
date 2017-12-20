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
	private boolean isHeld = false;
	private float newPlanetMass = 1;
	private final ArrayList<Planet> planets = new ArrayList<>();
	private int mouseX;
	private int mouseY;
	private boolean stopped = false;
	private MainActivity main;
	private int state = 0;
	private int currX;
	private int currY;
	private int newColor;
	private boolean lockedOn;
	private float zoom = 1;
	private Planet lockedPlanet;
	private int xPos;
	private int yPos;
	private boolean moving = false;
	private float touchRadius = 0;
	private boolean twoPoint = false;
	private long tapTime = 0;
	private Bitmap rotate;
	private final Matrix matrix = new Matrix();
	private final Rect rect = new Rect();
	private int shorterSide;
	private Planet planet;


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
		matrix.postRotate(90);
		matrix.setScale(1.5F, 3F);
		Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.star);
		rotate = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
	}

	public void setMain(MainActivity m) {
		main = m;
	}

	public void onStop() {
		stopped = true;
		isHeld = false;
	}

	public void onResume() {
		stopped = false;
	}

	private int colorChooser(int index) {
		if (index % 5 == 0)
			return Color.GREEN;
		else if (index % 5 == 0)
			return Color.CYAN;
		else if (index % 4 == 0)
			return Color.MAGENTA;
		else if (index % 3 == 0)
			return Color.WHITE;
		else if (index % 2 == 0)
			return Color.YELLOW;
		return Color.RED;
	}

	public void onDraw(Canvas canvas) {
		if (shorterSide==0)
			shorterSide=getWidth() < getHeight() ? getWidth() : getHeight();
		g.setAntiAlias(false);
		float xModifier = 0;
		float yModifier = 0;
		if (state == 2) {
			if (lockedOn) {
				if (lockedPlanet == null)
					lockedOn = false;
				else {
					zoom = (float) (0.02 * shorterSide / lockedPlanet.getMass());
					zoom = Math.max(1, Math.min(zoom, 10));
					xModifier = lockedPlanet.getXPos() - lockedPlanet.getMass() * 30;
					yModifier = lockedPlanet.getYPos() - lockedPlanet.getMass() * 30;
					xModifier = (int) Math.max(0, Math.min(xModifier, getWidth() - getWidth() / zoom));
					yModifier = (int) Math.max(0, Math.min(yModifier, getHeight() - getHeight() / zoom));
					xPos = (int) xModifier;
					yPos = (int) yModifier;
				}
			} else {
				xModifier = xPos;
				yModifier = yPos;
			}
		}
		rect.set((int) (-xModifier * zoom), (int) (-yModifier * zoom)
				, (int) ((getWidth() - xModifier) * zoom), (int) ((getHeight() - yModifier) * zoom));
		canvas.drawBitmap(rotate, null, rect, g);
		g.setColor(Color.WHITE);
		g.setStyle(Paint.Style.STROKE);
		g.setStrokeWidth(10);
		//canvas.drawRect((float) (-xModifier * zoom), (float) (-yModifier * zoom)
		//		, (float) ((getWidth() - xModifier) * zoom), (float) ((getHeight() - yModifier) * zoom), g);
		g.setStyle(Paint.Style.FILL);
		for (int i = 0; i < planets.size(); i++) {
			Planet planet = planets.get(i);
			g.setColor(planet.getColor());
			canvas.drawCircle((int) (planet.getXPos() * zoom - xModifier * zoom), (int) (planet.getYPos() * zoom - yModifier * zoom),
					(int) (planet.getMass() * 10 * zoom), g);
			planet.setSpeed(planet.getXSpeed(), planet.getYSpeed());
		}
		if (isHeld) {
			g.setColor(newColor);
			canvas.drawCircle((int) (mouseX - newPlanetMass * 0), (int) (mouseY - newPlanetMass * 0), (int) (newPlanetMass * 10), g);
			if (state == 1) {
				g.setColor(Color.WHITE);
				g.setStrokeWidth((int) (newPlanetMass));
				g.setStrokeCap(Paint.Cap.ROUND);
				canvas.drawLine(mouseX + (mouseX - currX), mouseY + (mouseY - currY), currX, currY, g);
			}
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
			mouseX = (int) e.getX();
			mouseY = (int) e.getY();
			if (state <= 1) {
				newColor = colorChooser(planets.size());
				isHeld = true;
			}
			if (e.getPointerCount() == 2) {
				mouseX = (int) ((e.getX(0) + e.getX(1)) / 2);
				mouseY = (int) ((e.getY(0) + e.getY(1)) / 2);
				twoPoint=true;
			}
			if (lockedOn)
				lockedOn = false;
		}
		if (state == 2) {
			if (e.getPointerCount()==1&&(Math.abs(mouseX - e.getX()) > 20 || Math.abs(mouseY - e.getY()) > 20)) {
				moving = true;
				lockedOn = false;
			}
			else if (e.getPointerCount()==2&&(Math.abs(mouseX - (e.getX(0) + e.getX(1)) / 2) > 20 || Math.abs(mouseY - (e.getY(0) + e.getY(1)) / 2) > 20)){
				moving=true;
				lockedOn=false;
			}
			if (!lockedOn) {
				if (e.getPointerCount() == 2) {
					float currentTouchRadius = (float) Math.sqrt(Math.pow(e.getX(0) - e.getX(1), 2) + Math.pow(e.getY(0) - e.getY(1), 2));
					if (touchRadius!=0) {
						zoom += (currentTouchRadius - touchRadius) / 500;
						zoom = Math.max(1, Math.min(zoom, 10));
					}
					touchRadius = currentTouchRadius;
					int midX = (int) ((e.getX(0) + e.getX(1)) / 2);
					int midY = (int) ((e.getY(0) + e.getY(1)) / 2);
					xPos -= midX - mouseX;
					yPos -= midY - mouseY;
					mouseX = midX;
					mouseY = midY;
					xPos = (int) Math.max(0, Math.min(xPos, getWidth() - getWidth() / zoom));
					yPos = (int) Math.max(0, Math.min(yPos, getHeight() - getHeight() / zoom));
				} else if (!twoPoint) {
					xPos -= (int) (e.getX() - mouseX);
					yPos -= (int) (e.getY() - mouseY);
					mouseX = (int) e.getX();
					mouseY = (int) e.getY();
					xPos = (int) Math.max(0, Math.min(xPos, getWidth() - getWidth() / zoom));
					yPos = (int) Math.max(0, Math.min(yPos, getHeight() - getHeight() / zoom));
				}
			}
		}
		float tempMass;
		if (isHeld) {
			if (state == 0) {
				mouseX = (int) e.getX();
				mouseY = (int) e.getY();
				tempMass = newPlanetMass;
				if (mouseX < tempMass * 10)
					mouseX = (int) (tempMass * 10);
				else if (mouseX > getWidth() - tempMass * 10)
					mouseX = (int) (getWidth() - tempMass * 10);
				if (mouseY < tempMass * 10)
					mouseY = (int) (tempMass * 10);
				else if (mouseY > getHeight() - tempMass * 10)
					mouseY = (int) (getHeight() - tempMass * 10);
				if (tempMass * 20 >= getWidth() || tempMass * 20 >= getHeight())
					isHeld = false;
			} else if (state == 1) {
				currX = (int) e.getX();
				currY = (int) e.getY();
			}
		}
		if (e.getActionMasked() == MotionEvent.ACTION_UP) {
			if (state == 0) {
				tempMass = newPlanetMass;
				isHeld = false;
				if (mouseX - tempMass * 5 < 0 || mouseX + tempMass * 5 > getWidth() || mouseY - tempMass * 5 < 0 || mouseY + tempMass * 5 > getHeight())
					return true;
				planets.add(new Planet(mouseX, mouseY, tempMass, newColor));
			} else if (state == 1) {
				planets.add(new Planet(mouseX, mouseY, newPlanetMass, newColor));
				planets.get(planets.size() - 1).setSpeed(10 * (mouseX - e.getX()) / getHeight(),
						10 * (mouseY - e.getY()) / getHeight());
				isHeld = false;
			} else if (state == 2) {
				if (!moving) {
					float xModifier = xPos;
					float yModifier = yPos;
					if (lockedOn) {
						if (lockedPlanet == null)
							lockedOn = false;
						else {
							zoom = (float) (0.02 * shorterSide / lockedPlanet.getMass());
							zoom = Math.max(1, Math.min(zoom, 10));
							xModifier = lockedPlanet.getXPos() - lockedPlanet.getMass() * 30;
							yModifier = lockedPlanet.getYPos() - lockedPlanet.getMass() * 30;
							xModifier = (int) Math.max(0, Math.min(xModifier, getWidth() - getWidth() / zoom));
							yModifier = (int) Math.max(0, Math.min(yModifier, getHeight() - getHeight() / zoom));
						}
					}
					double radius;
					for (int i = 0; i < planets.size(); i++) {
						planet = planets.get(i);
						radius = Math.sqrt(Math.pow(e.getX() - zoom * (planet.getXPos() - xModifier), 2) +
								Math.pow(e.getY() - zoom * (planet.getYPos() - yModifier), 2));
						if (radius < planet.getMass() * 10) {
							lockedOn = true;
							xPos = (int) planet.getXPos();
							yPos = (int) planet.getYPos();
							lockedPlanet = planet;
							main.notifyMessage("Planet locked on");
							return true;
						}
					}
				}
					if (System.currentTimeMillis() - tapTime < 500 && !twoPoint&&!lockedOn) {
						zoom = 1;
						xPos = 0;
						mouseX = 0;
						yPos = 0;
						mouseY = 0;
						main.notifyMessage("Reset view");
					}
					tapTime = System.currentTimeMillis();
				if (twoPoint && e.getPointerCount() == 1) {
					touchRadius = 0;
					twoPoint = false;
				}
				moving = false;
			}
		}
		return true;
	}


	public void run() {
		//noinspection InfiniteLoopStatement
		while (true) {
			move();
			if (!stopped) {
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (isHeld)
					newPlanetMass += 0.05;
				else
					newPlanetMass = 1;
			}
		}
	}

	private void move() {
		try {
			for (int i = 0; i < planets.size(); i++) {
				planet = planets.get(i);
				if (planet.getMass() * 10 >= getWidth() || planet.getMass() >= getHeight()) {
					planets.remove(i);
					i--;
					repaint();
					main.notifyMessage("*POOF* Planet is Too Large and Gone");
					continue;
				}
				float xForce = 0;
				float yForce = 0;
				for (int t = 0; t < planets.size(); t++) {
					Planet otherPlanet = planets.get(t);
					if (planet != otherPlanet) {
						int xRadius = (int) (planet.getXPos() - otherPlanet.getXPos());
						int yRadius = (int) (planet.getYPos() - otherPlanet.getYPos());
						int radius = (int) Math.sqrt(Math.pow(xRadius, 2) + Math.pow(yRadius, 2));
						if (radius < planet.getMass() * 10 + otherPlanet.getMass() * 10) {
							float vXMomentum = (planet.getMass() * planet.getXSpeed() + otherPlanet.getMass() * otherPlanet.getXSpeed())
									/ (planet.getMass() + otherPlanet.getMass());
							float vYMomentum = (planet.getMass() * planet.getYSpeed() + otherPlanet.getMass() * otherPlanet.getYSpeed())
									/ (planet.getMass() + otherPlanet.getMass());
							planet.setSpeed(vXMomentum, vYMomentum);
							otherPlanet.setSpeed(vXMomentum, vYMomentum);
							planet.setPos(planet.getXPos() + planet.getXSpeed(), planet.getYPos() + planet.getYSpeed());
							Planet largerPlanet = planet.getMass() > otherPlanet.getMass() ? planet : otherPlanet;
							Planet smallerPlanet = largerPlanet == planet ? otherPlanet : planet;
							planets.remove(smallerPlanet);
							largerPlanet.setMass(largerPlanet.getMass() + smallerPlanet.getMass());
							while (largerPlanet.getXPos() <= largerPlanet.getMass() * 10)
								largerPlanet.setPos(largerPlanet.getXPos() + 1, largerPlanet.getYPos());
							while (largerPlanet.getXPos() >= getWidth() - largerPlanet.getMass() * 10)
								largerPlanet.setPos(largerPlanet.getXPos() - 1, largerPlanet.getYPos());
							while (largerPlanet.getYPos() <= largerPlanet.getMass() * 10)
								largerPlanet.setPos(largerPlanet.getXPos(), largerPlanet.getYPos() + 1);
							while (largerPlanet.getYPos() >= getHeight() - largerPlanet.getMass() * 10)
								largerPlanet.setPos(largerPlanet.getXPos(), largerPlanet.getYPos() - 1);
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
				if (planet.getXPos() <= planet.getMass() * 10)
					planet.setSpeed(Math.abs(planet.getXSpeed()), planet.getYSpeed());
				else if (planet.getXPos() >= getWidth() - planet.getMass() * 10)
					planet.setSpeed(-Math.abs(planet.getXSpeed()), planet.getYSpeed());
				if (planet.getYPos() <= planet.getMass() * 10)
					planet.setSpeed(planet.getXSpeed(), Math.abs(planet.getYSpeed()));
				else if (planet.getYPos() >= getHeight() - planet.getMass() * 10)
					planet.setSpeed(planet.getXSpeed(), -Math.abs(planet.getYSpeed()));
				planet.setSpeed(planet.getXSpeed() + xAccel, planet.getYSpeed() + yAccel);
				planet.setPos(planet.getXPos() + planet.getXSpeed(), planet.getYPos() + planet.getYSpeed());
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		repaint();
	}

	private void repaint() {
		postInvalidate();
	}

	void restart() {
		main.notifyMessage("*KABLAM* All Planets Imploded");
		for (int i = 0; i < planets.size(); i++) {
			planets.remove(i);
			i--;
		}
		isHeld = false;
	}

	void increase() {
		for (Planet planet : planets) {
			float newSpeed;
			if (planet.getXSpeed() > planet.getYSpeed()) {
				newSpeed = speedUp(planet.getXSpeed());
				planet.setSpeed(newSpeed, planet.getYSpeed() * newSpeed / planet.getXSpeed());
			} else {
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
		isHeld = false;
		if (state == 0) {
			state++;
			main.notifyMessage("Mode Changed to Speed Shooter");
		} else if (state == 1) {
			state++;
			main.notifyMessage("Mode changed to Camera Mode");
		} else if (state == 2) {
			state = 0;
			lockedOn = false;
			xPos = 0;
			yPos = 0;
			zoom = 1;
			moving = false;
			main.notifyMessage("Mode Changed to Drag and Drop");
		}
	}

	private float speedUp(float f) {
		if (f > 0)
			return (float) (30 / (1 + Math.pow(Math.E, -f / 4)) - 14.5);
		return (float) (-30 / (1 + Math.pow(Math.E, f / 4)) + 14.5);
	}
}
