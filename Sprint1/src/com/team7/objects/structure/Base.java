package com.team7.objects.structure;

import com.team7.objects.CommandQueue;
import com.team7.objects.Player;
import com.team7.objects.Tile;
import com.team7.objects.unit.Unit;
import com.team7.objects.unit.combatUnit.MeleeUnit;
import com.team7.objects.unit.combatUnit.RangedUnit;
import com.team7.objects.unit.nonCombatUnit.Colonist;
import com.team7.objects.unit.nonCombatUnit.Explorer;

import java.util.HashMap;

public class Base extends Structure {

    private static int ids = 1;

    public Base(Tile startTile, Player player) {

        HashMap<String, Integer> productionRateMap = new HashMap<>(); //holds the number of turns it takes for a Base to create a Unit
        productionRateMap.put("Melee", 2);
        productionRateMap.put("Ranged", 2);
        productionRateMap.put("Colonist", 5);
        productionRateMap.put("Explorer", 1);

        setId( takeId() );
        setOwner(player);
        setStats(new StructureStats(0, 0, 50, productionRateMap, 300, 8));
        setLocation(startTile);
        setCommandQueue(new CommandQueue()); //create reference to a CommandQueue
        setPowered(true); //a Base is powered upon creation
        setMovesFrozen(0);
        setAttackDirection(0);
        setDefenseDirection(0);
    }

    public Unit createUnit(String type) {

        Unit unit = null;

        if(type == "Colonist"){
            unit = new Colonist(this.getLocation(), this.getOwner());
        }
        else if(type == "Explorer"){
            unit = new Explorer(this.getLocation(), this.getOwner());
        }
        else if(type == "Melee"){
            unit = new MeleeUnit(this.getLocation(), this.getOwner());
        }
        else if(type == "Ranged"){
            unit = new RangedUnit(this.getLocation(), this.getOwner());
        } else {
            System.out.println("Not a valid type");
        }

        // add unit to tile and to player's array and take away money
        this.getLocation().addUnitToTile(unit);
        this.getOwner().addUnit(unit);
        this.getOwner().setMoney(this.getOwner().getMoney() - unit.getUnitStats().getUpkeep());

        return unit;
    }


    public void healUnits() {

        for(int i = 0; i < this.getLocation().getUnits().size(); i++){
            this.getLocation().getUnits().get(i).getUnitStats().setHealth(this.getLocation().getUnits().get(i).getUnitStats().getHealth() + 25);
        }

    }

    public int takeId() {
        for(int i = 0; i < 10; i++) {
            if( ((ids >> i) & 1) == 0 ) {
                returnId(i);
                return  i;
            }
        }
        return  -1;
    }

    public void returnId(int id) {
        ids = (ids ^ (1 << id) );
    }



}
