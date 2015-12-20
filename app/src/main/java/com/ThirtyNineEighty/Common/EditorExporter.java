package com.ThirtyNineEighty.Common;

import android.os.Environment;

import com.ThirtyNineEighty.Common.Math.Vector3;
import com.ThirtyNineEighty.Game.Objects.Descriptions.Description;
import com.ThirtyNineEighty.Game.Objects.Land;
import com.ThirtyNineEighty.Game.Objects.WorldObject;
import com.ThirtyNineEighty.Game.Worlds.IWorld;
import com.ThirtyNineEighty.System.GameContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EditorExporter
{
  private static final String dirName = "/TanksMaps";
  private static final String separator = "::";

  public static void exportMap(IWorld world) throws IOException
  {
    ArrayList<WorldObject<?, ?>> objects = new ArrayList<>();

    world.getObjects(objects);

    File externalStorage = Environment.getExternalStorageDirectory();
    File storage = new File(externalStorage + dirName);

    if (!storage.exists())
    {
      boolean created = storage.mkdirs();
      if (!created)
        throw new IOException("Can't create TanksMaps directory");
    }

    File outputFile = File.createTempFile("map", ".txt", storage);
    FileOutputStream stream = new FileOutputStream(outputFile);
    OutputStreamWriter writer = new OutputStreamWriter(stream);

    for (WorldObject<?, ?> object : objects)
    {
      Description description = object.getDescription();
      String descriptionStr = description.getName();
      Vector3 position = object.getPosition();
      Vector3 angles = object.getAngles();

      // Skip land
      if (Land.sand.equals(descriptionStr))
        continue;

      writer.append(descriptionStr).append(separator);
      writer.append(position.toString()).append(separator);
      writer.append(angles.toString()).append(separator);

      writer.append('\n');
    }

    writer.close();
  }

  public static void importMap(IWorld world, String name) throws IOException
  {
    File externalStorage = Environment.getExternalStorageDirectory();
    File storage = new File(externalStorage + dirName);

    if (!storage.exists())
      throw new IOException("TanksDirectory not found");

    File inputFile = new File(storage, name);

    FileInputStream stream = new FileInputStream(inputFile);
    InputStreamReader reader = new InputStreamReader(stream);

    BufferedReader bufferedReader = new BufferedReader(reader);

    while (true)
    {
      String objectStr = bufferedReader.readLine();
      if (objectStr == null)
        break;

      String[] objectPartsStr = objectStr.split(separator);

      String descriptionStr = objectPartsStr[0];
      String positionStr = objectPartsStr[1];
      String anglesStr = objectPartsStr[2];

      WorldObject<?, ?> object = GameContext.factory.createObject(descriptionStr);
      Vector3 position = new Vector3(positionStr);
      Vector3 angles = new Vector3(anglesStr);

      object.setPosition(position);
      object.setAngles(angles);

      world.add(object);
    }
  }

  public static List<String> getMaps()
  {
    File externalStorage = Environment.getExternalStorageDirectory();
    File storage = new File(externalStorage + dirName);

    if (!storage.exists())
      return null;

    ArrayList<String> result = new ArrayList<>();
    Collections.addAll(result, storage.list());
    return result;
  }
}
