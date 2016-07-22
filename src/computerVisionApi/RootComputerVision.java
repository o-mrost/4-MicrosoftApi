package computerVisionApi;

public class RootComputerVision {

	private FullDescription description;
	
	public RootComputerVision(){
		setDescription(new FullDescription());
	}

	public FullDescription getDescription() {
		return description;
	}

	public void setDescription(FullDescription description) {
		this.description = description;
	}
	
}
