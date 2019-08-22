package com.galiyara.sandy.galiyara.GDatabase;

import androidx.room.Room;
import android.content.Context;

public class GDatabaseClient {

    private static GDatabaseClient gDatabaseClient;
    private GDatabase gDatabase;

    private GDatabaseClient(Context ctx) {
        gDatabase = Room.databaseBuilder(ctx,GDatabase.class,"GaliyaraDB").build();
    }

    static synchronized GDatabaseClient getInstance(Context ctx) {
        if(gDatabaseClient == null)
            gDatabaseClient = new GDatabaseClient(ctx);
        return gDatabaseClient;
    }
    public GDatabase getGDatabase() {
        return gDatabase;
    }
}
