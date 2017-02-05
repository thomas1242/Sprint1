package com.team7.objects;

import com.team7.objects.areaEffects.ElixirShower;
import com.team7.objects.items.Obstacle;
import com.team7.objects.resource.HieroglyphicBooks;
import com.team7.objects.resource.MoneyBag;
import com.team7.objects.resource.MoonRocks;
import com.team7.objects.unit.Unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created in the controller
 */
public class Navigator {
    Map map;
    Unit selectedUnit;
    Queue<Tile> tilePath;
    int x=0;
    int y=0;

    ArrayList<Unit> selectedUnits;
    int unitsLeft;
    int[] healthOfAllUnits = new int[25];
    int healthIndex = 0;

    int health;
    int movement;
    int collectedResearch = 0;
    int collectedMoney = 0;
    int collectedConstruction = 0;

    //when MOVE mode is executed
    public Navigator(Map map, Unit selectedUnit){
        this.map = map;
        this.selectedUnit = selectedUnit;
        selectedUnits = new ArrayList<>();
        selectedUnits.add(selectedUnit);
        x = selectedUnit.getLocation().getxCoordinate();
        y = selectedUnit.getLocation().getyCoordinate();
        tilePath = new LinkedList<>();
        tilePath.add(selectedUnit.getLocation());
        movement = selectedUnit.getUnitStats().getMovement();

        Arrays.fill(healthOfAllUnits, 25);
        unitsLeft = selectedUnits.size();
        healthOfAllUnits[0] = selectedUnit.getUnitStats().getHealth();
    }

    public Navigator(Map map, Army army){
        this.map = map;
        selectedUnits = army.getUnits();
        x = army.getRallyPoint().getxCoordinate();
        y = army.getRallyPoint().getyCoordinate();
        tilePath = new LinkedList<>();
        tilePath.add(army.getRallyPoint());
        movement = army.getSlowestSpeed();

        unitsLeft = selectedUnits.size();

        Arrays.fill(healthOfAllUnits, 25);
        for(int i = 0; i < selectedUnits.size() - 1; i++){
            healthOfAllUnits[i] = selectedUnits.get(i).getUnitStats().getHealth();
        }
    }



    //when arrow key is pressed
    public boolean parseInputCommand(char command){
        int tmpX = x;
        int tmpY = y;
        switch (command){
            case '1':       //SW
                tmpY++;
                tmpX--;
                break;
            case '2':       //S
                tmpY++;
                break;
            case '3':       //SE
                tmpY++;
                tmpX++;
                break;
            case '4':       //W
                tmpX--;
                break;
            //case "5": center
            //    break;
            case '6':       //E
                tmpX++;
                break;
            case '7':       //NW
                tmpY--;
                tmpX--;
                break;
            case '8':       //N
                tmpY--;
                break;
            case '9':       //NE
                tmpY--;
                tmpX++;
                break;
        }

//        for(Unit u : selectedUnits){
//            health = healthOfAllUnits[healthIndex];
//            healthIndex++;

            //TODO check if unit is frozen
            if (isInBounds(tmpX, tmpY)){ //first ensure Tile is in Bounds
                if (isTilePassable(map.getTile(tmpX, tmpY))){//second ensure Tile is passable by current Unit
                    if (hasMovementLeft()){ //third ensure that a unit can still move
//                         if(isUnitAlive() && hasUnitRemaining())
//                        The above statement is not working properly
                        if (!isUnitAlive()) { //ensure unit is alive to affect stats and navigator has unit
                            System.out.println("Added to queue");
                            calculateNetEffectByTile(map.getTile(tmpX, tmpY));
                            tilePath.add(map.getTile(tmpX, tmpY)); //only add to tilePath if Unit survived the way
                        }
                        x = tmpX;
                        y = tmpY;
                        return true;
                    }
                }
            }
//        }

        return false;
    }

