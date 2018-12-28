package waw.itemImage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import waw.Main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;


public class ItemImageLoader {

    public static String URL_APPLE = "https://d1u5p3l4wpay3k.cloudfront.net/minecraft_gamepedia/7/7d/Apple.png";

    private static HashMap<Integer, BufferedImage> ImageMap = new HashMap<>();

    private static HashMap<Integer, HashMap<Integer, String>> ItemImagePathMap = new HashMap<>();

    public static BufferedImage getImage(int id, int data){
        System.out.println(id);
        if(ItemImagePathMap.containsKey(id)){
            try {
                BufferedImage img = ImageIO.read(new File(Main.dataPath + "files/" + ItemImagePathMap.get(id).get(data) + ".png"));
                return img;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void downloadImage(int id,String path) {

        try {

            URL url = new URL(path);

            HttpURLConnection conn =
                    (HttpURLConnection) url.openConnection();
            conn.setAllowUserInteraction(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("GET");
            conn.connect();

            int httpStatusCode = conn.getResponseCode();

            if(httpStatusCode != HttpURLConnection.HTTP_OK){
                throw new Exception();
            }

            // Input Stream
            DataInputStream dataInStream
                    = new DataInputStream(
                    conn.getInputStream());

            BufferedImage  image = ImageIO.read(dataInStream);

            ImageMap.put(id, image);

            dataInStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void loadItemImagePath(){

        ObjectMapper mapper = new ObjectMapper();

        HashMap<String, Integer> itemIdList = new HashMap<>();
        try {
            JsonNode items = mapper.readTree(new File(Main.dataPath + "files/items.json"));
            System.out.println(items.asText() + items.size());
            for (int i = 0; i < items.size(); i++) {
                int id  = items.get(i).get("type").asInt();
                String name  = items.get(i).get("text_type").asText();
                itemIdList.put(name, id);
            }

            System.out.println(itemIdList.toString());

            JsonNode root = mapper.readTree(new File(Main.dataPath + "files/textures/item_texture.json"));
            JsonNode texture_data = root.get("texture_data");
            Iterator<String> it = texture_data.fieldNames();
            while(it.hasNext()) {
                String itemName = it.next();
                JsonNode texture = texture_data.get(itemName).get("textures");
                HashMap<Integer, String> map = new HashMap<>();
                if(texture.size() > 1){
                    for(int i = 0; i < texture.size(); i++){
                        String path = texture.get(i).asText();
                        map.put(i,path);
                    }
                }else{
                    map.put(0,texture.asText());
                }

                if(itemIdList.containsKey(itemName)){
                    int id = itemIdList.get(itemName);
                    ItemImagePathMap.put(id, map);
                }else{
                    System.out.println("Cannnot find Item name is " + itemName);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

      //  System.out.println(ItemImagePathMap.toString());
    }


}
