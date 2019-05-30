package view;

public class Media {
	private String path;
	private String name;
	private long currentTime=(long) 0;
	public Media() {
		// TODO Auto-generated constructor stub
	}
	public Media(String path,String name)
	{
		this.path=path;
		this.name=name;
	}
	public Long getCurrentTime() {
		return currentTime;
	}
	public String getPath() {
		return path;
	}
	public String getName() {
		return name;
	}
	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setPath(String path) {
		this.path = path;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name;
	}
}
