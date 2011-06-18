// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import net.minecraft.client.Minecraft;

// Referenced classes of package net.minecraft.src:
//            EntityPlayerSP, MathHelper, World, Packet19EntityAction, 
//            NetClientHandler, AxisAlignedBB, Packet11PlayerPosition, Packet13PlayerLookMove, 
//            Packet12PlayerLook, Packet10Flying, Packet14BlockDig, Packet3Chat, 
//            Packet18Animation, Packet9Respawn, Packet101CloseWindow, Container, 
//            InventoryPlayer, StatBase, Session, Entity, 
//            EntityItem

public class EntityClientPlayerMP extends EntityPlayerSP
{

    public EntityClientPlayerMP(Minecraft minecraft, World world, Session session, NetClientHandler netclienthandler)
    {
        super(minecraft, world, session, 0);
        field_9380_bx = 0;
        field_21093_bH = false;
        field_9382_bF = false;
        wasSneaking = false;
        field_12242_bI = 0;
        sendQueue = netclienthandler;
    }

    public boolean attackEntityFrom(Entity entity, int i)
    {
        return false;
    }

    public void heal(int i)
    {
    }

    public void onUpdate()
    {
        if(!worldObj.blockExists(MathHelper.floor_double(posX), 64, MathHelper.floor_double(posZ)))
        {
            return;
        } else
        {
            super.onUpdate();
            func_4056_N();
            return;
        }
    }

    public void func_4056_N()
    {
        if(field_9380_bx++ == 20)
        {
            sendInventoryChanged();
            field_9380_bx = 0;
        }
        boolean flag = isSneaking();
        if(flag != wasSneaking)
        {
            if(flag)
            {
                sendQueue.addToSendQueue(new Packet19EntityAction(this, 1));
            } else
            {
                sendQueue.addToSendQueue(new Packet19EntityAction(this, 2));
            }
            wasSneaking = flag;
        }
        double d = posX - oldPosX;
        double d1 = boundingBox.minY - field_9378_bz;
        double d2 = posY - oldPosY;
        double d3 = posZ - oldPosZ;
        double d4 = rotationYaw - oldRotationYaw;
        double d5 = rotationPitch - oldRotationPitch;
        boolean flag1 = d1 != 0.0D || d2 != 0.0D || d != 0.0D || d3 != 0.0D;
        boolean flag2 = d4 != 0.0D || d5 != 0.0D;
        if(ridingEntity != null)
        {
            if(flag2)
            {
                sendQueue.addToSendQueue(new Packet11PlayerPosition(motionX, -999D, -999D, motionZ, onGround));
            } else
            {
                sendQueue.addToSendQueue(new Packet13PlayerLookMove(motionX, -999D, -999D, motionZ, rotationYaw, rotationPitch, onGround));
            }
            flag1 = false;
        } else
        if(flag1 && flag2)
        {
            sendQueue.addToSendQueue(new Packet13PlayerLookMove(posX, boundingBox.minY, posY, posZ, rotationYaw, rotationPitch, onGround));
            field_12242_bI = 0;
        } else
        if(flag1)
        {
            sendQueue.addToSendQueue(new Packet11PlayerPosition(posX, boundingBox.minY, posY, posZ, onGround));
            field_12242_bI = 0;
        } else
        if(flag2)
        {
            sendQueue.addToSendQueue(new Packet12PlayerLook(rotationYaw, rotationPitch, onGround));
            field_12242_bI = 0;
        } else
        {
            sendQueue.addToSendQueue(new Packet10Flying(onGround));
            if(field_9382_bF != onGround || field_12242_bI > 200)
            {
                field_12242_bI = 0;
            } else
            {
                field_12242_bI++;
            }
        }
        field_9382_bF = onGround;
        if(flag1)
        {
            oldPosX = posX;
            field_9378_bz = boundingBox.minY;
            oldPosY = posY;
            oldPosZ = posZ;
        }
        if(flag2)
        {
            oldRotationYaw = rotationYaw;
            oldRotationPitch = rotationPitch;
        }
    }

    public void dropCurrentItem()
    {
        sendQueue.addToSendQueue(new Packet14BlockDig(4, 0, 0, 0, 0));
    }

    private void sendInventoryChanged()
    {
    }

    protected void joinEntityItemWithWorld(EntityItem entityitem)
    {
    }

    public void sendChatMessage(String s)
    {
        sendQueue.addToSendQueue(new Packet3Chat(s));
    }

    public void swingItem()
    {
        super.swingItem();
        sendQueue.addToSendQueue(new Packet18Animation(this, 1));
    }

    public void respawnPlayer()
    {
        sendInventoryChanged();
        sendQueue.addToSendQueue(new Packet9Respawn((byte)dimension));
    }

    protected void damageEntity(int i)
    {
        health -= i;
    }

    public void func_20059_m()
    {
        sendQueue.addToSendQueue(new Packet101CloseWindow(craftingInventory.windowId));
        inventory.setItemStack(null);
        super.func_20059_m();
    }

    public void setHealth(int i)
    {
        if(field_21093_bH)
        {
            super.setHealth(i);
        } else
        {
            health = i;
            field_21093_bH = true;
        }
    }

    public void addStat(StatBase statbase, int i)
    {
        if(statbase == null)
        {
            return;
        }
        if(statbase.field_27088_g)
        {
            super.addStat(statbase, i);
        }
    }

    public void func_27027_b(StatBase statbase, int i)
    {
        if(statbase == null)
        {
            return;
        }
        if(!statbase.field_27088_g)
        {
            super.addStat(statbase, i);
        }
    }
    
    public void handleKeyPress(int i, boolean flag) {
        //BukkitContrib Start
        if (BukkitContrib.isEnabled()) {
            sendQueue.addToSendQueue(new CustomPacket(new PacketKeyPress((byte)i, flag, (MovementInputFromOptions)movementInput)));
            Minecraft game = BukkitContrib.getGameInstance();
            if (game != null && BukkitContrib.getVersion() > 5 && flag) {
                final GameSettings settings = game.gameSettings;
                if (i == settings.keyBindToggleFog.keyCode) {
                    byte view = (byte)settings.renderDistance;
                    byte newView = BukkitContrib.getNextRenderDistance(view);
                    final KeyBinding old = settings.keyBindToggleFog;
                    settings.keyBindToggleFog = new KeyBinding("key.fog", -1);
                    (new BukkitContribResetKeyThread(settings, old)).start();
                    if (view != newView) {
                        settings.renderDistance = newView;
                        sendQueue.addToSendQueue(new CustomPacket(new PacketRenderDistance((byte)newView)));
                    }
                }
            }
        }
        //BukkitContrib End
        super.handleKeyPress(i, flag);
    }
    
    //BukkitContrib Start
    public void updateCloak() {
        if (this.cloakUrl == null || this.playerCloakUrl == null) {
            super.updateCloak();
            System.out.println("UpdateCloak");
        }
    }
    //BukkitContrib End

    public NetClientHandler sendQueue;
    private int field_9380_bx;
    private boolean field_21093_bH;
    private double oldPosX;
    private double field_9378_bz;
    private double oldPosY;
    private double oldPosZ;
    private float oldRotationYaw;
    private float oldRotationPitch;
    private boolean field_9382_bF;
    private boolean wasSneaking;
    private int field_12242_bI;
}
