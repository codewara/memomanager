package Model;

public class Block {
    private final int index;
    public boolean used;
    public String data;

    public Block(int index) {
        this.index = index;
        this.used = false;
        this.data = "";
    }

    public int getIndex() { return index; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
}
