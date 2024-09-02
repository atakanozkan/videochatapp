package com.videochat.common.agora.media;

public interface Packable {
    ByteBuf marshal(ByteBuf out);
}