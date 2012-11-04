package se.z_app.stb.api;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;


import se.z_app.stb.STB;




public class STBContainer extends Observable implements Iterable<STB>{
	private STB stb;
	private LinkedList<STB> stbs = new LinkedList<STB>();
	
	
	private static class SingletonHolder { 
        public static final STBContainer INSTANCE = new STBContainer();
	}
	
	
	public static STBContainer instance(){
		return SingletonHolder.INSTANCE;	
	}
	private STBContainer(){
	}
	
	//This i overridden due to the problem with instances initiation at different times
	@Override
	public void addObserver(Observer observer) {
		super.addObserver(observer);
		if(getActiveSTB() != null){
			observer.update(this, null);
		}
	}
	
	public STB getActiveSTB(){
		return stb;
	}
	
	public void setActiveSTB(STB stb){
		if(this.stb != null && this.stb.equals(stb)) return;
		
		this.stb = stb;
		setChanged();
		notifyObservers();
	}
	
	public boolean isActiveSTB(STB stb){
		return this.stb.equals(stb);
	}
	
	public boolean addSTB(STB stb){
		if(!stbs.contains(stb)){
			stbs.add(stb);
			return true;
		}
		return false;
	}
	public boolean removeSTB(STB stb){
		return stbs.remove(stb);
	}
	
	public boolean containsSTB(STB stb){
		return stbs.contains(stb);
	}
	@Override
	public Iterator<STB> iterator() {
		return stbs.iterator();
	}
	
	public STB[] getSTBs(){
		STB stbToBeReturned[] = new STB[stbs.size()];
		stbs.toArray(stbToBeReturned);
		return stbToBeReturned;
	}
	
	public void reset(){
		stbs = new LinkedList<STB>();
		stb = null;
		hasChanged();
		notifyObservers();
	}
	
	
	
}
