/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package google.hashcode.pkg2016;

/**
 *
 * @author james
 */
public class Action {

    public Drone d;
    public ActionCommands a;

    public Action(Drone d, ActionCommands a) {
        this.d = d;
        this.a = a;
    }

    public void applyAction() {
        if (!d.isBusy()) {
            switch (a) {
                case FLY:

                    // do flying
                    break;
                case LOAD:
                    
                    break;
                    
                case DELIVER:
                    
                    break;
                                       
                case UNLOAD:
                    
                    break;
                    
                case WAIT:
                    
                    break;
                    
            }
        }
        
        
        
    }

}
