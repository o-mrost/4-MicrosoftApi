package bingImageSearchApi;

import java.util.ArrayList;

public class RootBing {

	private ArrayList<Urls> value;
	
	public RootBing(){
		setValue(new ArrayList<Urls>());
	}

	public ArrayList<Urls> getValue() {
		return value;
	}

	public void setValue(ArrayList<Urls> value) {
		this.value = value;
	}
	
}
