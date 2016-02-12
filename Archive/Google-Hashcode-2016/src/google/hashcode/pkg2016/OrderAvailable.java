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
public class OrderAvailable {

    public Warehouse w;
    public ProductType p;

    public OrderAvailable(Warehouse w, ProductType p) {
        this.p = p;
        this.w = w;
    }

}
