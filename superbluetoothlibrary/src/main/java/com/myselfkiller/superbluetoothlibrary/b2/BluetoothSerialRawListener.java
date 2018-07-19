package com.myselfkiller.superbluetoothlibrary.b2;

abstract class BluetoothSerialRawListener {

    /**
     * Specified message is read from the serial port.
     *
     * @param bytes The byte array read.
     */
    public abstract void onBluetoothSerialReadRaw(byte[] bytes);

    /**
     * Specified message is written to the serial port.
     *
     * @param bytes The byte array written.
     */
    public abstract void onBluetoothSerialWriteRaw(byte[] bytes);

}
