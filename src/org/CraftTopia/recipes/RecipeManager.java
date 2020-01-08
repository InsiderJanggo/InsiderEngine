package org.CraftTopia.recipes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.CraftTopia.game.Game;

public class RecipeManager
{
	private static RecipeManager __instance;

	public static RecipeManager getInstance()
	{
		if (__instance == null)
		{
			__instance = new RecipeManager();
		}
		return __instance;
	}
	
	public void loadRecipes() throws IOException
	{
		File file = Game.getInstance().getRelativeFile(Game.FILE_BASE_APPLICATION, "res/recipes.txt");
		
		BufferedReader br =new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		
		for (String line = br.readLine(); line != null; line = br.readLine())
		{
			line = line.trim();
			if (line.isEmpty() || line.startsWith("/*") || line.startsWith("//"))
			{
				continue;
			}
			
			String[] parts = line.split(" ");
			
			String recipe = parts[0];
			String result = parts[1];
			int amount = Integer.parseInt(parts[2]);
			
			addRecipe(new Recipe(recipe, result, amount));
		}
		
		br.close();
	}

	private List<Recipe> _recipes;

	private RecipeManager()
	{
		_recipes = new ArrayList<Recipe>();
	}

	public void addRecipe(Recipe r)
	{
		_recipes.add(r);
	}

	public Recipe getRecipe(int[][] ingredients)
	{
		for (Recipe r : _recipes)
		{
			if (r.equalsRecipe(ingredients))
			{
				return r;
			}
		}
		return null;
	}
}