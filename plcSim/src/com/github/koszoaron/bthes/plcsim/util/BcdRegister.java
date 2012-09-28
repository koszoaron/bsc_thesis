package com.github.koszoaron.bthes.plcsim.util;

public class BcdRegister extends Register {

    /**
     * A register containing BCD values
     * 
     * @param address The address of the register
     */
    public BcdRegister(int address) {
        super(address);
    }
    
    @Override
    public int getValue() {
        int res = upperByte * 100 + lowerByte;
        return res;
    }

    @Override
    public void setValue(int value) {
        upperByte = value / 100;
        lowerByte = value % 100;
    }

    @Override
    public int getType() {
        return Constants.REGISTER_TYPE_BCD;
    }
}
