package Model;

public class Block {
    private final int index;
    public boolean used;
    public String data;

    // Constructor for initializing a block with an index
    public Block(int index) {
        this.index = index;
        this.used = false;
        this.data = "";
    }

    // Getter for index
    public int getIndex() { return index; }

    // Getter and setter for used status
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }

    // Setter for data
    public void setData(String data) { this.data = data; }
}
