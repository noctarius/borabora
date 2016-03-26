package com.noctarius.borabora;

final class ByteArrayOutput
        implements Output {

    private final byte[] array;

    ByteArrayOutput(byte[] array) {
        this.array = array;
    }

    @Override
    public void write(long index, byte value) {
        array[(int) index] = value;
    }

    @Override
    public boolean ensureCapacity(long index, long length) {
        if (index >= array.length) {
            return false;
        }
        if (index + length >= array.length) {
            return false;
        }
        return true;
    }

}
