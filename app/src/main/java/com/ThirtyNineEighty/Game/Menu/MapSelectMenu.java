package com.ThirtyNineEighty.Game.Menu;

import com.ThirtyNineEighty.Game.Map.Descriptions.MapDescription;
import com.ThirtyNineEighty.Game.Menu.Controls.Button;
import com.ThirtyNineEighty.Game.Worlds.GameStartArgs;
import com.ThirtyNineEighty.Game.Worlds.TankSelectWorld;
import com.ThirtyNineEighty.Renderable.GL.GLLabel;
import com.ThirtyNineEighty.Resources.MeshMode;
import com.ThirtyNineEighty.Resources.Sources.FileContentSource;
import com.ThirtyNineEighty.Resources.Sources.FileMapDescriptionSource;
import com.ThirtyNineEighty.System.GameContext;

public class MapSelectMenu
  extends BaseMenu
{
  private final GameStartArgs args;
  private final Selector mapSelector;

  private GLLabel mapName;
  private GLLabel closed;

  public MapSelectMenu(final GameStartArgs args)
  {
    this.args = args;
    this.mapSelector = new Selector(FileContentSource.maps, new Selector.Callback()
    {
      @Override
      public void onChange(String current)
      {
        args.setMapName(current);
        mapName.setValue(current);
        closed.setVisible(!isMapOpen(current));
      }
    });
  }

  @Override
  public void initialize()
  {
    super.initialize();

    Button prevMapButton = new Button("Prev map");
    prevMapButton.setPosition(70, -440);
    prevMapButton.setSize(300, 200);
    prevMapButton.setClickListener(new Runnable()
    {
      @Override
      public void run()
      {
        mapSelector.Prev();
      }
    });
    add(prevMapButton);

    Button nextMapButton = new Button("Next map");
    nextMapButton.setPosition(390, -440);
    nextMapButton.setSize(300, 200);
    nextMapButton.setClickListener(new Runnable()
    {
      @Override
      public void run()
      {
        mapSelector.Next();
      }
    });
    add(nextMapButton);

    Button selectTank = new Button("Select tank");
    selectTank.setPosition(760, -440);
    selectTank.setSize(400, 200);
    selectTank.setClickListener(new Runnable()
    {
      @Override
      public void run()
      {
        if (!isMapOpen(args.getMapName()))
          return;

        GameContext.content.setMenu(new TankSelectMenu(args));
        GameContext.content.setWorld(new TankSelectWorld(args));
      }
    });
    add(selectTank);

    Button menu = new Button("Menu");
    menu.setPosition(-810, -440);
    menu.setSize(300, 200);
    menu.setClickListener(new Runnable()
    {
      @Override
      public void run()
      {
        GameContext.content.setWorld(null);
        GameContext.content.setMenu(new MainMenu());
      }
    });
    add(menu);

    mapName = new GLLabel(mapSelector.getCurrent(), GLLabel.FontSimple, 40, 60, MeshMode.Dynamic);
    bind(mapName);

    closed = new GLLabel("Closed");
    closed.setPosition(0, 100);
    closed.setVisible(!isMapOpen(mapSelector.getCurrent()));
    bind(closed);
  }

  private boolean isMapOpen(String mapName)
  {
    MapDescription mapDescription = GameContext.resources.getMap(new FileMapDescriptionSource(mapName));
    return mapDescription.openedOnStart || GameContext.gameProgress.isMapOpen(mapName);
  }
}
