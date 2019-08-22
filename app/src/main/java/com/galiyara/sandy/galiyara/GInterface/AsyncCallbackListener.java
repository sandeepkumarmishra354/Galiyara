package com.galiyara.sandy.galiyara.GInterface;

public interface AsyncCallbackListener {
    void taskInitiated();
    void taskCompleted(int d);
    void taskProgress(int pd);
}
