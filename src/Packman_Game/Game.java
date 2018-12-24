package Packman_Game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.ArrayList;


import Geom.Point3D;

public class Game {
	/**
	 * This class represents game- list of packmans fruits ghosts and boxs.
	 * Game can be saved on scv file and can be read from csv file.
	 * @author Bar Genish
	 * @author Elyashiv Deri
	 */
	private ArrayList<Fruit> Fruitarr=new ArrayList<>();
	private ArrayList<Packman>Packmanarr=new ArrayList<>();
	private ArrayList<Ghost>Ghostarr=new ArrayList<>();
	private ArrayList<Box>Boxarr=new ArrayList<>();
	public String GameName;//for the test.
	public Game(ArrayList<Packman>arr,ArrayList<Fruit> array,ArrayList<Ghost>ghostarray,ArrayList<Box>boxarr) {//constracors
		setFruitArr(array);
		setBoxarr(boxarr);
		setGhostarr(ghostarray);
		setPackmanArr(arr);
	}
	public Game() {
		Fruitarr=new ArrayList<>();
		Packmanarr=new ArrayList<>();
		Boxarr=new ArrayList<>();
		Ghostarr=new ArrayList<>();
	}
	public Game(Game g) {
		Fruitarr=g.getFruitArr();
		Packmanarr=g.getPackmanArr();
		Boxarr=g.getBoxarr();
		Ghostarr=g.getGhostarr();
	}
	public ArrayList<Packman> getPackmanArr() {//getters and setters
		return Packmanarr;
	}
	public void setPackmanArr(ArrayList<Packman> packarr) {
		this.Packmanarr = packarr;
	}
	public ArrayList<Fruit> getFruitArr() {
		return Fruitarr;
	}
	public void setFruitArr(ArrayList<Fruit> fruitarray) {
		this.Fruitarr = fruitarray;
	}
	public ArrayList<Ghost> getGhostarr() {
		return Ghostarr;
	}
	public void setGhostarr(ArrayList<Ghost> ghostarr) {
		Ghostarr = ghostarr;
	}
	public ArrayList<Box> getBoxarr() {
		return Boxarr;
	}
	public void setBoxarr(ArrayList<Box> boxarr) {
		Boxarr = boxarr;
	}
	/**
	 * write the Game as string(helps to save the game in csv file).
	 * @return string of the Game.
	 */
	public String toString() {
		String s="Type,ID,Lat,Lon,Alt,Speed/Weight,Radius,"+Packmanarr.size()+","+Fruitarr.size()+Boxarr.size()+"\n";
		for(int i=0;i<Packmanarr.size();i++) {
			s+="P,"+Packmanarr.get(i).toString()+"\n";
		}
		for(int i=0;i<Ghostarr.size();i++) {
			s+="G,"+Ghostarr.get(i).toString()+"\n";
		}
		for(int i=0;i<Fruitarr.size();i++) {
			s+="F,"+Fruitarr.get(i).toString()+"\n";
		}
		for(int i=0;i<Boxarr.size();i++) {
			s+="B,"+Boxarr.get(i).toString()+"\n";
		}
		return s;
	}
	/**
	 * This function make a new game from the csv that we got. 
	 * @param CsvFile the path of csv file that we want to read from him.
	 * @return Game g with all data from the csv.
	 */
	public Game load(String CsvFile) 
	{
		String line = "";
		String cvsSplitBy = ",";
		Game g=new Game();
		try (BufferedReader br = new BufferedReader(new FileReader(CsvFile))) 
		{
			line=br.readLine();
			while ((line = br.readLine()) != null) 
			{
				String[] userInfo = line.split(cvsSplitBy);
				if(userInfo[0].equals("P")) 
					g.Packmanarr.add(new Packman(Integer.parseInt(userInfo[1]), new Point3D(userInfo[2]+","+userInfo[3]+","+userInfo[4]),Double.parseDouble(userInfo[5]),Double.parseDouble(userInfo[6])));
				else if(userInfo[0].equals("B"))
					g.Boxarr.add(new Box(Integer.parseInt(userInfo[1]),new Point3D(userInfo[2]+","+userInfo[3]+","+userInfo[4]),new Point3D(userInfo[5]+","+userInfo[6]+","+userInfo[7])));
				else if(userInfo[0].equals("F"))
					g.Fruitarr.add(new Fruit(Integer.parseInt(userInfo[1]), new Point3D(userInfo[2]+","+userInfo[3]+","+userInfo[4]),Double.parseDouble(userInfo[5])));
				else if(userInfo[0].equals("G"))
					g.Ghostarr.add(new Ghost(Integer.parseInt(userInfo[1]),new Point3D(userInfo[2]+","+userInfo[3]+","+userInfo[4]),Double.parseDouble(userInfo[5]),Double.parseDouble(userInfo[6])));
			}

		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		return g;
	}
	/**
	 * This function make a new csv file with all the game details. 
	 * @param g the Game that we want to saved his details on csv.
	 */
	public void save(Game g) {
		LocalTime s=LocalTime.now();
		String time=s.toString().replaceAll(":", ".");
		String fileName="game"+time+".csv";
		GameName=fileName;
		String newfilepath="data\\"+fileName;
		PrintWriter pw=null;
		try 
		{
			pw = new PrintWriter(new File(newfilepath));
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			return;
		}
		pw.write(g.toString());
		pw.close();
		System.out.println("saved: "+newfilepath);
	}
}