package waw.skinChanger;

public class SkinPixel {

    public int a;
    public int r;
    public int g;
    public int b;

    public  SkinPixel(int r, int g, int b, int a){
        this.r = r & 0xff;
        this.g = g & 0xff;
        this.b = b & 0xff;
        this.a = a & 0xff;
    }

}
