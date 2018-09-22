package sovsen;

import java.util.ArrayList;

public class ObserverList extends ArrayList {

    private ArrayList<ClientController> observers = new ArrayList<>();

    public ObserverList(){

    }


    public ClientController getObserver(int index){
        return observers.get(index);
    }


    public void addObserver(ClientController c){
        observers.add(c);
    }


    public void resetObservers(){
        observers.clear();
    }


    public ArrayList<ClientController> getObservers(){
        return observers;
    }


}
