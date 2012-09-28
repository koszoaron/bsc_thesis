package com.github.koszoaron.bthes.plcsim.util;

public abstract class Register {

    protected int upperByte = 0;
    protected int lowerByte = 0;
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
     * Returns the type of the register
     * 
     * @return <code>Constants.REGISTER_TYPE_HEX</code> or <code>Constants.REGISTER_TYPE_BCD</code>
     */
    public abstract int getType();
    
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
    public int getUpperByte() {
        return upperByte;
    }
    
    /**
     * Returns the lower byte of the register
     * 
     * @return
     */
    public int getLowerByte() {
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
    
    @Override
    public String toString() {
        String res = "R: " + address + ", T: " + (getType() == 0 ? "hex" : "bcd") + ", U: " + upperByte + ", L: " + lowerByte + ", V: " + getValue();
        return res;
    }
}