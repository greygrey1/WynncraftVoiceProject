package com.wynnvp.wynncraftvp.events.custom;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.GenericEvent;

public class PacketEvent<T extends Packet<?>> extends GenericEvent<T> {

    final T packet;
    final NetHandlerPlayClient playClient;
    final ChannelHandler handler;
    final ChannelHandlerContext ctx;

    public PacketEvent(T packet, NetHandlerPlayClient playClient, ChannelHandler handler, ChannelHandlerContext ctx) {
        super((Class<T>) packet.getClass());
        this.packet = packet;
        this.playClient = playClient;
        this.handler = handler;
        this.ctx = ctx;
    }

    public T getPacket() {
        return packet;
    }

    public NetHandlerPlayClient getPlayClient() {
        return playClient;
    }

    public ChannelHandler getHandler() {
        return handler;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    public static class Incoming<T extends Packet<?>> extends PacketEvent<T> {

        public Incoming(T packet, NetHandlerPlayClient playClient, ChannelInboundHandler adapter, ChannelHandlerContext ctx) {
            super(packet, playClient, adapter, ctx);
        }

        public void emulateRead(Packet<?> packet) {
            try {
                ((ChannelInboundHandler) handler).channelRead(ctx, packet);
            } catch (Exception ignored) {
            }
        }

        @Override
        public ChannelInboundHandler getHandler() {
            return (ChannelInboundHandler) handler;
        }

    }

}