    //when ENTER is pressed
    public void updateModel(){
        System.out.println("src coordinate X: "+selectedUnit.getLocation().getxCoordinate());
        System.out.println("src coordinate Y: "+selectedUnit.getLocation().getyCoordinate());

        for(Unit u : selectedUnits){
            System.out.println("current unit: " + u);
            System.out.println("command size: " + tilePath.size());
            selectedUnit = u;

            if (tilePath.peek() != null){
                tilePath.peek().removeUnitFromTile(selectedUnit); //remove unit from starting point
                //Previously the loop was running for less no. of times because it was checking the tilePath.size()
                // which is changing on every iteration because of deletion
                int t=tilePath.size();
                for(int i = 0; i < t - 1; i++){
//                    System.out.println("current tile in path: " + tilePath.size());
                    tilePath.remove().clearTile();    //remove all tiles in path EXCEPT the last one
                    //last element in tilePath is the unit destination
                }

                //now only one Tile left in tilePath, the last one (destination)
                tilePath.peek().addUnitToTile(selectedUnit);        //add Unit to final tile
                selectedUnit.setLocation(tilePath.peek());          //add tile location to unit

                if (selectedUnit.getArmy() != null){
                    selectedUnit.getArmy().setRallyPoint(tilePath.peek());
                }

                tilePath.remove().clearTile();                      //clear tile of resources
            }

            //update stats
            selectedUnit.getUnitStats().setHealth(this.health);

        }

        System.out.println("target coordinate X: "+selectedUnit.getLocation().getxCoordinate());
        System.out.println("target coordinate Y: "+selectedUnit.getLocation().getyCoordinate());

        selectedUnit.getOwner().setMoney(selectedUnit.getOwner().getMoney() + this.collectedMoney);
        selectedUnit.getOwner().setConstruction(selectedUnit.getOwner().getConstruction() + this.collectedConstruction);
        selectedUnit.getOwner().setResearch(selectedUnit.getOwner().getResearch() + this.collectedResearch);

    }


    private boolean hasUnitRemaining(){
        if (!isUnitAlive()){    //if unit is dead, decrement units left
            unitsLeft--;
        }
        return unitsLeft > 0;
    }
    private boolean isUnitAlive(){
        return health > 0;
    }
    //calculate all tile effects
    //TODO test instant death
    private void calculateNetEffectByTile(Tile currentTile) {

        //AreaEffect
        if (currentTile.getAreaEffect() != null){
            if(currentTile.getAreaEffect().isInstantDeath()){
                health = 0; //instant death will set health to 0
                return;
            }
           health += currentTile.getAreaEffect().getHealthEffect();
        }

        if (isUnitAlive()) { //stat collection only occurs if unit is alive
            //Resource
            if (currentTile.getResource() != null){
                if (currentTile.getResource() instanceof MoneyBag){
                    //increase Money stat
                    collectedMoney += currentTile.getResource().getStatInfluence();
                }
                if (currentTile.getResource() instanceof HieroglyphicBooks){
                    //increase Research stat
                    collectedResearch += currentTile.getResource().getStatInfluence();
                }
                if (currentTile.getResource() instanceof MoonRocks){
                    //increase Construction stat
                    collectedConstruction += collectedConstruction;
                }
            }

            //Item (OneShotItem by default)
            if (currentTile.getItem() != null){
                collectedMoney += currentTile.getItem().getStatInfluence();
            }
        }
    }


    private boolean isInBounds(int x, int y) {
        if (x <= 19 && x >= 0 && y <= 19 && y >= 0) {
            return true;
        }
        return false;
    }

    private boolean isTilePassable(Tile tmp) {
        //check if impassible
        if (!tmp.getTerrain().isPassable()){
            return false;
        }
        //check if obstacle
        if (tmp.getItem() != null){
            if (tmp.getItem() instanceof Obstacle){
                return false;
            }
        }
        return true;
    }

    private boolean hasMovementLeft() {

        return true;
    }
}
