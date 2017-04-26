/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timer;

import java.net.*;
import java.io.*;

/**
 *
 * @author sniper
 */
public class Main {

    public static void main(String[] args) throws IOException {
        GUI gui = new GUI();
        Thread t = new Thread(gui);
        t.run();
    }
}
