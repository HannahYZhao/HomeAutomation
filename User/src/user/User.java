package user;

import java.util.ArrayList;

import android.content.Context;

public class User {
	private String name; 
	private ArrayList<Item> wishlist;
	private TasteManager mytaste;
	private Context con;
	
	public User(String name, Context con){
		this.name = name;
                this.con = con;
		wishlist = new ArrayList<Item>();
		mytaste = new TasteManager(con);
	}
	
	public String getName(){
		return name;
	}

        public Context getContext(){
		return con;
	}

	public ArrayList<Item> getWishlist(){
		return wishlist;
	}
	
	public void addToWishlist(Item item){
		wishlist.add(item);
	}
	
	public void removeFromWishlist(Item item){
		wishlist.remove(item);
	}
      
	public TasteManager getTasteManager(){
		return mytaste;
	}

        public Item getItemFromWishlist(int i){
		return wishlist.get(i);
	}
	
}