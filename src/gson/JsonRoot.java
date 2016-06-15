package gson;

public class JsonRoot {

	private DescriptionText description;
	
	public JsonRoot(){
		setDescription(new DescriptionText());
	}

	public DescriptionText getDescription() {
		return description;
	}

	public void setDescription(DescriptionText description) {
		this.description = description;
	}
	
}
