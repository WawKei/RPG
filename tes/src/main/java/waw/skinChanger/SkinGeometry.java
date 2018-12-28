package waw.skinChanger;

public class SkinGeometry {

    private int textureWidth = 64;
    private int textureHeight = 64;

    private String META_Modelversion = "1.0.6";

    private String rigtype = "normal";

    private boolean animetionArmsDown = false;

    private boolean animationArmsOutFront = false;

    private boolean animationStatueOfLibertyArms = false;

    private boolean animationSingleArmAnimation = false;

    private boolean animationStationaryLegs = false;

    private boolean animatinoSingleLegAnimation = false;

    private boolean animationNoHeadBob = false;

    private boolean animationDontShowArmor = false;

    private boolean animationUpSideDown = false;

    private boolean animationInvertedCrouch = false;


    public SkinGeometry(){

    }

    public int getTextureWidth(){
        return  this.textureWidth;
    }

    public void setTextureWidth(int textureWidth){
        this.textureWidth = textureWidth;
    }

    public int getTextureHeight(){
        return  this.textureHeight;
    }

    public void setTextureHeight(int textureHeight){
        this.textureHeight = textureHeight;
    }

    public String getMETA_Modelversion(){
        return this.META_Modelversion;
    }

    public void setMETA_Modelversion(String METAModelversion){
        this.META_Modelversion = METAModelversion;
    }

}
