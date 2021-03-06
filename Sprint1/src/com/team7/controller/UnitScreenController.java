package com.team7.controller;

import com.team7.objects.Army;
import com.team7.view.*;
import com.team7.objects.Game;
import com.team7.objects.unit.Unit;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class UnitScreenController {
    private Game game;
    private View view;

    public UnitScreenController(Game game, View view) {
        this.game = game;
        this.view = view;
        setUnits((ArrayList<Unit>) game.getCurrentPlayer().getUnits());
        setArmies(game.getCurrentPlayer().getArmies());
        addActionListeners();
    }

    void setUnits(ArrayList<Unit> units) {
        view.getScreen().getUnitScreen().setUnits(units);
    }
    void setArmies(ArrayList<Army> armies) { view.getScreen().getUnitScreen().setArmies(armies);}
    private void addActionListeners() {

        //Add Action Listener for "Create Army" button
        view.getScreen().getUnitScreen().getAddArmyButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == view.getScreen().getUnitScreen().getAddArmyButton()) {
                    String unitString = (String) view.getScreen().getUnitScreen().getUnitList().getSelectedValue();
                    if (unitString == null) {
                        view.getScreen().getUnitScreen().displayMessage("Select a unit to create an army with");
                        return;
                    } else if (game.getCurrentPlayer().getArmies().size() >= 10) {
                        view.getScreen().getUnitScreen().displayMessage("You already have 10 armies");
                        return;
                    }
                    //System.out.println("Create Army hit with fetched unit string " + unitString);
                    Unit selected = null;
                    List<Unit> units = game.getCurrentPlayer().getUnits();
                    for (Unit u : units) {
                        String cur = u.getType() + " " + u.getId();
                        if (cur.equals(unitString)) {
                            System.out.println("Found unit " + u.getType());
                            selected = u;
                            if (!u.getType().equals("Colonist") && !u.getType().equals("Explorer")) {
                                if (u.getArmy() == null) {
                                    Army newArmy = new Army(selected.getLocation(), game.getCurrentPlayer());
                                    newArmy.addUnitToArmy(u);
                                    game.getCurrentPlayer().addArmy(newArmy);
                                    view.getScreen().getUnitScreen().getArmyModel().addElement(newArmy.getName());
                                    view.getScreen().getUnitScreen().changeListVal();
                                    view.getScreen().getUnitScreen().repaint();
                                    System.out.println("Created " + newArmy.getName());
                                    System.out.println("There are now " + game.getCurrentPlayer().getArmies().size() + " armies.");
                                } else {
                                    view.getScreen().getUnitScreen().displayMessage("That unit is already in an army");
                                }
                            } else {
                                view.getScreen().getUnitScreen().displayMessage("You can't construct an army with that type of unit");
                            }
                            break;
                        }
                    }
                } else {
                    System.out.println("ERROR");
                }
            }
        });

        //Add ActionListener for disband armies button
        view.getScreen().getUnitScreen().getDisbandArmyButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == view.getScreen().getUnitScreen().getDisbandArmyButton()) {
                    String selectedArmy = view.getScreen().getUnitScreen().getArmyList().getSelectedValue();
                    List<Army> armies = game.getCurrentPlayer().getArmies();

                    for (Army a: armies) {
                        System.out.println(a.getName());
                        if (a.getName().equals(selectedArmy)) {
                            view.getScreen().getUnitScreen().getArmyModel().removeElement(a.getName());
                            view.getScreen().getUnitScreen().repaint();
                            a.disband();
                            return;
                        }
                    }

                    System.out.println("ERROR: Could not delete " + selectedArmy);
                }
            }
        });

        //Add ActionListener for add unit button
        view.getScreen().getUnitScreen().getAddUnitButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == view.getScreen().getUnitScreen().getAddUnitButton()) {
                    //System.out.println("Hit add unit button");
                    String selectedUnitString = (String) view.getScreen().getUnitScreen().getUnitList().getSelectedValue();
                    String selectedArmyString = view.getScreen().getUnitScreen().getArmyList().getSelectedValue();

                    Unit selectedUnitObject = null;
                    Army selectedArmyObject = null;

                    if (selectedArmyString == null || selectedUnitString == null) {
                        view.getScreen().getUnitScreen().displayMessage("Select a Unit and an Army");
                        return;
                    }

                    List<Unit> units = game.getCurrentPlayer().getUnits();
                    for (Unit u:units) {
                        if (selectedUnitString.equals(u.getType() + " " + u.getId())) {
                            selectedUnitObject = u;
                        }
                    }
                    List<Army> armies = game.getCurrentPlayer().getArmies();
                    for (Army a: armies) {
                        if (selectedArmyString.equals(a.getName())) {
                            selectedArmyObject = a;
                        }
                    }

                    if (selectedUnitObject == null) {
                        System.out.println("Could not find Unit" + selectedUnitString);
                        return;
                    }

                    if (selectedArmyObject == null) {
                        System.out.println("Could not find Army " + selectedArmyString);
                        return;
                    }

                    if (selectedUnitObject.getType() == "Explorer" || selectedUnitObject.getType() == "Colonist") {
                        view.getScreen().getUnitScreen().displayMessage("That unit can not join an army");
                        return;
                    }

                    if (selectedUnitObject.getArmy() != null) {
                        view.getScreen().getUnitScreen().displayMessage("That unit is already in an army");
                        return;
                    }

                    selectedArmyObject.addUnitToArmy(selectedUnitObject);
                    Army finalSelectedArmyObject = selectedArmyObject;
                    view.getScreen().getUnitScreen().getUnitsinArmyList().setModel(new AbstractListModel() {
                        @Override
                        public int getSize() {
                            return finalSelectedArmyObject.getUnits().size();
                        }

                        @Override
                        public Object getElementAt(int index) {
                            return finalSelectedArmyObject.getUnits().get(index).getType() + " " + finalSelectedArmyObject.getUnits().get(index).getId();
                        }
                    });
                    view.getScreen().getUnitScreen().repaint();
                }
            }
        });

        //Add ActionListener for army selection
        view.getScreen().getUnitScreen().getArmyList().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                String selectedArmy = view.getScreen().getUnitScreen().getArmyList().getSelectedValue();
                List<Army> armies = game.getCurrentPlayer().getArmies();

                for (Army a: armies) {
                    if (a.getName().equals(selectedArmy)) {
                        view.getScreen().getUnitScreen().getUnitsinArmyList().setModel(new AbstractListModel() {
                            @Override
                            public int getSize() {
                                return a.getUnits().size();
                            }

                            @Override
                            public Object getElementAt(int index) {
                                return a.getUnits().get(index).getType() + " " + a.getUnits().get(index).getId();
                            }
                        });

                        view.getScreen().getUnitScreen().setQueueModel(a.getCommandQueue().getCommands());
                        view.getScreen().getUnitScreen().repaint();
                    }
                }
            }
        });

        //Add Action Listener for remove unit
        view.getScreen().getUnitScreen().getRemoveUnitButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == view.getScreen().getUnitScreen().getRemoveUnitButton()) {
                    String selectedUnitString = (String) view.getScreen().getUnitScreen().getUnitsinArmyList().getSelectedValue();
                    String selectedArmyString = view.getScreen().getUnitScreen().getArmyList().getSelectedValue();

                    Unit selectedUnitObject = null;
                    Army selectedArmyObject = null;

                    if (selectedArmyString == null || selectedUnitString == null) {
                        view.getScreen().getUnitScreen().displayMessage("Select a Unit and an Army");
                        return;
                    }

                    List<Unit> units = game.getCurrentPlayer().getUnits();
                    for (Unit u:units) {
                        if (selectedUnitString.equals(u.getType() + " " + u.getId())) {
                            selectedUnitObject = u;
                        }
                    }
                    List<Army> armies = game.getCurrentPlayer().getArmies();
                    for (Army a: armies) {
                        if (selectedArmyString.equals(a.getName())) {
                            selectedArmyObject = a;
                        }
                    }

                    selectedArmyObject.removeUnitFromArmy(selectedUnitObject);
                    Army finalSelectedArmyObject = selectedArmyObject;
                    view.getScreen().getUnitScreen().getUnitsinArmyList().setModel(new AbstractListModel() {
                        @Override
                        public int getSize() {
                            return finalSelectedArmyObject.getUnits().size();
                        }

                        @Override
                        public Object getElementAt(int index) {
                            return finalSelectedArmyObject.getUnits().get(index).getType() + " " + finalSelectedArmyObject.getUnits().get(index).getId();
                        }
                    });
                    view.getScreen().getUnitScreen().repaint();
                }
            }
        });

        //Add action listener for cancel command button
        view.getScreen().getUnitScreen().getCancelCommand().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == view.getScreen().getUnitScreen().getCancelCommand()) {
                    String selectedCommand = view.getScreen().getUnitScreen().getQueueList().getSelectedValue();
                    String selectedArmy = view.getScreen().getUnitScreen().getArmyList().getSelectedValue();
                    if (selectedArmy == null || selectedCommand == null) {
                        return;
                    }
                    List<Army> armies = game.getCurrentPlayer().getArmies();
                    for (Army a : armies) {
                        if (selectedArmy.equals(a.getName())) {
                            a.getCommandQueue().removeCommand(selectedCommand);
                            view.getScreen().getUnitScreen().setQueueModel(a.getCommandQueue().getCommands());
                            System.out.println("Removed command " + selectedCommand + " from queue");
                        }
                    }
                }
            }
        });

        //Add action listener for move up button
        view.getScreen().getUnitScreen().getMoveUpCommandButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == view.getScreen().getUnitScreen().getMoveUpCommandButton()) {
                    String selectedCommand = view.getScreen().getUnitScreen().getQueueList().getSelectedValue();
                    String selectedArmy = view.getScreen().getUnitScreen().getArmyList().getSelectedValue();
                    if (selectedArmy == null || selectedCommand == null) {
                        return;
                    }
                    List<Army> armies = game.getCurrentPlayer().getArmies();
                    for (Army a : armies) {
                        if (selectedArmy.equals(a.getName())) {
                            a.getCommandQueue().raiseCommmand(selectedCommand);
                            view.getScreen().getUnitScreen().setQueueModel(a.getCommandQueue().getCommands());
                            System.out.println("Raised " + selectedCommand + " from queue");
                        }
                    }
                }
            }
        });

        //Add Action listener for move down button
        view.getScreen().getUnitScreen().getMoveDownCommandButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == view.getScreen().getUnitScreen().getMoveDownCommandButton()) {
                    String selectedCommand = view.getScreen().getUnitScreen().getQueueList().getSelectedValue();
                    String selectedArmy = view.getScreen().getUnitScreen().getArmyList().getSelectedValue();
                    if (selectedArmy == null || selectedCommand == null) {
                        return;
                    }
                    List<Army> armies = game.getCurrentPlayer().getArmies();
                    for (Army a : armies) {
                        if (selectedArmy.equals(a.getName())) {
                            a.getCommandQueue().lowerCommand(selectedCommand);
                            view.getScreen().getUnitScreen().setQueueModel(a.getCommandQueue().getCommands());
                            System.out.println("Lowered" + selectedCommand + " from queue");
                        }
                    }
                }
            }
        });
    }



}
