package cfs.guider.action;


public class Action{
    public enum ActionType{
        INSERT, MOVE
    }
    ActionType type;

    public Action(ActionType type){
        this.type = type;
    }

}