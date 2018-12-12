package nightmare.world.gen.structure;

import com.google.common.collect.Maps;
import java.util.Map;
import nightmare.nbt.NBTTagCompound;
import nightmare.world.World;

public class MapGenStructureIO
{
    private static Map < String, Class <? extends StructureStart >> startNameToClassMap = Maps. < String, Class <? extends StructureStart >> newHashMap();
    private static Map < Class <? extends StructureStart > , String > startClassToNameMap = Maps. < Class <? extends StructureStart > , String > newHashMap();
    private static Map < String, Class <? extends StructureComponent >> componentNameToClassMap = Maps. < String, Class <? extends StructureComponent >> newHashMap();
    private static Map < Class <? extends StructureComponent > , String > componentClassToNameMap = Maps. < Class <? extends StructureComponent > , String > newHashMap();

    public static void registerStructure(Class <? extends StructureStart > startClass, String structureName)
    {
        startNameToClassMap.put(structureName, startClass);
        startClassToNameMap.put(startClass, structureName);
    }

    public static void registerStructureComponent(Class <? extends StructureComponent > componentClass, String componentName)
    {
        componentNameToClassMap.put(componentName, componentClass);
        componentClassToNameMap.put(componentClass, componentName);
    }

    public static String getStructureStartName(StructureStart start)
    {
        return (String)startClassToNameMap.get(start.getClass());
    }

    public static String getStructureComponentName(StructureComponent component)
    {
        return (String)componentClassToNameMap.get(component.getClass());
    }

    public static StructureStart getStructureStart(NBTTagCompound tagCompound, World worldIn)
    {
        StructureStart structurestart = null;

        try
        {
            Class <? extends StructureStart > oclass = (Class)startNameToClassMap.get(tagCompound.getString("id"));

            if (oclass != null)
            {
                structurestart = (StructureStart)oclass.newInstance();
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }

        if (structurestart != null)
        {
            structurestart.readStructureComponentsFromNBT(worldIn, tagCompound);
        }
        else
        {
            System.out.println("Error");
        }

        return structurestart;
    }

    public static StructureComponent getStructureComponent(NBTTagCompound tagCompound, World worldIn)
    {
        StructureComponent structurecomponent = null;

        try
        {
            Class <? extends StructureComponent > oclass = (Class)componentNameToClassMap.get(tagCompound.getString("id"));

            if (oclass != null)
            {
                structurecomponent = (StructureComponent)oclass.newInstance();
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }

        if (structurecomponent != null)
        {
            structurecomponent.readStructureBaseNBT(worldIn, tagCompound);
        }
        else
        {
            System.out.print("Error");
        }

        return structurecomponent;
    }

    static
    {
        //registerStructure(StructureMineshaftStart.class, "Mineshaft");
        //registerStructure(MapGenVillage.Start.class, "Village");
        //registerStructure(MapGenNetherBridge.Start.class, "Fortress");
        //registerStructure(MapGenStronghold.Start.class, "Stronghold");
        //registerStructure(MapGenScatteredFeature.Start.class, "Temple");
        //registerStructure(StructureOceanMonument.StartMonument.class, "Monument");
        //StructureMineshaftPieces.registerStructurePieces();
        //StructureVillagePieces.registerVillagePieces();
        //StructureNetherBridgePieces.registerNetherFortressPieces();
        //StructureStrongholdPieces.registerStrongholdPieces();
        //ComponentScatteredFeaturePieces.registerScatteredFeaturePieces();
        //StructureOceanMonumentPieces.registerOceanMonumentPieces();
    }
}