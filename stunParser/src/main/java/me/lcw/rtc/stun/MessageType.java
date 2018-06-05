package me.lcw.rtc.stun;

public enum MessageType {
    REQUEST(0x0001), 
    SUCCESS(0x0101), 
    FAILURE(0x0111), 
    INDICATION(0x0011);

    public final int bits;
    private MessageType(int bits) {
        this.bits = bits;
    }

    public static boolean isValidType(int bits) {
        for(MessageType ty: MessageType.values()) {
            if(ty.bits == bits) {
                return true;
            }
        }
        return false;
    }
}