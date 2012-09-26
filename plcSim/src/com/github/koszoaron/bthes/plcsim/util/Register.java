package com.github.koszoaron.bthes.plcsim.util;

public abstract class Register {

    protected byte upperByte = 0;
    protected byte lowerByte = 0;
    private int address;
    
    /**
     * Returns the value of the register
     * 
     * @return An integer with the register value
     */
    public abstract int getValue();
    
    /**
     * Sets the value of the register
     * 
     * @param value An integer with the new register value
     */
    public abstract void setValue(int value);
    
    /**
     * An object representation of a register
     * 
     * @param address The address of the register
     */
    protected Register(int address) {
        this.address = address;
    }
    
    /**
     * Returns the upper byte of the register
     * 
     * @return
     */
    public byte getUpperByte() {
        return upperByte;
    }
    
    /**
     * Returns the lower byte of the register
     * 
     * @return
     */
    public byte getLowerByte() {
        return lowerByte;
    }
    
    /**
     * Returns the address of the register
     * 
     * @return
     */
    public int getAddress() {
        return address;
    }
}