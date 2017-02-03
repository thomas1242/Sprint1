package com.team7.view;

import com.team7.Main;
import com.team7.objects.*;
import com.team7.objects.areaEffects.ElixirShower;
import com.team7.objects.areaEffects.Storm;
import com.team7.objects.resource.HieroglyphicBooks;
import com.team7.objects.resource.MoneyBag;
import com.team7.objects.resource.MoonRocks;
import com.team7.objects.terrain.Crater;
import com.team7.objects.terrain.Desert;
import com.team7.objects.terrain.FlatLand;
import com.team7.objects.terrain.Mountains;
import com.team7.objects.unit.Unit;
import com.team7.objects.unit.nonCombatUnit.Colonist;
import com.team7.objects.unit.nonCombatUnit.Explorer;

import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainViewImage extends JPanel implements MouseListener {

        public static BufferedImage image;
        private Graphics2D g2d;
        private int MAP_IMAGE_WIDTH_IN_PIXELS;
        private int MAP_IMAGE_HEIGHT_IN_PIXELS;

        private final static int TILE_SIZE = 67;   // in pixels

        private final static int TILES_VISIBLE_X = 11;
        private final static int TILES_VISIBLE_Y = 7;

        //
        public int x_center, y_center;    // where the window is focused on
        public int x_dest, y_dest;        // where the window should be focused on
        //
        private BufferedImage tileImage_1;
        private BufferedImage tileImage_2;
        private BufferedImage tileImage_3;
        private BufferedImage tileImage_4;

        private BufferedImage moneyBagImage;
        private BufferedImage moonRockImage;
        private BufferedImage hieroglyphicBookImage;

        private BufferedImage elixerShowerImage;
        private BufferedImage stormImage;

        private  BufferedImage colonistImage;
        private  BufferedImage explorerImage;
            //
        private BufferedImage mapImage;
        private MainViewSelection mainViewSelection;

        // -------
        private Map map;
        private Tile[][] grid;

        public MainViewImage( MainViewSelection ms )
        {
            MAP_IMAGE_WIDTH_IN_PIXELS = 733;
            MAP_IMAGE_HEIGHT_IN_PIXELS = 469;

            image = new BufferedImage(MAP_IMAGE_WIDTH_IN_PIXELS, MAP_IMAGE_HEIGHT_IN_PIXELS, BufferedImage.TYPE_INT_ARGB);
            g2d = (Graphics2D)image.createGraphics();
            addMouseListener(this);

            this.mainViewSelection = ms;

            // load tile images
            try {
               tileImage_1 = ImageIO.read(new File(String.valueOf(Main.class.getClass().getResource("/resources/terrains/mountain_img.png")).replace("file:","")));
               tileImage_2 = ImageIO.read(new File(String.valueOf(Main.class.getClass().getResource("/resources/terrains/hills_img.png")).replace("file:","")));
               tileImage_3 = ImageIO.read(new File(String.valueOf(Main.class.getClass().getResource("/resources/terrains/sand_img.jpg")).replace("file:","")));
               tileImage_4 = ImageIO.read(new File(String.valueOf(Main.class.getClass().getResource("/resources/terrains/grass_img.jpg")).replace("file:","")));

               moneyBagImage = ImageIO.read(new File(String.valueOf(Main.class.getClass().getResource("/resources/items/moneyBag.png")).replace("file:","")));
               moonRockImage = ImageIO.read(new File(String.valueOf(Main.class.getClass().getResource("/resources/items/moonRock.png")).replace("file:","")));
               hieroglyphicBookImage = ImageIO.read(new File(String.valueOf(Main.class.getClass().getResource("/resources/items/hieroglyphicBook.png")).replace("file:","")));

               elixerShowerImage = ImageIO.read(new File(String.valueOf(Main.class.getClass().getResource("/resources/areaEffects/elixirShowerImage.png")).replace("file:","")));
               stormImage = ImageIO.read(new File(String.valueOf(Main.class.getClass().getResource("/resources/areaEffects/stormImage.png")).replace("file:","")));
               colonistImage = ImageIO.read(new File(String.valueOf(Main.class.getClass().getResource("/resources/units/colonistImage.png")).replace("file:","")));
               explorerImage = ImageIO.read(new File(String.valueOf(Main.class.getClass().getResource("/resources/units/explorerImage.png")).replace("file:","")));

                // moonRockImage.getScaledInstance(30, 30, Image.SCALE_DEFAULT);
                // hieroglyphicBookImage.getScaledInstance(30, 30, Image.SCALE_DEFAULT);

                // mapImage = new BufferedImage(1340 , 1340, BufferedImage.TYPE_INT_ARGB);
                // Graphics2D g2 = (Graphics2D)mapImage.createGraphics();
            }
            catch (IOException e) {}

            drawMapArea();

            x_center = 0;   // initially focus on top left corner of the map
            y_center = 0;
            x_dest = 0;
            y_dest = 0;
        }

        public void setMap(Map map) {
            this.map = map;
            this.grid = map.getGrid();
        }

        private BufferedImage drawSubsectionOfMap(int x, int y) {


            BufferedImage tempImg = new BufferedImage(733, 439, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2ds = (Graphics2D)tempImg.createGraphics();
            g2ds.setFont(g2ds.getFont().deriveFont(30f));

            int x_coord, y_coord;   // pixel coordinates of top left corner of image drawn

            for(int i = 0; i < TILES_VISIBLE_X; i++) {          // tile index on sub-screen
                for(int j = 0; j < TILES_VISIBLE_Y; j++) {

                    int xx = x + i;                // tile index on whole map
                    int yy = y + j;

                    if(xx < 0)                     // adjust if out of bounds
                        xx = 0;
                    else if (xx > 19)
                        xx = 19;

                    if(yy < 0)
                        yy = 0;
                    else if(yy > 19)
                        yy = 19;

                    x_coord = i * TILE_SIZE;
                    y_coord = j * TILE_SIZE;

                    // draw terrain
                    if( grid[xx][yy].getTerrain() instanceof Mountains) {
                        g2ds.drawImage(tileImage_1, x_coord, y_coord, null);
                    }
                    else if (grid[xx][yy].getTerrain() instanceof Crater) {
                        g2ds.drawImage(tileImage_2, x_coord, y_coord, null);
                    }
                    else if (grid[xx][yy].getTerrain() instanceof Desert) {
                        g2ds.drawImage(tileImage_3, x_coord, y_coord, null);
                    }
                    else if (grid[xx][yy].getTerrain() instanceof FlatLand) {
                        g2ds.drawImage(tileImage_4, x_coord, y_coord, null);
                    }

                    // draw area effects
                    if(grid[xx][yy].getAreaEffect() instanceof Storm) {
                        g2ds.drawImage(stormImage, x_coord, y_coord, null);
                    }
                    else if (grid[xx][yy].getAreaEffect() instanceof ElixirShower) {
                        g2ds.drawImage(elixerShowerImage, x_coord + 10, y_coord + 10, null);
                    }

                    // draw resources
                    if( grid[xx][yy].getResource() instanceof MoonRocks) {
                        g2ds.drawImage(moonRockImage, x_coord, y_coord + 35, null);
                    }
                    else if ( grid[xx][yy].getResource() instanceof MoneyBag) {
                        g2ds.drawImage(moneyBagImage, x_coord + 35, y_coord, null);
                    }
                    else if ( grid[xx][yy].getResource() instanceof HieroglyphicBooks) {
                        g2ds.drawImage(hieroglyphicBookImage, x_coord + 30, y_coord + 35, null);
                    }

                    // draw units
                    int colonistCount = 0, explorerCount = 0;
                    ArrayList<Unit> units = grid[xx][yy].getUnits();
                    if( !units.isEmpty() ) {    // if there are units on this tile
                        for(int n = 0; n < units.size(); n++) {
                            if( units.get(n) instanceof Colonist)
                                colonistCount++;
                            if( units.get(n) instanceof Explorer)
                                explorerCount++;
                        }
                        System.out.println("tile[" + xx + "][" + yy + "] has " + colonistCount + " colonist and " + explorerCount + " explorer(s)");
                        if(colonistCount != 0) {
                            g2ds.drawImage(colonistImage, x_coord, y_coord, null);
                            g2ds.drawString( Integer.toString( colonistCount ), x_coord, y_coord + 45);
                        }
                        if(explorerCount != 0) {
                            g2ds.drawImage(explorerImage, x_coord, j*67, null);
                            g2ds.drawString( Integer.toString( explorerCount ), x_coord, y_coord + 45);
                        }
                    }

                }
            }

            int center_pixel_x = (TILES_VISIBLE_X/2)*TILE_SIZE  + TILE_SIZE/2;
            int center_pixel_y = (TILES_VISIBLE_Y/2)*TILE_SIZE  + TILE_SIZE/2;

             g2ds.setColor(Color.BLACK);
             g2ds.drawLine(center_pixel_x - 5, center_pixel_y, center_pixel_x + 5, center_pixel_y );
             g2ds.drawLine(center_pixel_x , center_pixel_y - 5, center_pixel_x, center_pixel_y + 5 );

            return tempImg;
        }

        public void paintComponent( Graphics g )
        {
            super.paintComponent( g );
            g.drawImage( image, 33, 0, this );
        }

    public void drawMapArea() {
        g2d.setColor( new Color(210, 210, 230) );
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        repaint();
    }

    public void drawMap() {
        image = drawSubsectionOfMap(TILES_VISIBLE_X/2 - TILES_VISIBLE_X/2, TILES_VISIBLE_Y/2 - TILES_VISIBLE_Y/2);
        repaint();
    }

    private int[] getFocus() {
        int[] center = {x_center, y_center};
        return center;
    }


        public BufferedImage getCurrImage() {
            return image;
        }

        // implement MouseListener interface methods:
        public void mousePressed(MouseEvent e) {}

        public void mouseReleased(MouseEvent e) {}

        public void mouseEntered(MouseEvent e) {}

        public void mouseExited(MouseEvent e) {}

        public void mouseClicked(MouseEvent e) {

           double x_offset = (e.getX() - 733/2 - 33)/(double)TILE_SIZE;   // offset in number of tiles
           double y_offset = -1*(469/2 - e.getY())/(double)TILE_SIZE;

           if(x_offset % 1 > 0.5) {
            x_offset += 1;
           }
           else if(x_offset % 1 < -0.5) {
            x_offset += -1;
           }

           if(y_offset % 1 > 0.5) {
            y_offset += 1;
           }
           else if(y_offset % 1 < -0.5) {
            y_offset += -1;
           }

           // System.out.println("go towards (" + (int)x_offset + ", " + (int)y_offset + ")" );

           x_dest = x_center + (int)x_offset;
           y_dest = y_center + (int)y_offset;

            if(x_dest < 0)              // adjust if out of bounds
                x_dest = 0;
            else if (x_dest >= 20 - TILES_VISIBLE_X)
                x_dest = 20 - TILES_VISIBLE_X;

            if(y_dest < 0)
                y_dest = 0;
            else if(y_dest >= 20 - TILES_VISIBLE_Y)
                y_dest = 20 - TILES_VISIBLE_Y;

            mainViewSelection.setFocus( x_dest , y_dest );
            if( x_center != x_dest || y_center != y_dest) {

                    new Thread( new Runnable()
                    {
                        public void run()
                        {
                            int x_diff = (x_dest - x_center);
                            int y_diff = (y_dest - y_center);

                            int delta_x = 0, delta_y = 0;

                            if(x_diff != 0) {
                                delta_x = ((x_dest - x_center) > 0) ? 1 : -1;
                            }
                            if(y_diff != 0) {
                                delta_y = ((y_dest - y_center) > 0) ? 1 : -1;
                            }

                            while (x_diff != 0 || y_diff != 0) {    // while view isnt focused on destination tile

                                if(x_diff != 0) {                   // move focus 1 unit towards destination
                                    x_center += delta_x;
                                    x_diff -= delta_x;
                                }
                                if(y_diff != 0) {
                                    y_center += delta_y;
                                    y_diff -= delta_y;
                                }

//                                if(x_center < 0)              // adjust if out of bounds
//                                    x_center = 0;
//                                else if (x_center >= 20 - TILES_VISIBLE_X - 1)
//                                    x_center = 20 - TILES_VISIBLE_X/2 - 1;
//
//                                if(y_center < 0)
//                                    y_center = 0;
//                                else if(y_center >= 20 - TILES_VISIBLE_Y/2 - 1)
//                                    y_center = 20 - TILES_VISIBLE_Y/2 - 1;

                                System.out.println("get frame focus at (" + (int)x_center + ", " + (int)y_center + ")" );

                                final BufferedImage mapSubsection = drawSubsectionOfMap(x_center, y_center);
                                SwingUtilities.invokeLater( new Runnable()   // queue frame i on EDT for display
                                {
                                    public void run()
                                    {
                                        image = mapSubsection;
                                        repaint();
                                    }
                                });

                                try{
                                    Thread.sleep(50);
                                }
                                catch(Exception e) {}

                            }   // end of while loop

                        }
                    } ).start();

            }
        }

    class AnimateMapThread extends Thread {
        Map map;
        AnimateMapThread(Map map) {
            this.map = map;
        }

        public void run() {
            System.out.println("wow");
        }
    }
}
