package com.github.koszoaron.bthes.plcsim.util;

public class HexRegister extends Register {

    /**
     * A register containing hexadecimal values
     * 
     * @param address The address of the register
     */
    public HexRegister(int address) {
        super(address);
    }

    @Override
    public int getValue() {
        int res = upperByte << 8;
        res = res | lowerByte;
        return res;
    }

    @Override
    public void setValue(int value) {
        if (value < 256) {
            lowerByte = value;
            upperByte = 0;
        } else {
            lowerByte = value & 0xff;
            upperByte = value >> 8;
        }
    }
    
    @Override
    public int getType() {
        return Constants.REGISTER_TYPE_HEX;
    }

}