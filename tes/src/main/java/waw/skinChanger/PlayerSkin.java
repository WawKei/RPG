package waw.skinChanger;

import cn.nukkit.entity.data.Skin;
import waw.skinChanger.skinComponents.*;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerSkin {

    /**
     * 　すべてのスキンがもつボディコンポ―ネント
     */
    public static int HEAD = 0;
    public static int HAT = 1;
    public static int RIGHT_LEG = 2;
    public static int BODY = 3;
    public static int RIGHT_ARM = 4;

    /**
     * 　これらのコンポーネントは、64x64,128x128のスキンのみで使用される
     */
    public static int RIGHT_PANTS = 5;
    public static int JACKET = 6;
    public static int RIGHT_SLEEVE = 7;
    public static int LEFT_ARM = 8;
    public static int LEFT_SLEEVE = 9;
    public static int LEFT_LEG = 10;
    public static int LEFT_PANTS = 11;

    /**
     *  このコンポーネントは、スキンフィールドではなく、ジオメトリデータでしようされる。
     */
    public static int HELMET = 12;

    /**
     *  スキンコンポーネント for 64x , 128x
     */
    public static HashMap<Integer, Class<? extends SkinComponent>> COMPONENTS_LARGE_FORMAT = new HashMap<>();

    /**
     *  スキンコンポーネント for 64x32
     */
    public static HashMap<Integer, Class<? extends SkinComponent>> COMPONENTS_SMALL_FORMAT = new HashMap<>();

    static {
        COMPONENTS_LARGE_FORMAT.put(HEAD, Head.class);
        COMPONENTS_LARGE_FORMAT.put(HAT, Hat.class);
        COMPONENTS_LARGE_FORMAT.put(RIGHT_LEG, RightLeg.class);
        COMPONENTS_LARGE_FORMAT.put(BODY, Body.class);
        COMPONENTS_LARGE_FORMAT.put(RIGHT_ARM, RightArm.class);
        COMPONENTS_LARGE_FORMAT.put(RIGHT_PANTS, RightPants.class);
        COMPONENTS_LARGE_FORMAT.put(JACKET, Jacket.class);
        COMPONENTS_LARGE_FORMAT.put(RIGHT_SLEEVE, RightSleeve.class);
        COMPONENTS_LARGE_FORMAT.put(LEFT_ARM, LeftArm.class);
        COMPONENTS_LARGE_FORMAT.put(LEFT_SLEEVE, LeftSleeve.class);
        COMPONENTS_LARGE_FORMAT.put(LEFT_LEG, LeftLeg.class);
        COMPONENTS_LARGE_FORMAT.put(LEFT_PANTS, LeftPants.class);

        COMPONENTS_LARGE_FORMAT.put(HELMET, Helmet.class);


        COMPONENTS_SMALL_FORMAT.put(HEAD, Head.class);
        COMPONENTS_SMALL_FORMAT.put(HAT, Hat.class);
        COMPONENTS_SMALL_FORMAT.put(RIGHT_LEG, RightLeg.class);
        COMPONENTS_SMALL_FORMAT.put(BODY, Body.class);
        COMPONENTS_SMALL_FORMAT.put(RIGHT_ARM, RightArm.class);

        COMPONENTS_SMALL_FORMAT.put(HELMET, Helmet.class);
    }

    private ArrayList<SkinPixel> pixels = new ArrayList<>();

    private int skinWidth = 64;
    private int skinHeight = 64;

    private ArrayList<SkinComponent> components = new ArrayList<>();

    private String geometryName = "";

    private SkinGeometry geometry  = null;

    public PlayerSkin(Skin skin){
        this(skin, false);
    }

    public PlayerSkin(Skin skin, boolean ignoreSkin){
        byte[] skinData = skin.getSkinData();
        String geometryData = skin.getGeometryData();
        String geometryName = skin.getGeometryName();

        this.skinHeight = (int)Math.sqrt(skinData.length / 4);
        this.skinWidth = this.skinHeight;
        if(this.skinHeight < 64){
            this.skinHeight = 32;
            this.skinWidth = 64;
        }

        if(geometryData == null){

        }
    }
/*
    public String getGeometryData(String ){

    }
*/

}
