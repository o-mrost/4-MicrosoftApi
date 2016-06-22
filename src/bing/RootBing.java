package bing;

import java.util.ArrayList;

public class RootBing {

	private ArrayList<Data> value;
	
	public RootBing(){
		setValue(new ArrayList<Data>());
	}

	public ArrayList<Data> getValue() {
		return value;
	}

	public void setValue(ArrayList<Data> value) {
		this.value = value;
	}
	
}
