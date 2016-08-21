package exnihiloadscensio;

import exnihiloadscensio.blocks.BlockCrucible;
import exnihiloadscensio.blocks.ENBlocks;
import exnihiloadscensio.config.Config;
import exnihiloadscensio.items.ENItems;
import exnihiloadscensio.items.ItemResource;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class Recipes {
	
	public static void init()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(ENItems.hammerWood, new Object[] { " x ", " yx", "y  ", 'x', "plankWood", 'y', "stickWood"}));
		GameRegistry.addRecipe(new ShapedOreRecipe(ENItems.hammerStone, new Object[] { " x ", " yx", "y  ", 'x', "cobblestone", 'y', "stickWood"}));
		GameRegistry.addRecipe(new ShapedOreRecipe(ENItems.hammerIron, new Object[] { " x ", " yx", "y  ", 'x', "ingotIron", 'y', "stickWood"}));
		GameRegistry.addRecipe(new ShapedOreRecipe(ENItems.hammerGold, new Object[] { " x ", " yx", "y  ", 'x', "ingotGold", 'y', "stickWood"}));
		GameRegistry.addRecipe(new ShapedOreRecipe(ENItems.hammerDiamond, new Object[] { " x ", " yx", "y  ", 'x', "gemDiamond", 'y', "stickWood"}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(ENItems.crookWood, new Object[] { "xx"," x"," x", 'x', "stickWood"}));
		GameRegistry.addRecipe(new ShapedOreRecipe(ENItems.crookBone, new Object[] { "xx"," x"," x", 'x', Items.BONE}));
		
		if (Config.enableBarrels)
			GameRegistry.addRecipe(new ShapedOreRecipe(ENBlocks.barrelWood, new Object[] {"x x","x x", "xyx", 'x', "plankWood", 'y', "slabWood"}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.COBBLESTONE, new Object[] {"xx","xx", 'x', ItemResource.getResourceStack("stones")}));
		GameRegistry.addShapelessRecipe(ItemResource.getResourceStack("porcelain_clay"), new ItemStack(Items.CLAY_BALL), new ItemStack(Items.DYE, 1, 15));
	}

}
