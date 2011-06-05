// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;


// Referenced classes of package net.minecraft.src:
//            MovementInput, GameSettings, KeyBinding, EntityPlayer

public class MovementInputFromOptions extends MovementInput
{

    public MovementInputFromOptions(GameSettings gamesettings)
    {
        movementKeyStates = new boolean[10];
        gameSettings = gamesettings;
    }

    public void checkKeyForMovementInput(int i, boolean flag)
    {
        byte byte0 = -1;
        if(i == gameSettings.keyBindForward.keyCode)
        {
            byte0 = 0;
        }
        if(i == gameSettings.keyBindBack.keyCode)
        {
            byte0 = 1;
        }
        if(i == gameSettings.keyBindLeft.keyCode)
        {
            byte0 = 2;
        }
        if(i == gameSettings.keyBindRight.keyCode)
        {
            byte0 = 3;
        }
        if(i == gameSettings.keyBindJump.keyCode)
        {
            byte0 = 4;
        }
        if(i == gameSettings.keyBindSneak.keyCode)
        {
            byte0 = 5;
        }
        if(byte0 >= 0)
        {
            movementKeyStates[byte0] = flag;
        }
    }

    public void resetKeyState()
    {
        for(int i = 0; i < 10; i++)
        {
            movementKeyStates[i] = false;
        }

    }

    public void updatePlayerMoveState(EntityPlayer entityplayer)
    {
        moveStrafe = 0.0F;
        moveForward = 0.0F;
        if(movementKeyStates[0])
        {
            moveForward++;
        }
        if(movementKeyStates[1])
        {
            moveForward--;
        }
        if(movementKeyStates[2])
        {
            moveStrafe++;
        }
        if(movementKeyStates[3])
        {
            moveStrafe--;
        }
        jump = movementKeyStates[4];
        sneak = movementKeyStates[5];
        if(sneak)
        {
            moveStrafe *= 0.29999999999999999D;
            moveForward *= 0.29999999999999999D;
        }
    }

    private boolean movementKeyStates[];
    public GameSettings gameSettings; //BukkitContrib
}
