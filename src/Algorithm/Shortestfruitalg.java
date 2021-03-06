package Algorithm;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import Coords.LatLonAlt;
import Geom.Circle;
import Geom.Path;
import Geom.Point3D;
import Packman_Game.Box;
import Packman_Game.Fruit;
import Packman_Game.Game;
import Packman_Game.Ghost;
import Packman_Game.Map;
import Packman_Game.Player;
import Robot.Packman;
import graph.Graph;
import graph.Graph_Algo;
import graph.Node;
/**
 * This class is the main algorithem class, it calculates the path and return the cloest fruit. 
 */
public class Shortestfruitalg {
	private Game game;
	private Game tempgame;
	private int width;
	private int hight;
	private double eps = 10;
	Map map=new Map();

	public void setGame(Game g) {
		this.game=g;
	}
	/**
	 * Regular constractor.
	 * @param g Game
	 * @param w width
	 * @param h hight
	 */
	public Shortestfruitalg(Game g,int w,int h) {
		game = new Game(g);
		width=w;
		hight=h;
		tempgame=new Game(game);
	}
	/**
	 * Helps func that convert all the points to pixels and move the boxs in eps.
	 */
	private void initforpixels() {
		Point3D p;
		Point3D p1;
		for(int i=0;i<tempgame.getBoxarr().size();i++) {
			p=map.CoordsToPixel(tempgame.getBoxarr().get(i).getLeftDown(), width, hight);
			p1=map.CoordsToPixel(tempgame.getBoxarr().get(i).getRightUp(), width, hight);
			p.set_x(p.x()-eps);
			p.set_y(p.y()+eps);
			p1.set_x(p1.x()+eps);
			tempgame.getBoxarr().get(i).setLeftDown(p);
			tempgame.getBoxarr().get(i).setRightUp(p1);
		}
		for(int i=0;i<tempgame.getFruitArr().size();i++) {
			tempgame.getFruitArr().get(i).setPos(map.CoordsToPixel(tempgame.getFruitArr().get(i).getPos(), width, hight));
		}
		for(int i=0;i<tempgame.getGhostarr().size();i++) {
			tempgame.getGhostarr().get(i).setPos(map.CoordsToPixel(tempgame.getGhostarr().get(i).getPos(),width,hight));
		}
		for(int i=0;i<tempgame.getPackmanArr().size();i++) {
			tempgame.getPackmanArr().get(i).setPos(map.CoordsToPixel(tempgame.getPackmanArr().get(i).getPos(), width, hight));
		}
		tempgame.getPlayer().setPos(map.CoordsToPixel(tempgame.getPlayer().getPos(), width, hight));
	}
	/**
	 * Help func that calculaet time to the specifi fruit.
	 * @param p the player
	 * @param f the fruit
	 * @return double the time it will take to eat the fruit.
	 */
	private double Calculatetime(Player p, Fruit f) {
		Circle c = new Circle(p.getPos(), p.getRadius());
		Map m = new Map();
		double dist = m.distance3d(c.get_cen(), f.getPos()) - c.get_radius();
		if (dist <= 0)
			return 0;
		return dist / p.getSpeed();
	}
	/**
	 * Help func that calculate time till the player eat the fruit with obstacle.
	 * @param p the player
	 * @param f the fruit
	 * @return the time till the player will eat the fruit.
	 */
	private double Calculatetimewithbox(Player p, Fruit f) {
		Circle c = new Circle(p.getPos(), p.getRadius());
		Map map = new Map();
		double dist = 0;
		Path path = (calcpath(f, game));
		dist = path.GetDist();
		if (!path.getPoints().isEmpty())
			dist += map.distance3d(c.get_cen(), path.getPoints().get(0));
		return dist;
	}
	/**
	 * The main func that choose which algo to use and return the cloest fruit.
	 * @param g game
	 * @return fruit the closest fruit.
	 */
	public Fruit shortpathalgo(Game g) {
		game=g;
		if (game.getBoxarr().isEmpty())
			return algowithoutboxes();
		return algowithoutboxes();
	}
	/**
	 * Func that calculate the shortest fruit without obstacle.
	 * @return Fruit - the cloest fruit.
	 */
	private Fruit algowithoutboxes() {
		double min = Double.MAX_VALUE;
		double tmp = 0;
		Fruit fruittemp = game.getFruitArr().get(0);
		for (int i = 0; i < game.getFruitArr().size(); i++) {
			tmp = Calculatetime(game.getPlayer(), game.getFruitArr().get(i));
			if (tmp < min) {
				min = tmp;
				fruittemp = game.getFruitArr().get(i);
			}
		}
		return fruittemp;
	}
	/**
	 * Func that calculate the cloest fruit with obstacle.
	 * @return the cloest fruit.
	 */
	private Fruit algowithboxs() {
		double min = Double.MAX_VALUE;
		double tmp = 0;
		Fruit fruittemp = game.getFruitArr().get(0);
		for (int i = 0; i < game.getFruitArr().size(); i++) {
			tmp = Calculatetimewithbox(game.getPlayer(), game.getFruitArr().get(i));
			if (tmp < min) {
				min = tmp;
				fruittemp = game.getFruitArr().get(i);
			}
		}
		return fruittemp;
	}
	/**
	 * Helps func that check if the point is in obstacle. 
	 * @param point point that we want to check.
	 * @return boolean if yes or no.
	 */
	private boolean isIn(Point3D point) {
		boolean ans = true;
		double x = point.x();
		double y = point.y();
		for (Box box : game.getBoxarr()) {
			if (x > box.getRightUp().x() || x < box.getLeftDown().x() || y > box.getLeftDown().y()
					|| y < box.getRightUp().y()) {
				ans = false;
			}
		}
		return ans;
	}
	/**
	 * Help func that calc the path to the fruit with obstacle.
	 * @param fruit that we want to check the player path to him.
	 * @param game our game.
	 * @return path the path to the fruit.
	 */
	private Path calcpath(Fruit fruit, Game game) {
		Path p = new Path();
		if(LineofSight(game.getPlayer().getPos(), fruit.getPos())==true) {
			p.getPoints().add(fruit.getPos());
			return p;
		}
		initforpixels();
		ArrayList<Point3D> Points = new ArrayList<>();
		Points.add(tempgame.getPlayer().getPos());
		Graph graph = new Graph();
		graph.add(new Node("source"));
		graph.add(new Node("target"));
		int count=0;
		for (Box box : tempgame.getBoxarr()) {
			Point3D leftdown = box.getLeftDown();
			Point3D rightup = box.getRightUp();
			Point3D rightdown = new Point3D(rightup.ix(), leftdown.iy());
			Point3D leftup = new Point3D(leftdown.ix(), rightup.iy());
			graph.add(new Node(""+count++));
			graph.add(new Node(""+count++));
			graph.add(new Node(""+count++));
			graph.add(new Node(""+count++));
			Points.add(leftdown);
			Points.add(rightup);
			Points.add(rightdown);
			Points.add(leftup);
		}
		for (int i = 1; i < graph.size()-1; i++) {
			if(LineofSight(Points.get(0),Points.get(i))==true)
				graph.addEdge("source", ""+i, Points.get(0).distance2D(Points.get(i)));
			System.out.println("source >>  " + i );
		}
		for(int i=1;i<graph.size()-1;i++) {
			for(int j=i+1;j<graph.size()-1;j++) {
				if(LineofSight(Points.get(i), Points.get(j))==true)
					graph.addEdge(""+i,""+j,Points.get(i).distance2D(Points.get(j)));
				System.out.println( i+ "   >>  " + j );
			}
		}
		for(int i=0;i<tempgame.getBoxarr().size();i++) {
			Point3D leftdown = tempgame.getBoxarr().get(i).getLeftDown();
			Point3D rightup = tempgame.getBoxarr().get(i).getRightUp();
			Point3D rightdown = new Point3D(rightup.ix(), leftdown.iy());
			Point3D leftup = new Point3D(leftdown.ix(), rightup.iy());
			if(LineofSight(leftdown, fruit.getPos())==true) 
				graph.addEdge(""+i+1, "target", leftdown.distance2D(fruit.getPos()));
			if(LineofSight(rightup, fruit.getPos())==true) 
				graph.addEdge(""+i+1, "target", rightup.distance2D(fruit.getPos()));
			if(LineofSight(rightdown, fruit.getPos())==true) 
				graph.addEdge(""+i+1, "target", rightdown.distance2D(fruit.getPos()));
			if(LineofSight(leftup, fruit.getPos())==true) 
				graph.addEdge(""+i+1, "target", leftup.distance2D(fruit.getPos()));
		}
		Graph_Algo.dijkstra(graph,"source");
		return p;
	}
	/**
	 * Func that escape from ghost.
	 * @param p the player
	 * @param f the fruit that we want to eat
	 * @return the angle that we want to for escape ghosts.
	 */
	public double escapefroomguest(Player p, Fruit f) {
		Map map = new Map();
		for (int i = 0; i < game.getGhostarr().size(); i++) {
			if (map.distance3d(p.getPos(), game.getGhostarr().get(i).getPos()) < 10)
				if (map.azimuth_elevation_dist(p.getPos(), f.getPos())[0] == map.azimuth_elevation_dist(p.getPos(),
						game.getGhostarr().get(i).getPos())[0]) {
					return searchangle(p, f, game.getGhostarr().get(i));
				}
		}
		return -1;
	}
	/**
	 * Help func that search the best angle to escape the ghost.
	 * @param p our player.
	 * @param f the fruit that we want to eat.
	 * @param g our game.
	 * @return the angle that we want to escape to.
	 */
	private double searchangle(Player p, Fruit f, Ghost g) {
		double Pangle = p.getAzimuth();
		double angle = Pangle;
		for (int i = 1; i <= 6; i++) {
			angle += 30 * i;
			if (move(f, angle) == true)
				return angle;
			angle = Pangle - (30 * i);
			if (move(f, angle) == true)
				return angle;
		}
		return -1;
	}
	/**
	 * Help func that check if the angle that we want to go to her is good or not,
	 * @param p our packman
	 * @param f the fruit we want to eat,
	 * @return boolean if it good or not.
	 */
	private boolean escapeposcheck(Packman p, Fruit f) {
		Point3D tmp = p.getLocation();
		if (isIn(tmp) == true)
			return false;
		for (int i = 0; i < game.getGhostarr().size(); i++) {
			Point3D temp = p.getLocation();
			if (temp.equals(game.getGhostarr().get(i).getPos()))
				return false;
		}
		return true;
	}
	/**
	 * Help func that move the player and than check if his next pos is good enogth or not.
	 * @param f the fruit.
	 * @param angle the angle that we want to escape to.
	 * @return bollean if its good or not.
	 */
	private boolean move(Fruit f, double angle) {
		Packman p = new Packman(new LatLonAlt(game.getPlayer().getPos().x(), game.getPlayer().getPos().y(), 0),
				game.getPlayer().getSpeed());
		p.setOrientation(angle);
		p.move(100.0D);
		if (escapeposcheck(p, f) == false)
			return false;
		return true;
	}
	/**
	 * Help func for play alone that go autumaticlly to the fruit if ur 10 meters from him.
	 * @return the angle that we want to go to.
	 */
	public double Go2Fruit() {
		Map map = new Map();
		for (int i = 0; i < game.getFruitArr().size(); i++) {
			if (map.distance3d(game.getPlayer().getPos(), game.getFruitArr().get(i).getPos()) < 10)
				return map.azimuth_elevation_dist(game.getPlayer().getPos(), game.getFruitArr().get(i).getPos())[0];
		}
		return -1;
	}
	/**
	 * Helps func that check if thier is box between our player and fruit.
	 * @param rect1 the box.
	 * @param line2 the line between the player and the fruit.
	 * @return if there is box or not.
	 */
	private boolean isColliding(Rectangle2D rect1, Line2D line2) {
		if (line2 != null) {
			return line2.intersects(rect1);
		}
		return false;
	}
	/**
	 * Func that cheack if there is obstacle between 2 points.
	 * @param point1 the first point to check.
	 * @param point2 the second point to check.
	 * @return if there is obstacle or not.
	 */
	public boolean LineofSight(Point3D point1, Point3D point2) {
		Line2D line = new Line2D.Double(point1.x(), point1.y(), point2.x(), point2.y());
		ArrayList<Box> boxs = new ArrayList<>();
		for (int i = 0; i < game.getBoxarr().size(); i++) {
			boxs.add(new Box(game.getBoxarr().get(i)));
			double minx = Math.min(boxs.get(i).getLeftDown().x(), boxs.get(i).getRightUp().x());
			double miny = Math.min(boxs.get(i).getLeftDown().y(), boxs.get(i).getRightUp().y());
			double xwidth = Math.abs(boxs.get(i).getLeftDown().x() - boxs.get(i).getRightUp().x());
			double yhight = Math.abs(boxs.get(i).getLeftDown().y() - boxs.get(i).getRightUp().y());
			Rectangle2D r = new Rectangle2D.Double(minx, miny, xwidth, yhight);
			if (isColliding(r, line) == true)
				return false;
		}
		return true;
	}
}
