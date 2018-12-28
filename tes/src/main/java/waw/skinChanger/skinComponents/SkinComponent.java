package waw.skinChanger.skinComponents;

import waw.skinChanger.SkinPixel;

import java.util.ArrayList;

public class SkinComponent {

    private ArrayList<SkinPixel> pixels = new ArrayList<>();

    private Geometry geometry = null;

    protected  int skinWidht = 0;
    protected  int skinHeight = 0;

    protected int xOffset = 0;
    protected int yOffset = 0;

    protected  String geometryComponentName = "";

    protected boolean hashSkin = true;


}
