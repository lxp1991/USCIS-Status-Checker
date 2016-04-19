
public class Case {
	
	public String reciptNumber;
	
	public String updateDate;
	
	public String status;
	
	public String content;
	
	public Case() {
		reciptNumber = "";
		content = "";
		status = "";
		updateDate = "";
	}
	
	public Case(String content) {
		this.content = content;
	}
	
	@Override public String toString() {
		String result = "Update date: " 
				+ this.updateDate
				+ "    "
				+ "Recipt Number: "
				+ this.reciptNumber
				+ "    "
				+ "Status: "
				+ this.status
				+ "    "
				+ "Content: "
				+ this.content
				+ "\n";
		return result;
	}
	
}
