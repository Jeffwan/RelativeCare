/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.pitt.relativecare;

import com.google.android.gms.maps.internal.m;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Storage for geofence values, implemented in SharedPreferences.
 * For a production app, use a content provider that's synced to the
 * web or loads geofence data based on current location.
 */
public class SimpleGeofenceStore {

    private final SharedPreferences mPrefs;
    private static final String SHARED_PREFERENCE_NAME =
                    MainActivity.class.getSimpleName();

    // Create the SharedPreferences storage with private access only
    // 构造方法，传进来的时候就的给context，这样sp 就能初始化好了
    public SimpleGeofenceStore(Context context) {
        mPrefs =
                context.getSharedPreferences(
                        SHARED_PREFERENCE_NAME,
                        Context.MODE_PRIVATE);
    }

    /**
     * Returns a stored geofence by its id, or returns {@code null} if it's not found.
     */
    public SimpleGeofence getGeofence(String id) {

    	// getGeofenceFieldKey 就是一个工具类，拼装一下SharedPeference的Key, 弄成一个长串了
    	// return 时候直接调用Geofence的构造方法，组装一个 object传回去
    	String name = mPrefs.getString(getGeofenceFieldKey(id, GeofenceUtils.KEY_NAME), GeofenceUtils.INVALID_STRING_VALUE);
    	String address = mPrefs.getString(getGeofenceFieldKey(id, GeofenceUtils.KEY_ADDRESS), GeofenceUtils.INVALID_STRING_VALUE);
        double lat = mPrefs.getFloat(getGeofenceFieldKey(id, GeofenceUtils.KEY_LATITUDE), GeofenceUtils.INVALID_FLOAT_VALUE);        
        double lng = mPrefs.getFloat(getGeofenceFieldKey(id, GeofenceUtils.KEY_LONGITUDE), GeofenceUtils.INVALID_FLOAT_VALUE);
        float radius = mPrefs.getFloat(getGeofenceFieldKey(id, GeofenceUtils.KEY_RADIUS), GeofenceUtils.INVALID_FLOAT_VALUE);
        long expirationDuration = mPrefs.getLong(getGeofenceFieldKey(id, GeofenceUtils.KEY_EXPIRATION_DURATION), GeofenceUtils.INVALID_LONG_VALUE);
        int transitionType = mPrefs.getInt(getGeofenceFieldKey(id, GeofenceUtils.KEY_TRANSITION_TYPE), GeofenceUtils.INVALID_INT_VALUE);

        // If none of the values is incorrect, return the object
        if (
        	name != GeofenceUtils.INVALID_STRING_VALUE &&
        	address != GeofenceUtils.INVALID_STRING_VALUE &&
            lat != GeofenceUtils.INVALID_FLOAT_VALUE &&
            lng != GeofenceUtils.INVALID_FLOAT_VALUE &&
            radius != GeofenceUtils.INVALID_FLOAT_VALUE &&
            expirationDuration != GeofenceUtils.INVALID_LONG_VALUE &&
            transitionType != GeofenceUtils.INVALID_INT_VALUE) {

            // Return a true Geofence object, 这里用构造方法返回的一个SimpleGeofence Object
            return new SimpleGeofence(id, name, address, lat, lng, radius, expirationDuration, transitionType);
        // Otherwise, return null.
        } else {
            return null;
        }
    }

    /**
     * Save a geofence.
     */
    public void setGeofence(String id, SimpleGeofence geofence) {

    	Editor editor = mPrefs.edit();

        // Write the Geofence values to SharedPreferences
        // Lat & Lng 原本是double的，现在转换成 Float
    	editor.putString(getGeofenceFieldKey(id, GeofenceUtils.KEY_NAME), geofence.getName());
    	editor.putString(getGeofenceFieldKey(id, GeofenceUtils.KEY_ADDRESS), geofence.getAddress());
        editor.putFloat(getGeofenceFieldKey(id, GeofenceUtils.KEY_LATITUDE), (float) geofence.getLatitude());
        editor.putFloat(getGeofenceFieldKey(id, GeofenceUtils.KEY_LONGITUDE), (float) geofence.getLongitude());
        editor.putFloat(getGeofenceFieldKey(id, GeofenceUtils.KEY_RADIUS), geofence.getRadius());
        editor.putLong(getGeofenceFieldKey(id, GeofenceUtils.KEY_EXPIRATION_DURATION), geofence.getExpirationDuration());
        editor.putInt(getGeofenceFieldKey(id, GeofenceUtils.KEY_TRANSITION_TYPE), geofence.getTransitionType());

        // Commit the changes
        editor.commit();
    }

    public void clearGeofence(String id) {

        // Remove a flattened geofence object from storage by removing all of its keys
    	// 删除的话，直接移除字段，但是这样只是删除了SP中的，真正的有UnRegister么？
        Editor editor = mPrefs.edit();
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_NAME));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_ADDRESS));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_LATITUDE));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_LONGITUDE));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_RADIUS));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_EXPIRATION_DURATION));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_TRANSITION_TYPE));
        editor.commit();
    }

    /**
     * Given a Geofence object's ID and the name of a field
     * (for example, GeofenceUtils.KEY_LATITUDE), return the key name of the
     * object's values in SharedPreferences.
     *
     * @param id The ID of a Geofence object
     * @param fieldName The field represented by the key
     * @return The full key name of a value in SharedPreferences
     */
    private String getGeofenceFieldKey(String id, String fieldName) {

        return
                GeofenceUtils.KEY_PREFIX +
                id +
                "_" +
                fieldName;
    }
}
