package com.github.koszoaron.bthes.plcsim;

import com.github.koszoaron.bthes.plcsim.util.BcdRegister;
import com.github.koszoaron.bthes.plcsim.util.HexRegister;
import com.github.koszoaron.bthes.plcsim.util.Register;

public class PlcSim {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Register r1 = new HexRegister(0x01);
        r1.setValue(0x1234);        
        System.out.println(r1.toString());
        
        Register r2 = new BcdRegister(0x02);
        r2.setValue(1234);
        System.out.println(r2.toString());
    }

}
